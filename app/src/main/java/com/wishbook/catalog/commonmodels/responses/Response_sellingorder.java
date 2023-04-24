package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 17/04/16.
 */
public class Response_sellingorder implements Serializable {

    String id;
    String order_number;
    String company;
    String total_rate;
    String date;
    String time;
    String processing_status;
    String customer_status;
    String user;
    ArrayList<String> items;
    String sales_image;
    String sales_image_2;
    String sales_image_3;
    String purchase_image;
    String company_name;
    String seller_company;
    String seller_company_name;
    String note;
    String total_products;
    String tracking_details="";
    String supplier_cancel="";
    String buyer_cancel="";
    String payment_details="";
    String payment_date="";
    String dispatch_date="";
    String broker_company="";
    String broker_company_name="";
    String seller_ref="";
    Boolean selected = false ;
    String buyer_table_id="";
    String payment_status ="";
    String brokerage_fees = "";
    String brokerage ="";
    ArrayList<String> images;
    boolean isBrokerage = false;
    String buyer_credit_rating;






    public String getBuyer_credit_rating() {
        return buyer_credit_rating;
    }

    public void setBuyer_credit_rating(String buyer_credit_rating) {
        this.buyer_credit_rating = buyer_credit_rating;
    }

    public String getBuyer_table_id() {
        return buyer_table_id;
    }

    public void setBuyer_table_id(String buyer_table_id) {
        this.buyer_table_id = buyer_table_id;
    }

    public String getSales_image_2() {
        return sales_image_2;
    }

    public void setSales_image_2(String sales_image_2) {
        this.sales_image_2 = sales_image_2;
    }

    public String getSales_image_3() {
        return sales_image_3;
    }

    public void setSales_image_3(String sales_image_3) {
        this.sales_image_3 = sales_image_3;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Response_sellingorder(String id) {
        this.id = id;
    }

    public String getTracking_details() {
        return tracking_details;
    }

    public void setTracking_details(String tracking_details) {
        this.tracking_details = tracking_details;
    }

    public String getSupplier_cancel() {
        return supplier_cancel;
    }

    public void setSupplier_cancel(String supplier_cancel) {
        this.supplier_cancel = supplier_cancel;
    }

    public String getBuyer_cancel() {
        return buyer_cancel;
    }

    public void setBuyer_cancel(String buyer_cancel) {
        this.buyer_cancel = buyer_cancel;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getDispatch_date() {
        return dispatch_date;
    }

    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getBroker_company_name() {
        return broker_company_name;
    }

    public void setBroker_company_name(String broker_company_name) {
        this.broker_company_name = broker_company_name;
    }

    public String getSeller_ref() {
        return seller_ref;
    }

    public void setSeller_ref(String seller_ref) {
        this.seller_ref = seller_ref;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public void setTotal_rate(String total_rate) {
        this.total_rate = total_rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public String getSales_image() {
        return sales_image;
    }

    public void setSales_image(String sales_image) {
        this.sales_image = sales_image;
    }

    public String getPurchase_image() {
        return purchase_image;
    }

    public void setPurchase_image(String purchase_image) {
        this.purchase_image = purchase_image;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getSeller_company() {
        return seller_company;
    }

    public void setSeller_company(String seller_company) {
        this.seller_company = seller_company;
    }

    public String getSeller_company_name() {
        return seller_company_name;
    }

    public void setSeller_company_name(String seller_company_name) {
        this.seller_company_name = seller_company_name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public boolean isBrokerage() {
        return isBrokerage;
    }

    public void setBrokerage(boolean brokerage) {
        isBrokerage = brokerage;
    }

    public String getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(String brokerage) {
        this.brokerage = brokerage;
    }
}
