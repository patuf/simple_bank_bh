package com.simplebank.accounts.customer;

import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class LocalRepoCustomerDataProvider implements CustomerDataProvider {

    @Autowired
    private CustomerRepository customerRepo;

    public LocalRepoCustomerDataProvider(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Optional<Customer> findById(long customerId) {
        return customerRepo.findById(customerId);
    }
}
