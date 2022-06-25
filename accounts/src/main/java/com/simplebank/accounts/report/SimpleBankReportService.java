package com.simplebank.accounts.report;

import com.simplebank.accounts.report.model.AccountAndBalance;
import com.simplebank.accounts.report.model.BankTransaction;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

/**
 * A service providing the necessary methods to retrieve various reports for SimpleBank
 */
public interface SimpleBankReportService {
    /**
     * Finds a Page of customers, along with their balances. Not sortable.
     * @param pageable the paging options
     * @return A Page of customers and their balances. Empty page if none are found.
     */
    Page<CustomerAndBalance> findAllCustomers(Pageable pageable);

    /**
     * Finds a Page of accounts for a given customerId, along with their balances. Not sortable.
     * @param customerId the id of the customer
     * @param pageable the paging options
     * @return A Page of accounts for the given customerId, along with the balance for each account.
     */
    Page<AccountAndBalance> findAccountsByCustomerId(Long customerId, Pageable pageable);

    /**
     * Finds a page of transactions for a given accountId.
     * This method queries the BankTransaction service and directly return the results form it.
     * @param accountId the id of the account
     * @param pageable the paging options
     * @return A HATEOAS compliant PagedModel of the page of transactions.
     */
    PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(Long accountId, Pageable pageable);
}
