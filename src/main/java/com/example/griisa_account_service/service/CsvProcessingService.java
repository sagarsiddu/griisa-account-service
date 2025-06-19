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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private UserCsvRecordMapper userCsvRecordMapper;

    public void processCsv(MultipartFile file) throws Exception {
        if (!CsvUtils.isCsvFile(file)) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        List<FailedRecord> parseFailures = new ArrayList<>();
        Iterator<UserCsvRecordDto> iterator = CsvUtils.streamingCsvIterator(file, (rowNum, errorMsg) -> {
            parseFailures.add(new FailedRecord(null, "Parse error at row " + rowNum + ": " + errorMsg, "PARSE_ERROR", "ParseError", ""));
        });
        final int CHUNK_SIZE = 500;
        List<UserCsvRecordDto> chunk = new ArrayList<>(CHUNK_SIZE);
        while (iterator.hasNext()) {
            UserCsvRecordDto dto = iterator.next();
            if (dto != null) {
                chunk.add(dto);
            }
            if (chunk.size() == CHUNK_SIZE) {
                processChunk(chunk);
                chunk.clear();
            }
        }
        if (!chunk.isEmpty()) {
            processChunk(chunk);
        }
        // Persist all parse failures
        if (!parseFailures.isEmpty()) {
            recordPersistenceService.persistFailedRecordsBatch(parseFailures);
        }
    }

    private void processChunk(List<UserCsvRecordDto> chunk) {
        List<User> users = new ArrayList<>();
        List<FailedRecord> failedRecords = new ArrayList<>();
        for (UserCsvRecordDto dto : chunk) {
            Set<ConstraintViolation<UserCsvRecordDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                failedRecords.add(new FailedRecord(null, "Validation failed: " + errors + " | ", "VALIDATION_ERROR", "ValidationError", serialize(dto)));
                continue;
            }

            boolean kycValid = false;
            try {
                kycValid = kycClient.validate(dto);
            } catch (Exception ex) {
                failedRecords.add(new FailedRecord(null, "KYC validation error: " + ex.getMessage() + " | ", "KYC_ERROR", "KycError", serialize(dto)));
                continue;
            }

            try {
                User user = toUserEntity(dto);
                user.setCreatedBy(dto.getEmail());
                user.setUpdatedBy(dto.getEmail());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                users.add(user);
            } catch (Exception ex) {
                failedRecords.add(new FailedRecord(null, "User entity creation error: " + ex.getMessage() + " | ", "ENTITY_CREATION_ERROR", "EntityCreationError", serialize(dto)));
            }
        }
        if (!users.isEmpty()) {
            recordPersistenceService.persistUsersBatch(users);
        }
        if (!failedRecords.isEmpty()) {
            recordPersistenceService.persistFailedRecordsBatch(failedRecords);
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
        User user = userCsvRecordMapper.toUser(dto);
        KycRecord kyc = userCsvRecordMapper.toKycRecord(dto);
        user.setKyc(kyc);
        Account account = userCsvRecordMapper.toAccount(dto);
        account.setAccountNumber(accountNumberGenerator.generate(dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber()));
        account.setStatus("ACTIVE");
        user.setAccount(account);
        return user;
    }
}


