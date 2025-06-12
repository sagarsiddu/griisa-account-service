package com.example.griisa_account_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank @Size(max = 50)
    private String firstName;

    @NotBlank @Size(max = 50)
    private String lastName;

    @Email @NotBlank @Size(max = 100)
    private String email;

    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;
    private LocalDate dateOfBirth;

    @NotBlank @Size(max = 20)
    private String aadhaarNumber;

    @NotBlank @Size(max = 20)
    private String panNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String idDocumentType;
    private String idDocumentNumber;
}

