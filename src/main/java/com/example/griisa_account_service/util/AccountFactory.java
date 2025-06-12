package com.example.griisa_account_service.util;

import com.example.griisa_account_service.entity.Account;
import com.example.griisa_account_service.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

@Component
public class AccountFactory {
    public Account create(User user) {
        String acctNum = "ACCT" + Year.now().getValue() + String.format("%08d", user.getUserId());
        return Account.builder()
                .user(user)
                .accountNumber(acctNum)
                .status("ACTIVE")
                .openedDate(LocalDate.now())
                .balance(BigDecimal.ZERO)
                .currency("INR")
                .createdAt(LocalDateTime.now())
                .build();
    }
}