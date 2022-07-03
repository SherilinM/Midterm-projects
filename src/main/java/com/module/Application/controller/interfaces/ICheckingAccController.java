package com.module.Application.controller.interfaces;

import com.module.Application.models.Account;
import com.module.Application.models.CheckingAcc;
import com.module.Application.controller.dto.accounts.CheckingAccDto;

import java.util.List;
import java.util.Optional;

public interface ICheckingAccController {

    /** Method to create new Checking Account **/
    public Account create(CheckingAccDto checkingAccDto);

    /** Method to find all checking accounts (you have to be and ADMIN) **/
    public List<CheckingAcc> showAll();

    /** Method to find one checking account by id (you have to be and ADMIN) **/
    public Optional<CheckingAcc> find(Long id);
}