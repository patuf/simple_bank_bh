package com.simplebank.accounts.acc;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.simplebank.accounts.report.SimpleBankReportController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * An implementation of RepresentationModelAssembler, that wraps an Account object into a HATEOAS-compliant response
 * and adds the contextual links for an Account representation.
 * @param <T> The Type of Entity to be wrapped. It is upper-bound to Account.
 */
@Component
public class AccountModelAssembler<T extends Account> implements RepresentationModelAssembler<T, EntityModel<T>> {

    @Override
    public EntityModel<T> toModel(T account) {

        return EntityModel.of(account, //
                linkTo(methodOn(AccountController.class).one(account.getAccountId())).withSelfRel(),
                linkTo(methodOn(SimpleBankReportController.class).getTransactionsForAccount(account.getAccountId(), null)).withRel("transactionsForAccount"),
                linkTo(methodOn(SimpleBankReportController.class).getAccountsForCustomer(account.getCustomerId(), null)).withRel("accountsForCustomer"));
    }
}
