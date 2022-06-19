package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("rest/v1.0/report")
public class TransactionsReportController {
    @Autowired
    TransactionsReportDao reportDao;

    @GetMapping("/balancesForAccounts")
    public List<Balance> findBalancesForAccount(@RequestParam List<Long> accountIds) {
        return reportDao.findBalancesForAccounts(accountIds);
    }

    @GetMapping("/totalBalanceForAccounts")
    public Double findTotalBalanceForAccount(@RequestParam List<Long> accountIds) {
        return reportDao.findTotalBalanceForAccounts(accountIds);
    }
}
