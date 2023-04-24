package com.wishbook.catalog.commonmodels.responses;

public class SharedByMe {
    private String id;
    private String brand_image;
    private String title;
    private String full_catalog_orders_only;
    private String image;
    private String type;
    private String brand_name;
    private String total_products;
    private String is_disable;

    public String getIs_disable() {
        return is_disable;
    }

    public void setIs_disable(String is_disable) {
        this.is_disable = is_disable;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getBrand_image() {
        return brand_image;
    }

    public void setBrand_image(String brand_image) {
        this.brand_image = brand_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", brand_image = " + brand_image + ", title = " + title + ", full_catalog_orders_only = " + full_catalog_orders_only + ", image = " + image + ", type = " + type + ", brand_name = " + brand_name + ", total_products = " + total_products + "]";
    }
}
