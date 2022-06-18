package com.simplebank.accounts.customer;

// TODO: Remove this interface and its descendants, if not used
public interface CustomerDataProvider {

    public Customer findById(long customerId);
}
