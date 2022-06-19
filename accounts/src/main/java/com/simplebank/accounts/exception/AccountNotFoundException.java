package com.simplebank.accounts.exception;

import com.simplebank.accounts.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(Long accountId) {
        super("Could not find account with ID: " + accountId);
    }
}
