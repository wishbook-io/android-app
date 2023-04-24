package com.wishbook.catalog.commonmodels.postpatchmodels;

import com.wishbook.catalog.commonmodels.ProductQunatityRate;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class SalesOrderCreate {
    String order_number;
    String seller_company;
    String company;
    String catalog;
    String processing_status;
    Boolean is_supplier_approved;
    String buyer_preferred_logistics;
    String ship_to;
    String broker_company;
    String shipping_charges;
    String preffered_shipping_provider;
    String brokerage_fees;
    String note;
    String wbmoney_points_used;
    String shipping_method;
    ArrayList<ProductQunatityRate> items;

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public SalesOrderCreate() {
    }

    public SalesOrderCreate(String order_number, String seller_company, String company, String processing_status, String catalog, ArrayList<ProductQunatityRate> items, Boolean is_supplier_approved) {
        this.order_number = order_number;
        this.seller_company = seller_company;
        this.company = company;
        this.catalog = catalog;
        this.items = items;
        this.is_supplier_approved = is_supplier_approved;
        this.processing_status = processing_status;
    }

    public SalesOrderCreate(String order_number, String seller_company, String company,  String catalog, ArrayList<ProductQunatityRate> items, Boolean is_supplier_approved) {
        this.order_number = order_number;
        this.seller_company = seller_company;
        this.company = company;
        this.catalog = catalog;
        this.items = items;
        this.is_supplier_approved = is_supplier_approved;
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    public Boolean getIs_supplier_approved() {
        return is_supplier_approved;
    }

    public void setIs_supplier_approved(Boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
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


    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public ArrayList<ProductQunatityRate> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductQunatityRate> items) {
        this.items = items;
    }

    public String getBuyer_preferred_logistics() {
        return buyer_preferred_logistics;
    }

    public void setBuyer_preferred_logistics(String buyer_preferred_logistics) {
        this.buyer_preferred_logistics = buyer_preferred_logistics;
    }

    public String getShip_to() {
        return ship_to;
    }

    public void setShip_to(String ship_to) {
        this.ship_to = ship_to;
    }

    public String getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(String shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public String getPreffered_shipping_provider() {
        return preffered_shipping_provider;
    }

    public void setPreffered_shipping_provider(String preffered_shipping_provider) {
        this.preffered_shipping_provider = preffered_shipping_provider;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWbmoney_points_used() {
        return wbmoney_points_used;
    }

    public void setWbmoney_points_used(String wbmoney_points_used) {
        this.wbmoney_points_used = wbmoney_points_used;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
    }
}
