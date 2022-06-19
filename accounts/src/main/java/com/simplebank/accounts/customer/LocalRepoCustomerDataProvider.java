package com.simplebank.accounts.customer;

import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

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
