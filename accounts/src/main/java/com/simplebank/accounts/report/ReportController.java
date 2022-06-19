package com.simplebank.accounts.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController()
@RequestMapping("rest/v1.0/report")
public class ReportController {

    @Autowired private ReportDao reportDao;

    @GetMapping("/customers")
    public CollectionModel<EntityModel<CustomerAndBalance>> getCustomers(Pageable pageable) {

        methodOn(ReportController.class).getCustomers(null);
        List<EntityModel<CustomerAndBalance>> customers = reportDao.findAllCustomers(pageable).stream()
                .map(customer -> EntityModel.of(customer,
                        linkTo(methodOn(ReportController.class).getAccounts(customer.getCustomerId(), pageable)).withSelfRel(),
                        linkTo(methodOn(ReportController.class).getCustomers(pageable)).withRel("customers")))
                .collect(Collectors.toList());

        return CollectionModel.of(customers, linkTo(methodOn(ReportController.class).getCustomers(pageable)).withSelfRel());
    }

    @GetMapping("/customerAccounts/{customerId}")
    public CollectionModel<EntityModel<AccountAndBalance>> getAccounts(@PathVariable long customerId, Pageable pageable) {
        return null;
    }

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }
}
