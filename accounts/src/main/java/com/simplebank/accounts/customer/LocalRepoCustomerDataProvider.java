package com.simplebank.accounts.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class LocalRepoCustomerDataProvider implements CustomerDataProvider {

    @Autowired
    private CustomerRepository customerRepo;

    public LocalRepoCustomerDataProvider(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Customer findById(long customerId) {
        return customerRepo.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
