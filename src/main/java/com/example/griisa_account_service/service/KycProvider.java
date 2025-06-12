package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.User;

import java.util.concurrent.CompletableFuture;

public interface KycProvider {
    CompletableFuture<String> performKYC(User user, String aadhaar, String pan);
}