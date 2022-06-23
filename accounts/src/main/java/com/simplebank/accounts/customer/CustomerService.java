package com.simplebank.accounts.customer;

import java.util.Optional;

public interface CustomerService {

    Optional<Customer> findById(long customerId);
}
