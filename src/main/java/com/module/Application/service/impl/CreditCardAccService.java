package com.module.Application.service.impl;
import com.module.Application.models.AccountHolder;
import com.module.Application.models.CreditCardAcc;
import com.module.Application.classes.Money;
import com.module.Application.controller.dto.accounts.CreditCardAccDto;
import com.module.Application.service.interfaces.ICreditCardAccService;
import com.module.Application.repository.AccountHolderRepository;
import com.module.Application.repository.CreditCardAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardAccService implements ICreditCardAccService {

    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    /** Method to create new Credit Card Accounts **/
    public CreditCardAcc create(CreditCardAccDto creditCardAccDto) {
        CreditCardAcc creditCardAcc;
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(creditCardAccDto.getPrimaryOwnerId());
        if(accountHolder.isPresent()){
            creditCardAcc = new CreditCardAcc(accountHolder.get(),
                    new Money(creditCardAccDto.getBalance()));
            if (creditCardAccDto.getSecondaryOwnerId()!=null){
                Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(creditCardAccDto.getSecondaryOwnerId());
                if (secondaryOwner.isPresent()){
                    creditCardAcc.setSecondaryOwner(secondaryOwner.get());
                }
            }
            if (creditCardAccDto.getCreditLimit()!=null){
                creditCardAcc.setCreditLimit(new Money(creditCardAccDto.getCreditLimit()));
            }
            if (creditCardAccDto.getInterestRate()!=null){
                creditCardAcc.setInterestRate(creditCardAccDto.getInterestRate());
            }
            return creditCardAccRepository.save(creditCardAcc);
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary owner not found");
        }
    }

    /** Method to find all credit card accounts (you have to be and ADMIN) **/
    public List<CreditCardAcc> showAll(){
        if (creditCardAccRepository.findAll().size()==0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No checking accounts found");
        }
        return creditCardAccRepository.findAll();
    }

    /** Method to find one credit card account by id (you have to be and ADMIN) **/
    public Optional<CreditCardAcc> find(Long id){
        if (creditCardAccRepository.findById(id).isPresent()){
            return creditCardAccRepository.findById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no account with the provided id");
        }
    }
}