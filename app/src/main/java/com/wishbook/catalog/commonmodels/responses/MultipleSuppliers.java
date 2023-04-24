package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;
import java.util.ArrayList;

public class MultipleSuppliers implements Serializable{

    private String relation_id;

    private String company_id;

    private String city_name;

    private boolean trusted_seller;

    private String chat_user;

    private String name;

    private String state_name;

    private String seller_score;

    private boolean is_supplier_approved;

    private String enquiry_id;

    private boolean sell_full_catalog;

    private ArrayList<ResponseSellerPolicy> seller_policy;


    public String getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public boolean getTrusted_seller() {
        return trusted_seller;
    }

    public void setTrusted_seller(boolean trusted_seller) {
        this.trusted_seller = trusted_seller;
    }

    public String getChat_user() {
        return chat_user;
    }

    public void setChat_user(String chat_user) {
        this.chat_user = chat_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getSeller_score() {
        return seller_score;
    }

    public void setSeller_score(String seller_score) {
        this.seller_score = seller_score;
    }

    public boolean is_supplier_approved() {
        return is_supplier_approved;
    }

    public void setIs_supplier_approved(boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
    }

    public ArrayList<ResponseSellerPolicy> getSeller_policy() {
        return seller_policy;
    }

    public void setSeller_policy(ArrayList<ResponseSellerPolicy> seller_policy) {
        this.seller_policy = seller_policy;
    }

    public String getEnquiry_id() {
        return enquiry_id;
    }

    public void setEnquiry_id(String enquiry_id) {
        this.enquiry_id = enquiry_id;
    }

    public boolean isSell_full_catalog() {
        return sell_full_catalog;
    }

    public void setSell_full_catalog(boolean sell_full_catalog) {
        this.sell_full_catalog = sell_full_catalog;
    }
}
