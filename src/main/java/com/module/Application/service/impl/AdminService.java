package com.module.Application.service.impl;

import com.module.Application.classes.Money;
import com.module.Application.enums.SystemRole;
import com.module.Application.models.*;
import com.module.Application.controller.dto.accounts.MoneyDto;
import com.module.Application.controller.dto.users.AdminDto;
import com.module.Application.controller.dto.users.ThirdPartyDto;
import com.module.Application.repository.AccountRepository;
import com.module.Application.repository.AdminRepository;
import com.module.Application.repository.RoleRepository;
import com.module.Application.repository.ThirdPartyRepository;
import com.module.Application.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AdminService implements IAdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AccountRepository accountRepository;


    /** Method to create a new Admin and associate his role **/
    public Admin createAdmin(AdminDto adminDto) {
        Admin admin = new Admin(adminDto.getName(), adminDto.getUsername(), adminDto.getPassword());
        adminRepository.save(admin);
        roleRepository.save(new Role(SystemRole.ADMIN, admin));
        return admin;
    }

    /** Method to create a new Third-Party **/
    public ThirdParty createThirdParty(ThirdPartyDto thirdPartyDto) {
        ThirdParty thirdParty = new ThirdParty(thirdPartyDto.getName(), thirdPartyDto.getHashKey());
        thirdPartyRepository.save(thirdParty);
        return thirdParty;
    }

    /** Route to modify any account balance (you have to be an ADMIN) **/
    public Account modifyBalance(Long id, MoneyDto modifiedBalance){
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()){
            account.get().setBalance(new Money(modifiedBalance.getAmount()));

            // If the account type is Savings or Checking, verify if after updating balance, amount is below minimum balance
            if (account.get() instanceof CheckingAcc){
                if (account.get().getBalance().getAmount().compareTo(((CheckingAcc) account.get()).getMinimumBalance().getAmount())>=0){
                    ((CheckingAcc) account.get()).setBelowMinimumBalance(false);
                } else {
                    ((CheckingAcc) account.get()).setBelowMinimumBalance(true);
                }
            } else if (account.get() instanceof SavingsAcc){
                if (account.get().getBalance().getAmount().compareTo(((SavingsAcc) account.get()).getMinimumBalance().getAmount())>=0){
                    ((SavingsAcc) account.get()).setBelowMinimumBalance(false);
                } else {
                    ((SavingsAcc) account.get()).setBelowMinimumBalance(true);
                }
            }

            return accountRepository.save(account.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
    }

}