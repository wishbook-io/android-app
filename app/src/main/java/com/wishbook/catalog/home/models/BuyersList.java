package com.wishbook.catalog.home.models;

import java.io.Serializable;

/**
 * Created by root on 20/9/16.
 */
public class BuyersList implements Serializable {
    String company_id;
    String company_name;
    String broker_id;
    String id;
    String buying_person_name;
    String phone_number;
    String brokerage_fees;
    public boolean isContactChecked;


    public BuyersList() {
    }

    public BuyersList(String company_id, String company_name, String broker_id) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.broker_id = broker_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getBroker_id() {
        return broker_id;
    }

    public void setBroker_id(String broker_id) {
        this.broker_id = broker_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuying_person_name() {
        return buying_person_name;
    }

    public void setBuying_person_name(String buying_person_name) {
        this.buying_person_name = buying_person_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isContactChecked() {
        return isContactChecked;
    }

    public void setContactChecked(boolean contactChecked) {
        isContactChecked = contactChecked;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }
}
