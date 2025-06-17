package com.example.griisa_account_service.repository;

import com.example.griisa_account_service.entity.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Long> {
    Optional<Kyc> findByUniqueValidationKey(String key);
}
