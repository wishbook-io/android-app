package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 21/12/16.
 */
public class Countries {
    String id;
    String name;
    String phone_code;

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

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public Countries(String id, String name, String phone_code) {
        this.id = id;
        this.name = name;
        this.phone_code = phone_code;
    }
}
