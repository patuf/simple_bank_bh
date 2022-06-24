package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankTransactionsReportService {
    List<Balance> findBalancesForCustomers(List<Long> customerIds);

    List<Balance> findBalancesForAccounts(List<Long> accountIds);

    Page<BankTransaction> getTransactionsForAccount(Long accountId, Pageable pageable);
}
