package com.example.griisa_account_service.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class VaultService {
    private final ConcurrentHashMap<String, String> vault = new ConcurrentHashMap<>();

    public void storeSecret(String ref, String secret) {
        vault.put(ref, secret);
        System.out.println("VaultService | storeSecret : vault details: " + vault);
    }

    public String getSecret(String ref) {
        String secret = vault.get(ref);
        System.out.println("VaultService | getSecret : Retrieving secret for ref: " + ref+", secret: " + secret);
        if (secret == null) {
            throw new IllegalArgumentException("Secret not found for ref: " + ref);
        }
        return secret;
    }
}
