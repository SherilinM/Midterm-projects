package com.module.Application.controller.impl;
import com.module.Application.controller.interfaces.IAdminController;
import com.module.Application.controller.dto.accounts.MoneyDto;
import com.module.Application.controller.dto.users.AdminDto;
import com.module.Application.controller.dto.users.ThirdPartyDto;
import com.module.Application.models.Account;
import com.module.Application.models.Admin;
import com.module.Application.models.ThirdParty;
import com.module.Application.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AdminController implements IAdminController {

    @Autowired
    private IAdminService adminService;

    /** Route to create a new Admin **/
    @PostMapping("/admin/create-new-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin createAdmin(@RequestBody @Valid AdminDto adminDto) {
        return adminService.createAdmin(adminDto);
    }

    /** Route to create a new Third Party **/
    @PostMapping("/admin/create-third-party")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty createThirdParty(@RequestBody @Valid ThirdPartyDto thirdPartyDto) {
        return adminService.createThirdParty(thirdPartyDto);
    }

    /** Route to modify any account balance (you have to be an ADMIN) **/
    @PatchMapping("/admin/modify-balance/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account modifyBalance(@PathVariable("id") Long id, @RequestBody @Valid MoneyDto modifiedBalance){
        return adminService.modifyBalance(id, modifiedBalance);
    }

}