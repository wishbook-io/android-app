package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchCancelOrder {
    String id;
    String processing_status;
    String customer_status;
    String supplier_cancel;
    String buyer_cancel;
    String processing_note;

    public PatchCancelOrder(String id, String processing_status) {
        this.id = id;
        this.processing_status = processing_status;
    }

    public PatchCancelOrder(String id, String processing_status, String customer_status, String supplier_cancel) {
        this.id = id;
        this.processing_status = processing_status;
        this.customer_status = customer_status;
        this.supplier_cancel = supplier_cancel;
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

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    public void setCustomer_status(String customer_status) {
        this.customer_status = customer_status;
    }

    public String getSupplier_cancel() {
        return supplier_cancel;
    }

    public void setSupplier_cancel(String supplier_cancel) {
        this.supplier_cancel = supplier_cancel;
    }

    public String getBuyer_cancel() {
        return buyer_cancel;
    }

    public void setBuyer_cancel(String buyer_cancel) {
        this.buyer_cancel = buyer_cancel;
    }

    public String getProcessing_note() {
        return processing_note;
    }

    public void setProcessing_note(String processing_note) {
        this.processing_note = processing_note;
    }
}
