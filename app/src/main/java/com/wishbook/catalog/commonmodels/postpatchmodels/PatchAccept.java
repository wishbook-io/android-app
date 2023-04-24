package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchAccept {
    String id;
    String processing_status;
    String warehouse;
    String payment_details;
    String seller_extra_discount_percentage;

    public PatchAccept() {
    }

    public PatchAccept(String id, String processing_status) {
        this.id = id;
        this.processing_status = processing_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getSeller_extra_discount_percentage() {
        return seller_extra_discount_percentage;
    }

    public void setSeller_extra_discount_percentage(String seller_extra_discount_percentage) {
        this.seller_extra_discount_percentage = seller_extra_discount_percentage;
    }
}
