package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KycService {

    private final KycProvider kycProvider;

    public KycService(KycProvider kycProvider) {
        this.kycProvider = kycProvider;
    }

    public CompletableFuture<String> performKYC(User user, String aadhaar, String pan) {
        return kycProvider.performKYC(user, aadhaar, pan);
    }
}