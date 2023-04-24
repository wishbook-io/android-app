package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class RequestEav {

    private String size_mix;

    private int number_pcs_design_per_set;

    private String stitching_type;

    private ArrayList<String> size;

    private ArrayList<String> fabric;

    private ArrayList<String> work;

    private String other;

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getSize_mix() {
        return size_mix;
    }

    public void setSize_mix(String size_mix) {
        this.size_mix = size_mix;
    }

    public int getNumber_pcs_design_per_set() {
        return number_pcs_design_per_set;
    }

    public void setNumber_pcs_design_per_set(int number_pcs_design_per_set) {
        this.number_pcs_design_per_set = number_pcs_design_per_set;
    }

    public String getStitching_type() {
        return stitching_type;
    }

    public void setStitching_type(String stitching_type) {
        this.stitching_type = stitching_type;
    }

    public ArrayList<String> getSize() {
        return size;
    }

    public void setSize(ArrayList<String> size) {
        this.size = size;
    }

    public ArrayList<String> getFabric() {
        return fabric;
    }

    public void setFabric(ArrayList<String> fabric) {
        this.fabric = fabric;
    }

    public ArrayList<String> getWork() {
        return work;
    }

    public void setWork(ArrayList<String> work) {
        this.work = work;
    }
}
