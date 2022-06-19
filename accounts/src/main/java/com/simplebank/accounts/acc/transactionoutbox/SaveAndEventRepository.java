package com.simplebank.accounts.acc.transactionoutbox;

import com.simplebank.accounts.acc.Account;

public interface SaveAndEventRepository {
    <S extends Account> S saveAndEvent(S entity);
}
