package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 10/8/17.
 */

public class PostKycGst {
    String id;
    String pan;
    String gstin;
    String arn;
    Boolean add_gst_to_price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public Boolean getAdd_gst_to_price() {
        return add_gst_to_price;
    }

    public void setAdd_gst_to_price(Boolean add_gst_to_price) {
        this.add_gst_to_price = add_gst_to_price;
    }
}
