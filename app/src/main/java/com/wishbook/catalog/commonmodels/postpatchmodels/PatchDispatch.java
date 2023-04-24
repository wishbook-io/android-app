package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchDispatch {
    String id;
    String processing_status;
    String tracking_details;
    String dispatch_date;
    String mode;
    String tracking_number;
    String transporter_courier;

    public PatchDispatch(String id, String processing_status, String tracking_details, String dispatch_date) {
        this.id = id;
        this.processing_status = processing_status;
        this.tracking_details = tracking_details;
        this.dispatch_date = dispatch_date;
    }

    public PatchDispatch(String id, String processing_status, String dispatch_date, String mode, String tracking_number, String transporter_courier) {
        this.id = id;
        this.processing_status = processing_status;
        this.dispatch_date = dispatch_date;
        this.mode = mode;
        this.tracking_number = tracking_number;
        this.transporter_courier = transporter_courier;
    }

    public PatchDispatch() {
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    public String getTracking_details() {
        return tracking_details;
    }

    public void setTracking_details(String tracking_details) {
        this.tracking_details = tracking_details;
    }

    public String getDispatch_date() {
        return dispatch_date;
    }

    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public String getTransporter_courier() {
        return transporter_courier;
    }

    public void setTransporter_courier(String transporter_courier) {
        this.transporter_courier = transporter_courier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
