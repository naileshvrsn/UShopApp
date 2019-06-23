package com.ushop.ushopapp;

public class Cart {
    private String pid,pname, price, quanity, discount;

    public Cart(){

    }

    public Cart(String pid, String pname, String price, String quanity, String discount) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quanity = quanity;
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuanity() {
        return quanity;
    }

    public void setQuanity(String quanity) {
        this.quanity = quanity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}