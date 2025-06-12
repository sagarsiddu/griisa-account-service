package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class FileProcessingService {

    @Autowired
    private AccountService accountService;

    private static final Logger log = LoggerFactory.getLogger(FileProcessingService.class);

    @Async
    public CompletableFuture<Integer> processFileAsync(MultipartFile file) {
        List<UserRequestDTO> dtos = parseCsv(file);
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

    private List<UserRequestDTO> parseCsv(MultipartFile file) {
        List<UserRequestDTO> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext(); // skip header
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 14) {
                    log.warn("Skipping line due to insufficient columns: {}", Arrays.toString(line));
                    continue;
                }

                try {
                    String email = line[2].trim();
                    String phone = line[3].trim();
                    String aadhaar = line[12].trim();
                    String pan = line[13].trim();

                    // Validations
                    if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
                        throw new IllegalArgumentException("Invalid email format: " + email);
                    }
                    if (!phone.matches("\\d{10}")) {
                        throw new IllegalArgumentException("Invalid phone number: " + phone);
                    }
                    if (!aadhaar.matches("\\d{12}")) {
                        throw new IllegalArgumentException("Invalid Aadhaar number: " + aadhaar);
                    }
                    if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
                        throw new IllegalArgumentException("Invalid PAN number: " + pan);
                    }

                    UserRequestDTO dto = UserRequestDTO.builder()
                            .firstName(line[0].trim())
                            .lastName(line[1].trim())
                            .email(email)
                            .phoneNumber(phone)
                            .dateOfBirth(LocalDate.parse(line[4].trim()))
                            .addressLine1(line[5].trim())
                            .addressLine2(line[6].trim())
                            .city(line[7].trim())
                            .state(line[8].trim())
                            .zipCode(line[9].trim())
                            .idDocumentType(line[10].trim())
                            .idDocumentNumber(line[11].trim())
                            .aadhaarNumber(aadhaar)
                            .panNumber(pan)
                            .build();

                    users.add(dto);
                } catch (Exception e) {
                    log.error("Error parsing line: {}, Error: {}", Arrays.toString(line), e.getMessage(), e);
                }
            }

        } catch (IOException | CsvValidationException e) {
            log.error("CSV parsing failed: {}", e.getMessage(), e);
        }
        return users;
    }

}
