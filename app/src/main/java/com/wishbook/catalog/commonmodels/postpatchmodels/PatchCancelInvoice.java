package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 12/8/17.
 */

public class PatchCancelInvoice {
    String id;
    String status;

    public PatchCancelInvoice(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
