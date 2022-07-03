package com.module.Application.models;

import com.module.Application.classes.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class AccountHolder extends User {

    /** Propiedades **/

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfBirth;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "address", column = @Column(name = "primary_address_direction")),
    })
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "address", column = @Column(name = "mailing_address_direction")),
    })
    private Address mailingAddress;

    @OneToMany(mappedBy = "primaryOwner")
    @JsonIgnore
    private List<Account> primaryAccounts;

    @OneToMany(mappedBy = "secondaryOwner")
    @JsonIgnore
    private List<Account> secondaryAccounts;

    /** Constructor **/

    /** Constructor vacio **/
    public AccountHolder() {
    }

    /** Constructor con dirección principal y secundaria **/
    public AccountHolder(String name, String username, String password, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
        super(name, username, password);
        setDateOfBirth(dateOfBirth);
        setPrimaryAddress(primaryAddress);
        setMailingAddress(mailingAddress);
    }

    /** Constructor con solo dirección principañ **/
    public AccountHolder(String name, String username, String password, LocalDate dateOfBirth, Address primaryAddress) {
        super(name, username, password);
        setDateOfBirth(dateOfBirth);
        setPrimaryAddress(primaryAddress);
    }

    /** Getters y Setters **/

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public List<Account> getPrimaryAccounts() {
        return primaryAccounts;
    }

    public void setPrimaryAccounts(List<Account> primaryAccounts) {
        this.primaryAccounts = primaryAccounts;
    }

    public List<Account> getSecondaryAccounts() {
        return secondaryAccounts;
    }

    public void setSecondaryAccounts(List<Account> secondaryAccounts) {
        this.secondaryAccounts = secondaryAccounts;
    }
}
