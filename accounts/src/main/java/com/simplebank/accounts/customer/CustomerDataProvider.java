package com.simplebank.accounts.customer;

import java.util.Optional;

public interface CustomerDataProvider {

    public Optional<Customer> findById(long customerId);
}
