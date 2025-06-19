package com.example.griisa_account_service.controller;

import com.example.griisa_account_service.service.CsvProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/csv")
public class CsvUploadController {

    private final CsvProcessingService csvProcessingService;

    @Autowired
    public CsvUploadController(CsvProcessingService csvProcessingService) {
        this.csvProcessingService = csvProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only CSV files are allowed");
        }

        try {
            csvProcessingService.processCsv(file);
            return ResponseEntity.ok("File processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        // This endpoint can be used to check the status of the CSV processing
        // For now, we return a simple message
        return ResponseEntity.ok("CSV processing service is running");
    }
}
