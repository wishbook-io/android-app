package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by tech2 on 29/7/17.
 */
public class RejectBuyer {
    String id;
    String status;

    public RejectBuyer(String id, String status) {
        this.id = id;
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
