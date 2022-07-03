package com.module.Application.service.interfaces;

import com.module.Application.models.Account;
import com.module.Application.models.Admin;
import com.module.Application.models.ThirdParty;
import com.module.Application.controller.dto.accounts.MoneyDto;
import com.module.Application.controller.dto.users.AdminDto;
import com.module.Application.controller.dto.users.ThirdPartyDto;

public interface IAdminService {

    /** Method to create a new Account Holder **/
    public Admin createAdmin(AdminDto adminDto);

    /** Method to create a new Third Party **/
    public ThirdParty createThirdParty(ThirdPartyDto thirdPartyDto);


    public Account modifyBalance(Long id, MoneyDto modifiedBalance);
}
