package com.module.Application.controller.impl;

import com.module.Application.controller.interfaces.ICheckingAccController;
import com.module.Application.controller.dto.accounts.CheckingAccDto;
import com.module.Application.models.Account;
import com.module.Application.models.CheckingAcc;
import com.module.Application.service.interfaces.ICheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class CheckingAccController implements ICheckingAccController {

    @Autowired
    private ICheckingAccService checkingAccService;

    /** Method to create new Checking Accounts (you have to be ADMIN). If the account holder is
     * less than 24, an Student Check Account is created instead **/
    @PostMapping("/admin/create-checkAcc")
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody @Valid CheckingAccDto checkingAccDto) {
        return checkingAccService.create(checkingAccDto);
    }

    /** Method to find all checking accounts (you have to be and ADMIN) **/
    @GetMapping("/admin/checkAcc")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingAcc> showAll() {
        return checkingAccService.showAll();
    }

    /** Method to find one checking account by id (you have to be and ADMIN) **/
    @GetMapping("/admin/checkAcc/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<CheckingAcc> find(@PathVariable Long id) {
        return checkingAccService.find(id);
    }
}