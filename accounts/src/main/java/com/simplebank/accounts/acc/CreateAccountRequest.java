package com.simplebank.accounts.acc;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;

@Validated
public class CreateAccountRequest {

    @NotNull
    private long customerId;
    private double initialCredit;

    public CreateAccountRequest(long customerId, double initialCredit) {
        this.customerId = customerId;
        this.initialCredit = initialCredit;
    }

    public long getCustomerId() {
        return customerId;
    }

    public double getInitialCredit() {
        return initialCredit;
    }
}
