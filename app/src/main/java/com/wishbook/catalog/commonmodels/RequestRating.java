package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Image;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestRating implements Serializable {

    private String id;

    private String product;

    private String order_item;

    private String review;

    private String rating;

    private String user;

    private String first_name;

    private String last_name;

    private ArrayList<Image> review_photos;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOrder_item() {
        return order_item;
    }

    public void setOrder_item(String order_item) {
        this.order_item = order_item;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public ArrayList<Image> getReview_photos() {
        return review_photos;
    }

    public void setReview_photos(ArrayList<Image> review_photos) {
        this.review_photos = review_photos;
    }
}
