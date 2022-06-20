package com.simplebank.accounts.report;

import com.simplebank.accounts.customer.Customer;

public class CustomerAndBalance extends Customer implements ContainsBalance {
    Integer numAccounts;
    Double balance;

    public Integer getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(Integer numAccounts) {
        this.numAccounts = numAccounts;
    }

    @Override
    public Double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
