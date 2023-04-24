package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_Branches {

    private String id;
    private Response_BuyerCities city;
    private Response_BuyerStates state;
    private String name;
    private String street_address;
    private String pincode;
    private String phone_number;
    private String company;

    public Response_Branches(String id, Response_BuyerCities city, Response_BuyerStates state, String name, String street_address, String pincode, String phone_number, String company) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.name = name;
        this.street_address = street_address;
        this.pincode = pincode;
        this.phone_number = phone_number;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Response_BuyerCities getCity() {
        return city;
    }

    public void setCity(Response_BuyerCities city) {
        this.city = city;
    }

    public Response_BuyerStates getState() {
        return state;
    }

    public void setState(Response_BuyerStates state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return street_address;
    }

    public void setStreetAddress(String street_address) {
        this.street_address = street_address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
