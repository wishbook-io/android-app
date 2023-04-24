package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.io.Serializable;

/**
 * Created by root on 10/8/17.
 */
 public class Shipments implements Serializable {
    String id;
    String datetime;
    String mode;
    String tracking_number;
    String details;
    String transporter_courier;
    String invoice;
    String status;
    String aws_url;
    boolean buyer_req_to_mark_delivered;
    boolean rrc_eligible;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTransporter_courier() {
        return transporter_courier;
    }

    public void setTransporter_courier(String transporter_courier) {
        this.transporter_courier = transporter_courier;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAws_url() {
        return aws_url;
    }

    public void setAws_url(String aws_url) {
        this.aws_url = aws_url;
    }

    public boolean isBuyer_req_to_mark_delivered() {
        return buyer_req_to_mark_delivered;
    }

    public void setBuyer_req_to_mark_delivered(boolean buyer_req_to_mark_delivered) {
        this.buyer_req_to_mark_delivered = buyer_req_to_mark_delivered;
    }

    public boolean isRrc_eligible() {
        return rrc_eligible;
    }

    public void setRrc_eligible(boolean rrc_eligible) {
        this.rrc_eligible = rrc_eligible;
    }
}
