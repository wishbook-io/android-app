package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchVerifyOrder {
    String id;
    String customer_status;

    public PatchVerifyOrder(String id, String customer_status) {
        this.id = id;
        this.customer_status = customer_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_status() {
        return customer_status;
    }

    public void setCustomer_status(String customer_status) {
        this.customer_status = customer_status;
    }
}
