package com.simplebank.accounts.acc;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class AccountModelAssembler implements RepresentationModelAssembler<CreateAccountRequest, EntityModel<CreateAccountRequest>> {

    @Override
    public EntityModel<CreateAccountRequest> toModel(CreateAccountRequest createAccReq) {

        return EntityModel.of(createAccReq, //
//                linkTo(methodOn(AccountController.class).one(employee.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).createAccount(createAccReq)).withSelfRel());
    }
}
