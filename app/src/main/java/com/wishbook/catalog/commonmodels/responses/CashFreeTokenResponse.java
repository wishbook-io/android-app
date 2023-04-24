package com.wishbook.catalog.commonmodels.responses;

public class CashFreeTokenResponse {

    String status;

    String cftoken;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCftoken() {
        return cftoken;
    }

    public void setCftoken(String cftoken) {
        this.cftoken = cftoken;
    }
}
