package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Userprofile {
    String alternate_email;
    String phone_number;
    String user_image;
    String user_approval_status;
    String language;
    String moneysmart_data_status;
    String branch_ref_link;


    public Userprofile() {
    }

    public Userprofile(String alternate_email, String phone_number, String user_image) {
        this.alternate_email = alternate_email;
        this.phone_number = phone_number;
        this.user_image = user_image;
    }


    public String getBranch_ref_link() {
        return branch_ref_link;
    }

    public void setBranch_ref_link(String branch_ref_link) {
        this.branch_ref_link = branch_ref_link;
    }

    public String getAlternate_email() {
        return alternate_email;
    }

    public void setAlternate_email(String alternate_email) {
        this.alternate_email = alternate_email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_approval_status() {
        return user_approval_status;
    }

    public void setUser_approval_status(String user_approval_status) {
        this.user_approval_status = user_approval_status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMoneysmart_data_status() {
        return moneysmart_data_status;
    }

    public void setMoneysmart_data_status(String moneysmart_data_status) {
        this.moneysmart_data_status = moneysmart_data_status;
    }



}
