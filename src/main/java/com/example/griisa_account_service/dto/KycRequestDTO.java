// KycRequestDTO.java
package com.example.griisa_account_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KycRequestDTO {
    @NotBlank
    private String aadhaar;
    @NotBlank
    private String pan;

    // getters and setters
}