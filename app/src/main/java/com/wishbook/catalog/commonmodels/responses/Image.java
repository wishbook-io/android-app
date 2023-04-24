package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class Image implements Serializable {
    private String thumbnail_small;

    private String full_size;

    private String thumbnail_medium;

    public Image(String thumbnail_small, String full_size, String thumbnail_medium) {
        this.thumbnail_small = thumbnail_small;
        this.full_size = full_size;
        this.thumbnail_medium = thumbnail_medium;
    }

    public Image(String thumbnail_small, String thumbnail_medium) {
        this.thumbnail_small = thumbnail_small;
        this.thumbnail_medium = thumbnail_medium;
    }

    public String getThumbnail_small() {
        return thumbnail_small;
    }

    public void setThumbnail_small(String thumbnail_small) {
        this.thumbnail_small = thumbnail_small;
    }

    public String getThumbnail_medium() {
        return thumbnail_medium;
    }

    public void setThumbnail_medium(String thumbnail_medium) {
        this.thumbnail_medium = thumbnail_medium;
    }

    public String getFull_size() {
        return full_size;
    }

    public void setFull_size(String full_size) {
        this.full_size = full_size;
    }

    @Override
    public String toString() {
        return "ClassPojo [thumbnail_small = " + thumbnail_small + ", full_size = " + full_size + "]";
    }
}