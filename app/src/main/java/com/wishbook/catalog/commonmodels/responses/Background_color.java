package com.wishbook.catalog.commonmodels.responses;

public class Background_color {
    private String center_color;

    private String start_color;

    private String angle;

    private String end_color;

    public String getCenter_color() {
        return center_color;
    }

    public void setCenter_color(String center_color) {
        this.center_color = center_color;
    }

    public String getStart_color() {
        return start_color;
    }

    public void setStart_color(String start_color) {
        this.start_color = start_color;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getEnd_color() {
        return end_color;
    }

    public void setEnd_color(String end_color) {
        this.end_color = end_color;
    }

    @Override
    public String toString() {
        return "ClassPojo [center_color = " + center_color + ", start_color = " + start_color + ", angle = " + angle + ", end_color = " + end_color + "]";
    }
}
