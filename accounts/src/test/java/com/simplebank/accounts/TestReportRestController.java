package com.simplebank.accounts;

import com.simplebank.accounts.acc.AccountModelAssembler;
import com.simplebank.accounts.acc.AccountService;
import com.simplebank.accounts.acc.AccountStatus;
import com.simplebank.accounts.customer.CustomerRepository;
import com.simplebank.accounts.report.CustomerModelAssembler;
import com.simplebank.accounts.report.SimpleBankReportController;
import com.simplebank.accounts.report.SimpleBankReportService;
import com.simplebank.accounts.report.model.AccountAndBalance;
import com.simplebank.accounts.report.model.BankTransaction;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SimpleBankReportController.class)
@AutoConfigureMockMvc
public class TestReportRestController {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accService;
    @MockBean
    private SimpleBankReportService simpleBankReportService;
    @MockBean
    PlatformTransactionManager trMan;
    @MockBean
    private CustomerRepository customerRepo;

    @Test
    public void testGetCustomers() throws Exception {

        PageRequest pageable = PageRequest.of(1, 2);
        List<CustomerAndBalance> result = Arrays.asList(
                new CustomerAndBalance(1L, "Test1", "Sur1", 1, 100.),
                new CustomerAndBalance(2L, "Test2", "Sur2", 2, 200.));
        Page<CustomerAndBalance> custPage = new PageImpl<>(result, pageable, 5);

        given(simpleBankReportService.findAllCustomers(pageable)).willReturn(custPage);

        mvc.perform(get("/rest/v1.0/report/customers")
                .param("page", "1")
                .param("size", "2")
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.customerAndBalanceList[0].customerId").value(1))
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.prev.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.next.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists());
    }

    @Test
    public void testGetAccountsForCustomer() throws Exception {
        PageRequest pageable = PageRequest.of(1, 2);
        List<AccountAndBalance> accounts = Arrays.asList(
                new AccountAndBalance(1L, 2L, LocalDateTime.now(), AccountStatus.ACTIVE, 200.),
                new AccountAndBalance(2L, 2L, LocalDateTime.now(), AccountStatus.ACTIVE, 300.)
        );
        Page<AccountAndBalance> accPage = new PageImpl<>(accounts, pageable, 5);

        given(simpleBankReportService.findAccountsByCustomerId(2L, pageable)).willReturn(accPage);

        mvc.perform(get("/rest/v1.0/report/customerAccounts/2")
                .param("page", "1")
                .param("size", "2")
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.accountAndBalanceList[0].accountId").value(1))
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.prev.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.next.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists());
    }

    @Test
    public void testGetTransactionsForAccount() throws Exception {
        PageRequest pageable = PageRequest.of(1, 2);
        List<BankTransaction> accounts = Arrays.asList(
                new BankTransaction(200., LocalDateTime.now()),
                new BankTransaction(300., LocalDateTime.now())
        );
        Page<BankTransaction> accPage = new PageImpl<>(accounts, pageable, 5);

        PagedResourcesAssembler<BankTransaction> pra = new PagedResourcesAssembler<BankTransaction>(null, UriComponentsBuilder.fromUriString("http://localshot").build());
        PagedModel<EntityModel<BankTransaction>> res = pra.toModel(accPage);

        given(simpleBankReportService.getTransactionsForAccount(1L, pageable)).willReturn(res);

        mvc.perform(get("/rest/v1.0/report/accountTransactions/1")
                .param("page", "1")
                .param("size", "2")
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.bankTransactionList[1].amount").value(300))
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.prev.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.next.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists());
    }

    @TestConfiguration
    static class AssemblersBeanConfiguration {
        @Bean
        CustomerModelAssembler<CustomerAndBalance> cbModelAssembler() {
            return new CustomerModelAssembler<>();
        }

        @Bean
        AccountModelAssembler<AccountAndBalance> accModelAssembler() {
            return new AccountModelAssembler<>();
        }
    }
}
