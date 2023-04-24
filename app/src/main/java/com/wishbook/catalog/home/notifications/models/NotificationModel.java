package com.wishbook.catalog.home.notifications.models;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Vigneshkarnika on 05/08/16.
 */
public class NotificationModel {
    String table_id="";
    String push_type="";
    String push_id="";
    String name="";
    String image="";
    String notId="";
    String title="";
    String message="";
    String company_image="";
    String collapse_key="";
    Boolean read;
    String  otherPara;

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationModel(){
    }

    public NotificationModel(String table_id, String push_type, String push_id, String name, String image, String notId, String title, String message, String company_image, String collapse_key, Boolean read, String otherPara) {
        this.table_id = table_id;
        this.push_type = push_type;
        this.push_id = push_id;
        this.name = name;
        this.image = image;
        this.notId = notId;
        this.title = title;
        this.message = message;
        this.company_image = company_image;
        this.collapse_key = collapse_key;
        this.read = read;
        this.otherPara = otherPara;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNotId() {
        return notId;
    }

    public void setNotId(String notId) {
        this.notId = notId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompany_image() {
        return company_image;
    }

    public void setCompany_image(String company_image) {
        this.company_image = company_image;
    }

    public String getCollapse_key() {
        return collapse_key;
    }

    public void setCollapse_key(String collapse_key) {
        this.collapse_key = collapse_key;
    }

    public String getOtherPara() {
        return otherPara;
    }

    public void setOtherPara(String otherPara) {
        this.otherPara = otherPara;
    }
}
