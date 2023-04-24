package com.wishbook.catalog.login.models;

import com.wishbook.catalog.commonmodels.CompanyUser;
import com.wishbook.catalog.commonmodels.Userprofile;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Response_User {
    String id;
    String username;
    String first_name;
    String last_name;
    String email;
    ArrayList<String> groups;
    CompanyUser companyuser;
    Userprofile userprofile;
    boolean is_superuser;

    public Response_User() {
    }

    public Response_User(String id, String username, String first_name, String last_name, String email, ArrayList<String> groups, CompanyUser companyUser, Userprofile userprofile) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.groups = groups;
        this.companyuser = companyUser;
        this.userprofile = userprofile;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public CompanyUser getCompanyUser() {
        return companyuser;
    }

    public void setCompanyUser(CompanyUser companyUser) {
        this.companyuser = companyUser;
    }

    public Userprofile getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(Userprofile userprofile) {
        this.userprofile = userprofile;
    }

    public boolean isIs_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(boolean is_superuser) {
        this.is_superuser = is_superuser;
    }
}
