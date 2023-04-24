package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.ThumbnailObj;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class Response_catalogAdd {
    String id;
    String brand;
    String title;
    ThumbnailObj thumbnail;
    String view_permission;
    String category;
    String company;
    String picasa_url;
    String picasa_id;
    String total_products;
    String full_catalog_orders_only;
    String sell_full_catalog;

    public Response_catalogAdd(String id, String brand, String title, ThumbnailObj thumbnail, String view_permission, String category, String company, String picasa_url, String picasa_id, String total_products, String full_catalog_orders_only, String sell_full_catalog) {
        this.id = id;
        this.brand = brand;
        this.title = title;
        this.thumbnail = thumbnail;
        this.view_permission = view_permission;
        this.category = category;
        this.company = company;
        this.picasa_url = picasa_url;
        this.picasa_id = picasa_id;
        this.total_products = total_products;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.sell_full_catalog = sell_full_catalog;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ThumbnailObj getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailObj thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getView_permission() {
        return view_permission;
    }

    public void setView_permission(String view_permission) {
        this.view_permission = view_permission;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPicasa_url() {
        return picasa_url;
    }

    public void setPicasa_url(String picasa_url) {
        this.picasa_url = picasa_url;
    }

    public String getPicasa_id() {
        return picasa_id;
    }

    public void setPicasa_id(String picasa_id) {
        this.picasa_id = picasa_id;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }

    public String getSell_full_catalog() {
        return sell_full_catalog;
    }

    public void setSell_full_catalog(String sell_full_catalog) {
        this.sell_full_catalog = sell_full_catalog;
    }

}
