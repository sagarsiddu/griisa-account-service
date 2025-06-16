package com.example.griisa_account_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {   //AccountReqDTO
    //Add kyc and account and user objects combined
    @NotBlank @Size(max = 50)
    private String firstName;

    @NotBlank @Size(max = 50)
    private String lastName;

    @Email @NotBlank @Size(max = 100)
    private String email;

    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;
    private LocalDate dateOfBirth;

    @NotBlank
    @Pattern(regexp = "\\d{12}")
    private String aadhaarNumber;

    @NotBlank @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]")
    private String panNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String idDocumentType;
    private String idDocumentNumber;
    private String gender;
}

