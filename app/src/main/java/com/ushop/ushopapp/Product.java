package com.ushop.ushopapp;


public class Product {

    private String productId;
    private String name;
    private String description;
    private double unitPrice;
    private String category;
    private String store;
    private String imageLocation;


    public Product() {

    }

    // Used when the product has image url
    public Product(String productId,String name, String description, double unitPrice, String category, String store, String imageLocation) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.store = store;
        this.imageLocation = imageLocation;
    }
    // used when the product has an image
    public Product(String name, String description, double unitPrice, String category, String store) {

        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.store = store;
        this.imageLocation = imageLocation;
    }
    public Product(String name, String description, double unitPrice, String category, String store, String imageLocation) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.store = store;
        this.imageLocation = imageLocation;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
