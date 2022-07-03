package com.module.Application.controller.dto.accounts;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class CreditCardAccDto extends AccountDto {

    /** --------------------Properties-------------------- **/

    @DecimalMax(value = "1000000.00", message = "The credit limit has to be less than 100000.00")
    @DecimalMin(value = "100.00", message = "The credit limit has to be more than 100.00")
    private BigDecimal creditLimit;

    @DecimalMax(value = "0.2", message = "The interest rate has to be less than 0.2")
    @DecimalMin(value = "0.1", message = "The interest rate has to be more than 0.1")
    private BigDecimal interestRate;

    /** --------------------Constructor---------------------- **/

    public CreditCardAccDto() {
    }

    /** -----------------Getters & Setters------------------ **/

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}