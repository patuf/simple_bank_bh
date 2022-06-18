package com.simplebank.accounts;

import com.simplebank.accounts.acc.Account;

public interface SaveAndEventRepository {
    <S extends Account> S saveAndEvent(S entity);
}
