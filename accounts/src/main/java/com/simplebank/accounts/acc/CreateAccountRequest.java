package com.simplebank.accounts.acc;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * This data class maps the createAccount command payload that comes from the REST controller.
 */
@Validated
public class CreateAccountRequest {

    @NotNull(message = "customerId is mandatory!")
    private Long customerId;
    @NotNull(message = "initialCredit is mandatory!")
    private Double initialCredit;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(Long customerId, Double initialCredit) {
        this.customerId = customerId;
        this.initialCredit = initialCredit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getInitialCredit() {
        return initialCredit;
    }

    public void setInitialCredit(Double initialCredit) {
        this.initialCredit = initialCredit;
    }


}
