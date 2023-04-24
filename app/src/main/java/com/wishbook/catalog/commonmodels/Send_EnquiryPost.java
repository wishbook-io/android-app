package com.wishbook.catalog.commonmodels;

/**
 * Created by tech2 on 23/3/17.
 */

public class Send_EnquiryPost {

    private String buyer_type;

    private String details;

    private String enquiry_catalog;
    private String enquiry_item_type;
    private String enquiry_quantity;

    private String selling_company;
    public String getEnquiry_catalog() {
        return enquiry_catalog;
    }

    public void setEnquiry_catalog(String enquiry_catalog) {
        this.enquiry_catalog = enquiry_catalog;
    }

    public String getEnquiry_item_type() {
        return enquiry_item_type;
    }

    public void setEnquiry_item_type(String enquiry_item_type) {
        this.enquiry_item_type = enquiry_item_type;
    }

    public String getEnquiry_quantity() {
        return enquiry_quantity;
    }

    public void setEnquiry_quantity(String enquiry_quantity) {
        this.enquiry_quantity = enquiry_quantity;
    }

    public String getBuyer_type() {
        return buyer_type;
    }

    public void setBuyer_type(String buyer_type) {
        this.buyer_type = buyer_type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(String selling_company) {
        this.selling_company = selling_company;
    }
}
