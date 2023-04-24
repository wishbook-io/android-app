package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 25/4/17.
 */
public class CompanyProfile {

    private String push_downstream;
    private String id;
    private String name;
    private String country;
    private String email;
    private String state;
    private String city;
    private String state_name;
    private String city_name;
    private String phone_number;
    private CompanyGroupFlag company_group_flag;
    private Boolean company_type_filled;
    private Boolean buyers_assigned_to_salesman;
    private boolean is_profile_set;
    private String first_name;
    private String last_name;
    private String refered_by;
    private String paytm_phone_number;
    private String branch_ref_link;
    private boolean order_disabled;

    private Advancedcompanyprofile advancedcompanyprofile;


    public String getBranch_ref_link() {
        return branch_ref_link;
    }

    public void setBranch_ref_link(String branch_ref_link) {
        this.branch_ref_link = branch_ref_link;
    }

    public String getPaytm_phone_number() {
        return paytm_phone_number;
    }

    public void setPaytm_phone_number(String paytm_phone_number) {
        this.paytm_phone_number = paytm_phone_number;
    }

    public Boolean getBuyers_assigned_to_salesman() {
        return buyers_assigned_to_salesman;
    }

    public void setBuyers_assigned_to_salesman(Boolean buyers_assigned_to_salesman) {
        this.buyers_assigned_to_salesman = buyers_assigned_to_salesman;
    }

    public Boolean getCompany_type_filled() {
        return company_type_filled;
    }

    public void setCompany_type_filled(Boolean company_type_filled) {
        this.company_type_filled = company_type_filled;
    }

    public CompanyGroupFlag getCompany_group_flag() {
        return company_group_flag;
    }

    public void setCompany_group_flag(CompanyGroupFlag company_group_flag) {
        this.company_group_flag = company_group_flag;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPush_downstream() {
        return push_downstream;
    }

    public void setPush_downstream(String push_downstream) {
        this.push_downstream = push_downstream;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean is_profile_set() {
        return is_profile_set;
    }

    public void setIs_profile_set(boolean is_profile_set) {
        this.is_profile_set = is_profile_set;
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

    public String getRefered_by() {
        return refered_by;
    }

    public void setRefered_by(String refered_by) {
        this.refered_by = refered_by;
    }

    public Advancedcompanyprofile getAdvancedcompanyprofile() {
        return advancedcompanyprofile;
    }

    public void setAdvancedcompanyprofile(Advancedcompanyprofile advancedcompanyprofile) {
        this.advancedcompanyprofile = advancedcompanyprofile;
    }

    public boolean isOrder_disabled() {
        return order_disabled;
    }

    public void setOrder_disabled(boolean order_disabled) {
        this.order_disabled = order_disabled;
    }
}
