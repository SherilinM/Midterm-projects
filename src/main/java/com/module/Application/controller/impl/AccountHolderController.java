package com.module.Application.controller.impl;
import com.module.Application.controller.interfaces.IAccountHolderController;
import com.module.Application.controller.dto.users.AccountHolderDto;
import com.module.Application.models.AccountHolder;
import com.module.Application.service.interfaces.IAccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AccountHolderController implements IAccountHolderController {

    @Autowired
    private IAccountHolderService accountHolderService;

    /** Route to create a new Account Holder **/
    @PostMapping("/admin/create-account-holder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder create(@RequestBody @Valid AccountHolderDto accountHolderDto){
        return accountHolderService.create(accountHolderDto);
    }

}