package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 20/7/17.
 */

public class CommmonFilterOptionModel {
    int id;
    String request_filter_name;
    String name;
    Boolean selected;

    public CommmonFilterOptionModel(int position,String name, Boolean selected,String request_filter_name) {
        this.id =position;
        this.name = name;
        this.selected = selected;
        this.request_filter_name=request_filter_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequest_filter_name() {
        return request_filter_name;
    }

    public void setRequest_filter_name(String request_filter_name) {
        this.request_filter_name = request_filter_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
