package com.example.griisa_account_service.dto;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String firstName, lastName, email, phoneNumber, dateOfBirth,
            addressLine1, addressLine2, city, state, zipCode,
            idDocumentType, idDocumentNumber,
            aadhaarNumber, panNumber;
}
