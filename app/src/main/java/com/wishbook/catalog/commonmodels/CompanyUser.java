package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class CompanyUser {
    String id;
    String username;
    String companyname;
    String company_type;
    String brand_added_flag;
    String company;
    String user;
    String deputed_to;
    String deputed_to_name;
    String total_my_catalogs;
    String total_brand_followers;
    String address;



    public CompanyUser(String id, String username, String companyname, String company_type, String brand_added_flag, String company, String user) {
        this.id = id;
        this.username = username;
        this.companyname = companyname;
        this.company_type = company_type;
        this.brand_added_flag = brand_added_flag;
        this.company = company;
        this.user = user;
    }

    public String getDeputed_to() {
        return deputed_to;
    }

    public void setDeputed_to(String deputed_to) {
        this.deputed_to = deputed_to;
    }

    public String getDeputed_to_name() {
        return deputed_to_name;
    }

    public void setDeputed_to_name(String deputed_to_name) {
        this.deputed_to_name = deputed_to_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public String getBrand_added_flag() {
        return brand_added_flag;
    }

    public void setBrand_added_flag(String brand_added_flag) {
        this.brand_added_flag = brand_added_flag;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTotal_my_catalogs() {
        return total_my_catalogs;
    }

    public void setTotal_my_catalogs(String total_my_catalogs) {
        this.total_my_catalogs = total_my_catalogs;
    }

    public String getTotal_brand_followers() {
        return total_brand_followers;
    }

    public void setTotal_brand_followers(String total_brand_followers) {
        this.total_brand_followers = total_brand_followers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
