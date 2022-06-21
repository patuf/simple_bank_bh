package com.simplebank.accounts.report.model;

/**
 * A temporary data class, used to map the balance-related responses from the Transaction service
 */
public class Balance {

    private Long id;
    private Double balance;

    public Balance() {
    }

    public Balance(Long id, Double balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
