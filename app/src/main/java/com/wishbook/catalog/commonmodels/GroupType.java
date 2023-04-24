package com.wishbook.catalog.commonmodels;

/**
 * Created by nani on 17-04-2016.
 */
public class GroupType {

    private String id;
    private String name;

    public GroupType(String id, String name) {
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
