package com.simplebank.accounts.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ReportDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<CustomerAndBalance> findAllCustomers(Pageable pageable) {
        SqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        CustomerAndBalanceRowMapper rowMapper = new CustomerAndBalanceRowMapper();

        return jdbcTemplate.query("SELECT c.CUSTOMER_ID, c.NAME, c.SURNAME, count(1) as NUM_ACCS\n" +
                "FROM CUSTOMER c\n" +
                "LEFT JOIN ACCOUNT a ON c.CUSTOMER_ID = a.CUSTOMER_ID\n" +
                "GROUP BY c.CUSTOMER_ID\n" +
                "LIMIT :lim OFFSET :ofs", namedParameters, rowMapper);
    }

    public List<AccountAndBalance> findAccountByCustomerId(Long customerId, Pageable pageable) {
        MapSqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        namedParameters.addValue("customerId", customerId);

        AccountAndBalanceRowMapper rowMapper = new AccountAndBalanceRowMapper();
        return jdbcTemplate.query("SELECT ID, CUSTOMER_ID, ACCOUNT_STATUS FROM ACCOUNT WHERE CUSTOMER_ID = :customerId LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);
    }

    private MapSqlParameterSource nonPageableSqlParams() {
        return new MapSqlParameterSource()
                .addValue("lim", Integer.MAX_VALUE)
                .addValue("ofs", 0);
    }
}
