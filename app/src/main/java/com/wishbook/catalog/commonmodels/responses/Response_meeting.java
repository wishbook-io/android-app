package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 10/04/16.
 */
public class Response_meeting {
    String id;
    String user;
    String duration;
    String buying_company_name;
    String start_datetime;
    String end_datetime;
    String start_lat;
    String start_long;
    String end_lat;
    String end_long;
    String status;
    String buying_company_ref;
    ArrayList<String> salesorder;
    String total_products;
    String note;
    String buyer_table_id;
    String buyer_name_text;

    public Response_meeting(String id, String user, String duration, String buying_company_name, String start_datetime, String end_datetime, String start_lat, String start_long, String end_lat, String end_long, String status, String buying_company_ref, ArrayList<String> salesorder, String total_products) {
        this.id = id;
        this.user = user;
        this.duration = duration;
        this.buying_company_name = buying_company_name;
        this.start_datetime = start_datetime;
        this.end_datetime = end_datetime;
        this.start_lat = start_lat;
        this.start_long = start_long;
        this.end_lat = end_lat;
        this.end_long = end_long;
        this.status = status;
        this.buying_company_ref = buying_company_ref;
        this.salesorder = salesorder;
        this.total_products = total_products;
    }

    public Response_meeting(String id, String start_lat, String start_long) {
        this.id = id;
        this.start_lat = start_lat;
        this.start_long = start_long;
    }


    public String getBuyer_table_id() {
        return buyer_table_id;
    }

    public void setBuyer_table_id(String buyer_table_id) {
        this.buyer_table_id = buyer_table_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBuying_company_name() {
        return buying_company_name;
    }

    public void setBuying_company_name(String buying_company_name) {
        this.buying_company_name = buying_company_name;
    }

    public String getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public void setStart_lat(String start_lat) {
        this.start_lat = start_lat;
    }

    public String getStart_long() {
        return start_long;
    }

    public void setStart_long(String start_long) {
        this.start_long = start_long;
    }

    public String getEnd_lat() {
        return end_lat;
    }

    public void setEnd_lat(String end_lat) {
        this.end_lat = end_lat;
    }

    public String getEnd_long() {
        return end_long;
    }

    public void setEnd_long(String end_long) {
        this.end_long = end_long;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuying_company_ref() {
        return buying_company_ref;
    }

    public void setBuying_company_ref(String buying_company_ref) {
        this.buying_company_ref = buying_company_ref;
    }

    public ArrayList<String> getSalesorder() {
        return salesorder;
    }

    public void setSalesorder(ArrayList<String> salesorder) {
        this.salesorder = salesorder;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getBuyer_name_text() {
        return buyer_name_text;
    }

    public void setBuyer_name_text(String buyer_name_text) {
        this.buyer_name_text = buyer_name_text;
    }
}
