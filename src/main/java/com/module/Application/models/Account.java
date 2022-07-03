package com.module.Application.models;

import com.module.Application.classes.Money;
import com.module.Application.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {

    /** Propiedades **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(optional = false)
    protected AccountHolder primaryOwner;

    @ManyToOne
    protected AccountHolder secondaryOwner;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate createdDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate lastInterestUpdate;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    protected Money balance;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "penalty_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name ="penalty_fee_currency"))
    })
    protected Money penaltyFee;

    @OneToMany(mappedBy="originAccount")
    @JsonIgnore
    protected List<Operation> sentMoney;

    @JsonIgnore
    @OneToMany(mappedBy="destinationAccount")
    protected List<Operation> receivedMoney;

    @Enumerated(EnumType.STRING)
    protected AccountStatus accountStatus;

    /** Constructor**/

    /** Constructor vacio **/
    public Account() {
    }

    /** Constructor con propietario principal y secundario **/
    public Account(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance) {
        setPrimaryOwner(primaryOwner);
        setSecondaryOwner(secondaryOwner);
        setCreatedDate();
        setLastInterestUpdate(LocalDate.now());
        setBalance(balance);
        setPenaltyFee();
        setAccountStatus(AccountStatus.ACTIVE);
    }

    /** Constructor con un owner primario **/
    public Account(AccountHolder primaryOwner, Money balance) {
        setPrimaryOwner(primaryOwner);
        setCreatedDate();
        setLastInterestUpdate(LocalDate.now());
        setBalance(balance);
        setPenaltyFee();
        setAccountStatus(AccountStatus.ACTIVE);
    }

    /** Getters y Setters **/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate() {
        this.createdDate = LocalDate.now();
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public Money getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee() {
        this.penaltyFee = new Money(new BigDecimal("40"));
    }

    public List<Operation> getSentMoney() {
        return sentMoney;
    }

    public void setSentMoney(List<Operation> sentMoney) {
        this.sentMoney = sentMoney;
    }

    public List<Operation> getReceivedMoney() {
        return receivedMoney;
    }

    public void setReceivedMoney(List<Operation> receivedMoney) {
        this.receivedMoney = receivedMoney;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getLastInterestUpdate() {
        return lastInterestUpdate;
    }

    public void setLastInterestUpdate(LocalDate localdate) {
        this.lastInterestUpdate = localdate;
    }


}
