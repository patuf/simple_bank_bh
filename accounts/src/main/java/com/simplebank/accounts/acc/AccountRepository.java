package com.simplebank.accounts.acc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> getAccountsByCustomerId(long customerId, Pageable pageable);
}
