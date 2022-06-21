package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.AccountModelAssembler;
import com.simplebank.accounts.report.model.AccountAndBalance;
import com.simplebank.accounts.report.model.BankTransaction;
import com.simplebank.accounts.report.model.CustomerAndBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController()
@RequestMapping("rest/v1.0/report")
public class SimpleBankReportController {

    @Autowired
    private SimpleBankReportService simpleBankReportService;
    @Autowired
    private PagedResourcesAssembler<CustomerAndBalance> custResourceAssembler;
    @Autowired
    private PagedResourcesAssembler<AccountAndBalance> accResourceAssembler;
    @Autowired
    private AccountModelAssembler<AccountAndBalance> accModelAssembler;

    @GetMapping("/customers")
//    public CollectionModel<EntityModel<CustomerAndBalance>> getCustomers(Pageable pageable) {
    public PagedModel<EntityModel<CustomerAndBalance>> getCustomers(Pageable pageable) {

        Page<CustomerAndBalance> custPage = simpleBankReportService.findAllCustomers(pageable);

        return custResourceAssembler.toModel(custPage, customer -> EntityModel.of(customer,
                linkTo(methodOn(SimpleBankReportController.class).getAccountsForCustomer(customer.getCustomerId(), pageable)).withRel("customerAccounts")));
    }

    @GetMapping("/customerAccounts/{customerId}")
    public PagedModel<EntityModel<AccountAndBalance>> getAccountsForCustomer(@PathVariable long customerId, Pageable pageable) {
        Page<AccountAndBalance> accountsPage = simpleBankReportService.findAccountsByCustomerId(customerId, pageable);

        return accResourceAssembler.toModel(accountsPage, accModelAssembler);
    }

    @GetMapping("/accountTransactions/{accountId}")
    public PagedModel<EntityModel<BankTransaction>> getTransactionsForAccount(@PathVariable Long accountId, Pageable pageable) {
        return simpleBankReportService.getTransactionsForAccount(accountId, pageable);
    }
}
