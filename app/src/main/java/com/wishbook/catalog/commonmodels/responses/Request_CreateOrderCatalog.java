package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.ProductQntRate;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 19/04/16.
 */
public class Request_CreateOrderCatalog {
    String order_number;
    String seller_company;
    String company;
    ArrayList<ProductQntRate> items;
    String processing_status;
    String customer_status;
    String broker_company;
    String catalog;
    String brokerage_fees;
    String note;
    public Request_CreateOrderCatalog() {
        this.order_number = "";
        this.seller_company = "";
        this.company = "";
        this.items = new ArrayList<>();
        /*this.processing_status = "";
        this.customer_status = "";*/
        this.broker_company = "";
        this.catalog="";
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getSeller_company() {
        return seller_company;
    }

    public void setSeller_company(String seller_company) {
        this.seller_company = seller_company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public ArrayList<ProductQntRate> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductQntRate> items) {
        this.items = items;
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    public String getCustomer_status() {
        return customer_status;
    }

    public void setCustomer_status(String customer_status) {
        this.customer_status = customer_status;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getcatalog() {
        return catalog;
    }

    public void setcatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
