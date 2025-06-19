package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor // Removed to avoid duplicate constructor
@Builder
public class FailedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(length = 10000, columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 100)
    private String errorCode;

    @Column(columnDefinition = "TEXT")
    private String errorType;

    public FailedRecord(Long id, String reason, String errorCode, String errorType, String payload) {
        this.id = id;
        this.reason = reason;
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.payload = payload;
    }
}