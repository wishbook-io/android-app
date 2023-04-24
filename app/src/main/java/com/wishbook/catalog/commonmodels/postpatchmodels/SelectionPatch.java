package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class SelectionPatch {
    String id;
    String name;
    ArrayList<String> products;

    public SelectionPatch(String id, ArrayList<String> products) {
        this.id = id;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }
}
