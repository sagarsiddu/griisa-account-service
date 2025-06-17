package com.example.griisa_account_service.processor;

import com.example.griisa_account_service.service.AccountNumberService;
import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.Account;
import com.example.griisa_account_service.entity.FailedRecord;
import com.example.griisa_account_service.entity.Kyc;
import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.repository.FailedRecordRepository;
import com.example.griisa_account_service.repository.KycRepository;
import com.example.griisa_account_service.service.KycClientService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

// package com.example.griisa_account_service.processor;

@Component
@RequiredArgsConstructor
public class UserItemProcessor implements ItemProcessor<UserRequestDTO, User> {

    private final KycRepository kycRepo;
    private final FailedRecordRepository failedRepo;
    private final KycClientService kycClient;
    private final AccountNumberService accNumberService;

    @Override
    public User process(UserRequestDTO dto) {
        String uniqueKey = dto.getIdDocumentNumber() + "_" + dto.getAadhaarNumber();
        if (kycRepo.findByUniqueValidationKey(uniqueKey).isPresent()) return null;

        User user = new User();
        BeanUtils.copyProperties(dto, user);

        Kyc kyc = new Kyc();
        BeanUtils.copyProperties(dto, kyc);
        kyc.setUniqueValidationKey(uniqueKey);
        kyc.setUser(user);
        user.setKyc(kyc);

        // External KYC
        KycClientService.KycResponse resp = kycClient.validate(kyc);
        kyc.setStatus(resp.status());
        kyc.setVerificationDetails(resp.details());

        if ("FAILED".equalsIgnoreCase(resp.status())) {
            failedRepo.save(new FailedRecord("KYC_FAILED", new Gson().toJson(dto)));
            return user;
        }

        Account acc = new Account();
        acc.setAccountNumber(accNumberService.generate(user, kyc));
        kyc.setAccount(acc);

        return user;
    }
}

