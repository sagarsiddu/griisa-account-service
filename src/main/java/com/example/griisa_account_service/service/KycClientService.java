package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.Kyc;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

// package com.example.griisa_account_service.service;

@Service
public class KycClientService {

    private final WebClient client;

    public KycClientService(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8080/kyc").build();
    }

    public KycResponse validate(Kyc kyc) {
        KycRequest req = new KycRequest(kyc.getAadhaarNumber(), kyc.getPanNumber(), kyc.getIdDocumentNumber());
        return client.post()
                .uri("/validate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(KycResponse.class)
                .block();
    }

    public record KycRequest(String aadhaarNumber, String panNumber, String idDocumentNumber) {}
    public record KycResponse(String status, String details) {}
}

