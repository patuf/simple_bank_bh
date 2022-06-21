package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
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

@RestController()
@RequestMapping("rest/v1.0/report")
public class BankTransactionsReportController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private BankTransactionsReportService reportDao;
    @Autowired
    private PagedResourcesAssembler<BankTransaction> transactionPageAssembler;

    @GetMapping("/balancesForAccounts")
    public List<Balance> findBalancesForAccounts(@RequestParam List<Long> entityIds) {
        return reportDao.findBalancesForAccounts(entityIds);
    }

    @GetMapping("/balancesForCustomers")
    public List<Balance> findBalanceForCustomers(@RequestParam List<Long> entityIds) {
        return reportDao.findBalancesForCustomers(entityIds);
    }

    @GetMapping("/transactionsForAccount")
    public PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(@RequestParam Long accountId, Pageable pageable) {
        Page<BankTransaction> transactions = reportDao.getTransactionsForAccount(accountId, pageable);
        return transactionPageAssembler.toModel(transactions);
    }
}
