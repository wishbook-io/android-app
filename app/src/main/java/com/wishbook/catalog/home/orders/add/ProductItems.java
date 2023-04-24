package com.wishbook.catalog.home.orders.add;

/**
 * Created by root on 4/2/17.
 */
public class ProductItems {
    String product;
    String rate;
    String quantity;
    int selling_company;

    public ProductItems(String product, String rate, String quantity, int selling_company) {
        this.product = product;
        this.rate = rate;
        this.quantity = quantity;
        this.selling_company = selling_company;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(int selling_company) {
        this.selling_company = selling_company;
    }
}
