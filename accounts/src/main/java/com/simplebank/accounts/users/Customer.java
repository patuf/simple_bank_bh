package com.simplebank.accounts.users;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
//@Table(schema="accounts")
@Table
public class Customer {
    @Id
    private Long customerId;
    @NotNull
    private String name;
    @NotNull
    private String surname;

    public Customer(Long customerId, String name, String surname) {
        this.customerId = customerId;
        this.name = name;
        this.surname = surname;
    }
}
