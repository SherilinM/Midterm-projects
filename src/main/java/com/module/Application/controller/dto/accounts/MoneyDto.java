package com.module.Application.controller.dto.accounts;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class MoneyDto {

    /** --------------------Properties-------------------- **/

    @DecimalMin("0.00")
    private BigDecimal amount;

    public MoneyDto() {
    }

    /** --------------------Constructor---------------------- **/

    public MoneyDto(@DecimalMin("0.00") BigDecimal amount) {
        setAmount(amount);
    }

    /** -----------------Getters & Setters------------------ **/

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
