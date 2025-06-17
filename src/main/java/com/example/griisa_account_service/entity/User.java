package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName, lastName, email, phoneNumber, dateOfBirth;
    private String addressLine1, addressLine2, city, state, zipCode;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Kyc kyc;

    public void setKyc(Kyc kyc) {
        this.kyc = kyc;
        if (kyc != null) kyc.setUser(this);
    }
}
