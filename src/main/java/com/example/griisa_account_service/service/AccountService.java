package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.Account;
import com.example.griisa_account_service.entity.KycRecord;
import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.repo.AccountRepository;
import com.example.griisa_account_service.repo.KycRepository;
import com.example.griisa_account_service.repo.UserRepository;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@Service
public class AccountService {

    @Autowired private UserRepository userRepo;
    @Autowired private KycRepository kycRepo;
    @Autowired private AccountRepository accountRepo;
    @Autowired private KycService kycService;
    @Autowired private FailedRecordService failedRecordService;

    public void processBatch(List<UserRequestDTO> requests) {
        for (UserRequestDTO dto : requests) {
            try {
                // Delegates per-record processing to REQUIRES_NEW
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
            validate(dto);

            User user = userRepo.save(buildUser(dto));

            String kycResult = kycService.performKYC(user, dto.getAadhaarNumber(), dto.getPanNumber()).get();

            log.info("AccountService | processSingleUserTransactional: KYC result for {}: {}", dto.getEmail(), kycResult);
            KycRecord kyc = kycRepo.save(buildKyc(user, dto, kycResult));

            if ("SUCCESS".equalsIgnoreCase(kycResult)) {
                Account acc = accountRepo.save(buildAccount(user));
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

    private void validate(UserRequestDTO dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private User buildUser(UserRequestDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .idDocumentType(dto.getIdDocumentType())
                .idDocumentNumber(dto.getIdDocumentNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private KycRecord buildKyc(User user, UserRequestDTO dto, String status) {
        return KycRecord.builder()
                .user(user)
                .aadhaarNumber(dto.getAadhaarNumber())
                .panNumber(dto.getPanNumber())
                .kycStatus(status)
                .verificationDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Account buildAccount(User user) {
        String acctNum = "ACCT" + Year.now().getValue() + String.format("%08d", user.getUserId());
        return Account.builder()
                .user(user)
                .accountNumber(acctNum)
                .status("ACTIVE")
                .openedDate(LocalDate.now())
                .balance(BigDecimal.ZERO)
                .currency("INR")
                .createdAt(LocalDateTime.now())
                .build();
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

