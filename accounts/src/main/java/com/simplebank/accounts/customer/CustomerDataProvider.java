package com.simplebank.accounts.customer;

public interface CustomerDataProvider {

    public Customer findById(long customerId);
}
