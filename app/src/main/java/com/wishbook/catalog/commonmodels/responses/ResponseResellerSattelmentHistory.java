package com.wishbook.catalog.commonmodels.responses;

public class ResponseResellerSattelmentHistory {

    private String amount;

    private String id;

    private String created;

    private String company;

    private String date;

    private String payment_method;

    private String payment_details;

    private String modified;

    private String deep_link_log;

    private String display_text_log;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getDeep_link_log() {
        return deep_link_log;
    }

    public void setDeep_link_log(String deep_link_log) {
        this.deep_link_log = deep_link_log;
    }

    public String getDisplay_text_log() {
        return display_text_log;
    }

    public void setDisplay_text_log(String display_text_log) {
        this.display_text_log = display_text_log;
    }
}
