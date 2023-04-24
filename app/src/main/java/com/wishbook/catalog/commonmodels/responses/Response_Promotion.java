package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 1/7/17.
 */

public class Response_Promotion {
    int id;
    ImageBanner image;
    String image_ppoi;
    String landing_page_type;
    String landing_page;
    String start_date;
    String end_date;
    String status;
    String active;
    String url;
    String campaign_name;
    String review_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageBanner getImage() {
        return image;
    }

    public void setImage(ImageBanner image) {
        this.image = image;
    }

    public String getImage_ppoi() {
        return image_ppoi;
    }

    public void setImage_ppoi(String image_ppoi) {
        this.image_ppoi = image_ppoi;
    }

    public String getLanding_page_type() {
        return landing_page_type;
    }

    public void setLanding_page_type(String landing_page_type) {
        this.landing_page_type = landing_page_type;
    }

    public String getLanding_page() {
        return landing_page;
    }

    public void setLanding_page(String landing_page) {
        this.landing_page = landing_page;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public String getReview_type() {
        return review_type;
    }

    public void setReview_type(String review_type) {
        this.review_type = review_type;
    }
}
