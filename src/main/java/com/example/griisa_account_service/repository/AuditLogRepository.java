package com.example.griisa_account_service.repository;

import com.example.griisa_account_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<User, Long> {
    // Since AuditLog is @MappedSuperclass, it can’t be a direct JPA repository target.
    // So we interact with its concrete classes like User.
}