package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;

import java.util.List;

public class Balance {

    private Long accountId;
    private Double balance;

    public Balance() {
    }

    public Balance(Long accountId, Double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
