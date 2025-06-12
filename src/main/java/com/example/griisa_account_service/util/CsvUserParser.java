package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvUserParser {

    private static final Logger log = LoggerFactory.getLogger(CsvUserParser.class);
    private final UserRequestValidator userRequestValidator;

    public CsvUserParser(UserRequestValidator userRequestValidator) {
        this.userRequestValidator = userRequestValidator;
    }

    public List<UserRequestDTO> parse(MultipartFile file) {
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
                    UserRequestDTO dto = UserRequestDTO.builder()
                            .firstName(line[0].trim())
                            .lastName(line[1].trim())
                            .email(line[2].trim())
                            .phoneNumber(line[3].trim())
                            .dateOfBirth(LocalDate.parse(line[4].trim()))
                            .addressLine1(line[5].trim())
                            .addressLine2(line[6].trim())
                            .city(line[7].trim())
                            .state(line[8].trim())
                            .zipCode(line[9].trim())
                            .idDocumentType(line[10].trim())
                            .idDocumentNumber(line[11].trim())
                            .aadhaarNumber(line[12].trim())
                            .panNumber(line[13].trim())
                            .build();

                    userRequestValidator.validate(dto);
                    users.add(dto);
                } catch (ConstraintViolationException e) {
                    log.warn("Validation failed for line: {} | Violations: {}", Arrays.toString(line), e.getConstraintViolations());
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
