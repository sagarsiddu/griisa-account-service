package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false, unique = true)
    private String accountNumber;

    @Column(length = 20)
    private String accountType;

    private BigDecimal balance;

    @Column(length = 10)
    private String currency;

    @Column(length = 20)
    private String status;

    private LocalDate openedDate;
    private LocalDate closedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
