package com.simplebank.accounts.acc.transactionoutbox;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.customer.CustomerDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
