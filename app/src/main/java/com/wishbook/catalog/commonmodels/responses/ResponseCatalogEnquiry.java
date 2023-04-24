package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class ResponseCatalogEnquiry implements Serializable {

    private String buying_company;

    private String selling_company_name;

    private String catalog_title;

    private String text;

    private String status;

    private String catalog;

    private String item_quantity;

    private String selling_company;

    private String id;

    private String thumbnail;

    private String created;

    private String enquiry_type;

    private String price_range;

    private String total_products;

    private String buyerName;

    private String sellerName;

    private String applogic_conversation_id;

    private String buying_company_chat_user;

    private String selling_company_chat_user;

    private String product_type;

    private String product;

    public ResponseCatalogEnquiry() {
    }

    public ResponseCatalogEnquiry(String buying_company, String catalog_title, String id, String thumbnail, String price_range, String total_products, String buyerName) {
        this.buying_company = buying_company;
        this.catalog_title = catalog_title;
        this.id = id;
        this.thumbnail = thumbnail;
        this.price_range = price_range;
        this.total_products = total_products;
        this.buyerName = buyerName;
    }

    public String getSelling_company_name() {
        return selling_company_name;
    }

    public void setSelling_company_name(String selling_company_name) {
        this.selling_company_name = selling_company_name;
    }

    public String getBuying_company() {
        return buying_company;
    }

    public void setBuying_company(String buying_company) {
        this.buying_company = buying_company;
    }

    public String getCatalog_title() {
        return catalog_title;
    }

    public void setCatalog_title(String catalog_title) {
        this.catalog_title = catalog_title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(String item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(String selling_company) {
        this.selling_company = selling_company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEnquiry_type() {
        return enquiry_type;
    }

    public void setEnquiry_type(String enquiry_type) {
        this.enquiry_type = enquiry_type;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getApplogic_conversation_id() {
        return applogic_conversation_id;
    }

    public void setApplogic_conversation_id(String applogic_conversation_id) {
        this.applogic_conversation_id = applogic_conversation_id;
    }

    public String getBuying_company_chat_user() {
        return buying_company_chat_user;
    }

    public void setBuying_company_chat_user(String buying_company_chat_user) {
        this.buying_company_chat_user = buying_company_chat_user;
    }

    public String getSelling_company_chat_user() {
        return selling_company_chat_user;
    }

    public void setSelling_company_chat_user(String selling_company_chat_user) {
        this.selling_company_chat_user = selling_company_chat_user;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "ResponseCatalogEnquiry{" +
                "buying_company='" + buying_company + '\'' +
                ", catalog_title='" + catalog_title + '\'' +
                ", text='" + text + '\'' +
                ", status='" + status + '\'' +
                ", catalog='" + catalog + '\'' +
                ", item_quantity='" + item_quantity + '\'' +
                ", selling_company='" + selling_company + '\'' +
                ", id='" + id + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", created='" + created + '\'' +
                ", enquiry_type='" + enquiry_type + '\'' +
                ", price_range='" + price_range + '\'' +
                ", total_products='" + total_products + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", sellerName='" + sellerName + '\'' +
                '}';
    }
}
