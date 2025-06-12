package com.example.griisa_account_service.util;

import com.example.griisa_account_service.entity.FailedRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FailedRecordFactory {
    public FailedRecord create(long id, String rawData, String errorMessage) {
        return FailedRecord.builder()
                .id(id)
                .rawData(rawData)
                .errorMessage(errorMessage)
                .failedAt(LocalDateTime.now())
                .build();
    }

    public FailedRecord create(String rawData, String errorMessage) {
        return FailedRecord.builder()
                .rawData(rawData)
                .errorMessage(errorMessage)
                .failedAt(LocalDateTime.now())
                .build();
    }
}