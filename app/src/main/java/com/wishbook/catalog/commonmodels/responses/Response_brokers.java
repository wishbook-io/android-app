package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by Vigneshkarnika on 23/07/16.
 */
public class Response_brokers {
    String company_name;
    String id;
    String company_id;

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    String brokerage_fees;


    public Response_brokers(String company_name, String id, String company_id) {
        this.company_name = company_name;
        this.id = id;
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }
}

