package com.wishbook.catalog.home.models;

/**
 * Created by root on 22/8/16.
 */
public class Add_Catalog_Response
{
    private String id;

    private String category;

    private String title;

    private Thumbnail thumbnail;

    private String view_permission;

    private String picasa_url;

    private String company;

    private String brand;

    private String sell_full_catalog;

    private String picasa_id;

    private String total_products;

    private String catalog_type;

    private String product_id;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCategory ()
    {
        return category;
    }

    public void setCategory (String category)
    {
        this.category = category;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public Thumbnail getThumbnail ()
    {
        return thumbnail;
    }

    public void setThumbnail (Thumbnail thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getView_permission ()
    {
        return view_permission;
    }

    public void setView_permission (String view_permission)
    {
        this.view_permission = view_permission;
    }

    public String getPicasa_url ()
    {
        return picasa_url;
    }

    public void setPicasa_url (String picasa_url)
    {
        this.picasa_url = picasa_url;
    }

    public String getCompany ()
    {
        return company;
    }

    public void setCompany (String company)
    {
        this.company = company;
    }

    public String getBrand ()
    {
        return brand;
    }

    public void setBrand (String brand)
    {
        this.brand = brand;
    }

    public String getSell_full_catalog ()
    {
        return sell_full_catalog;
    }

    public void setSell_full_catalog (String sell_full_catalog)
    {
        this.sell_full_catalog = sell_full_catalog;
    }

    public String getPicasa_id ()
    {
        return picasa_id;
    }

    public void setPicasa_id (String picasa_id)
    {
        this.picasa_id = picasa_id;
    }

    public String getTotal_products ()
    {
        return total_products;
    }

    public void setTotal_products (String total_products)
    {
        this.total_products = total_products;
    }

    public String getCatalog_type() {
        return catalog_type;
    }

    public void setCatalog_type(String catalog_type) {
        this.catalog_type = catalog_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", category = "+category+", title = "+title+", thumbnail = "+thumbnail+", view_permission = "+view_permission+", picasa_url = "+picasa_url+", company = "+company+", brand = "+brand+", sell_full_catalog = "+sell_full_catalog+", picasa_id = "+picasa_id+", total_products = "+total_products+"]";
    }

    public class Thumbnail {
        private String thumbnail_medium;

        private String thumbnail_small;

        private String full_size;

        public String getThumbnail_medium() {
            return thumbnail_medium;
        }

        public void setThumbnail_medium(String thumbnail_medium) {
            this.thumbnail_medium = thumbnail_medium;
        }

        public String getThumbnail_small() {
            return thumbnail_small;
        }

        public void setThumbnail_small(String thumbnail_small) {
            this.thumbnail_small = thumbnail_small;
        }

        public String getFull_size() {
            return full_size;
        }

        public void setFull_size(String full_size) {
            this.full_size = full_size;
        }

        @Override
        public String toString() {
            return "ClassPojo [thumbnail_medium = " + thumbnail_medium + ", thumbnail_small = " + thumbnail_small + ", full_size = " + full_size + "]";
        }
    }
}

