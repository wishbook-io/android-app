package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 26/7/17.
 */

public class CompanyGroupFlag {
    public String id;
    public Boolean manufacturer;
    public Boolean wholesaler_distributor;
    public Boolean retailer;
    public Boolean online_retailer_reseller;
    public Boolean broker;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Boolean manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getWholesaler_distributor() {
        return wholesaler_distributor;
    }

    public void setWholesaler_distributor(Boolean wholesaler_distributor) {
        this.wholesaler_distributor = wholesaler_distributor;
    }

    public Boolean getRetailer() {
        return retailer;
    }

    public void setRetailer(Boolean retailer) {
        this.retailer = retailer;
    }

    public Boolean getOnline_retailer_reseller() {
        return online_retailer_reseller;
    }

    public void setOnline_retailer_reseller(Boolean online_retailer_reseller) {
        this.online_retailer_reseller = online_retailer_reseller;
    }

    public Boolean getBroker() {
        return broker;
    }

    public void setBroker(Boolean broker) {
        this.broker = broker;
    }
}
