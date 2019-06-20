package com.ushop.ushopapp;

import java.util.Calendar;
import java.util.Date;

public class User {
    String name;
    String street;
    String suburb;
    String city;
    String postCode;
    Date dateOfBirth;

    public User() {
    }

    public User(String name, String street, String suburb, String city, String postCode, Date dateOfBirth) {
        this.name = name;
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.postCode = postCode;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
