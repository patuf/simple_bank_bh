package com.simplebank.accounts.acc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    AccountModelAssembler<Account> accModelAssembler;
    @Autowired
    private AccountService accService;

    @GetMapping("/{accountId}")
    EntityModel<Account> one(@PathVariable long accountId) {
        Account account = accService.findOne(accountId);

        return accModelAssembler.toModel(account);
    }

    @PostMapping
    @Transactional
    ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account newAccount = accService.createAccount(request.getCustomerId(), request.getInitialCredit());

        EntityModel<Account> entityModel = accModelAssembler.toModel(newAccount);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
