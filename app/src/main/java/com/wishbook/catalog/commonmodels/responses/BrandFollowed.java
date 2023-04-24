package com.wishbook.catalog.commonmodels.responses;



public class BrandFollowed {

    private String id;

    private String company;

    private String brand;

    private String user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", company = "+company+", brand = "+brand+", user = "+user+"]";
    }
}
