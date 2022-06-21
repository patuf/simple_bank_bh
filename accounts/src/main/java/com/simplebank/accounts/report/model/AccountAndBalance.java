package com.simplebank.accounts.report.model;

import com.simplebank.accounts.acc.Account;

public class AccountAndBalance extends Account implements ContainsBalance {
    private Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
