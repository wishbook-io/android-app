package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 21/11/16.
 */
public class InventoryAddStock {
    private String product;
    private String product_title;
    private int quantity;
    private String adjustment_type;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAdjustment_type() {
        return adjustment_type;
    }

    public void setAdjustment_type(String adjustment_type) {
        this.adjustment_type = adjustment_type;
    }

    public InventoryAddStock(String product_title, String product, int quantity, String adjustment_type) {
        this.product = product;
        this.product_title = product_title;
        this.quantity = quantity;
        this.adjustment_type = adjustment_type;
    }
}
