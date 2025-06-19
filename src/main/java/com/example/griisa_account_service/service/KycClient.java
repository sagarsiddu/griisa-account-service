package com.example.griisa_account_service.service;

import com.example.griisa_account_service.dto.UserCsvRecordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KycClient {

    private final WebClient webClient;
    private final String kycApiPath;

    public KycClient(WebClient.Builder webClientBuilder,
                     @Value("${kyc.api.path}") String kycApiPath) {
        this.webClient = webClientBuilder.build();
        this.kycApiPath = kycApiPath;
    }

    public boolean validate(UserCsvRecordDto dto) {
        log.info("Validating KYC for user: {}", dto.getEmail());
        try {
            return Boolean.TRUE.equals(webClient.post()
                    .uri(kycApiPath)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            return false;
        }
    }
}