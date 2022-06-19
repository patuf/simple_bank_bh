package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.AccountModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
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
public class ReportController {

    @Autowired
    private ReportDao reportDao;
    @Autowired
    PagedResourcesAssembler<CustomerAndBalance> custResourceAssembler;
    @Autowired
    PagedResourcesAssembler<AccountAndBalance> accResourceAssembler;
    @Autowired
    AccountModelAssembler<AccountAndBalance> accModelAssembler;

    @GetMapping("/customers")
//    public CollectionModel<EntityModel<CustomerAndBalance>> getCustomers(Pageable pageable) {
    public PagedModel<EntityModel<CustomerAndBalance>> getCustomers(Pageable pageable) {

        Page<CustomerAndBalance> custPage = reportDao.findAllCustomers(pageable);

        return custResourceAssembler.toModel(custPage, customer -> {
            return EntityModel.of(customer,
                    linkTo(methodOn(ReportController.class).getAccounts(customer.getCustomerId(), pageable)).withRel("customerAccounts"));
        });
    }

    @GetMapping("/customerAccounts/{customerId}")
    public CollectionModel<EntityModel<AccountAndBalance>> getAccounts(@PathVariable long customerId, Pageable pageable) {
        Page<AccountAndBalance> accountsPage = reportDao.findAccountByCustomerId(customerId, pageable);

        return accResourceAssembler.toModel(accountsPage, accModelAssembler::toModel);
    }

    @GetMapping("/accountTransactions/{accountId}")
    public PagedModel<?> getTransactions(@PathVariable Long accountId, Pageable pageable) {
        return null;
    }

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }
}
