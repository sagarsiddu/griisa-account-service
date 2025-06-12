package com.example.griisa_account_service.controller;

import com.example.griisa_account_service.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired private FileProcessingService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        CompletableFuture<Integer> records = fileService.processFileAsync(file);
        return ResponseEntity.accepted().body(records.toString() + "records processed from " + file.getOriginalFilename());
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Account Service is up and running");
    }
}
