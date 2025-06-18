package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50, nullable = false)
    private String firstName;

    @Column(length = 50, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false)
    private String phoneNumber;

    private String dateOfBirth;

    @Column(length = 100)
    private String addressLine1;

    @Column(length = 100)
    private String addressLine2;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 30)
    private String idDocumentType;

    @Column(length = 50)
    private String idDocumentNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
