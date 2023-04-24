package com.wishbook.catalog.login.models;

/**
 * Created by Vigneshkarnika on 07/07/16.
 */
public class CompanyList {
    String id;
    String name;

    public CompanyList(String id, String name) {
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
