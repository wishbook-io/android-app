package com.wishbook.catalog.login.models;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Response_States {
    String id;
    String state_name;

    public Response_States(String id, String state_name) {
        this.id = id;
        this.state_name = state_name;
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
}
