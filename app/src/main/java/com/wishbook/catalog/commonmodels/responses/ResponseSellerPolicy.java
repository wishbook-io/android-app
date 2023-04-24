package com.wishbook.catalog.commonmodels.responses;


import java.io.Serializable;
import java.util.ArrayList;

public class ResponseSellerPolicy implements Serializable {

    private String id;

    private String company;

    private String policy_type;

    private String policy;

    private ArrayList<String> language;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPolicy_type() {
        return policy_type;
    }

    public void setPolicy_type(String policy_type) {
        this.policy_type = policy_type;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public ArrayList<String> getLanguage() {
        return language;
    }

    public void setLanguage(ArrayList<String> language) {
        this.language = language;
    }
}
