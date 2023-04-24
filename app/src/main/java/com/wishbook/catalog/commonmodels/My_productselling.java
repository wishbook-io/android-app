package com.wishbook.catalog.commonmodels;

import java.io.Serializable;

public class My_productselling implements Serializable {

    private String available_sizes;

    private boolean sell_full_catalog;

    private boolean selling;


    public String getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(String available_sizes) {
        this.available_sizes = available_sizes;
    }

    public boolean isSell_full_catalog() {
        return sell_full_catalog;
    }

    public void setSell_full_catalog(boolean sell_full_catalog) {
        this.sell_full_catalog = sell_full_catalog;
    }

    public boolean isSelling() {
        return selling;
    }

    public void setSelling(boolean selling) {
        this.selling = selling;
    }
}
