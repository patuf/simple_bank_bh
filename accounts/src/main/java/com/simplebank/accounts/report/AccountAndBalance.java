package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.Account;

public class AccountAndBalance extends Account {
    private Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
