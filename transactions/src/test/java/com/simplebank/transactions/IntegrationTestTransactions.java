package com.simplebank.transactions;

import com.simplebank.transactions.trans.BankTransaction;
import com.simplebank.transactions.trans.BankTransactionRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TransactionsApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTestTransactions {

    @Autowired
    private MockMvc mvc;
    @Autowired
    BankTransactionRepository trRepo;

    @Test
    public void testCreateTransactionAsync() throws Exception {
        BankTransaction bTr = new BankTransaction(1L, 1L, 200., LocalDateTime.now());

        long initialTrCount = trRepo.count();

        String reqContent = String.format("{\"customerId\": %d, \"accountId\": %d, \"amount\": %f, \"timeCreated\": \"%s\"}",
                bTr.getCustomerId(),
                bTr.getAccountId(),
                bTr.getAmount(),
                DateTimeFormatter.ISO_DATE_TIME.format(bTr.getTimeCreated()));

        mvc.perform(post("/rest/v1.0/transaction")
                .content(reqContent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isAccepted());

        // 3 Retries to give the taskExecutor some time to deal with the asynchronous task
        boolean match = false;
        for (int i = 0; i < 3; i++) {
            if (trRepo.count() == initialTrCount + 1) {
                match = true;
                break;
            }
            Thread.sleep(100);
        }
        assertTrue(match);
    }
}
