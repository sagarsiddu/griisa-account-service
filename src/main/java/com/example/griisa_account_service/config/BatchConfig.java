package com.example.griisa_account_service.config;

import com.example.griisa_account_service.dto.UserRequestDTO;
import com.example.griisa_account_service.entity.User;
import com.example.griisa_account_service.processor.UserItemProcessor;
import com.example.griisa_account_service.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.context.annotation.*;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final DataSource dataSource;
    private final EntityManagerFactory emf;
    private final UserRepository userRepository;
    private final UserItemProcessor userItemProcessor;

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<UserRequestDTO> reader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        return new FlatFileItemReaderBuilder<UserRequestDTO>()
                .name("userItemReader")
                .resource(new FileSystemResource(inputFile))
                .linesToSkip(1)
                .delimited()
                .names("firstName", "lastName", "email", "phoneNumber", "dateOfBirth", "addressLine1",
                        "addressLine2", "city", "state", "zipCode", "idDocumentType", "idDocumentNumber",
                        "aadhaarNumber", "panNumber")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(UserRequestDTO.class);
                }})
                .build();
    }

    @Bean
    public CompositeItemProcessor<UserRequestDTO, User> processor() {
        return new CompositeItemProcessorBuilder<UserRequestDTO, User>()
                .delegates(List.of(userItemProcessor))
                .build();
    }

    @Bean
    public ItemWriter<User> writer() {
        return new RepositoryItemWriterBuilder<User>()
                .repository(userRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("user-step", jobRepository)
                .<UserRequestDTO, User>chunk(500, transactionManager(emf))
                .reader(reader(null))  // Spring will inject the correct value from job parameters at runtime
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("user-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
