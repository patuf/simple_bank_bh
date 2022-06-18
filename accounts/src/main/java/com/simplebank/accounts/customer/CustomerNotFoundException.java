package com.simplebank.accounts.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("Could not find customer with ID: " + customerId);
    }
}
