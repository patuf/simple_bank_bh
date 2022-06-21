package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.report.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class SimpleBankReportService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${service.transaction.uri.balances.for.accounts}")
    private String balForAccUri;
    @Value("${service.transaction.uri.balances.for.customers}")
    private String balForCustUri;
    @Value("${service.transaction.uri.transactions.for.account}")
    private String trForAccUri;

    public Page<CustomerAndBalance> findAllCustomers(Pageable pageable) {
        SqlParameterSource namedParameters;
        if (pageable.isPaged()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("lim", pageable.getPageSize())
                    .addValue("ofs", pageable.getOffset());
        } else {
            namedParameters = nonPageableSqlParams();
        }
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM CUSTOMER", namedParameters, Integer.class);
        assert count != null;

        CustomerAndBalanceRowMapper rowMapper = new CustomerAndBalanceRowMapper();
        List<CustomerAndBalance> customers = jdbcTemplate.query(
                "SELECT c.CUSTOMER_ID, c.NAME, c.SURNAME, count(1) as NUM_ACCS " +
                        "FROM CUSTOMER c " +
                        "LEFT JOIN ACCOUNT a ON c.CUSTOMER_ID = a.CUSTOMER_ID " +
                        "GROUP BY c.CUSTOMER_ID " +
                        "LIMIT :lim OFFSET :ofs", namedParameters, rowMapper);
        enrichWithBalances(balForCustUri, customers, Customer::getCustomerId, rowMapper);

        return new PageImpl<>(customers, pageable, count);
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

        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) " +
                        "FROM ACCOUNT " +
                        "WHERE CUSTOMER_ID = :customerId"
                , namedParameters, Integer.class);
        assert count != null;

        AccountAndBalanceRowMapper rowMapper = new AccountAndBalanceRowMapper();
        List<AccountAndBalance> accounts = jdbcTemplate.query("SELECT ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_STATUS, TIME_CREATED FROM ACCOUNT WHERE CUSTOMER_ID = :customerId LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);

        enrichWithBalances(balForAccUri, accounts, Account::getAccountId, rowMapper);

        return new PageImpl<>(accounts, pageable, count);
    }

    public PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(Long accountId, Pageable pageable) {

        URI uri = UriComponentsBuilder.fromUriString(trForAccUri)
                .queryParam("accountId", accountId)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build().toUri();
        return restTemplate.getForObject(uri, BankTransactionPagedModel.class);
    }

    private <K extends ContainsBalance> void enrichWithBalances(String uriString, List<K> entities, Function<K, Long> idFinder, IndexKeepingRowMapper<Long, K> rowMapper) {
        List<Long> entityIds = entities.stream().map(idFinder).collect(Collectors.toList());
        URI uri = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("entityIds", entityIds)
                .build().toUri();
        Balance[] balances = restTemplate.getForObject(uri, Balance[].class);
        assert balances != null;
        for (Balance ab : balances) {
            rowMapper.getFromIndex(ab.getId()).setBalance(ab.getBalance());
        }
    }

    private MapSqlParameterSource nonPageableSqlParams() {
        return new MapSqlParameterSource()
                .addValue("lim", Integer.MAX_VALUE)
                .addValue("ofs", 0);
    }

    /**
     * Used to provide static typisation of the PagedModel's generics - helps the Jackson parser properly parse the rest response
     * The class is static, because Jackson can't instantiate it otherwise
     */
    private static class BankTransactionPagedModel extends PagedModel<EntityModel<BankTransaction>> {
    }
}
