package com.module.Application.models;

import com.sun.istack.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ThirdParty {

    /** Propiedades **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String hashKey;

    /** Constructor **/
    public ThirdParty() {}

    public ThirdParty(@NotNull String name, @NotNull String hashKey) {
        setName(name);
        setHashKey(hashKey);
    }


    /** Getters y Setters **/

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashKey() {
        return hashKey;
    }
    public void setHashKey(String hashKey) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.hashKey = passwordEncoder.encode(hashKey);
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
