package com.example.griisa_account_service.listener;

import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

@Component
public class BatchJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {}

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Batch finished with status: " + jobExecution.getStatus());
    }
}
