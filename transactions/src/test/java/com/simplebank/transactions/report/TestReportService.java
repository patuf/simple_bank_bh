package com.simplebank.transactions.report;

import com.simplebank.transactions.report.Balance;
import com.simplebank.transactions.report.BankTransactionsReportService;
import com.simplebank.transactions.trans.BankTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class TestReportService {

    @Autowired
    BankTransactionsReportService btService;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @DisplayName("Test finding balances for two customers, one of which has no transactions")
    @Test
    public void testFindBalancesForCustomers() {

        List<Balance> balances = btService.findBalancesForCustomers(Arrays.asList(1L, 2L));

        assertThat(balances.size()).isEqualTo(1);
        assertThat(balances.get(0).getId()).isEqualTo(1L);
        assertThat(balances.get(0).getBalance()).isEqualTo(560);
    }

    @DisplayName("Test finding balances for two customers, none of which has transactions")
    @Test
    public void testFindBalancesForCustomers_noTransactions() {

        List<Balance> balances = btService.findBalancesForCustomers(Arrays.asList(2L, 3L));

        assertThat(balances.size()).isEqualTo(0);
    }

    @DisplayName("Test finding balances for two accounts, one of which has no transactions")
    @Test
    public void testFindBalancesForAccounts() {
        List<Balance> balances = btService.findBalancesForAccounts(Arrays.asList(1L, 2L));

        assertThat(balances.size()).isEqualTo(2);
        assertThat(balances.get(0).getBalance()).isEqualTo(-440.);
        assertThat(balances.get(0).getId()).isEqualTo(1L);
        assertThat(balances.get(1).getBalance()).isEqualTo(1000.);
        assertThat(balances.get(1).getId()).isEqualTo(2L);
    }

    @DisplayName("Test finding transactions for an account, expected full page of 20 elements, 2 pages")
    @Test
    public void testGetTransactionsForAccount_fullPage() {
        Pageable pageImpl = PageRequest.of(0, 20);

        Page<BankTransaction> resPage = btService.getTransactionsForAccount(1L, pageImpl);

        assertThat(resPage.getTotalElements()).isEqualTo(25L);
        assertThat(resPage.getTotalPages()).isEqualTo(2);
        assertThat(resPage.getContent().get(0).getAmount()).isEqualTo(100.);
    }

    @DisplayName("Test finding transactions for an account, expected single page of 1 elements")
    @Test
    public void testGetTransactionsForAccount_oneItem() {
        Pageable pageImpl = PageRequest.of(0, 20);

        Page<BankTransaction> resPage = btService.getTransactionsForAccount(2L, pageImpl);

        assertThat(resPage.getTotalElements()).isEqualTo(1L);
        assertThat(resPage.getTotalPages()).isEqualTo(1);
        assertThat(resPage.getContent().get(0).getAmount()).isEqualTo(1000.);
    }

    @DisplayName("Test finding transactions for an account, expected noelements")
    @Test
    public void testGetTransactionsForAccount_noItems() {
        Pageable pageImpl = PageRequest.of(0, 20);

        Page<BankTransaction> resPage = btService.getTransactionsForAccount(3L, pageImpl);

        assertThat(resPage.getTotalElements()).isEqualTo(0L);
        assertThat(resPage.getTotalPages()).isEqualTo(0);
    }
}
