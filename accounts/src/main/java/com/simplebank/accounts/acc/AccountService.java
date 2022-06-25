package com.simplebank.accounts.acc;

import com.simplebank.accounts.exception.AccountNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * A service providing the necessary commands to manage the CRUD lifecycle of Accounts.
 */
public interface AccountService {

    /**
     /**
     * Attempts to find an Account in the database, by its is id.
     * @param accountId
     * @throws AccountNotFoundException if an Account with the given id doesn't exist in the database.
     * @return the Account, if found
     */
    Account findOne(Long accountId);

    /**
     * Creates a new account with the given parameters. Implementation should first check if the customerId exists,
     * If initialCredit is not 0, then a bank transaction must be logged for this account, with the same creation time
     * as the account itself.
     * @param customerId the id of the customer owning hte account
     * @param initialCredit the initial credit on that account. If != 0, then a bank transaction must be logged for this account.
     * @param createdAt The time of creation of this account. If a transaction is logged, this is the timestamp of the transaction too
     * @throws AccountNotFoundException if an Account with the given id doesn't exist in the database.
     * @return The created Account
     */
    Account createAccount(Long customerId, Double initialCredit, LocalDateTime createdAt);
}
