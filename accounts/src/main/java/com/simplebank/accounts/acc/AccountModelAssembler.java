package com.simplebank.accounts.acc;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.simplebank.accounts.report.ReportController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class AccountModelAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {

    @Override
    public EntityModel<Account> toModel(Account account) {

        return EntityModel.of(account, //
                linkTo(methodOn(AccountController.class).one(account.getId())).withSelfRel(),
                linkTo(methodOn(ReportController.class).getTransactions(account.getId(), null)).withRel("transactions"));
    }
}
