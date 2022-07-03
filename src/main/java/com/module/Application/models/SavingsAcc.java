package com.module.Application.models;

import com.module.Application.classes.Money;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class SavingsAcc extends Account {

    /** Propiedades **/

    private String secretKey;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "minimum_balance_currency"))
    })
    private Money minimumBalance;

    @Column(columnDefinition = "DECIMAL(5,4)")
    private BigDecimal interestRate;

    private Boolean belowMinimumBalance;

    /** Constructor **/

    /** Constructor vacio **/
    public SavingsAcc() {
    }

    /** Default Constructor con propietario principal y secundario **/
    public SavingsAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey) {
        super(primaryOwner, secondaryOwner, balance);
        setSecretKey(secretKey);
        setDefaultMinimumBalance();
        setDefaultInterestRate();
        setBelowMinimumBalance(false);
    }

    /** Default Constructor con solo el dueño principal **/
    public SavingsAcc(AccountHolder primaryOwner, Money balance, String secretKey) {
        super(primaryOwner, balance);
        setSecretKey(secretKey);
        setDefaultMinimumBalance();
        setDefaultInterestRate();
        setBelowMinimumBalance(false);
    }

    /** Constructor con saldo minimo y tasa de interes con propietario principal y secundario **/
    public SavingsAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Money minimumBalance, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, balance);
        setSecretKey(secretKey);
        setMinimumBalance(minimumBalance);
        setInterestRate(interestRate);
        setBelowMinimumBalance(false);
    }

    /** Constructor con saldo minimo y tasa de interes con el propietario principal  **/
    public SavingsAcc(AccountHolder primaryOwner, Money balance, String secretKey, Money minimumBalance, BigDecimal interestRate) {
        super(primaryOwner, balance);
        setSecretKey(secretKey);
        setMinimumBalance(minimumBalance);
        setInterestRate(interestRate);
        setBelowMinimumBalance(false);
    }

    /** Getters y Setters **/

    public String getSecretKey() {
        return secretKey;
    }

    // clave secreta
    public void setSecretKey(String secretKey) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.secretKey = passwordEncoder.encode(secretKey);
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setDefaultMinimumBalance() {
        this.minimumBalance = new Money(new BigDecimal("1000"));
    }

    // para establcer el saldo minimo
    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setDefaultInterestRate() {
        this.interestRate = new BigDecimal("0.0025");
    }

    // para establecer la tasa de interes
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Boolean getBelowMinimumBalance() {
        return belowMinimumBalance;
    }

    public void setBelowMinimumBalance(Boolean belowMiniminBalance) {
        this.belowMinimumBalance = belowMiniminBalance;
    }
}
