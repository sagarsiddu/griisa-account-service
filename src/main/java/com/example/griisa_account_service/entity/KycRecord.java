package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idDocumentType;
    private String idDocumentNumber;
    private String aadhaarNumber;
    private String panNumber;
    @Column(name = "unique_validation_key", unique = true)
    private String uniqueValidationKey;

    @OneToOne(mappedBy = "kyc")
    private User user;
}