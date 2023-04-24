package com.wishbook.catalog.login.models;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Response_Cities {
    String id;
    String state_name;
    String city_name;
    String state;

    public Response_Cities(String id, String state_name, String city_name, String state) {
        this.id = id;
        this.state_name = state_name;
        this.city_name = city_name;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
