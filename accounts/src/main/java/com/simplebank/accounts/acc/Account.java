package com.simplebank.accounts.acc;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(indexes = @Index(name = "customerId", columnList = "customerId"))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    @NotNull
    private Long customerId;
    @NotNull
    private LocalDateTime timeCreated;
    @NotNull
    private AccountStatus accountStatus;

    public Account() {
    }

    public Account(Long customerId, LocalDateTime timeCreated, AccountStatus accountStatus) {
        this.customerId = customerId;
        this.timeCreated = timeCreated;
        this.accountStatus = accountStatus;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long id) {
        this.accountId = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = AccountStatus.values()[accountStatus];
    }

}
