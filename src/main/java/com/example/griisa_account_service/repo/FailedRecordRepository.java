package com.example.griisa_account_service.repo;

import com.example.griisa_account_service.entity.FailedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedRecordRepository extends JpaRepository<FailedRecord, Long> {
}

