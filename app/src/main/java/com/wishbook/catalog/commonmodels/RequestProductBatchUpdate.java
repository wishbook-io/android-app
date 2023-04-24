package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.List;

public class RequestProductBatchUpdate {

    List<ProductObj> products;

    public List<ProductObj> getProducts() {
        return products;
    }

    public void setProducts(List<ProductObj> products) {
        this.products = products;
    }
}
