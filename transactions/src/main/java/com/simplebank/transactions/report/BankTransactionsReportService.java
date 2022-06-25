package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * The Service providing the reposting services for SimpleBank's BankTransactions service.
 */
public interface BankTransactionsReportService {
    /**
     * Finds the balances for each of the elements of a list of customerIds.
     * @param customerIds The list of customerIds
     * @return A list of balances, one for each customerId. If a customer has no transactions, its balance will be 0
     */
    List<Balance> findBalancesForCustomers(List<Long> customerIds);

    /**
     * Finds the balances for each of the elements of a list of accountIds.
     * @param accountIds The list of accountIds
     * @return A list of balances, one for each accountId. If an account has no transactions, its balance will be 0
     */
    List<Balance> findBalancesForAccounts(List<Long> accountIds);

    /**
     * Finds a page of transactions for the given accountId.
     * @param accountId the accountId to search balances for
     * @return A HATEOAS-compliant PagedModel of BankTransactions, if the account has no transactions then the PagedModel will have empty content.
     */
    Page<BankTransaction> getTransactionsForAccount(Long accountId, Pageable pageable);
}
