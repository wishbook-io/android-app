package com.wishbook.catalog.commonmodels.responses;


import java.io.Serializable;

public class ResponseSavedFilters implements Serializable {
    private String id;

    private String title;

    private String sub_text;

    private String params;

    private String user;

    private boolean subscribed;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_text() {
        return sub_text;
    }

    public void setSub_text(String sub_text) {
        this.sub_text = sub_text;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}

