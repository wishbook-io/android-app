package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class ResponseRating {

    private String id;

    private String order;

    private String buyer_remark_other;

    private String seller_remark_other;

    private ArrayList<String> buyer_remark;

    private String buyer_rating;

    private ArrayList<String> seller_remark;

    private String seller_rating;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getBuyer_remark_other() {
        return buyer_remark_other;
    }

    public void setBuyer_remark_other(String buyer_remark_other) {
        this.buyer_remark_other = buyer_remark_other;
    }

    public String getSeller_remark_other() {
        return seller_remark_other;
    }

    public void setSeller_remark_other(String seller_remark_other) {
        this.seller_remark_other = seller_remark_other;
    }

    public ArrayList<String> getBuyer_remark() {
        return buyer_remark;
    }

    public void setBuyer_remark(ArrayList<String> buyer_remark) {
        this.buyer_remark = buyer_remark;
    }

    public String getBuyer_rating() {
        return buyer_rating;
    }

    public void setBuyer_rating(String buyer_rating) {
        this.buyer_rating = buyer_rating;
    }

    public ArrayList<String> getSeller_remark() {
        return seller_remark;
    }

    public void setSeller_remark(ArrayList<String> seller_remark) {
        this.seller_remark = seller_remark;
    }

    public String getSeller_rating() {
        return seller_rating;
    }

    public void setSeller_rating(String seller_rating) {
        this.seller_rating = seller_rating;
    }
}
