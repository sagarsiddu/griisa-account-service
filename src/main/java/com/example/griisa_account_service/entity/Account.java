package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data @NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;

    @OneToOne
    @JoinColumn(name = "kyc_id", nullable = false)
    private Kyc kyc;

}
