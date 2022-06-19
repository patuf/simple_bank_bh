package com.simplebank.accounts.customer;

import com.simplebank.accounts.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super("Could not find customer with ID: " + customerId);
    }
}
