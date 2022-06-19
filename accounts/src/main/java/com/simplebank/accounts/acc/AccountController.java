package com.simplebank.accounts.acc;

import com.simplebank.accounts.customer.CustomerNotFoundException;
import com.simplebank.accounts.customer.CustomerRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    AccountRepository accRepo;
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    TransactionOutboxRepository troutRepo;
    @Autowired
    AccountModelAssembler assembler;
    @Autowired
    KafkaTemplate<CreateTransactionOutbox, CreateTransactionOutbox> kafka;
    @Autowired
    PagedResourcesAssembler<Account> pagedResourcesAssembler;

    @GetMapping("/byCustomer/{customerId}")
    ResponseEntity<PagedModel<EntityModel<Account>>> getAccountsByUsedId(@PathVariable long customerId, Pageable pageable) {
        Page<Account> accountsPage = accRepo.getAccountsByCustomerId(customerId, pageable);

        PagedModel<EntityModel<Account>> collModel = pagedResourcesAssembler.toModel(accountsPage);

        return new ResponseEntity<>(collModel, HttpStatus.OK);
    }


    @PostMapping
    @Transactional
    ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        customerRepo.findById(request.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
//        customerDataProvider.findById(request.getCustomerId());
        Account newAccount = new Account(request.getCustomerId(), ZonedDateTime.now(), AccountStatus.ACTIVE);
        newAccount = accRepo.save(newAccount);

        if (request.getInitialCredit() != 0) {
            troutRepo.save(new CreateTransactionOutbox(request.getCustomerId(), newAccount.getId(), request.getInitialCredit()));
        }

        EntityModel<CreateAccountRequest> entityModel = assembler.toModel(request);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }


    void publishCreateTransactionCommand(CreateTransactionOutbox command) {
        log.info("Gonna send to kafka now");
//        kafka.send();
    }
}
