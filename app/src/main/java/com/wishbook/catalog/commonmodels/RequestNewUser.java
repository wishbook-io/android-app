package com.wishbook.catalog.commonmodels;


public class RequestNewUser {

    private String invite_group_type;

    private String phone_number;

    private String state;

    private boolean retailer;

    private String country;

    private String city;

    private String first_name;

    private String last_name;

    private String connect_supplier;

    private String invite_as;

    private String company_name;

    private String email;

    private boolean manufacturer;

    private String meeting;

    private boolean online_retailer_reseller;

    private boolean broker;

    private boolean wholesaler_distributor;

    private boolean is_guest_user_registration;

    private String refered_by;

    private String user_group_type;

    private String company_id;





    public boolean isRetailer() {
        return retailer;
    }

    public boolean isManufacturer() {
        return manufacturer;
    }

    public boolean isOnline_retailer_reseller() {
        return online_retailer_reseller;
    }

    public boolean isBroker() {
        return broker;
    }

    public boolean isWholesaler_distributor() {
        return wholesaler_distributor;
    }


    public String getInvite_group_type() {
        return invite_group_type;
    }

    public void setInvite_group_type(String invite_group_type) {
        this.invite_group_type = invite_group_type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean getRetailer() {
        return retailer;
    }

    public void setRetailer(boolean retailer) {
        this.retailer = retailer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getConnect_supplier() {
        return connect_supplier;
    }

    public void setConnect_supplier(String connect_supplier) {
        this.connect_supplier = connect_supplier;
    }

    public String getInvite_as() {
        return invite_as;
    }

    public void setInvite_as(String invite_as) {
        this.invite_as = invite_as;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(boolean manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMeeting() {
        return meeting;
    }

    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }

    public boolean getOnline_retailer_reseller() {
        return online_retailer_reseller;
    }

    public void setOnline_retailer_reseller(boolean online_retailer_reseller) {
        this.online_retailer_reseller = online_retailer_reseller;
    }

    public boolean getBroker() {
        return broker;
    }

    public void setBroker(boolean broker) {
        this.broker = broker;
    }

    public boolean getWholesaler_distributor() {
        return wholesaler_distributor;
    }

    public void setWholesaler_distributor(boolean wholesaler_distributor) {
        this.wholesaler_distributor = wholesaler_distributor;
    }

    public String getUser_group_type() {
        return user_group_type;
    }

    public void setUser_group_type(String user_group_type) {
        this.user_group_type = user_group_type;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public boolean isIs_guest_user_registration() {
        return is_guest_user_registration;
    }

    public void setIs_guest_user_registration(boolean is_guest_user_registration) {
        this.is_guest_user_registration = is_guest_user_registration;
    }

    public String getRefered_by() {
        return refered_by;
    }

    public void setRefered_by(String refered_by) {
        this.refered_by = refered_by;
    }
}
