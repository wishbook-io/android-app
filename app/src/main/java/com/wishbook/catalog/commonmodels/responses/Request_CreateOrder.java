package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

import com.wishbook.catalog.commonmodels.ProductQntRate;

/**
 * Created by Vigneshkarnika on 19/04/16.
 */
public class Request_CreateOrder {
    String order_number;
    String seller_company;
    String company;
    ArrayList<ProductQntRate> items;
    String broker_company;
    String selection;
    String processing_status;
    public Request_CreateOrder() {
        this.order_number = "";
        this.seller_company = "";
        this.company = "";
        this.items = new ArrayList<>();

        this.broker_company = "";
        this.selection="";
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


    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }
}
