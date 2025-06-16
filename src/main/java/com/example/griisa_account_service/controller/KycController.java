package com.example.griisa_account_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class KycController {

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> payload) {
        String aadhaar = payload.get("aadhaar");
        String pan = payload.get("pan");
        log.info("Received KYC verification request with Aadhaar: {}, PAN: {}", aadhaar, pan);

        if (aadhaar != null && aadhaar.startsWith("9")) {
            return ResponseEntity.ok("FAILURE");
        } else if (pan != null && pan.startsWith("Z")) {
            return ResponseEntity.ok("FAILURE");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
