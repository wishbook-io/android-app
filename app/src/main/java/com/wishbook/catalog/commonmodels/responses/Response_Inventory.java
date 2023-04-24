package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.ProductObj;

/**
 * Created by root on 30/6/17.
 */

public class Response_Inventory {

    String id;
    int in_stock;
    String blocked;
    String open_sale;
    String open_purchase;
    String warehouse;
    ProductObj product;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(int in_stock) {
        this.in_stock = in_stock;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getOpen_sale() {
        return open_sale;
    }

    public void setOpen_sale(String open_sale) {
        this.open_sale = open_sale;
    }

    public String getOpen_purchase() {
        return open_purchase;
    }

    public void setOpen_purchase(String open_purchase) {
        this.open_purchase = open_purchase;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public ProductObj getProduct() {
        return product;
    }

    public void setProduct(ProductObj product) {
        this.product = product;
    }
}
