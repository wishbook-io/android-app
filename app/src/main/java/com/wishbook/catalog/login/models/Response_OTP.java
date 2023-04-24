package com.wishbook.catalog.login.models;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Response_OTP {
    String phone_number;
    String created_date;

    public Response_OTP(String phone_number, String created_date) {
        this.phone_number = phone_number;
        this.created_date = created_date;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
