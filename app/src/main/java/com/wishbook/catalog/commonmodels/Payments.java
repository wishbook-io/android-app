package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 11/8/17.
 */

public class Payments {
    String id;
    String amount;
    String mode;
    String status;
    String details;
    String datetime;
    String transaction_reference;
    String by_company;
    String to_company;
    String user;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTransaction_reference() {
        return transaction_reference;
    }

    public void setTransaction_reference(String transaction_reference) {
        this.transaction_reference = transaction_reference;
    }

    public String getBy_company() {
        return by_company;
    }

    public void setBy_company(String by_company) {
        this.by_company = by_company;
    }

    public String getTo_company() {
        return to_company;
    }

    public void setTo_company(String to_company) {
        this.to_company = to_company;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
