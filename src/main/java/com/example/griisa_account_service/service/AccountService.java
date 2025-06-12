package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.Account;
import com.example.griisa_account_service.entity.KycRecord;
import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.repo.AccountRepository;
import com.example.griisa_account_service.repo.KycRepository;
import com.example.griisa_account_service.repo.UserRepository;
import com.example.griisa_account_service.util.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final UserRepository userRepo;
    private final KycRepository kycRepo;
    private final AccountRepository accountRepo;
    private final KycService kycService;
    private final FailedRecordService failedRecordService;
    private final UserRequestValidator userRequestValidator;
    private final UserFactory userFactory;
    private final KycRecordFactory kycRecordFactory;
    private final AccountFactory accountFactory;

    @Autowired
    public AccountService(
            UserRepository userRepo,
            KycRepository kycRepo,
            AccountRepository accountRepo,
            KycService kycService,
            FailedRecordService failedRecordService,
            UserRequestValidator userRequestValidator,
            UserFactory userFactory,
            KycRecordFactory kycRecordFactory,
            AccountFactory accountFactory
    ) {
        this.userRepo = userRepo;
        this.kycRepo = kycRepo;
        this.accountRepo = accountRepo;
        this.kycService = kycService;
        this.failedRecordService = failedRecordService;
        this.userRequestValidator = userRequestValidator;
        this.userFactory = userFactory;
        this.kycRecordFactory = kycRecordFactory;
        this.accountFactory = accountFactory;
    }

    public void processBatch(List<UserRequestDTO> requests) {
        for (UserRequestDTO dto : requests) {
            try {
                processSingleUserTransactional(dto);
            } catch (Exception e) {
                log.error("Exception during per-record processing (should not affect others): {}", e.getMessage(), e);
            }
        }
    }

    @Retryable(
            value = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleUserTransactional(UserRequestDTO dto) {
        try {
            userRequestValidator.validate(dto);

            User user = userRepo.save(userFactory.create(dto));

            String kycResult = kycService.performKYC(user, dto.getAadhaarNumber(), dto.getPanNumber()).get();

            log.info("AccountService | processSingleUserTransactional: KYC result for {}: {}", dto.getEmail(), kycResult);
            KycRecord kyc = kycRepo.save(kycRecordFactory.create(user, dto, kycResult));

            if ("SUCCESS".equalsIgnoreCase(kycResult)) {
                Account acc = accountRepo.save(accountFactory.create(user));
                log.info("Account created successfully: {}", acc.getAccountNumber());
            } else {
                log.info("AccountService | processSingleUserTransactional: KYC failed for {}", dto.getEmail());
                handleFailure(dto, "KYC failed for " + dto.getEmail() + ". Account not created.");
                log.warn("KYC failed for {}. Account not created.", dto.getEmail());
            }

        } catch (ConstraintViolationException e) {
            handleFailure(dto, "Validation failed: " + e.getConstraintViolations());
        } catch (Exception e) {
            handleFailure(dto, "Processing failed: " + e.getMessage());
        }
    }

    private void handleFailure(UserRequestDTO dto, String errorMsg) {
        log.info("AccountService | handleFailure: {}", dto.toString());
        log.warn(errorMsg);
        failedRecordService.save(dtoToArray(dto), errorMsg);
    }

    private String[] dtoToArray(UserRequestDTO dto) {
        return new String[]{
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getAadhaarNumber(),
                dto.getPanNumber(),
                dto.getAddressLine1(),
                dto.getAddressLine2(),
                dto.getCity(),
                dto.getState(),
                dto.getZipCode(),
                dto.getIdDocumentType(),
                dto.getIdDocumentNumber()
        };
    }
}