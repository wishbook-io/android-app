package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchBuyerCancelOrder {
    String id;
    String processing_status;
    String customer_status;
    String buyer_cancel;

    public PatchBuyerCancelOrder(String id, String processing_status, String customer_status, String buyer_cancel) {
        this.id = id;
        this.processing_status = processing_status;
        this.customer_status = customer_status;
        this.buyer_cancel = buyer_cancel;
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

}
