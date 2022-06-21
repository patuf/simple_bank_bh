package com.simplebank.transactions.trans;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {@Index(name= "accountId", columnList = "accountId"), @Index(name = "customerId", columnList = "customerId")})
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long customerId;
    @NotNull
    private Long accountId;
    @NotNull
    private Double amount;
    @NotNull
    private LocalDateTime timeCreated;

    public BankTransaction() {
    }

    /**
     * A shorter version of the entity, used for reporting
     * @param amount The amount of the transaction
     * @param timeCreated The time of creation of the transaction
     */
    public BankTransaction(Double amount, LocalDateTime timeCreated) {
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
}
