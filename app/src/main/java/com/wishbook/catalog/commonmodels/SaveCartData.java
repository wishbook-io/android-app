package com.wishbook.catalog.commonmodels;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveCartData implements Serializable {

    public String id;
    public Products products;
    public String qty;

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Products getProducts() {
        if (products == null) {
            products = new Products();
        }
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public static class Products implements Serializable {
        public ArrayList<String> id = new ArrayList<>();

        public ArrayList<String> getId() {
            if (id == null) {
                id = new ArrayList<>();
            }
            return id;
        }

        public void setId(ArrayList<String> id) {
            this.id = id;
        }
    }

}

