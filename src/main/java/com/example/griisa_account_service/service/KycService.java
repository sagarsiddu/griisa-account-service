package com.example.griisa_account_service.service;

import com.example.griisa_account_service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KycService {
    @Autowired
    private WebClient webClient;

    public CompletableFuture<String> performKYC(User user, String aadhaar, String pan) {
        return webClient.post()
                .uri("http://localhost:8080/api/verify")
                .bodyValue(Map.of("aadhaar", aadhaar, "pan", pan))
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }
}
