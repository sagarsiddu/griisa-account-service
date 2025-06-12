package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserFactory {
    public User create(UserRequestDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .idDocumentType(dto.getIdDocumentType())
                .idDocumentNumber(dto.getIdDocumentNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }
}