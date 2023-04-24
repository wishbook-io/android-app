package com.wishbook.catalog.commonmodels.responses;

public class ResponseLeads {

    private String company_id;

    private String company_name;

    private String state_name;

    private String total_enquiry;

    private String city_name;


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

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getTotal_enquiry() {
        return total_enquiry;
    }

    public void setTotal_enquiry(String total_enquiry) {
        this.total_enquiry = total_enquiry;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
}
