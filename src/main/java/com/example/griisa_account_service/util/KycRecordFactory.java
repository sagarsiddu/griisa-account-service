package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.KycRecord;
import com.example.griisa_account_service.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KycRecordFactory {
    public KycRecord create(User user, UserRequestDTO dto, String status) {
        return KycRecord.builder()
                .user(user)
                .aadhaarNumber(dto.getAadhaarNumber())
                .panNumber(dto.getPanNumber())
                .kycStatus(status)
                .verificationDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}