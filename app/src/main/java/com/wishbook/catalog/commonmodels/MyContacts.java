package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 24/05/16.
 */
public class MyContacts {
String phone;
String company_image;
String name;
String company_name;
String connected_as;
    String chat_user;
    boolean is_visible;
    private String type;
    private ArrayList<String> group_type;
    private String state_name;
    private String city_name;
    private String credit_reference_id;
    private String company_id;



    private boolean isContactChecked=false;

    public MyContacts(String phone, String company_image, String name, String company_name,String connected_as) {
        this.phone = phone;
        this.company_image = company_image;
        this.name = name;
        this.company_name = company_name;
        this.connected_as=connected_as;
    }

    public boolean getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(boolean is_visible) {
        this.is_visible = is_visible;
    }

    public String getChat_user() {
        return chat_user;
    }

    public void setChat_user(String chat_user) {
        this.chat_user = chat_user;
    }

    public String getConnected_as() {
        return connected_as;
    }

    public void setConnected_as(String connected_as) {
        this.connected_as = connected_as;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany_image() {
        return company_image;
    }

    public void setCompany_image(String company_image) {
        this.company_image = company_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
    public void setIsContactChecked(boolean isContactChecked) {
        this.isContactChecked = isContactChecked;
    }

    public boolean isContactChecked() {
        return isContactChecked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getGroup_type() {
        return group_type;
    }

    public void setGroup_type(ArrayList<String> group_type) {
        this.group_type = group_type;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCredit_reference_id() {
        return credit_reference_id;
    }

    public void setCredit_reference_id(String credit_reference_id) {
        this.credit_reference_id = credit_reference_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        MyContacts that = (MyContacts) o;
        return that.phone.equals(phone) &&
                that.company_name.equals(company_name) &&
                that.company_id.equals(company_id);
    }


    @Override
    public String toString() {
        return "MyContacts{" +
                "phone='" + phone + '\'' +
                ", company_image='" + company_image + '\'' +
                ", name='" + name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", connected_as='" + connected_as + '\'' +
                ", chat_user='" + chat_user + '\'' +
                ", is_visible=" + is_visible +
                ", type='" + type + '\'' +
                ", group_type=" + group_type +
                ", state_name='" + state_name + '\'' +
                ", city_name='" + city_name + '\'' +
                ", credit_reference_id='" + credit_reference_id + '\'' +
                ", company_id='" + company_id + '\'' +
                ", isContactChecked=" + isContactChecked +
                '}';
    }
}
