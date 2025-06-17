package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.Kyc;
import com.example.griisa_account_service.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AccountNumberService {
    public String generate(User user, Kyc kyc) {
        String seed = user.getEmail().hashCode() + "-"
                + kyc.getAadhaarNumber().substring(0,4)
                + System.currentTimeMillis() % 10000;
        return "ACCT" + Math.abs(seed.hashCode());
    }
}

