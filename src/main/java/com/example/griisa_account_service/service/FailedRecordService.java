package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.FailedRecord;
import com.example.griisa_account_service.repo.FailedRecordRepository;
import com.example.griisa_account_service.util.FailedRecordFactory;
import com.example.griisa_account_service.util.FailedRecordIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FailedRecordService {

    private final FailedRecordRepository failedRecordRepository;
    private final FailedRecordIdGenerator idGenerator;
    private final FailedRecordFactory failedRecordFactory;

    @Autowired
    public FailedRecordService(
            FailedRecordRepository failedRecordRepository,
            FailedRecordIdGenerator idGenerator,
            FailedRecordFactory failedRecordFactory
    ) {
        this.failedRecordRepository = failedRecordRepository;
        this.idGenerator = idGenerator;
        this.failedRecordFactory = failedRecordFactory;
    }

    public void save(String[] lineData, String errorMessage) {
        log.info("FailedRecordService | save : Saving failed record with error: {}", errorMessage);
        String rawData = String.join(",", lineData);
        long id = idGenerator.generateId(rawData, errorMessage);
        FailedRecord record = failedRecordFactory.create(id, rawData, errorMessage);
        failedRecordRepository.save(record);
    }

    public void logFailedRecord(String rawLine, String errorMsg) {
        FailedRecord record = failedRecordFactory.create(rawLine, errorMsg);
        failedRecordRepository.save(record);
    }
}