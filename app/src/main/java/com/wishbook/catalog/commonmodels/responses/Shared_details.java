package com.wishbook.catalog.commonmodels.responses;

public class Shared_details {

    private String resell_price;

    private String average_price;

    private String actual_price;


    public String getResell_price() {
        return resell_price;
    }

    public void setResell_price(String resell_price) {
        this.resell_price = resell_price;
    }

    public String getActual_price() {
        return actual_price;
    }

    public void setActual_price(String actual_price) {
        this.actual_price = actual_price;
    }

    public String getAverage_price() {
        return average_price;
    }

    public void setAverage_price(String average_price) {
        this.average_price = average_price;
    }
}
