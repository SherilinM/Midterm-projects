package com.module.Application.controller.interfaces;

import com.module.Application.models.Account;
import com.module.Application.models.Admin;
import com.module.Application.models.ThirdParty;
import com.module.Application.controller.dto.accounts.MoneyDto;
import com.module.Application.controller.dto.users.AdminDto;
import com.module.Application.controller.dto.users.ThirdPartyDto;

public interface IAdminController {

    /** Method to create a new Admin **/
    public Admin createAdmin(AdminDto adminDto);

    /** Method to create a new Third Party **/
    public ThirdParty createThirdParty(ThirdPartyDto thirdPartyDto);

    /** Route to modify any account balance (you have to be an ADMIN) **/
    public Account modifyBalance(Long id, MoneyDto modifiedBalance);

}
