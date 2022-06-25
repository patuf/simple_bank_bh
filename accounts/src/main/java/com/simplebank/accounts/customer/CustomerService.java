package com.simplebank.accounts.customer;

import java.util.Optional;

/**
 * A service providing the necessary commands to manage the CRUD lifecycle of Accounts.
 */
public interface CustomerService {

    Customer findById(long customerId);
}
