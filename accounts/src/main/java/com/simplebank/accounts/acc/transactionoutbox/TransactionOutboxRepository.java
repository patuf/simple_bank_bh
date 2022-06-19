package com.simplebank.accounts.acc.transactionoutbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionOutboxRepository extends JpaRepository<CreateTransactionCommandOutbox, Long> {
}
