package com.wishbook.catalog.commonmodels.responses;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Eavdata implements Serializable {

    private ArrayList<String> work;

    private ArrayList<String> fabric;

    private ArrayList<String> size;

    private String other;

    private String number_pcs_design_per_set;

    private String stitching_type;

    private String style;

    private String Top;

    private String Bottom;

    private String Dupatta;

    @SerializedName("Dupatta Width")
    private String dupatta_width;

    @SerializedName("Dupatta Length")
    private String dupatta_length;


    public ArrayList<String> getWork() {
        return work;
    }

    public void setWork(ArrayList<String> work) {
        this.work = work;
    }

    public ArrayList<String> getFabric() {
        return fabric;
    }

    public void setFabric(ArrayList<String> fabric) {
        this.fabric = fabric;
    }

    public ArrayList<String> getSize() {
        return size;
    }

    public void setSize(ArrayList<String> size) {
        this.size = size;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getNumber_pcs_design_per_set() {
        return number_pcs_design_per_set;
    }

    public void setNumber_pcs_design_per_set(String number_pcs_design_per_set) {
        this.number_pcs_design_per_set = number_pcs_design_per_set;
    }


    public String getStitching_type() {
        return stitching_type;
    }

    public void setStitching_type(String stitching_type) {
        this.stitching_type = stitching_type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTop() {
        return Top;
    }

    public void setTop(String top) {
        Top = top;
    }

    public String getBottom() {
        return Bottom;
    }

    public void setBottom(String bottom) {
        Bottom = bottom;
    }

    public String getDupatta() {
        return Dupatta;
    }

    public void setDupatta(String dupatta) {
        Dupatta = dupatta;
    }

    public String getDupatta_width() {
        return dupatta_width;
    }

    public void setDupatta_width(String dupatta_width) {
        this.dupatta_width = dupatta_width;
    }

    public String getDupatta_length() {
        return dupatta_length;
    }

    public void setDupatta_length(String dupatta_length) {
        this.dupatta_length = dupatta_length;
    }

    @Override
    public String toString() {
        return "Eavdata{" +
                "work=" + work +
                ", fabric=" + fabric +
                ", size=" + size +
                ", other='" + other + '\'' +
                '}';
    }
}
