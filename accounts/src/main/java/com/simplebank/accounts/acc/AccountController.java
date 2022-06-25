package com.simplebank.accounts.acc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * The REST controller providing the basic CRUD operations for SimpleBank's Accounts.
 * Separating the reporting controller from the CRUD controller facilitates easier implementation of the CQRS pattern.
 */
@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    AccountModelAssembler<Account> accModelAssembler;
    @Autowired
    private AccountService accService;

    /**
     * GET endpoint. Finds one account by its id.
     *
     * @param accountId the accountId
     * @return A HATEOAS-compliant representation of Account
     */
    @GetMapping("/{accountId}")
    public EntityModel<Account> one(@PathVariable long accountId) {
        Account account = accService.findOne(accountId);

        return accModelAssembler.toModel(account);
    }

    /**
     * POST endpoint. Created a new account for given customerId and initialBalance
     *
     * @param request the data class containing the request body
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account newAccount = accService.createAccount(request.getCustomerId(), request.getInitialCredit(), LocalDateTime.now());

        EntityModel<Account> entityModel = accModelAssembler.toModel(newAccount);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .body(entityModel);
    }
}
