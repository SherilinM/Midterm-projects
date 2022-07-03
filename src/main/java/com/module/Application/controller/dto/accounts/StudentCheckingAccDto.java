package com.module.Application.controller.dto.accounts;

import javax.validation.constraints.NotNull;

public class StudentCheckingAccDto extends AccountDto{

    /** --------------------Properties-------------------- **/

    @NotNull
    private String secretKey;

    /** --------------------Constructor---------------------- **/

    public StudentCheckingAccDto() {
    }

    /** -----------------Getters & Setters------------------ **/

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
