package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class SellerInvoiceResponse {

    private String seller_invoice_number;

    private ArrayList<String> invoices;

    private String created;

    private String invoice_copy;

    private String modified;

    private String company;

    private String id;

    private String status;

    private boolean isEditMode;

    private ArrayList<SellerInvoice_images> images;



    public String getSeller_invoice_number() {
        return seller_invoice_number;
    }

    public void setSeller_invoice_number(String seller_invoice_number) {
        this.seller_invoice_number = seller_invoice_number;
    }

    public ArrayList<String> getInvoices() {
        return invoices;
    }

    public void setInvoices(ArrayList<String> invoices) {
        this.invoices = invoices;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getInvoice_copy() {
        return invoice_copy;
    }

    public void setInvoice_copy(String invoice_copy) {
        this.invoice_copy = invoice_copy;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
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

    public ArrayList<SellerInvoice_images> getImages() {
        return images;
    }

    public void setImages(ArrayList<SellerInvoice_images> images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }
}
