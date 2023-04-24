package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;


public class Response_Broker_Report {

    private String phone_number;

    private String sort_by;

    private String company;

    private String state;

    private String city;

    private String chat_user;

    private String supplier_id;

    private String company_id;


    private ArrayList<Response_Brokerage_Report> brokerage_in;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public ArrayList<Response_Brokerage_Report> getBrokerage_in() {
        return brokerage_in;
    }

    public void setBrokerage_in(ArrayList<Response_Brokerage_Report> brokerage_in) {
        this.brokerage_in = brokerage_in;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSort_by() {
        return sort_by;
    }

    public void setSort_by(String sort_by) {
        this.sort_by = sort_by;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getChat_user() {
        return chat_user;
    }

    public void setChat_user(String chat_user) {
        this.chat_user = chat_user;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }
}
