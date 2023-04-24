package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class CompanyTypePatch {
    private Boolean manufacturer;
    private Boolean wholesaler_distributor;
    private Boolean retailer;
    private Boolean online_retailer_reseller;
    private Boolean broker;

    public Boolean getBroker() {
        return broker;
    }

    public void setBroker(Boolean broker) {
        this.broker = broker;
    }

    public CompanyTypePatch() {
    }

    public CompanyTypePatch(Boolean manufacturer, Boolean wholesaler_distributor, Boolean retailer, Boolean online_retailer_reseller, Boolean broker) {
        this.manufacturer = manufacturer;
        this.wholesaler_distributor = wholesaler_distributor;
        this.retailer = retailer;
        this.online_retailer_reseller = online_retailer_reseller;
        this.broker = broker;
    }

    public CompanyTypePatch(Boolean wholesaler_distributor, Boolean retailer, Boolean online_retailer_reseller) {
        this.wholesaler_distributor = wholesaler_distributor;
        this.retailer = retailer;
        this.online_retailer_reseller = online_retailer_reseller;
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
}
