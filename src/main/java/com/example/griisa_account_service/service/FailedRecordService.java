package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.FailedRecord;
import com.example.griisa_account_service.repo.FailedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FailedRecordService {

    @Autowired
    private FailedRecordRepository failedRecordRepository;

    public void save(String[] lineData, String errorMessage) {
        FailedRecord record = FailedRecord.builder()
                .rawData(String.join(",", lineData))
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

