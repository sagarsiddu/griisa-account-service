package com.example.griisa_account_service.repo;

import com.example.griisa_account_service.entity.KycRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<KycRecord, Long> {}
