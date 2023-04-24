package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 25/4/17.
 */
public class OtpClass {
    String phone_number;
    int country;
    String otp;

    public OtpClass(String phone_number, int country) {
        this.phone_number = phone_number;
        this.country = country;
    }

    public OtpClass(String phone_number, int country, String otp) {
        this.phone_number = phone_number;
        this.country = country;
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }
}
