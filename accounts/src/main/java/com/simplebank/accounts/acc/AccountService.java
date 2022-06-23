package com.simplebank.accounts.acc;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface AccountService {
    Account findOne(Long accountId);

    @Transactional
    Account createAccount(Long customerId, Double initialCredit, LocalDateTime createdAt);
}
