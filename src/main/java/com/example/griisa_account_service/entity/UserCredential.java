package com.example.griisa_account_service.entity;

// src/main/java/com/example/griisa_account_service/entity/UserCredential.java
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String vaultSecretRef; // Reference to the password in the vault

    @Column(nullable = false)
    private String role; // e.g., ADMIN, USER

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
}
