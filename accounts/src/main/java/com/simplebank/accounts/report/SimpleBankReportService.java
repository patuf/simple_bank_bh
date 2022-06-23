package com.simplebank.accounts.report;

import com.simplebank.accounts.report.model.AccountAndBalance;
import com.simplebank.accounts.report.model.BankTransaction;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public interface SimpleBankReportService {
    Page<CustomerAndBalance> findAllCustomers(Pageable pageable);

    Page<AccountAndBalance> findAccountsByCustomerId(Long customerId, Pageable pageable);

    PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(Long accountId, Pageable pageable);
}
