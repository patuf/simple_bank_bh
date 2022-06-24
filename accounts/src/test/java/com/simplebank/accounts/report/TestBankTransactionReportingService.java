package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.AccountStatus;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommand;
import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.report.CustomerAndBalanceRowMapper;
import com.simplebank.accounts.report.SimpleBankReportService;
import com.simplebank.accounts.report.model.AccountAndBalance;
import com.simplebank.accounts.report.model.Balance;
import com.simplebank.accounts.report.model.BankTransaction;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestBankTransactionReportingService {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private SimpleBankReportService reportService;

//    private Account account;
//    private Customer customer;
//    private List<CustomerAndBalance> customersAndBalances;
//    private CreateTransactionCommand ctCommand0, ctCommand200;
    private Pageable pageable;

    @BeforeEach
    public void setup() {
//        account = new Account(2L, LocalDateTime.now(), AccountStatus.ACTIVE);
//        account.setAccountId(1L);
//        customer = new Customer(2L, "User1", "Surname1");
//        customersAndBalances = Arrays.asList(
//                new CustomerAndBalance(customer.getCustomerId(), customer.getName(), customer.getSurname(), 3, null),
//                new CustomerAndBalance(3L, "cust3", "sur3", 3, null)
//        );
        pageable = PageRequest.of(1, 2);
    }

    @DisplayName("Test finding all custommers with prepopulated in-mem database")
    @Test
    public void testFindAllCustomers() {

        Double balance = 200.6;
        given(restTemplate.getForObject(any(URI.class), eq(Balance[].class))).willReturn(new Balance[] {new Balance(3L, balance)});
        Page<CustomerAndBalance> allCustomers = reportService.findAllCustomers(pageable);

        assertThat(allCustomers).isNotNull();
        assertThat(allCustomers.getContent().get(0)).isNotNull();
        assertThat(allCustomers.getContent().get(0).getCustomerId()).isEqualTo(3L);
        assertThat(allCustomers.getContent().get(0).getBalance()).isEqualTo(balance);
    }

    @DisplayName("Test finding all customers with pre-populated in-mem database, but erroneous results in getting balance")
    @Test
    public void testFindAllCustomersErrBalance() {

        given(restTemplate.getForObject(any(URI.class), eq(Balance[].class))).willReturn(new Balance[] {new Balance(1L, 200.6)});
        assertThrows(AssertionError.class,
                () -> reportService.findAllCustomers(pageable));
    }

    @DisplayName("Test finding all accounts for a pre-populated customer in an in-mem database")
    @Test
    public void testFindAccountsByCustomerId() {
        Double balance = 200.6;
        Long customerWithAccounts = 1L; // As configured int the sql init script
        given(restTemplate.getForObject(any(URI.class), eq(Balance[].class))).willReturn(new Balance[] {new Balance(3L, balance)});

        Page<AccountAndBalance> accsForCustomer = reportService.findAccountsByCustomerId(customerWithAccounts, pageable);

        assertThat(accsForCustomer).isNotNull();
        assertThat(accsForCustomer.getContent().get(0)).isNotNull();
        assertThat(accsForCustomer.getContent().get(0).getCustomerId()).isEqualTo(1L);
        assertThat(accsForCustomer.getContent().get(0).getAccountId()).isEqualTo(3L);
        assertThat(accsForCustomer.getContent().get(0).getBalance()).isEqualTo(balance);
    }
}
