package com.module.Application.service.impl;
import com.module.Application.classes.Address;
import com.module.Application.enums.SystemRole;
import com.module.Application.models.AccountHolder;
import com.module.Application.models.Role;
import com.module.Application.controller.dto.users.AccountHolderDto;
import com.module.Application.service.interfaces.IAccountHolderService;
import com.module.Application.repository.AccountHolderRepository;
import com.module.Application.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHolderService implements IAccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    /** Method to create a new AccountHolder and associate his role **/
    public AccountHolder create(AccountHolderDto accountHolderDto) {
        AccountHolder accountHolder = new AccountHolder(accountHolderDto.getName(),
                accountHolderDto.getUsername(),
                accountHolderDto.getPassword(),
                accountHolderDto.getDateOfBirth(),
                new Address(accountHolderDto.getPrimaryAddress()));
        // Verify if i have 1 or 2 address and call the appropriate method
        if (accountHolderDto.getMailingAddress()!=null) {
            accountHolder.setMailingAddress(new Address(accountHolderDto.getMailingAddress()));
        }
        accountHolderRepository.save(accountHolder);
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, accountHolder));
        return accountHolder;
    }
}