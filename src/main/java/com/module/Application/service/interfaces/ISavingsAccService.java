package com.module.Application.service.interfaces;

import com.module.Application.models.SavingsAcc;
import com.module.Application.controller.dto.accounts.SavingsAccDto;

import java.util.List;
import java.util.Optional;

public interface ISavingsAccService {

    /** Method to create new Savings Account **/
    public SavingsAcc create(SavingsAccDto savingsAccDto);

    /** Method to find all savings accounts (you have to be and ADMIN) **/
    public List<SavingsAcc> showAll();

    /** Method to find one savings account by id (you have to be and ADMIN) **/
    public Optional<SavingsAcc> find(Long id);
}
