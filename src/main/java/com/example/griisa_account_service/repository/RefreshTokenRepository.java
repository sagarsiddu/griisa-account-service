package com.example.griisa_account_service.repository;

// src/main/java/com/example/griisa_account_service/repository/RefreshTokenRepository.java
import com.example.griisa_account_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
