package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_BuyerCities {

    private String id;
    private String state_name;
    private String city_name;
    private String state;

    public Response_BuyerCities(String id, String stateName, String cityName, String state) {
        this.id = id;
        this.state_name = stateName;
        this.city_name = cityName;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStateName() {
        return state_name;
    }

    public void setStateName(String stateName) {
        this.state_name = stateName;
    }

    public String getCityName() {
        return city_name;
    }

    public void setCityName(String city_name) {
        this.city_name = city_name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
