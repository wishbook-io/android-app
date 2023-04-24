package com.wishbook.catalog.commonmodels;


import java.util.ArrayList;

public class PatchBuyersGroup {

    private String segmentation_name;


    ArrayList<Integer> buyers;

    public ArrayList<Integer> getBuyer() {
        return buyers;
    }

    public void setBuyer(ArrayList<Integer> buyer) {
        this.buyers = buyer;
    }

    public String getSegmentation_name() {
        return segmentation_name;
    }

    public void setSegmentation_name(String segmentation_name) {
        this.segmentation_name = segmentation_name;
    }
}
