package com.wishbook.catalog.commonmodels.responses;

public class ResponseCatalogViewers {

    private String relationship_id;

    private String company_id;

    private String city_name;

    private String company_name;

    private String state_name;

    private String connected_as;

    private String phone_number;

    public String created_at;
    public String is_manufacturer;
    private String brand_i_own;

    public String getIs_manufacturer() {
        return is_manufacturer;
    }

    public void setIs_manufacturer() {
        this.is_manufacturer = is_manufacturer;
    }
    public String getRelationship_id() {
        return relationship_id;
    }

    public void setRelationship_id(String relationship_id) {
        this.relationship_id = relationship_id;
    }

    public String getBrand_i_own() {
        return brand_i_own;
    }

    public void setBrand_i_own(String brand_i_own) {
        this.brand_i_own = brand_i_own;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at() {
        this.created_at = created_at;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getConnected_as() {
        return connected_as;
    }

    public void setConnected_as(String connected_as) {
        this.connected_as = connected_as;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
