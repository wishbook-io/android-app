package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 1/7/17.
 */

public class ImageBanner {
    String full_size;
    String banner;
    String thumbnail_small;

    public ImageBanner() {
    }

    public ImageBanner(String full_size, String banner, String thumbnail_small) {
        this.full_size = full_size;
        this.banner = banner;
        this.thumbnail_small = thumbnail_small;
    }

    public String getFull_size() {
        return full_size;
    }

    public void setFull_size(String full_size) {
        this.full_size = full_size;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getThumbnail_small() {
        return thumbnail_small;
    }

    public void setThumbnail_small(String thumbnail_small) {
        this.thumbnail_small = thumbnail_small;
    }
}
