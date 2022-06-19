package com.simplebank.accounts.acc;

import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandOutbox;
import com.simplebank.accounts.acc.transactionoutbox.TransactionOutboxRepository;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.exception.AccountNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    AccountRepository accRepo;
    @Autowired
    TransactionOutboxRepository troutRepo;
    @Autowired
    AccountModelAssembler assembler;
    @Autowired
    CustomerDataProvider customerDataProvider;

    @GetMapping("/{accountId}")
    EntityModel<Account> one(@PathVariable long accountId) {
        log.info("Looking for account");
        Account account = accRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        log.info("Found account");


        return assembler.toModel(account);
    }

    @PostMapping
//    @Transactional
//    @Validated
    ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        // Customer data not needed, just checking for customer existence
//        customerRepo.findById(request.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        customerDataProvider.findById(request.getCustomerId());
        Account newAccount = new Account(request.getCustomerId(), LocalDateTime.now(), AccountStatus.ACTIVE);
        newAccount = accRepo.save(newAccount);

        if (request.getInitialCredit() != 0) {
            troutRepo.save(new CreateTransactionCommandOutbox(request.getCustomerId(), newAccount.getId(), request.getInitialCredit()));
        }

        EntityModel<Account> entityModel = assembler.toModel(newAccount);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
}
