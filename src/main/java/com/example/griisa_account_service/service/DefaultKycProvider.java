package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DefaultKycProvider implements KycProvider {

    private final WebClient webClient;
    private final String kycEndpoint;

    public DefaultKycProvider(WebClient webClient, @Value("${kyc.endpoint.uri}") String kycEndpoint) {
        this.webClient = webClient;
        this.kycEndpoint = kycEndpoint;
    }

    @Override
    public CompletableFuture<String> performKYC(User user, String aadhaar, String pan) {
        return webClient.post()
                .uri(kycEndpoint)
                .bodyValue(Map.of("aadhaar", aadhaar, "pan", pan))
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }
}