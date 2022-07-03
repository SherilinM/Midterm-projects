package com.module.Application.controller.dto.accounts;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sun.istack.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountDto {

    /** --------------------Properties-------------------- **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long primaryOwnerId;

    private Long secondaryOwnerId;

    @NotNull
    @Past(message="Back to the future was a good film, but is not supported in real life :(")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdDate;

    @NotNull
    @DecimalMin(value = "0.00", message = "The total amount has to be a positive number")
    private BigDecimal balance;

    @NotNull
    private BigDecimal penaltyFee;

    @NotNull
    private String accountStatus;

    /** --------------------Constructor---------------------- **/

    public AccountDto() {
    }

    /** -----------------Getters & Setters------------------ **/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrimaryOwnerId() {
        return primaryOwnerId;
    }

    public void setPrimaryOwnerId(Long primaryOwnerId) {
        this.primaryOwnerId = primaryOwnerId;
    }

    public Long getSecondaryOwnerId() {
        return secondaryOwnerId;
    }

    public void setSecondaryOwnerId(Long secondaryOwnerId) {
        this.secondaryOwnerId = secondaryOwnerId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate() {
        this.createdDate = LocalDate.now();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee() {
        this.penaltyFee = new BigDecimal("40");
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
