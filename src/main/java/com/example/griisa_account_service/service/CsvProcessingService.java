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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Service
@RequiredArgsConstructor
public class CsvProcessingService {

    private final UserRepository userRepo;
    private final FailedRecordRepository failedRepo;
    private final KycClient kycClient;
    private final AccountNumberGenerator accountNumberGenerator;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private RecordPersistenceService recordPersistenceService;

    public void processCsv(MultipartFile file) throws Exception {
        if (!CsvUtils.isCsvFile(file)) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        List<FailedRecord> parseFailures = new ArrayList<>();
        List<UserCsvRecordDto> records = CsvUtils.parseCsvFile(file, (rowNum, errorMsg) -> {
            parseFailures.add(new FailedRecord(null, "Parse error at row " + rowNum + ": " + errorMsg, ""));
        });
        // Persist all parse failures
        for (FailedRecord fr : parseFailures) {
            recordPersistenceService.persistUserOrLogFailure(null, null, fr.getReason());
        }

        log.info("Parsed {} records from CSV file", records.size());
        List<List<UserCsvRecordDto>> chunks = chunkList(records);
        log.info("Chunked records into {} parts", chunks.size());

        List<CompletableFuture<Void>> futures = chunks.stream()
                .map(chunk -> CompletableFuture.runAsync(() -> processChunk(chunk)))
                .toList();

        log.info("Processing futures {} chunks in parallel", futures);
        // Wait for all to complete
        futures.forEach(CompletableFuture::join);
    }

    private List<List<UserCsvRecordDto>> chunkList(List<UserCsvRecordDto> list) {
        return IntStream.range(0, (list.size() + 500 - 1) / 500)
                .mapToObj(i -> list.subList(i * 500, Math.min(list.size(), (i + 1) * 500)))
                .collect(Collectors.toList());
    }

    private void processChunk(List<UserCsvRecordDto> chunk) {
        for (UserCsvRecordDto dto : chunk) {
            Set<ConstraintViolation<UserCsvRecordDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                recordPersistenceService.persistUserOrLogFailure(null, dto, "Validation failed: " + errors + " | ");
                continue;
            }

            boolean kycValid = false;
            try {
                kycValid = kycClient.validate(dto);
            } catch (Exception ex) {
                recordPersistenceService.persistUserOrLogFailure(null, dto, "KYC validation error: " + ex.getMessage() + " | ");
                continue;
            }

            try {
                User user = toUserEntity(dto);
                user.setCreatedBy(dto.getEmail());
                user.setUpdatedBy(dto.getEmail());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                recordPersistenceService.persistUserOrLogFailure(user, dto, "Persistence failed: ");
            } catch (Exception ex) {
                recordPersistenceService.persistUserOrLogFailure(null, dto, "User entity creation error: " + ex.getMessage() + " | ");
            }
        }
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
        BeanUtils.copyProperties(dto, user);

        KycRecord kyc = new KycRecord();
        BeanUtils.copyProperties(dto, kyc);
        user.setKyc(kyc);

        Account account = new Account();
        account.setAccountNumber(accountNumberGenerator.generate(dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber()));
        account.setStatus("ACTIVE");
        user.setAccount(account);

        return user;
    }
}


