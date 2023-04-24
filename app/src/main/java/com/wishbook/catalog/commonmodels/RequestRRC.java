package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Rrc_items;

import java.util.ArrayList;

public class RequestRRC {


    private  String id;


    private String request_type;

    private ArrayList<Rrc_items> rrc_items;

    private String request_reason_text;

    private String invoice;

    private String order;

    private String request_status;

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public ArrayList<Rrc_items> getRrc_items() {
        return rrc_items;
    }

    public void setRrc_items(ArrayList<Rrc_items> rrc_items) {
        this.rrc_items = rrc_items;
    }

    public String getRequest_reason_text() {
        return request_reason_text;
    }

    public void setRequest_reason_text(String request_reason_text) {
        this.request_reason_text = request_reason_text;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
