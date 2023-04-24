package com.wishbook.catalog.commonmodels;


public class AuthenticationModel {

    String otp;

    boolean is_password_set;


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean getIs_password_set() {
        return is_password_set;
    }

    public void setIs_password_set(boolean is_password_set) {
        this.is_password_set = is_password_set;
    }
}
