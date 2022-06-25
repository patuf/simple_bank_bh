package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
import com.simplebank.transactions.trans.BankTransactionHttpController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The REST controller providing the reposting services for SimpleBank's BankTransactions service.
 * Separating the reporting controller from the CRUD controller facilitates easier implementation of the CQRS pattern.
 */
@RestController()
@RequestMapping("rest/v1.0/report")
public class BankTransactionsReportController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private BankTransactionsReportService reportService;
    @Autowired
    private PagedResourcesAssembler<BankTransaction> transactionPageAssembler;

    public BankTransactionsReportController() {
        log.info("BankTransactionsReportController initialized");
    }

    /**
     * GET endpoint. Finds the balances for each of the elements of a list of accountIds.
     * @param entityIds The list of accountIds
     * @return A list of balances, one for each accountId. If an account has no transactions, its balance will be 0
     */
    @GetMapping("/balancesForAccounts")
    public List<Balance> findBalancesForAccounts(@RequestParam List<Long> entityIds) {
        return reportService.findBalancesForAccounts(entityIds);
    }

    /**
     * GET endpoint. Finds the balances for each of the elements of a list of customerIds.
     * @param entityIds The list of customerIds
     * @return A list of balances, one for each customerId. If a customer has no transactions, its balance will be 0
     */
    @GetMapping("/balancesForCustomers")
    public List<Balance> findBalanceForCustomers(@RequestParam List<Long> entityIds) {
        return reportService.findBalancesForCustomers(entityIds);
    }

    /**
     * GET endpoint. Finds a page of transactions for the given accountId.
     * @param accountId the accountId to search balances for
     * @return A HATEOAS-compliant PagedModel of BankTransactions, if the account has no transactions then the PagedModel will have empty content.
     */
    @GetMapping("/transactionsForAccount")
    public PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(@RequestParam Long accountId, Pageable pageable) {
        Page<BankTransaction> transactions = reportService.getTransactionsForAccount(accountId, pageable);
        return transactionPageAssembler.toModel(transactions);
    }
}
