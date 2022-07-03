package com.module.Application.service.interfaces;

import com.module.Application.models.AccountHolder;
import com.module.Application.controller.dto.users.AccountHolderDto;

public interface IAccountHolderService {

    /** Method to create a new Account Holder **/
    public AccountHolder create(AccountHolderDto accountHolderDto);
}
