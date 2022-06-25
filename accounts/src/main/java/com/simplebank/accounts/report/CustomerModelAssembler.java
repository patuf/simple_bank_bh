package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.AccountController;
import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * An implementation of RepresentationModelAssembler, that wraps a CustomerAndBalance object into a HATEOAS-compliant response
 * and adds the contextual links for an CustomerAndBalance representation.
 * @param <T> The Type of Entity to be wrapped. It is upper-bound to Customer.
 */
@Component
public class CustomerModelAssembler<T extends Customer> implements RepresentationModelAssembler<T, EntityModel<T>> {

    @Override
    public EntityModel<T> toModel(T customer) {
        return EntityModel.of(customer,
                linkTo(methodOn(SimpleBankReportController.class).getAccountsForCustomer(customer.getCustomerId(), null)).withRel("customerAccounts"));
    }
}
