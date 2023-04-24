package com.wishbook.catalog.home.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class CatalogObj implements Serializable {
    String id;
    Response_Brands brand;
    ThumbnailObj thumbnail;
    String title;
    String thumbnail_ppoi;
    String view_permission;
    String picasa_id;
    String picasa_url;
    String deleted;
    String company;
    String category;

    public CatalogObj(String id, Response_Brands brand, ThumbnailObj thumbnail, String title, String thumbnail_ppoi, String view_permission, String picasa_id, String picasa_url, String deleted, String company, String category) {
        this.id = id;
        this.brand = brand;
        this.thumbnail = thumbnail;
        this.title = title;
        this.thumbnail_ppoi = thumbnail_ppoi;
        this.view_permission = view_permission;
        this.picasa_id = picasa_id;
        this.picasa_url = picasa_url;
        this.deleted = deleted;
        this.company = company;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail_ppoi() {
        return thumbnail_ppoi;
    }

    public void setThumbnail_ppoi(String thumbnail_ppoi) {
        this.thumbnail_ppoi = thumbnail_ppoi;
    }

    public String getView_permission() {
        return view_permission;
    }

    public void setView_permission(String view_permission) {
        this.view_permission = view_permission;
    }

    public String getPicasa_id() {
        return picasa_id;
    }

    public void setPicasa_id(String picasa_id) {
        this.picasa_id = picasa_id;
    }

    public String getPicasa_url() {
        return picasa_url;
    }

    public void setPicasa_url(String picasa_url) {
        this.picasa_url = picasa_url;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
