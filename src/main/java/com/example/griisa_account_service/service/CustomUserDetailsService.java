// src/main/java/com/example/griisa_account_service/service/CustomUserDetailsService.java
package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.UserCredential;
import com.example.griisa_account_service.repo.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserCredentialRepository credentialRepository;
    private final VaultService vaultService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential credential = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String password = vaultService.getSecret(credential.getVaultSecretRef());
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .roles(credential.getRole())
                .build();
    }
}