package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class ApprovedPatchBuyerStatus {
    String id;
    String status;
    String group_type;


    public ApprovedPatchBuyerStatus(String id, String status, String group_type) {
        this.id = id;
        this.status = status;
        this.group_type = group_type;
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
