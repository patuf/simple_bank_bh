package com.simplebank.accounts.acc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long customerId;
    @NotNull
    private ZonedDateTime timeCreated;
    @NotNull
    private AccountStatus accountStatus;

    public Account(Long customerId, ZonedDateTime timeCreated, AccountStatus accountStatus) {
        this.customerId = customerId;
        this.timeCreated = timeCreated;
        this.accountStatus = accountStatus;
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

    public ZonedDateTime getTimeCreated() {
        return timeCreated;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public enum AccountStatus {
        ACTIVE,
        CLOSED
    }
}
