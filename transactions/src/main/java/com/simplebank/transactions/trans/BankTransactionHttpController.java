package com.simplebank.transactions.trans;

import com.simplebank.transactions.report.Balance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * The REST controller providing the CRUD services for SimpleBank's BankTransactions service.
 * This controller is initialized only when a "messages-http" SpringBoot profile is set, i.e. the asynchronous
 * communication between the Accounts and BankTransactions service is done through REST endpoints.
 * Separating the reporting controller from the CRUD controller facilitates easier implementation of the CQRS pattern.
 */
@RestController()
@Profile({"messages-http", "test"})
@RequestMapping("rest/v1.0/transaction")
public class BankTransactionHttpController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private BankTransactionRepository btRepo;

    public BankTransactionHttpController() {
        log.info("BankTransactionHttpController initialized");
    }

    /**
     * POST endpoint. Registers a new BankTransaction as per the provided requestBody.
     * Being asynchronous, this method schedules an execution with the taskExecutor and returns without waiting for the result.
     * In an ideal implementation, it should asynchronously return a response by REST calling a confirmation endpoint back on the calling service.
     * @param bankTransaction The transaction to be registered
     * @return a generic "Accepted" answer.
     */
    @PostMapping
    public ResponseEntity<?> createTransactionAsync(@Valid @RequestBody BankTransaction bankTransaction) {
        log.debug(String.format("Consumed Create Message HTTP command for amount -> %s", bankTransaction.getAmount()));
        // Ignore command's ID, and insert in the table with freshly generated ID
        bankTransaction.setId(null);
        taskExecutor.execute(() -> btRepo.save(bankTransaction));
        return ResponseEntity.accepted().build();
    }
}
