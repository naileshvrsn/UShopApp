package com.ushop.ushopapp;

import android.widget.ImageView;

public class Product {
    private String name;
    private String description;
    private double unitPrice;
    private String category;
    private String store;
    private String imageLocation;
    private ImageView imageView;

    public Product() {

    }
    

    public Product(String name, String description, double unitPrice, String category, String store, String imageLocation) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.store = store;
        this.imageLocation = imageLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
