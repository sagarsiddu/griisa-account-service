package com.example.griisa_account_service.util;

import org.springframework.stereotype.Component;

@Component
public class FailedRecordIdGenerator {
    public long generateId(String rawData, String errorMessage) {
        String combined = rawData + "|" + errorMessage;
        return (long) combined.hashCode() & 0xffffffffL;
    }
}