package com.simplebank.transactions.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class BankTransactionsReportService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<Balance> findBalancesForCustomers(List<Long> customerIds) {
        String query = "SELECT CUSTOMER_ID, SUM(AMOUNT) as BALANCE FROM BANK_TRANSACTION WHERE CUSTOMER_ID in (:customerIds) GROUP BY CUSTOMER_ID;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("customerIds", customerIds);

        jdbcTemplate.query(query, namedParameters, balanceRowMapper("CUSTOMER_ID"));
        return jdbcTemplate.query(query, namedParameters, balanceRowMapper("CUSTOMER_ID"));
    }

    public List<Balance> findBalancesForAccounts(List<Long> accountIds) {
        String query = "SELECT ACCOUNT_ID, SUM(AMOUNT) as BALANCE FROM BANK_TRANSACTION WHERE ACCOUNT_ID in (:accountIds) GROUP BY ACCOUNT_ID;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("accountIds", accountIds);

        return jdbcTemplate.query(query, namedParameters, balanceRowMapper("ACCOUNT_ID"));
//                (ResultSet rs, int rowNum) -> new Balance(rs.getLong("ACCOUNT_ID"), rs.getDouble("BALANCE"))
    }

    private RowMapper<Balance> balanceRowMapper(String idColumnName) {
        return (ResultSet rs, int rowNum) -> new Balance(rs.getLong(idColumnName), rs.getDouble("BALANCE"));
    }
}
