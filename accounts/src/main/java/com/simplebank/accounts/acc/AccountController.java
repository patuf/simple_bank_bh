package com.simplebank.accounts.acc;

import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommand;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandRepository;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.exception.AccountNotFoundException;
import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    AccountRepository accRepo;
    @Autowired
    CreateTransactionCommandRepository troutRepo;
    @Autowired
    AccountModelAssembler<Account> accModelAssembler;
    @Autowired
    CustomerDataProvider customerDataProvider;

    @GetMapping("/{accountId}")
    EntityModel<Account> one(@PathVariable long accountId) {
        log.info("Looking for account");
        Account account = accRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        log.info("Found account");


        return accModelAssembler.toModel(account);
    }

    @PostMapping
    @Transactional
    ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        // Customer data not needed, just checking for customer existence
        customerDataProvider.findById(request.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        Account newAccount = accRepo.save(new Account(request.getCustomerId(), LocalDateTime.now(), AccountStatus.ACTIVE));

        if (request.getInitialCredit() != 0) {
            troutRepo.save(new CreateTransactionCommand(request.getCustomerId(), newAccount.getId(), request.getInitialCredit()));
        }

        EntityModel<Account> entityModel = accModelAssembler.toModel(newAccount);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
}
