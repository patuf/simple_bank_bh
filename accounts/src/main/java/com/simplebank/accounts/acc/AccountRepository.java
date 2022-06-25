package com.simplebank.accounts.acc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A JPA repository for the Account table/class
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
