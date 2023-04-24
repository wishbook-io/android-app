package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by Vigneshkarnika on 23/07/16.
 */
public class Response_Invite {
    String id;
    String relationship_type;
    String date;
    String time;
    String company;
    String user;

    public Response_Invite(String id, String relationship_type, String date, String time, String company, String user) {
        this.id = id;
        this.relationship_type = relationship_type;
        this.date = date;
        this.time = time;
        this.company = company;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationship_type() {
        return relationship_type;
    }

    public void setRelationship_type(String relationship_type) {
        this.relationship_type = relationship_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
