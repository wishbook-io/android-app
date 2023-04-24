package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.home.models.Response_Brands;

/**
 * Created by root on 11/4/17.
 */
public class Response_BrandSell {
    String id;
    String company;
    Response_Brands[] brand;

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

    public Response_Brands[] getBrands() {
        return brand;
    }

    public void setBrands(Response_Brands[] brands) {
        this.brand = brands;
    }
}
