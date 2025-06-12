package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.FailedRecord;
import com.example.griisa_account_service.repo.FailedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FailedRecordService {

    @Autowired
    private FailedRecordRepository failedRecordRepository;

    private long generateId(String rawData, String errorMessage) {
        String combined = rawData + "|" + errorMessage;
        return (long) combined.hashCode() & 0xffffffffL; // Ensures positive long
    }

    public void save(String[] lineData, String errorMessage) {
        log.info("FailedRecordService | save : Saving failed record with error: {}", errorMessage);
        String rawData = String.join(",", lineData);
        long id = generateId(rawData, errorMessage);
        FailedRecord record = FailedRecord.builder()
                .id(id)
                .rawData(rawData)
                .errorMessage(errorMessage)
                .failedAt(LocalDateTime.now())
                .build();

        failedRecordRepository.save(record);
    }

    public void logFailedRecord(String rawLine, String errorMsg) {
        FailedRecord record = FailedRecord.builder()
                .rawData(rawLine)
                .errorMessage(errorMsg)
                .failedAt(LocalDateTime.now())
                .build();
        failedRecordRepository.save(record);
    }
}

