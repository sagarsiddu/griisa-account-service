package com.example.griisa_account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class KycController {

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> payload) {
        String aadhaar = payload.get("aadhaar");
        String pan = payload.get("pan");

        if (aadhaar != null && aadhaar.startsWith("9")) {
            return ResponseEntity.ok("FAILURE");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
