package com.simplebank.accounts.acc;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.simplebank.accounts.report.ReportController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AccountModelAssembler<T extends Account> implements RepresentationModelAssembler<T, EntityModel<T>> {

    @Override
    public EntityModel<T> toModel(T account) {

        return EntityModel.of(account, //
                linkTo(methodOn(AccountController.class).one(account.getId())).withSelfRel(),
                linkTo(methodOn(ReportController.class).getTransactions(account.getId(), null)).withRel("transactions"));
    }
}
