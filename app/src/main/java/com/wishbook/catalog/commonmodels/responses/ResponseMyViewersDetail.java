package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseMyViewersDetail {

    private String id;

    private String title;

    private ArrayList<Viewers> viewers;

    private Image image;

    private String created_at;

    private String brand_name;

    private String total_products;

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

    public ArrayList<Viewers> getViewers() {
        return viewers;
    }

    public void setViewers(ArrayList<Viewers> viewers) {
        this.viewers = viewers;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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


    public class Viewers {
        private String company_id;

        private String company_name;

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        @Override
        public String toString() {
            return "ClassPojo [company_id = " + company_id + ", company_name = " + company_name + "]";
        }
    }
}
