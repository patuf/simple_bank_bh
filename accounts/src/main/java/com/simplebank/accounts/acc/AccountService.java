package com.simplebank.accounts.acc;

import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommand;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandRepository;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.exception.AccountNotFoundException;
import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accRepo;
    @Autowired
    private CreateTransactionCommandRepository troutRepo;
    @Autowired
    private CustomerDataProvider customerDataProvider;

    public AccountService(AccountRepository accRepo, CreateTransactionCommandRepository troutRepo, CustomerDataProvider customerDataProvider) {
        this.accRepo = accRepo;
        this.troutRepo = troutRepo;
        this.customerDataProvider = customerDataProvider;
    }

    public Account findOne(Long accountId) {
        return accRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public Account createAccount(Long customerId, Double initialCredit) {
        // Customer data not needed, just checking for customer existence
        customerDataProvider.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        Account newAccount = accRepo.save(new Account(customerId, LocalDateTime.now(), AccountStatus.ACTIVE));
        if (initialCredit != 0) {
            troutRepo.save(new CreateTransactionCommand(customerId, newAccount.getAccountId(), initialCredit));
        }

        return newAccount;
    }
}
