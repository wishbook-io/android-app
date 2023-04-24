package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_BuyerGroupType {

    private String id;
    private String name;

    public Response_BuyerGroupType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
