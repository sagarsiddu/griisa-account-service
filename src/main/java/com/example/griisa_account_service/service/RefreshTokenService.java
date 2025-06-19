package com.example.griisa_account_service.service;

// src/main/java/com/example/griisa_account_service/service/RefreshTokenService.java

import com.example.griisa_account_service.entity.RefreshToken;
import com.example.griisa_account_service.entity.UserCredential;
import com.example.griisa_account_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(UserCredential userCredential) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userCredential(userCredential)
                .expiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7)) // 7 days
                .build();
        return refreshTokenRepository.save(token);
    }

    // src/main/java/com/example/griisa_account_service/service/RefreshTokenService.java
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
