package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nani on 17-04-2016.
 */
public class SellingCompany {

     int id;
     String name;
     String push_downstream;
     String street_address;
     String pincode;
     String phone_number;
     String email;
     String website;
     String note;
     String status;
     String company_type;
     Object thumbnail;
     String brand_added_flag;
     String discovery_ok;
     String connections_preapproved;
     String no_suppliers;
     String have_salesmen;
     String sell_all_received_catalogs;
     String sell_shared_catalogs;
     String sms_buyers_on_overdue;
     String city;
     String state;
     String country;
     String chat_user;
        String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public SellingCompany(int id, String name, String push_downstream, String street_address, String pincode, String phone_number, String email, String website, String note, String status, String company_type, Object thumbnail, String brand_added_flag, String discovery_ok, String connections_preapproved, String no_suppliers, String have_salesmen, String sell_all_received_catalogs, String sell_shared_catalogs, String sms_buyers_on_overdue, String city, String state, String country, List<String> category, String chat_user) {
        this.id = id;
        this.name = name;
        this.push_downstream = push_downstream;
        this.street_address = street_address;
        this.pincode = pincode;
        this.phone_number = phone_number;
        this.email = email;
        this.website = website;
        this.note = note;
        this.status = status;
        this.company_type = company_type;
        this.thumbnail = thumbnail;
        this.brand_added_flag = brand_added_flag;
        this.discovery_ok = discovery_ok;
        this.connections_preapproved = connections_preapproved;
        this.no_suppliers = no_suppliers;
        this.have_salesmen = have_salesmen;
        this.sell_all_received_catalogs = sell_all_received_catalogs;
        this.sell_shared_catalogs = sell_shared_catalogs;
        this.sms_buyers_on_overdue = sms_buyers_on_overdue;
        this.city = city;
        this.state = state;
        this.country = country;

        this.chat_user=chat_user;
    }

    public String getChat_user() {
        return chat_user;
    }

    public void setChat_user(String chat_user) {
        this.chat_user = chat_user;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPush_downstream() {
        return push_downstream;
    }

    public void setPush_downstream(String push_downstream) {
        this.push_downstream = push_downstream;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public Object getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Object thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBrand_added_flag() {
        return brand_added_flag;
    }

    public void setBrand_added_flag(String brand_added_flag) {
        this.brand_added_flag = brand_added_flag;
    }

    public String getDiscovery_ok() {
        return discovery_ok;
    }

    public void setDiscovery_ok(String discovery_ok) {
        this.discovery_ok = discovery_ok;
    }

    public String getConnections_preapproved() {
        return connections_preapproved;
    }

    public void setConnections_preapproved(String connections_preapproved) {
        this.connections_preapproved = connections_preapproved;
    }

    public String getNo_suppliers() {
        return no_suppliers;
    }

    public void setNo_suppliers(String no_suppliers) {
        this.no_suppliers = no_suppliers;
    }

    public String getHave_salesmen() {
        return have_salesmen;
    }

    public void setHave_salesmen(String have_salesmen) {
        this.have_salesmen = have_salesmen;
    }

    public String getSell_all_received_catalogs() {
        return sell_all_received_catalogs;
    }

    public void setSell_all_received_catalogs(String sell_all_received_catalogs) {
        this.sell_all_received_catalogs = sell_all_received_catalogs;
    }

    public String getSell_shared_catalogs() {
        return sell_shared_catalogs;
    }

    public void setSell_shared_catalogs(String sell_shared_catalogs) {
        this.sell_shared_catalogs = sell_shared_catalogs;
    }

    public String getSms_buyers_on_overdue() {
        return sms_buyers_on_overdue;
    }

    public void setSms_buyers_on_overdue(String sms_buyers_on_overdue) {
        this.sms_buyers_on_overdue = sms_buyers_on_overdue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
