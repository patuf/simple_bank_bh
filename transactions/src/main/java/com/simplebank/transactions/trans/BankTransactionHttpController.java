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

    @PostMapping
    public ResponseEntity<?> createTransactionAsync(@Valid @RequestBody BankTransaction bankTransaction) {
        log.debug(String.format("Consumed Create Message HTTP command for amount -> %s", bankTransaction.getAmount()));
        // Ignore command's ID, and insert in the table with freshly generated ID
        bankTransaction.setId(null);
        taskExecutor.execute(() -> btRepo.save(bankTransaction));
        return ResponseEntity.accepted().build();
    }
}
