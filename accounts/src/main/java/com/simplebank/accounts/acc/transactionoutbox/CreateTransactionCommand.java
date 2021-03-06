package com.simplebank.accounts.acc.transactionoutbox;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The entity used to issue a "CreateTransaction" asynchronous command
 */
@Entity
public class CreateTransactionCommand {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long customerId;
    @NotNull
    private Long accountId;
    @NotNull
    private Double amount;
    @NotNull
    private LocalDateTime timeCreated;

    public CreateTransactionCommand() {
    }

    public CreateTransactionCommand(Long customerId, Long accountId, Double amount, LocalDateTime timeCreated) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.amount = amount;
        this.timeCreated = timeCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTransactionCommand that = (CreateTransactionCommand) o;
        return customerId.equals(that.customerId) && accountId.equals(that.accountId) && timeCreated.equals(that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, accountId, timeCreated);
    }
}
