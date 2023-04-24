package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseProducts {

    private String work;

    private String catalog;

    private String public_price;

    private Image image;

    private String sku;

    private ArrayList<Photos> photos;

    private String id;

    private String title;

    private String price;

    private String eav;

    private String sort_order;

    private String fabric;

    private String set_type_details;

    private String expiry_date;


    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPublic_price() {
        return public_price;
    }

    public void setPublic_price(String public_price) {
        this.public_price = public_price;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ArrayList<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photos> photos) {
        this.photos = photos;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEav() {
        return eav;
    }

    public void setEav(String eav) {
        this.eav = eav;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getSet_type_details() {
        return set_type_details;
    }

    public void setSet_type_details(String set_type_details) {
        this.set_type_details = set_type_details;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }
}
