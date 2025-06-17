package com.example.griisa_account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_records")
@Data @NoArgsConstructor
@AllArgsConstructor
public class FailedRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime failedAt = LocalDateTime.now();

    public FailedRecord(String reason, String payload) {
        this.reason = reason;
        this.payload = payload;
    }
}
