package com.module.Application.models;

import com.module.Application.classes.Money;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Operation {

    /** Propiedades **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "origin_account")
    private Account originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account")
    private Account destinationAccount;

    @Embedded
    private Money amount;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime transferenceDate;

    /** Constructor **/

    /** Contructor vacio**/
    public Operation() {}

    /** Constructor **/
    public Operation(Account originAccount, Account destinationAccount, Money amount, String name) {
        setOriginAccount(originAccount);
        setDestinationAccount(destinationAccount);
        setAmount(amount);
        setTransferenceDate(LocalDateTime.now());
        setName(name);

    }

    /** Getters y Setters **/

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Account getOriginAccount() {
        return originAccount;
    }
    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }
    public Account getDestinationAccount() {
        return destinationAccount;
    }
    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }
    public Money getAmount() {
        return amount;
    }
    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransferenceDate() {
        return transferenceDate;
    }

    public void setTransferenceDate(LocalDateTime transferenceDate) {
        this.transferenceDate = transferenceDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
