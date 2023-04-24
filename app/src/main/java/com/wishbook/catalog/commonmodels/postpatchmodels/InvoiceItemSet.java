package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 11/8/17.
 */

public class InvoiceItemSet {
    String order_item;
    int qty;

    public InvoiceItemSet(String order_item, int qty) {
        this.order_item = order_item;
        this.qty = qty;
    }

    public String getOrder_item() {
        return order_item;
    }

    public void setOrder_item(String order_item) {
        this.order_item = order_item;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
