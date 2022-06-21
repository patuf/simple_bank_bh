package com.simplebank.transactions.report;

import com.simplebank.transactions.trans.BankTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

@Service
public class BankTransactionsReportService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<Balance> findBalancesForCustomers(List<Long> customerIds) {
        String query = "SELECT CUSTOMER_ID, SUM(AMOUNT) as BALANCE FROM BANK_TRANSACTION WHERE CUSTOMER_ID in (:customerIds) GROUP BY CUSTOMER_ID";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("customerIds", customerIds);

        jdbcTemplate.query(query, namedParameters, balanceRowMapper("CUSTOMER_ID"));
        return jdbcTemplate.query(query, namedParameters, balanceRowMapper("CUSTOMER_ID"));
    }

    public List<Balance> findBalancesForAccounts(List<Long> accountIds) {
        String query = "SELECT ACCOUNT_ID, SUM(AMOUNT) as BALANCE FROM BANK_TRANSACTION WHERE ACCOUNT_ID in (:accountIds) GROUP BY ACCOUNT_ID";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("accountIds", accountIds);

        return jdbcTemplate.query(query, namedParameters, balanceRowMapper("ACCOUNT_ID"));
//                (ResultSet rs, int rowNum) -> new Balance(rs.getLong("ACCOUNT_ID"), rs.getDouble("BALANCE"))
    }

    public Page<BankTransaction> getTransactionsForAccount(Long accountId, Pageable pageable) {
        String cntQuery = "SELECT COUNT(*) FROM BANK_TRANSACTION WHERE ACCOUNT_ID = :accountId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("accountId", accountId)
                .addValue("lim", pageable.getPageSize())
                .addValue("ofs", pageable.getOffset());

        int count = jdbcTemplate.queryForObject(cntQuery, namedParameters, Integer.class);

        String findQuery = "SELECT AMOUNT, TIME_CREATED FROM BANK_TRANSACTION WHERE ACCOUNT_ID = :accountId LIMIT :lim OFFSET :ofs";
        List<BankTransaction> transactions = jdbcTemplate.query(findQuery, namedParameters, bankTransactionRowMapper());

        return new PageImpl<BankTransaction>(transactions, pageable, count);
    }

    private RowMapper<Balance> balanceRowMapper(String idColumnName) {
        return (ResultSet rs, int rowNum) -> new Balance(rs.getLong(idColumnName), rs.getDouble("BALANCE"));
    }

    private RowMapper<BankTransaction> bankTransactionRowMapper() {
        return (ResultSet rs, int rowNum) -> new BankTransaction(rs.getDouble("AMOUNT"), rs.getTimestamp("TIME_CREATED").toLocalDateTime());
    }
}
