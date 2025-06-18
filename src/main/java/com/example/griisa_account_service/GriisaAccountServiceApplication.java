package com.example.griisa_account_service;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableBatchProcessing
public class GriisaAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GriisaAccountServiceApplication.class, args);
	}

}
