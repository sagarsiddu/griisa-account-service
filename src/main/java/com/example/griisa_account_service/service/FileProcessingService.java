package com.example.griisa_account_service.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class FileProcessingService {

    private final JobLauncher jobLauncher;
    private final Job importJob;

    public FileProcessingService(JobLauncher jobLauncher, Job importJob) {
        this.jobLauncher = jobLauncher;
        this.importJob = importJob;
    }

    public void startBatch(Path csvFile) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", csvFile.toString())
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importJob, params);
    }
}
