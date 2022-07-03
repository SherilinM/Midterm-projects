package com.module.Application.controller.dto.accounts;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SavingsAccDto extends AccountDto {

    /** --------------------Properties-------------------- **/

    @NotNull
    private String secretKey;

    @DecimalMax(value = "1000.00", message = "The minimum balance has to be less than 1000.00")
    @DecimalMin(value = "100.00", message = "The minimum balance has to be more than 100.00")
    private BigDecimal minimumBalance;

    @DecimalMax(value = "0.5", message = "The interest rate has to be less than 0.5")
    @DecimalMin(value = "0" , message = "The interest rate has to be more than 0")
    private BigDecimal interestRate;

    /** --------------------Constructor---------------------- **/

    public SavingsAccDto() {
    }

    /** -----------------Getters & Setters------------------ **/

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}