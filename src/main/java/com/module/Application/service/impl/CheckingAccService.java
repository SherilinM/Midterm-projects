package com.module.Application.service.impl;
import com.module.Application.models.Account;
import com.module.Application.models.AccountHolder;
import com.module.Application.models.CheckingAcc;
import com.module.Application.models.StudentCheckingAcc;
import com.module.Application.classes.Money;
import com.module.Application.classes.Utils;
import com.module.Application.controller.dto.accounts.CheckingAccDto;
import com.module.Application.service.interfaces.ICheckingAccService;
import com.module.Application.repository.AccountHolderRepository;
import com.module.Application.repository.CheckingAccRepository;
import com.module.Application.repository.StudentCheckingAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CheckingAccService implements ICheckingAccService {

    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    /** Method to create new Checking Accounts (you have to be ADMIN). If the account holder is
     * less than 24, an Student Check Account is created instead **/
    public Account create(CheckingAccDto checkingAccDto) {
        CheckingAcc checkingAcc;
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(checkingAccDto.getPrimaryOwnerId());
        if (accountHolder.isPresent()){
            if(Utils.calculateYears(accountHolder.get().getDateOfBirth())<24){
                StudentCheckingAcc studentCheckingAcc = new StudentCheckingAcc(
                        accountHolder.get(),
                        new Money(checkingAccDto.getBalance()),
                        checkingAccDto.getSecretKey());
                if (checkingAccDto.getSecondaryOwnerId() != null){
                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(checkingAccDto.getSecondaryOwnerId());
                    if (secondaryOwner.isPresent()){
                        studentCheckingAcc.setSecondaryOwner(secondaryOwner.get());
                    }
                }
                return studentCheckingAccRepository.save(studentCheckingAcc);
            } else {
                checkingAcc = new CheckingAcc (accountHolder.get(),
                        new Money(checkingAccDto.getBalance()),
                        checkingAccDto.getSecretKey());
                if (checkingAccDto.getSecondaryOwnerId() != null){
                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(checkingAccDto.getSecondaryOwnerId());
                    if (secondaryOwner.isPresent()){
                        checkingAcc.setSecondaryOwner(secondaryOwner.get());
                    }
                }
                return checkingAccRepository.save(checkingAcc);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary owner not found");
        }
    }

    /** Method to find all checking accounts (you have to be and ADMIN) **/
    public List<CheckingAcc> showAll(){
        if (checkingAccRepository.findAll().size()==0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No checking accounts found");
        }
        return checkingAccRepository.findAll();
    }

    /** Method to find one checking account by id (you have to be and ADMIN) **/
    public Optional<CheckingAcc> find(Long id){
        if (checkingAccRepository.findById(id).isPresent()){
            return checkingAccRepository.findById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no account with the provided id");
        }
    }
}