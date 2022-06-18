package com.simplebank.accounts.acc;

import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.simplebank.accounts.customer.Customer;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.customer.CustomerNotFoundException;
import com.simplebank.accounts.customer.CustomerRepository;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController()
@RequestMapping("rest/v1.0/account")
public class AccountController {
    @Autowired
    AccountRepository accRepo;
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    AccountModelAssembler assembler;
    @Autowired
    TransactionOutbox outbox;
    @Autowired
    KafkaTemplate<CreateTransactionCommand, CreateTransactionCommand> kafka;

//    @GetMapping(path="/transactions/{userId}")
//    EntityModel<Object> getTransactions(@PathVariable long userId) {
//        Optional<Customer> customer = customerRepo.findById(userId);
//        return EntityModel.of(customer, //
//                linkTo(methodOn(AccountController.class).one(id)).withSelfRel(),
//                linkTo(methodOn(AccountController.class).all()).withRel("employees"));
//        return EntityModel.of(new Object(),linkTo(methodOn(AccountController.class).one(userId)).withSelfRel());
//    }

    @PostMapping
    @Transactional
    ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        customerRepo.findById(request.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
//        customerDataProvider.findById(request.getCustomerId());
        Account newAccount = new Account(request.getCustomerId(), ZonedDateTime.now(), Account.AccountStatus.ACTIVE);
        newAccount = accRepo.save(newAccount);

        if (request.getInitialCredit() != 0) {
            outbox.schedule(getClass()).publishCreateTransactionCommand(new CreateTransactionCommand(request.getCustomerId(), newAccount.getId(), request.getInitialCredit()));
        }

        EntityModel<CreateAccountRequest> entityModel = assembler.toModel(request);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }


    void publishCreateTransactionCommand(CreateTransactionCommand command) {
//        kafka.send();
    }
}
