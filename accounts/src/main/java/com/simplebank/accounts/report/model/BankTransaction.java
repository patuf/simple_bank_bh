package com.simplebank.accounts.report.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

public class BankTransaction {

    @NotNull
    private Double amount;
    @NotNull
    private LocalDateTime timeCreated;

    public BankTransaction() {
    }

    /**
     * For testing purposes
     */
    public BankTransaction(Double amount, LocalDateTime timeCreated) {
        this.amount = amount;
        this.timeCreated = timeCreated;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}
