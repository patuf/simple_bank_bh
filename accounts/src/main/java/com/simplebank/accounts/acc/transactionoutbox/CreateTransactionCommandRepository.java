package com.simplebank.accounts.acc.transactionoutbox;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The TransactionOutbox repository, used to store "CreateTransaction" asynchronous commands until they are picked by the Polling Publisher
 */
public interface CreateTransactionCommandRepository extends JpaRepository<CreateTransactionCommand, Long> {
}
