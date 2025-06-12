// src/main/java/com/example/griisa_account_service/service/UserCredentialService.java
package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.entity.UserCredential;
import com.example.griisa_account_service.repo.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCredentialService {
    private final UserCredentialRepository credentialRepository;
    private final VaultService vaultService;

    public UserCredential createCredentialForUser(User user, String role) {
        String username = generateUsername(user);
        String password = generateRandomPassword();
        String vaultRef = "user-password-" + UUID.randomUUID();

        vaultService.storeSecret(vaultRef, password);

        UserCredential credential = UserCredential.builder()
                .username(username)
                .vaultSecretRef(vaultRef)
                .role(role)
                .user(user)
                .build();

        return credentialRepository.save(credential);
    }

    private String generateUsername(User user) {
        return user.getFirstName().toLowerCase() + "." + user.getLastName().toLowerCase() + user.getUserId();
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }
}