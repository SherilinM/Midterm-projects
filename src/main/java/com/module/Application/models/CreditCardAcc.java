package com.module.Application.models;

import com.module.Application.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@PrimaryKeyJoinColumn(name="id")
public class CreditCardAcc extends Account {

    /** Propiedades **/

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency"))
    })
    private Money creditLimit;

    @Column(columnDefinition = "DECIMAL(5,4)")
    private BigDecimal interestRate;

    /** Constructor **/

    /** Constructor vacio **/
    public CreditCardAcc() {
    }

    /** Default Constructor con dueño principal y secundario **/
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance) {
        super(primaryOwner, secondaryOwner, balance);
        setDefaultCreditLimit();
        setDefaultInterestRate();
    }

    /** Default Constructor with only primary owner **/
    public CreditCardAcc(AccountHolder primaryOwner, Money balance) {
        super(primaryOwner, balance);
        setDefaultCreditLimit();
        setDefaultInterestRate();
    }

    /** Constructor con limite de credito y tasa de interés especifico, con dueño principal y secundario **/
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Money creditLimit, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, balance);
        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
    }

    /** Constructor con limite de credito al propietario principal **/
    public CreditCardAcc(AccountHolder primaryOwner, Money balance, Money creditLimit, BigDecimal interestRate) {
        super(primaryOwner, balance);
        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
    }

    /** Getters y Setters **/

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setDefaultCreditLimit() {
        this.creditLimit = new Money(new BigDecimal("100"));
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setDefaultInterestRate() {
        this.interestRate = new BigDecimal("0.2");
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
