package com.module.Application.classes;

import javax.persistence.Embeddable;

@Embeddable

public class Address {

    /** Properties **/
    private String address;

    /** Constructor  **/

    public Address(){}

    public Address(String address) {
        setDirection(address);

    }

    /** Getters & Setters **/

    public String getDirection() {return address;}
    public void setDirection(String direction) {this.address = direction;}

}
