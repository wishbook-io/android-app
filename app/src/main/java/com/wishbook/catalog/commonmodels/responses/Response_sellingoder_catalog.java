package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

/**
 * Created by root on 24/9/16.
 */
public class Response_sellingoder_catalog
{

        private String id;

        private String name;

        private String brand;

        private ArrayList<Response_Product> products;

        private String total_products;

        private String catalog_image;

        private String catalog_image_medium;

    public ArrayList<Response_Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Response_Product> products) {
        this.products = products;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getTotal_products ()
        {
            return total_products;
        }

        public void setTotal_products (String total_products)
        {
            this.total_products = total_products;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", name = "+name+", products = "+products+", total_products = "+total_products+"]";
        }

    public String getCatalog_image() {
        return catalog_image;
    }

    public void setCatalog_image(String catalog_image) {
        this.catalog_image = catalog_image;
    }

    public String getCatalog_image_medium() {
        return catalog_image_medium;
    }

    public void setCatalog_image_medium(String catalog_image_medium) {
        this.catalog_image_medium = catalog_image_medium;
    }
}