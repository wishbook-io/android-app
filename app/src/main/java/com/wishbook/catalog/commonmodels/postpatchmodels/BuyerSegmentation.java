package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class BuyerSegmentation {

    String id;
    String segmentation_name;
    String buyer_grouping_type;
    ArrayList<String> city;
    ArrayList<String> state;
    ArrayList<String> buyers;
    ArrayList<String> category;
    ArrayList<String> group_type;

    public BuyerSegmentation(String segmentation_name, ArrayList<String> city, ArrayList<String> category, ArrayList<String> group_type) {
        this.segmentation_name = segmentation_name;
        this.city = city;
        this.category = category;
        this.group_type = group_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyer_grouping_type() {
        return buyer_grouping_type;
    }

    public void setBuyer_grouping_type(String buyer_grouping_type) {
        this.buyer_grouping_type = buyer_grouping_type;
    }

    public ArrayList<String> getState() {
        return state;
    }

    public void setState(ArrayList<String> state) {
        this.state = state;
    }

    public ArrayList<String> getBuyers() {
        return buyers;
    }

    public void setBuyers(ArrayList<String> buyers) {
        this.buyers = buyers;
    }

    public BuyerSegmentation() {
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

    public ArrayList<String> getGroup_type() {
        return group_type;
    }

    public void setGroup_type(ArrayList<String> group_type) {
        this.group_type = group_type;
    }
}
