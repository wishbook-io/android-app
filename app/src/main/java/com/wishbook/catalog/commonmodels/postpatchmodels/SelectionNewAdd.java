package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class SelectionNewAdd {
    String name;
    ArrayList<String> products;

    public SelectionNewAdd(String name, ArrayList<String> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }
}
