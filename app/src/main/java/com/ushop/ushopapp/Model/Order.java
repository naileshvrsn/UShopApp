package com.ushop.ushopapp.Model;

import java.util.ArrayList;
import java.util.Date;

public class Order {

    //private String orderId;
    private String orderDate;
    //user details
    private String name;
    private String street;
    private String suburb;
    private String city;
    private String postalCode;
    //orderDetails
    private double subtotal;
    private double shipping;
    private double discount;
    private double total;
    private int productsCount;
    private String orderStatus;

    public Order(){}

//    public Order(String orderId, Date orderDate, String name, String street, String suburb, String city, String postalCode,
//                 double subtotal, double shipping, double discount, double total, String orderStatus) {
//        this.orderId = orderId;
//        this.orderDate = orderDate;
//        this.name = name;
//        this.street = street;
//        this.suburb = suburb;
//        this.city = city;
//        this.postalCode = postalCode;
//        this.subtotal = subtotal;
//        this.shipping = shipping;
//        this.discount = discount;
//        this.total = total;
//        this.orderStatus = orderStatus;
//
//    }

    public Order(String orderDate, String name, String street, String suburb, String city, String postalCode,
                 double subtotal, double shipping, double discount, double total, int productsCount,String orderStatus) {
        this.orderDate = orderDate;
        this.name = name;
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.postalCode = postalCode;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.discount = discount;
        this.total = total;
        this.productsCount= productsCount;
        this.orderStatus = orderStatus;
    }

//    public String getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }
}
