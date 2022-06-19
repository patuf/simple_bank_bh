package com.simplebank.accounts.acc.transactionoutbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CreateTransactionCommandRepository extends JpaRepository<CreateTransactionCommand, Long> {
}
