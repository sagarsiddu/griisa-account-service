// src/main/java/com/example/griisa_account_service/security/JwtUtil.java
package com.example.griisa_account_service.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Lazy
@Profile("!test")
public class JwtUtil {
    @Value("${jwt.rsa.private-key}")
    private Resource privateKeyResource;
    @Value("${jwt.rsa.public-key}")
    private Resource publicKeyResource;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final long jwtExpirationMs = 5 * 60 * 1000; // 5 minutes
    private final String issuer = "griisa-account-service";
    private final String audience = "griisa-api-users";

    @PostConstruct
    public void loadKeys() throws Exception {
        // Load private key
        try (InputStream is = privateKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN [A-Z ]+-----", "")
                    .replaceAll("-----END [A-Z ]+-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        }
        // Load public key
        try (InputStream is = publicKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN [A-Z ]+-----", "")
                    .replaceAll("-----END [A-Z ]+-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        }
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setAudience(audience)
                .setIssuer(issuer)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireAudience(audience)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireAudience(audience)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token);
            // Optionally check jti, exp, etc.
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}