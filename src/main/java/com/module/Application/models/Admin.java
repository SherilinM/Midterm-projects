package com.module.Application.models;

import javax.persistence.Entity;

@Entity
public class Admin extends User {

    /** Propiedades **/

    /** Constructor vacio **/
    public Admin() {
    }

    /** Constructor **/
    public Admin(String name, String username, String password) {
        super(name, username, password);
    }

}
