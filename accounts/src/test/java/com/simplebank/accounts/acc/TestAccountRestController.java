package com.simplebank.accounts.acc;

import com.simplebank.accounts.acc.*;
import com.simplebank.accounts.customer.CustomerRepository;
import com.simplebank.accounts.exception.AccountNotFoundException;
import com.simplebank.accounts.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//        classes = AccountsApplication.class)
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application.properties")
public class TestAccountRestController {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AccountService accService;
    @MockBean
    private CustomerRepository customerRepo;
    @MockBean
    PlatformTransactionManager trMan;

    private Account account;

    @BeforeEach
    public void setup() {
        account = new Account(2L, LocalDateTime.now(), AccountStatus.ACTIVE);
        account.setAccountId(1L);
    }

    @Test
    public void testCreateAccount() throws Exception {

        Double initialCredit = -200.5;

        given(accService.createAccount(eq(account.getCustomerId()), eq(initialCredit), any(LocalDateTime.class))).willReturn(account);

        mvc.perform(post("/rest/v1.0/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"customerId\": %d, \"initialCredit\": %f}", account.getCustomerId(), initialCredit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());
    }

    @Test
    public void testCreateAccCustomerNotFound() throws Exception {
        given(accService.createAccount(eq(9999L), eq(-200.), any(LocalDateTime.class))).willThrow(CustomerNotFoundException.class);
        mvc.perform(post("/rest/v1.0/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\": 9999, \"initialCredit\": -200}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindOneAccount() throws Exception {
        Account resAcc = new Account(1L, LocalDateTime.now(), AccountStatus.ACTIVE);
        resAcc.setAccountId(200L);
        given(accService.findOne(1L)).willReturn(resAcc);
        mvc.perform(get("/rest/v1.0/account/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\": 9999, \"initialCredit\": -200}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(200L))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());
    }

    @Test
    public void testFindOneAccountNotFound() throws Exception {
        given(accService.findOne(1L)).willThrow(AccountNotFoundException.class);
        mvc.perform(get("/rest/v1.0/account/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class AccountRestControllerTestConfiguration {
        @Bean
        AccountModelAssembler<Account> getAccountModelAssembler() {
            return new AccountModelAssembler<>();
        }
    }
}
