package com.simplebank.accounts.exception;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super("Could not find customer with ID: " + customerId);
    }
}
