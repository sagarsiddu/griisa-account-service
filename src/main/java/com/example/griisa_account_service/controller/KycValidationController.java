package com.example.griisa_account_service.controller;

import com.example.griisa_account_service.dto.UserCsvRecordDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kyc")
public class KycValidationController {

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody UserCsvRecordDto dto) {
        boolean valid = isValidAadhaar(dto.getAadhaarNumber())
                && isValidPan(dto.getPanNumber())
                && dto.getIdDocumentType() != null
                && !dto.getIdDocumentType().isBlank()
                && dto.getIdDocumentNumber() != null
                && dto.getIdDocumentNumber().length() >= 5;

        return ResponseEntity.ok(valid);
    }

    private boolean isValidAadhaar(String aadhaar) {
        return aadhaar != null && aadhaar.matches("\\d{12}");
    }

    private boolean isValidPan(String pan) {
        return pan != null && pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
    }
}

