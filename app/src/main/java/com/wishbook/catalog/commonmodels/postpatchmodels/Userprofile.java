package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class Userprofile {
    String phone_number;

    public Userprofile(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
