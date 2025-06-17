package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kyc",
        indexes = {@Index(name="idx_unique_key", columnList = "uniqueValidationKey", unique = true)})
@Data @NoArgsConstructor
@AllArgsConstructor
public class Kyc {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idDocumentType, idDocumentNumber, aadhaarNumber, panNumber;
    private String status;
    @Column(columnDefinition = "TEXT") private String verificationDetails;

    @Column(nullable = false, unique = true)
    private String uniqueValidationKey;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "kyc", cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    public void setAccount(Account account) {
        this.account = account;
        if (account != null) {
            account.setKyc(this);
        }
    }
}
