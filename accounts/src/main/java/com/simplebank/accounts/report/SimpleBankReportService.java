package com.simplebank.accounts.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class SimpleBankReportService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private RestTemplate restTemplate;

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
        List<CustomerAndBalance> customers = jdbcTemplate.query(
                "SELECT c.CUSTOMER_ID, c.NAME, c.SURNAME, count(1) as NUM_ACCS " +
                        "FROM CUSTOMER c " +
                        "LEFT JOIN ACCOUNT a ON c.CUSTOMER_ID = a.CUSTOMER_ID " +
                        "GROUP BY c.CUSTOMER_ID " +
                        "LIMIT :lim OFFSET :ofs", namedParameters, rowMapper);
        enrichWithBalances("http://localhost:8081/rest/v1.0/report/balancesForCustomers", customers, accBal -> accBal.getCustomerId(), rowMapper);

        return new PageImpl<CustomerAndBalance>(customers, pageable, count);
    }

    public Page<AccountAndBalance> findAccountsByCustomerId(Long customerId, Pageable pageable) {
        MapSqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        namedParameters.addValue("customerId", customerId);

        int count = jdbcTemplate.queryForObject(
                "SELECT count(*) " +
                        "FROM ACCOUNT " +
                        "WHERE CUSTOMER_ID = :customerId"
                , namedParameters, Integer.class);

        AccountAndBalanceRowMapper rowMapper = new AccountAndBalanceRowMapper();
        List<AccountAndBalance> accounts = jdbcTemplate.query("SELECT ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_STATUS, TIME_CREATED FROM ACCOUNT WHERE CUSTOMER_ID = :customerId LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);

        enrichWithBalances("http://localhost:8081/rest/v1.0/report/balancesForAccounts", accounts, accBal -> accBal.getAccountId(), rowMapper);

        return new PageImpl<AccountAndBalance>(accounts, pageable, count);
    }

    private <K extends ContainsBalance> Balance[] enrichWithBalances(String uriString, List<K> entities, Function<K, Long> idFinder, IndexKeepingRowMapper<Long, K> rowMapper) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        List<Long> entityIds = entities.stream().map(accBal -> idFinder.apply(accBal)).collect(Collectors.toList());
        URI uri = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("entityIds", entityIds)
                .build().toUri();
        Balance[] balances = restTemplate.getForObject(uri, Balance[].class);
        for (Balance ab : balances) {
            rowMapper.getFromIndex(ab.getId()).setBalance(ab.getBalance());
        }

        return balances;
    }

    private Balance[] getBalancesForAccounts_boring(List<AccountAndBalance> accounts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        List<Long> accountIds = accounts.stream().map(accBal -> accBal.getAccountId()).collect(Collectors.toList());
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8081/rest/v1.0/report/balancesForAccounts")
                .queryParam("accountIds", accountIds)
                .build().toUri();
        Balance[] balances = restTemplate.getForObject(uri, Balance[].class);
        return balances;
    }

    private MapSqlParameterSource nonPageableSqlParams() {
        return new MapSqlParameterSource()
                .addValue("lim", Integer.MAX_VALUE)
                .addValue("ofs", 0);
    }
}
