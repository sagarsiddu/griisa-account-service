package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_records")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kycId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String aadhaarNumber;

    @Column(length = 20, nullable = false)
    private String panNumber;

    @Column(length = 20)
    private String kycStatus;

    private String kycVerificationDetails;
    private LocalDateTime verificationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
