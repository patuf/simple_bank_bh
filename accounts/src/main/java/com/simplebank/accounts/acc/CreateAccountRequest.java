package com.simplebank.accounts.acc;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;

@Validated
public class CreateAccountRequest {

    @NotNull(message = "customerId is mandatory!")
    private Long customerId;
    @NotNull(message = "initialCredit is mandatory!")
    private Double initialCredit;

    public CreateAccountRequest(Long customerId, Double initialCredit) {
        this.customerId = customerId;
        this.initialCredit = initialCredit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Double getInitialCredit() {
        return initialCredit;
    }
}
