package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

/**
 * Created by root on 13/4/17.
 */
public class AddBrandDistributor {
    String company;
    ArrayList<String> brand;

    public AddBrandDistributor(ArrayList<String> brand) {
        this.brand = brand;
    }

    public AddBrandDistributor(String company, ArrayList<String> brand) {
        this.company = company;
        this.brand = brand;
    }


    public ArrayList<String> getBrand() {
        return brand;
    }

    public void setBrand(ArrayList<String> brand) {
        this.brand = brand;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


}
