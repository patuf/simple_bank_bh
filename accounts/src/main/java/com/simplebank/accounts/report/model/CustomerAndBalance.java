package com.simplebank.accounts.report.model;

import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.report.model.ContainsBalance;

public class CustomerAndBalance extends Customer implements ContainsBalance {
    private Integer numAccounts;
    private Double balance;

    public CustomerAndBalance() {
    }

    public CustomerAndBalance(Long customerId, String name, String surname, Integer numAccounts, Double balance) {
        super(customerId, name, surname);
        this.numAccounts = numAccounts;
        this.balance = balance;
    }

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
