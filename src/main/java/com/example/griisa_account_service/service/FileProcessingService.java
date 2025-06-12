package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.util.CsvUserParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FileProcessingService {

    private final AccountService accountService;
    private final CsvUserParser csvUserParser;
    private static final Logger log = LoggerFactory.getLogger(FileProcessingService.class);

    @Autowired
    public FileProcessingService(AccountService accountService, CsvUserParser csvUserParser) {
        this.accountService = accountService;
        this.csvUserParser = csvUserParser;
    }

    @Async
    public CompletableFuture<Integer> processFileAsync(MultipartFile file) {
        List<UserRequestDTO> dtos = csvUserParser.parse(file);
        log.info("FileProcessingService | processFileAsync: Parsed {} records from file", dtos.size());
        for (int i = 0; i < dtos.size(); i += 500) {
            List<UserRequestDTO> batch = dtos.subList(i, Math.min(i + 500, dtos.size()));
            try {
                accountService.processBatch(batch);
            } catch (Exception e) {
                log.error("Batch processing failed at batch {}: {}", i / 500, e.getMessage(), e);
            }
        }
        return CompletableFuture.completedFuture(dtos.size());
    }
}