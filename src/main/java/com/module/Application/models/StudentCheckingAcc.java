package com.module.Application.models;

import com.module.Application.classes.Money;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class StudentCheckingAcc extends Account {

    /** Propiedades **/

    private String secretKey;

    /** Constructor **/

    /** Constructor vacio**/
    public StudentCheckingAcc() {
    }

    /** Constructor con dueño principal y secundario **/
    public StudentCheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey) {
        super(primaryOwner, secondaryOwner, balance);
        setSecretKey(secretKey);
    }

    /** Constructor con solo el dueño principal **/
    public StudentCheckingAcc(AccountHolder primaryOwner, Money balance, String secretKey) {
        super(primaryOwner, balance);
        setSecretKey(secretKey);
    }

    /** Getters y Setters**/

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.secretKey = passwordEncoder.encode(secretKey);
    }
}
