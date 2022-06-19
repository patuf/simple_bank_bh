package com.simplebank.accounts.acc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionOutboxRepository extends JpaRepository<CreateTransactionOutbox, Long> {
}
