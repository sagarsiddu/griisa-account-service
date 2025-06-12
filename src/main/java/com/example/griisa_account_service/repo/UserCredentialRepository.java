// src/main/java/com/example/griisa_account_service/repository/UserCredentialRepository.java
package com.example.griisa_account_service.repo;

import com.example.griisa_account_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByUsername(String username);
}