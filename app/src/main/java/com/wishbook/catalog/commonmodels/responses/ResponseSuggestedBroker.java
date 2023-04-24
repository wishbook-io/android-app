package com.wishbook.catalog.commonmodels.responses;


public class ResponseSuggestedBroker {

    private String buyer_id;

    private String company_name;

    private String company_phone_number;

    private String company;

    private String company_chat_user;

    private String buying_company_name;


    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_phone_number() {
        return company_phone_number;
    }

    public void setCompany_phone_number(String company_phone_number) {
        this.company_phone_number = company_phone_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany_chat_user() {
        return company_chat_user;
    }

    public void setCompany_chat_user(String company_chat_user) {
        this.company_chat_user = company_chat_user;
    }

    public String getBuying_company_name() {
        return buying_company_name;
    }

    public void setBuying_company_name(String buying_company_name) {
        this.buying_company_name = buying_company_name;
    }
}
