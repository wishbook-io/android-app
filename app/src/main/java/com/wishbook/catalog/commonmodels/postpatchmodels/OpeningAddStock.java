package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 1/7/17.
 */

public class OpeningAddStock {
    String product;
    String product_title;
    int in_stock;

    public OpeningAddStock(String product, String product_title, int in_stock) {
        this.product = product;
        this.product_title = product_title;
        this.in_stock = in_stock;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public int getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(int in_stock) {
        this.in_stock = in_stock;
    }
}
