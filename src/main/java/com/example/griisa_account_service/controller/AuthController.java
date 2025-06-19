package com.example.griisa_account_service.controller;

// src/main/java/com/example/griisa_account_service/controller/AuthController.java
import com.example.griisa_account_service.entity.UserCredential;
import com.example.griisa_account_service.entity.RefreshToken;
import com.example.griisa_account_service.repository.UserCredentialRepository;
import com.example.griisa_account_service.security.JwtUtil;
import com.example.griisa_account_service.service.RefreshTokenService;
import com.example.griisa_account_service.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserCredentialRepository credentialRepository;
    private final VaultService vaultService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        return credentialRepository.findByUsername(username)
                .map(cred -> {
                    String storedPassword = vaultService.getSecret(cred.getVaultSecretRef());
                    if (storedPassword.equals(password)) {
                        String token = jwtUtil.generateToken(username);
                        RefreshToken refreshToken = refreshTokenService.createRefreshToken(cred);
                        return ResponseEntity.ok(new AuthResponse(token, refreshToken.getToken()));
                    }
                    return ResponseEntity.status(401).body("Invalid credentials");
                })
                .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }

    // src/main/java/com/example/griisa_account_service/controller/AuthController.java
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .filter(token -> token.getExpiryDate().isAfter(java.time.Instant.now()))
                .map(token -> {
                    String username = token.getUserCredential().getUsername();
                    String newAccessToken = jwtUtil.generateToken(username);
                    return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
                })
                .orElse(ResponseEntity.status(401).body(null));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam(defaultValue = "USER") String role) {
        if (credentialRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        String vaultRef = "user-" + username;
        vaultService.storeSecret(vaultRef, password);
        UserCredential credential = UserCredential.builder()
                .username(username)
                .vaultSecretRef(vaultRef)
                .role(role)
                .build();
        credentialRepository.save(credential);
        return ResponseEntity.ok("User registered successfully");
    }

    // DTO for response
    public record AuthResponse(String accessToken, String refreshToken) {}
}
