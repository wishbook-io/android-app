package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_BuyerStates {

    private String id;
    private String state_name;

    public Response_BuyerStates(String id, String state_name) {
        this.id = id;
        this.state_name = state_name;
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

    public void setStateName(String state_name) {
        this.state_name = state_name;
    }
}
