package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class ResponsePublicSeller {

    private String thumbnail;

    private String phone_number;

    private String email;

    private String chat_admin_user;

    private String name;

    private ArrayList<Response_buyingorder> selling_order;

    private ArrayList<ResponseSellerPolicy> seller_policy;

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Response_buyingorder> getSelling_order() {
        return selling_order;
    }

    public void setSelling_order(ArrayList<Response_buyingorder> selling_order) {
        this.selling_order = selling_order;
    }

    public String getChat_admin_user() {
        return chat_admin_user;
    }

    public void setChat_admin_user(String chat_admin_user) {
        this.chat_admin_user = chat_admin_user;
    }

    public ArrayList<ResponseSellerPolicy> getSeller_policy() {
        return seller_policy;
    }

    public void setSeller_policy(ArrayList<ResponseSellerPolicy> seller_policy) {
        this.seller_policy = seller_policy;
    }
}
