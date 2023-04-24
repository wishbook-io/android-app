package com.wishbook.catalog.home.models;

/**
 * Created by Vigneshkarnika on 11/04/16.
 */
public class Response_meeting_report {
    String total_items;
    String total_meeting;
    String day;
    String total_duration;

    public Response_meeting_report(String total_items, String total_meeting, String day, String total_duration) {
        this.total_items = total_items;
        this.total_meeting = total_meeting;
        this.day = day;
        this.total_duration = total_duration;
    }

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }

    public String getTotal_meeting() {
        return total_meeting;
    }

    public void setTotal_meeting(String total_meeting) {
        this.total_meeting = total_meeting;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(String total_duration) {
        this.total_duration = total_duration;
    }
}
