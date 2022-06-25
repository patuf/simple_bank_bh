package com.simplebank.accounts.exception;

/**
 * Thrown when a Customer was not found by their id.
 */
public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super("Could not find customer with ID: " + customerId);
    }
}
