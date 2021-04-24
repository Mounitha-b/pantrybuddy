package com.pantrybuddy.activity;

public class UserProduct {
    private String productName;
    private String manufacturer;
    private String expiryDate;
    private int count;

    public UserProduct(String productName, String manufacturer, String expiryDate, int count ){
        this.productName = productName;
        this.manufacturer = manufacturer;
        this.expiryDate = expiryDate;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }
}
