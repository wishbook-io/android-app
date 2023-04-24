package com.wishbook.catalog.commonmodels;

public class ImageLoadIssueDevices {

    String device_model;

    String brand;

    public ImageLoadIssueDevices(String device_model, String brand) {
        this.device_model = device_model;
        this.brand = brand;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
