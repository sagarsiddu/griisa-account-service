package com.example.griisa_account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadExecutorConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        return runnable -> Executors.newVirtualThreadPerTaskExecutor().submit(runnable);
    }
}


