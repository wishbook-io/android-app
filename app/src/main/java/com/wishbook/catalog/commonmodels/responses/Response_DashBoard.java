package com.wishbook.catalog.commonmodels.responses;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 09/06/16.
 */
public class Response_DashBoard {

    public JSONObject profile;
    public ArrayList<SalesInfo> salesmen;
    public CatalogInfo catalogs;
    public OrderInfo orders;
    public Contacts contacts;

    public String total_followers;

    public Response_DashBoard(JSONObject profile, ArrayList<SalesInfo> salesmen, CatalogInfo catalogs, OrderInfo orders, Contacts contacts) {
        this.profile = profile;
        this.salesmen = salesmen;
        this.catalogs = catalogs;
        this.orders = orders;
        this.contacts = contacts;

    }

    public JSONObject getProfile() {
        return profile;
    }

    public void setProfile(JSONObject profile) {
        this.profile = profile;
    }

    public ArrayList<SalesInfo> getSalesmen() {
        return salesmen;
    }

    public void setSalesmen(ArrayList<SalesInfo> salesmen) {
        this.salesmen = salesmen;
    }

    public CatalogInfo getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(CatalogInfo catalogs) {
        this.catalogs = catalogs;
    }

    public OrderInfo getOrderInfo() {
        return orders;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orders = orderInfo;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public String getTotal_followers() {
        return total_followers;
    }

    public void setTotal_followers(String total_followers) {
        this.total_followers = total_followers;
    }
}
