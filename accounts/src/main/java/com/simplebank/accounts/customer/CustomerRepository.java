package com.simplebank.accounts.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A JPA repository for the Customer table/class
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
