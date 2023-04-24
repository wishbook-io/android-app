package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Response_Country;
import com.wishbook.catalog.commonmodels.responses.Response_Invite;

/**
 * Created by Vigneshkarnika on 29/03/16.
 */
public class InviteeFull {
    String id;
    String invitee_number;
    String invitee_company;
    String invitee_name;
    String status;
    String invite_type;
    Response_Invite invite;
    Response_Country country;
  //  String registered_user;


    public Response_Invite getInvite() {
        return invite;
    }

    public void setInvite(Response_Invite invite) {
        this.invite = invite;
    }

    public Response_Country getCountry() {
        return country;
    }

    public void setCountry(Response_Country country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvitee_number() {
        return invitee_number;
    }

    public void setInvitee_number(String invitee_number) {
        this.invitee_number = invitee_number;
    }

    public String getInvitee_company() {
        return invitee_company;
    }

    public void setInvitee_company(String invitee_company) {
        this.invitee_company = invitee_company;
    }

    public String getInvitee_name() {
        return invitee_name;
    }

    public void setInvitee_name(String invitee_name) {
        this.invitee_name = invitee_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvite_type() {
        return invite_type;
    }

    public void setInvite_type(String invite_type) {
        this.invite_type = invite_type;
    }

//    public String getRegistered_user() {
//        return registered_user;
//    }
//
//    public void setRegistered_user(String registered_user) {
//        this.registered_user = registered_user;
//    }
}
