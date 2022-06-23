package com.simplebank.accounts.customer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LocalRepoCustomerService implements CustomerService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private CustomerRepository customerRepo;

    public LocalRepoCustomerService() {
        log.info("Customer service initialized - local JPA repositories flavour");
    }

    @Override
    public Optional<Customer> findById(long customerId) {
        return customerRepo.findById(customerId);
    }
}
