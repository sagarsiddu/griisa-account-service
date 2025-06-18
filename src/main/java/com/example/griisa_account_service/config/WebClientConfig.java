package com.example.griisa_account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient kycWebClient() {
        return WebClient.builder().baseUrl("http://localhost:8081") // Or config-driven
                .build();
    }
}

