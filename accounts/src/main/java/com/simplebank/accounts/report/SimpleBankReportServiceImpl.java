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
public
class SimpleBankReportServiceImpl implements SimpleBankReportService {
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

    @Override
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

        log.debug(String.format("findAllCustomers count found %d customers", count));

        CustomerAndBalanceRowMapper rowMapper = new CustomerAndBalanceRowMapper();
        List<CustomerAndBalance> customers = jdbcTemplate.query(
                "SELECT c.CUSTOMER_ID, c.NAME, c.SURNAME, count(1) as NUM_ACCS " +
                        "FROM CUSTOMER c " +
                        "LEFT JOIN ACCOUNT a ON c.CUSTOMER_ID = a.CUSTOMER_ID " +
                        "GROUP BY c.CUSTOMER_ID " +
                        "LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);
        enrichWithBalances(balForCustUri, rowMapper);

        return new PageImpl<>(customers, pageable, count);
    }

    @Override
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
        List<AccountAndBalance> accounts = jdbcTemplate.query(
                "SELECT ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_STATUS, TIME_CREATED " +
                        "FROM ACCOUNT " +
                        "WHERE CUSTOMER_ID = :customerId " +
                        "LIMIT :lim OFFSET :ofs",
                namedParameters, rowMapper);
        enrichWithBalances(balForAccUri, rowMapper);

        return new PageImpl<>(accounts, pageable, count);
    }

    @Override
    public PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(Long accountId, Pageable pageable) {

        URI uri = UriComponentsBuilder.fromUriString(trForAccUri)
                .queryParam("accountId", accountId)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build().toUri();
        return restTemplate.getForObject(uri, BankTransactionPagedModel.class);
    }

    /**
     * Enriches a list of ContainsBalance entities with
     * @param uriString The uri of the BankTransactions' synchronous rest endpoint that retrieves the balances for this type of entity.
     * @param rowMapper The IndexKeepingRowMapper that was used for mapping the resultSet into domain objects and keeps an in-memory index map of them.
     * @param <K> The particular type of the business object that was mapped by the IndexKeepingRowMapper
     */
    private <K extends ContainsBalance> void enrichWithBalances(String uriString, IndexKeepingRowMapper<Long, K> rowMapper) {
        log.debug("Enriching with balances for uri " + uriString);
        URI uri = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("entityIds", rowMapper.getIndexes())
                .build().toUri();
        Balance[] balances = restTemplate.getForObject(uri, Balance[].class);
        assert balances != null;
        for (Balance ab : balances) {
            K entity = rowMapper.getFromIndex(ab.getId());
            assert entity != null;
            entity.setBalance(ab.getBalance());
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
    static class BankTransactionPagedModel extends PagedModel<EntityModel<BankTransaction>> {
    }
}
