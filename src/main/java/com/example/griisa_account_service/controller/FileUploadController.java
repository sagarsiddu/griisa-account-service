package com.example.griisa_account_service.controller;

import com.example.griisa_account_service.service.FileProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final FileProcessingService service;

    public FileUploadController(FileProcessingService service) { this.service = service; }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            Path path = Files.createTempFile("input", ".csv");
            file.transferTo(path);
            service.startBatch(path);
            return ResponseEntity.accepted().body("Batch job started.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        log.info("Health check endpoint hit");
        return "Service is running";
    }
}

