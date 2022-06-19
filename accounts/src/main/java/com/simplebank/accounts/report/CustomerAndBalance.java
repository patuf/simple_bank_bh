package com.simplebank.accounts.report;

import com.simplebank.accounts.customer.Customer;

public class CustomerAndBalance extends Customer {
    int numAccounts;
    double balance;

    public int getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(int numAccounts) {
        this.numAccounts = numAccounts;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
