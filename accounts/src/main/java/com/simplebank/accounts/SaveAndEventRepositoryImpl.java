package com.simplebank.accounts;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.CreateAccountRequest;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.customer.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

public class SaveAndEventRepositoryImpl implements SaveAndEventRepository {
    @Autowired
    CustomerDataProvider customerDataProvider;


    @Override
    @Transactional
    public <S extends Account> S saveAndEvent(S entity) {
//        customerRepo.findById(request.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
//        Account newAccount = new Account(request.getCustomerId(), ZonedDateTime.now(), Account.AccountStatus.ACTIVE);
//        accRepo.save(newAccount);
//        EntityModel<CreateAccountRequest> entityModel = assembler.toModel(request);
        return null;
    }
}
