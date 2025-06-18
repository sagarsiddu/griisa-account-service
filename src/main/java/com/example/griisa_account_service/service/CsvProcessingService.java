package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserCsvRecordDto;
import com.example.griisa_account_service.entity.Account;
import com.example.griisa_account_service.entity.FailedRecord;
import com.example.griisa_account_service.entity.KycRecord;
import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.repository.FailedRecordRepository;
import com.example.griisa_account_service.repository.UserRepository;
import com.example.griisa_account_service.util.CsvUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CsvProcessingService {

    private final UserRepository userRepo;
    private final FailedRecordRepository failedRepo;
    private final KycClient kycClient;
    private final AccountNumberGenerator accountNumberGenerator;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public void processCsv(MultipartFile file) throws Exception {
        if (!CsvUtils.isCsvFile(file)) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        List<UserCsvRecordDto> records = CsvUtils.parseCsvFile(file);
        List<List<UserCsvRecordDto>> chunks = chunkList(records);

        List<CompletableFuture<Void>> futures = chunks.stream()
                .map(chunk -> CompletableFuture.runAsync(() -> processChunk(chunk)))
                .toList();

        // Wait for all to complete
        futures.forEach(CompletableFuture::join);
    }

    private List<List<UserCsvRecordDto>> chunkList(List<UserCsvRecordDto> list) {
        return IntStream.range(0, (list.size() + 500 - 1) / 500)
                .mapToObj(i -> list.subList(i * 500, Math.min(list.size(), (i + 1) * 500)))
                .collect(Collectors.toList());
    }

    private void processChunk(List<UserCsvRecordDto> chunk) {
        List<User> users = new ArrayList<>();
        List<FailedRecord> failed = new ArrayList<>();

        for (UserCsvRecordDto dto : chunk) {
            Set<ConstraintViolation<UserCsvRecordDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                failed.add(new FailedRecord(null, errors, serialize(dto)));
                continue;
            }

            try {
                boolean kycValid = kycClient.validate(dto);
                if (!kycValid) throw new Exception("KYC validation failed");

                User user = toUserEntity(dto);
                user.setCreatedBy(dto.getEmail());
                user.setUpdatedBy(dto.getEmail());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                users.add(user);
            } catch (Exception ex) {
                failed.add(new FailedRecord(null, ex.getMessage(), serialize(dto)));
            }
        }

        userRepo.saveAll(users);         // Includes cascade for KYC and Account
        failedRepo.saveAll(failed);      // Only for failed records
    }

    private String serialize(UserCsvRecordDto dto) {
        try {
            return new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private User toUserEntity(UserCsvRecordDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddressLine1(dto.getAddressLine1());
        user.setAddressLine2(dto.getAddressLine2());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setZipCode(dto.getZipCode());

        // KYC
        var kyc = new KycRecord();
        kyc.setIdDocumentType(dto.getIdDocumentType());
        kyc.setIdDocumentNumber(dto.getIdDocumentNumber());
        kyc.setAadhaarNumber(dto.getAadhaarNumber());
        kyc.setPanNumber(dto.getPanNumber());
        user.setKyc(kyc);

        // Account
        var account = new Account();
        account.setAccountNumber(accountNumberGenerator.generate(dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber()));
        account.setStatus("ACTIVE");
        user.setAccount(account);

        return user;
    }
}
