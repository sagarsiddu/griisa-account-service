package com.example.griisa_account_service.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class AccountNumberGenerator {

    public String generate(String firstName, String lastName, String phoneNumber) {
        String input = firstName + lastName + phoneNumber;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert first 10 digits of hashed bytes to numeric account number
            StringBuilder accountNumber = new StringBuilder("91"); // like country or bank code prefix

            for (int i = 0; i < 8; i++) {
                accountNumber.append(Math.abs(hash[i]) % 10); // only digits 0-9
            }

            return accountNumber.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating account number", e);
        }
    }
}

