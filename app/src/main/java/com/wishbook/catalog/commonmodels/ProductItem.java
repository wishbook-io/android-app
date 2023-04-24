package com.wishbook.catalog.commonmodels;

/**
 * Created by nani on 17-04-2016.
 */
public class ProductItem {

    String id;
    String product_title;
    String product_image;
    String quantity;
    String rate;
    String sales_order;
    String product;

    public ProductItem(String id, String product_title, String product_image, String quantity, String rate, String sales_order, String product) {
        this.id = id;
        this.product_title = product_title;
        this.product_image = product_image;
        this.quantity = quantity;
        this.rate = rate;
        this.sales_order = sales_order;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getSales_order() {
        return sales_order;
    }

    public void setSales_order(String sales_order) {
        this.sales_order = sales_order;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
