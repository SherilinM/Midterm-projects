package com.module.Application.service.interfaces;

import com.module.Application.models.CreditCardAcc;
import com.module.Application.controller.dto.accounts.CreditCardAccDto;

import java.util.List;
import java.util.Optional;

public interface ICreditCardAccService {

    /** Method to create new Savings Account **/
    public CreditCardAcc create(CreditCardAccDto creditCardAccDto);

    /** Method to find all credit card accounts (you have to be and ADMIN) **/
    public List<CreditCardAcc> showAll();

    /** Method to find one credit card account by id (you have to be and ADMIN) **/
    public Optional<CreditCardAcc> find(Long id);
}