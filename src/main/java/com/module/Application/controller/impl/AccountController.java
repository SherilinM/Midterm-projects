package com.module.Application.controller.impl;
import com.module.Application.classes.Money;
import com.module.Application.controller.interfaces.IAccountController;
import com.module.Application.controller.dto.accounts.OperationDto;
import com.module.Application.models.Operation;
import com.module.Application.service.impl.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AccountController implements IAccountController {

    @Autowired
    private AccountService accountService;

    /** Route to check any account balance (you have to be an ADMIN or an Account Holder) **/
    @GetMapping("/check-balance/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public Money checkBalance(@PathVariable("accountId") Long id, Principal principal) {
        return accountService.checkBalance(id, principal);
    }

    /** Route to make transfers between accounts in database **/
    @PatchMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public Operation transfer(@RequestBody @Valid OperationDto operationDto, Principal principal){
        return accountService.transfer(operationDto, principal);
    }

    /** Route to make transfers to third-party accounts from normal accounts **/
    @PatchMapping("/transfer/to-third-party/{hashKey}/{secretKey}")
    @ResponseStatus(HttpStatus.OK)
    public Operation transferToThirdParty(@PathVariable String hashKey, @PathVariable String secretKey,
                                          @RequestBody @Valid OperationDto operationDto){
        return accountService.transferToThirdParty(hashKey, secretKey, operationDto);
    }

    /** Route to make transfers from third-party accounts to normal accounts **/
    @PatchMapping("/transfer/from-third-party/{hashKey}/{secretKey}")
    @ResponseStatus(HttpStatus.OK)
    public Operation transferFromThirdParty(@PathVariable String hashKey, @PathVariable String secretKey,
                                            @RequestBody @Valid OperationDto operationDto){
        return accountService.transferFromThirdParty(hashKey, secretKey, operationDto);
    }

}
