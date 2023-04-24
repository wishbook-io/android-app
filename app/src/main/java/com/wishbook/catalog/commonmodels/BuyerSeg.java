package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 29/03/16.
 */
public class BuyerSeg {
    String id;
    String segmentation_name;
    ArrayList<String> city;
    ArrayList<String> state;
    ArrayList<String> category;
    ArrayList<String> buyers;
    String active_buyers;
    String last_generated;
    ArrayList<String> group_type;
    String buyer_grouping_type;
    String applozic_id;
    boolean isChecked;


    public BuyerSeg(String id, String segmentation_name, ArrayList<String> city, ArrayList<String> category, String active_buyers, String last_generated, ArrayList<String> group_type) {
        this.id = id;
        this.segmentation_name = segmentation_name;
        this.city = city;
        this.category = category;
        this.active_buyers = active_buyers;
        this.last_generated = last_generated;
        this.group_type = group_type;
    }

    public String getApplozic_id() {
        return applozic_id;
    }

    public void setApplozic_id(String applozic_id) {
        this.applozic_id = applozic_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSegmentation_name() {
        return segmentation_name;
    }

    public void setSegmentation_name(String segmentation_name) {
        this.segmentation_name = segmentation_name;
    }

    public ArrayList<String> getCity() {
        return city;
    }

    public void setCity(ArrayList<String> city) {
        this.city = city;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public String getActive_buyers() {
        return active_buyers;
    }

    public void setActive_buyers(String active_buyers) {
        this.active_buyers = active_buyers;
    }

    public String getLast_generated() {
        return last_generated;
    }

    public void setLast_generated(String last_generated) {
        this.last_generated = last_generated;
    }

    public ArrayList<String> getGroup_type() {
        return group_type;
    }

    public void setGroup_type(ArrayList<String> group_type) {
        this.group_type = group_type;
    }

    public ArrayList<String> getBuyers() {
        return buyers;
    }

    public void setBuyersl(ArrayList<String> buyers) {
        this.buyers = buyers;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<String> getState() {
        return state;
    }

    public void setState(ArrayList<String> state) {
        this.state = state;
    }

    public void setBuyers(ArrayList<String> buyers) {
        this.buyers = buyers;
    }

    public String getBuyer_grouping_type() {
        return buyer_grouping_type;
    }

    public void setBuyer_grouping_type(String buyer_grouping_type) {
        this.buyer_grouping_type = buyer_grouping_type;
    }
}
