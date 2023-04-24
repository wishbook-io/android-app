package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.home.models.ThumbnailObj;
import com.wishbook.catalog.home.models.Response_Brands;

/**
 * Created by Vigneshkarnika on 27/05/16.
 */
public class Response_catalogapp {
    String id;
    String title;
    Response_Brands brand;
    ThumbnailObj thumbnail;
    String view_permission;
    String[] category;
    String company;
    String picasa_url;
    String picasa_id;
    String total_products;
    String push_user_id;

    public Response_catalogapp(String id, String title, Response_Brands brand, ThumbnailObj thumbnail, String view_permission, String[] category, String company, String picasa_url, String picasa_id, String total_products, String push_user_id) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.thumbnail = thumbnail;
        this.view_permission = view_permission;
        this.category = category;
        this.company = company;
        this.picasa_url = picasa_url;
        this.picasa_id = picasa_id;
        this.total_products = total_products;
        this.push_user_id = push_user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Response_Brands getBrand() {
        return brand;
    }

    public void setBrand(Response_Brands brand) {
        this.brand = brand;
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

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
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

    public String getPush_user_id() {
        return push_user_id;
    }

    public void setPush_user_id(String push_user_id) {
        this.push_user_id = push_user_id;
    }
}
