package com.simplebank.accounts.exception;

import com.simplebank.accounts.exception.ResourceNotFoundException;

/**
 * Thrown when an Account was not found by its id.
 */
public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(Long accountId) {
        super("Could not find account with ID: " + accountId);
    }
}
