package com.simplebank.transactions.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class TransactionsReportDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public Double findTotalBalanceForAccounts(List<Long> accountIds) {
        String query = "SELECT SUM(AMOUNT) FROM BANK_TRANSACTION WHERE ACCOUNT_ID in (:accountIds);";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("accountIds", accountIds);
        return jdbcTemplate.queryForObject(query, namedParameters, Double.class);
    }

    public List<Balance> findBalancesForAccounts(List<Long> accountIds) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("accountIds", accountIds);
//        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM CUSTOMER", namedParameters, Integer.class);

        List<Balance> balances = jdbcTemplate.query("SELECT ACCOUNT_ID, SUM(AMOUNT) as BALANCE FROM BANK_TRANSACTION WHERE ACCOUNT_ID in (:accountIds) GROUP BY ACCOUNT_ID;",
                namedParameters,
                (ResultSet rs, int rowNum) -> new Balance(rs.getLong("ACCOUNT_ID"), rs.getDouble("BALANCE"))
        );
        return balances;
    }

    // TODO: Leave it for now, just in case we need to aggregate the total balance here too
    private class BalanceRowMapper implements RowMapper<Balance> {

        @Override
        public Balance mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Balance(rs.getLong("ACCOUNT_ID"), rs.getDouble("BALANCE"));
        }
    }
}
