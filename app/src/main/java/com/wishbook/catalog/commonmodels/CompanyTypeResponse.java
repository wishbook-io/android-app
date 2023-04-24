package com.wishbook.catalog.commonmodels;

public class CompanyTypeResponse {

    private boolean wholesaler_distributor;

    private String city;

    private boolean retailer;

    private String created;

    private String name;

    private String modified;

    private String phone_number;

    private String company;

    private String id;

    private boolean online_retailer_reseller;

    private boolean broker;

    private boolean manufacturer;


    public boolean isWholesaler_distributor() {
        return wholesaler_distributor;
    }

    public void setWholesaler_distributor(boolean wholesaler_distributor) {
        this.wholesaler_distributor = wholesaler_distributor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isRetailer() {
        return retailer;
    }

    public void setRetailer(boolean retailer) {
        this.retailer = retailer;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnline_retailer_reseller() {
        return online_retailer_reseller;
    }

    public void setOnline_retailer_reseller(boolean online_retailer_reseller) {
        this.online_retailer_reseller = online_retailer_reseller;
    }

    public boolean getBroker() {
        return broker;
    }

    public void setBroker(boolean broker) {
        this.broker = broker;
    }

    public boolean isManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(boolean manufacturer) {
        this.manufacturer = manufacturer;
    }
}
