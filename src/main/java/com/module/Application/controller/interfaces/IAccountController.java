package com.module.Application.controller.interfaces;

import com.module.Application.classes.Money;
import com.module.Application.models.Operation;
import com.module.Application.controller.dto.accounts.OperationDto;

import java.security.Principal;

public interface IAccountController {

    /** Method to check any account balance **/
    public Money checkBalance(Long id, Principal principal);

    /** Route to make transfers between accounts in database **/
    public Operation transfer(OperationDto operationDto, Principal principal);

    /** Route to make transfers to third-party accounts from normal accounts **/
    public Operation transferToThirdParty(String hashKey, String secretKey, OperationDto operationDto);

    /** Route to make transfers from third-party accounts to normal accounts **/
    public Operation transferFromThirdParty(String hashKey, String secretKey, OperationDto operationDto);
}
