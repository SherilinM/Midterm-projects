package com.module.Application.controller.interfaces;
import com.module.Application.controller.dto.users.AccountHolderDto;
import com.module.Application.models.AccountHolder;


public interface IAccountHolderController {

    /** Method to create a new Account Holder **/
    public AccountHolder create(AccountHolderDto accountHolderDto);
}