// KycResponseDTO.java
package com.example.griisa_account_service.dto;

public class KycResponseDTO {
    private String status;
    private String message;

    public KycResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // getters and setters
}