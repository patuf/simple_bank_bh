package com.simplebank.accounts.report.model;

/**
 * A marker interface showing that a data class contains balance.
 * Provides getter and setter methods for the "balance" field.
 */
public interface ContainsBalance {

    Double getBalance();
    void setBalance(Double balance);
}
