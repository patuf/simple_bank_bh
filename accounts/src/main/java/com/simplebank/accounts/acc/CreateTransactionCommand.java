package com.simplebank.accounts.acc;

public class CreateTransactionCommand {

    long customerId;
    long accId;
    double amount;

    public CreateTransactionCommand(long customerId, long accId, double amount) {
        this.customerId = customerId;
        this.accId = accId;
        this.amount = amount;
    }

    public long getCustomerId() {
        return customerId;
    }

    public long getAccId() {
        return accId;
    }

    public double getAmount() {
        return amount;
    }
}
