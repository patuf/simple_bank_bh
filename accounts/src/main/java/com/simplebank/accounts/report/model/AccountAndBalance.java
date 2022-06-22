package com.simplebank.accounts.report.model;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.AccountStatus;

import java.time.LocalDateTime;

public class AccountAndBalance extends Account implements ContainsBalance {
    private Double balance;

    public AccountAndBalance() {
    }

    public AccountAndBalance(Long accountId, Long customerId, LocalDateTime timeCreated, AccountStatus accountStatus, Double balance) {
        super(customerId, timeCreated, accountStatus);
        setAccountId(accountId);
        this.balance = balance;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
