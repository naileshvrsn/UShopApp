package com.ushop.ushopapp;

public class User {
    String uId;
    String name;
    String street;
    String suburb;
    String city;
    String postCode;

    public User() {
    }

    public User(String name, String street, String suburb, String city, String postCode) {
        this.name = name;
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.postCode = postCode;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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
}
