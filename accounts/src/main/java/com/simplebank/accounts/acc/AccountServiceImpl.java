package com.simplebank.accounts.acc;

import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommand;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandRepository;
import com.simplebank.accounts.customer.CustomerService;
import com.simplebank.accounts.exception.AccountNotFoundException;
import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accRepo;
    @Autowired
    private CreateTransactionCommandRepository troutRepo;
    @Autowired
    private CustomerService customerService;

    public AccountServiceImpl(AccountRepository accRepo, CreateTransactionCommandRepository troutRepo, CustomerService customerService) {
        this.accRepo = accRepo;
        this.troutRepo = troutRepo;
        this.customerService = customerService;
    }

    @Override
    public Account findOne(Long accountId) {
        return accRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public Account createAccount(Long customerId, Double initialCredit, LocalDateTime createdAt) {
        // Customer data not needed, just checking for customer existence
        customerService.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        Account newAccount = accRepo.save(new Account(customerId, createdAt, AccountStatus.ACTIVE));
        if (initialCredit != 0) {
            troutRepo.save(new CreateTransactionCommand(customerId, newAccount.getAccountId(), initialCredit, createdAt));
        }

        return newAccount;
    }
}
