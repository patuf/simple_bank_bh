package com.simplebank.accounts.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
class ReportDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
//    @Autowired
//    private RestTemplate restTemplate;

    public Page<CustomerAndBalance> findAllCustomers(Pageable pageable) {
        SqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM CUSTOMER", namedParameters, Integer.class);

        CustomerAndBalanceRowMapper rowMapper = new CustomerAndBalanceRowMapper();
        List<CustomerAndBalance> customers = jdbcTemplate.query("SELECT c.CUSTOMER_ID, c.NAME, c.SURNAME, count(1) as NUM_ACCS\n" +
                "FROM CUSTOMER c\n" +
                "LEFT JOIN ACCOUNT a ON c.CUSTOMER_ID = a.CUSTOMER_ID\n" +
                "GROUP BY c.CUSTOMER_ID\n" +
                "LIMIT :lim OFFSET :ofs", namedParameters, rowMapper);

        return new PageImpl<CustomerAndBalance>(customers, pageable, count);
    }

    public Page<AccountAndBalance> findAccountByCustomerId(Long customerId, Pageable pageable) {
        MapSqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        namedParameters.addValue("customerId", customerId);

        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM ACCOUNT WHERE CUSTOMER_ID = :customerId", namedParameters, Integer.class);

        AccountAndBalanceRowMapper rowMapper = new AccountAndBalanceRowMapper();
        List<AccountAndBalance> accounts = jdbcTemplate.query("SELECT ID, CUSTOMER_ID, ACCOUNT_STATUS, TIME_CREATED FROM ACCOUNT WHERE CUSTOMER_ID = :customerId LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);

        return new PageImpl<AccountAndBalance>(accounts, pageable, count);
    }

    private MapSqlParameterSource nonPageableSqlParams() {
        return new MapSqlParameterSource()
                .addValue("lim", Integer.MAX_VALUE)
                .addValue("ofs", 0);
    }
}
