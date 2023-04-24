package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 19/04/16.
 */
public class ProductQntRate {
    String product;
    String quantity;
    String rate;
    String packing_type;

    public ProductQntRate(String product, String quantity, String rate) {
        this.product = product;
        this.quantity = quantity;
        this.rate = rate;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public String getPacking_type() {
        return packing_type;
    }

    public void setPacking_type(String packing_type) {
        this.packing_type = packing_type;
    }
}
