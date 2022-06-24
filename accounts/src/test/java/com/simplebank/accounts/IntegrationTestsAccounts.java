package com.simplebank.accounts;

import com.simplebank.accounts.acc.Account;
import com.simplebank.accounts.acc.AccountRepository;
import com.simplebank.accounts.acc.AccountStatus;
import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AccountsApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTestsAccounts {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CreateTransactionCommandRepository ctRepo;
    @Autowired
    private AccountRepository accRepo;

    @LocalServerPort
    private int port;

    private Account account;

    @BeforeEach
    public void setup() {
        account = new Account(2L, LocalDateTime.now(), AccountStatus.ACTIVE);
//        account.setAccountId(1L);
    }


    @DisplayName("Testing creation of account failure due to non-existing customer")
    @Test
    @Order(1)
    public void testCreateAccount_noCustomer() throws Exception {

        double initialCredit = 0.;
        long customerId = 9999L;
        long initialAccCount = accRepo.count();
        long initialCtCount = ctRepo.count();
        long expectedAccId = initialAccCount + 1;

        mvc.perform(post("/rest/v1.0/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"customerId\": %d, \"initialCredit\": %f}", customerId, initialCredit)))
                .andExpect(status().isNotFound());

        // Try to find the account we just failed to create
        mvc.perform(get("/rest/v1.0/account/" + expectedAccId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Check chat the count of accounts is the same
        assertTrue(accRepo.count() == initialAccCount);
        // Check that no CreateTransactionCommand was sent to the Transactional Outbox
        assertTrue(ctRepo.count() == initialCtCount);
    }

    @Test
    @Order(2)
    public void testCreateAccount_noTransaction() throws Exception {

        double initialCredit = 0.;
        long customerId = 3L;
        long initialAccCount = accRepo.count();
        long initialCtCount = ctRepo.count();
        long expectedAccId = initialAccCount + 1;

        mvc.perform(post("/rest/v1.0/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"customerId\": %d, \"initialCredit\": %f}", customerId, initialCredit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(expectedAccId))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());

        // Find the account we just created
        mvc.perform(get("/rest/v1.0/account/" + expectedAccId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(expectedAccId))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());

        // Check chat the count of accounts has increased by 1
        assertTrue(accRepo.count() == initialAccCount + 1);
        // Check that no CreateTransactionCommand was sent to the Transactional Outbox
        assertTrue(ctRepo.count() == initialCtCount);
    }

    @Test
    @Order(3)
    public void testCreateAccount_withTransaction() throws Exception {

        Double initialCredit = -200.5;
        long customerId = 3L;
        long initialAccCount = accRepo.count();
        long initialCtCount = ctRepo.count();
        long expectedAccId = initialAccCount + 1;

        mvc.perform(post("/rest/v1.0/account")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"customerId\": %d, \"initialCredit\": %f}", account.getCustomerId(), initialCredit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(expectedAccId))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());

        // Find the account we just created
        mvc.perform(get("/rest/v1.0/account/" + expectedAccId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(expectedAccId))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.transactionsForAccount.href").exists())
                .andExpect(jsonPath("$._links.accountsForCustomer.href").exists());

        // Check chat the count of accounts has increased by 1
        assertTrue(accRepo.count() == initialAccCount + 1);
        // Check that CreateTransactionCommand was sent to the Transactional Outbox
        assertTrue(ctRepo.count() == initialCtCount + 1);
    }
}
