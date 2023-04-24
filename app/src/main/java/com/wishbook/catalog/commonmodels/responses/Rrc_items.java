package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.MyContacts;

import java.io.Serializable;

public class Rrc_items implements Serializable {

    private String catalog_title;

    private String product_sku;

    private String rrc;

    private String shipping;

    private String rate;

    private String order_item;

    private String created;

    private int qty;

    private String modified;

    private String discount;

    private String tax;

    private String id;


    public String getCatalog_title() {
        return catalog_title;
    }

    public void setCatalog_title(String catalog_title) {
        this.catalog_title = catalog_title;
    }

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getRrc() {
        return rrc;
    }

    public void setRrc(String rrc) {
        this.rrc = rrc;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getOrder_item() {
        return order_item;
    }

    public void setOrder_item(String order_item) {
        this.order_item = order_item;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        Rrc_items that = (Rrc_items) o;
        return that.id.equals(id);
    }

}
