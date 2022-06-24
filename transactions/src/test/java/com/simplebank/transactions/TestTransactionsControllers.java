package com.simplebank.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplebank.transactions.report.Balance;
import com.simplebank.transactions.report.BankTransactionsReportController;
import com.simplebank.transactions.report.BankTransactionsReportService;
import com.simplebank.transactions.trans.BankTransaction;
import com.simplebank.transactions.trans.BankTransactionHttpController;
import com.simplebank.transactions.trans.BankTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({BankTransactionHttpController.class, BankTransactionsReportController.class})
@AutoConfigureMockMvc
public class TestTransactionsControllers {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BankTransactionsReportService reportService;
    @MockBean
    private TaskExecutor taskExecutor;
    @MockBean
    private BankTransactionRepository btRepo;

    @Test
    public void testCreateTransactionAsync() throws Exception {
        BankTransaction bTr = new BankTransaction(1L, 1L, 200., LocalDateTime.now());

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

        // Expecting a simple asynchronous execution
        verify(taskExecutor, times(1)).execute(any(Runnable.class));
    }

    @Test
    public void testFindBalanceForAccounts() throws Exception {
        List<Balance> resBalances = Arrays.asList(
                new Balance(1L, 200.),
                new Balance(2L, 350.)
        );

        given(reportService.findBalancesForAccounts(any(List.class))).willReturn(resBalances);

        mvc.perform(get("/rest/v1.0/report/balancesForAccounts")
                .param("entityIds", "1")
                .contentType(MediaType.APPLICATION_JSON))
//                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].balance").value(200.))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].balance").value(350.));
    }

    @Test
    public void findBalanceForCustomers() throws Exception {
        List<Balance> resBalances = Arrays.asList(
                new Balance(1L, 200.),
                new Balance(2L, 350.)
        );

        given(reportService.findBalancesForCustomers(any(List.class))).willReturn(resBalances);

        mvc.perform(get("/rest/v1.0/report/balancesForCustomers")
                .param("entityIds", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].balance").value(200.))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].balance").value(350.));
    }

    @Test
    public void testGetTransactionsForAccount() throws Exception {

        Pageable pageable = PageRequest.of(1, 2);

        List<BankTransaction> result = Arrays.asList(
                new BankTransaction(2L, 1L, 200., LocalDateTime.now()),
                new BankTransaction(2L, 1L, 300., LocalDateTime.now())
        );
        Page<BankTransaction> trPage = new PageImpl<>(result, pageable, 5);

        given(reportService.getTransactionsForAccount(1L, pageable)).willReturn(trPage);

        mvc.perform(get("/rest/v1.0/report/transactionsForAccount")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .param("accountId", "1")
                .param("entityIds", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bankTransactionList[0].amount").value(200.))
                .andExpect(jsonPath("$._embedded.bankTransactionList[1].amount").value(300.))
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.prev.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.next.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists());
    }

//    @TestConfiguration
//    static class AssemblersBeanConfiguration {
//        @Bean
//        CustomerModelAssembler<CustomerAndBalance> cbModelAssembler() {
//            return new CustomerModelAssembler<>();
//        }
//
//        @Bean
//        AccountModelAssembler<AccountAndBalance> accModelAssembler() {
//            return new AccountModelAssembler<>();
//        }
//    }
}
