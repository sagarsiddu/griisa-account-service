package com.example.griisa_account_service.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kyc")
public class KycController {

    @PostMapping("/validate")
    public ResponseEntity<KycResponse> validate(@RequestBody KycRequest req) {
        boolean ok = req.aadhaarNumber().matches("\\d{12}") &&
                req.panNumber().matches("[A-Z]{5}\\d{4}[A-Z]");
        return ResponseEntity.ok(new KycResponse(ok ? "PASSED" : "FAILED", ok ? "KYC OK" : "Invalid"));
    }

    public static record KycRequest(String aadhaarNumber, String panNumber, String idDocumentNumber) {}
    public static record KycResponse(String status, String details) {}
}

