package com.wishbook.catalog.home.models;

import java.io.Serializable;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class BrandImageObject implements Serializable {
    String full_size;
    String thumbnail_small;

    public BrandImageObject(String full_size, String thumbnail_small) {
        this.full_size = full_size;
        this.thumbnail_small = thumbnail_small;
    }

    public String getFull_size() {
        return full_size;
    }

    public void setFull_size(String full_size) {
        this.full_size = full_size;
    }

    public String getThumbnail_small() {
        return thumbnail_small;
    }

    public void setThumbnail_small(String thumbnail_small) {
        this.thumbnail_small = thumbnail_small;
    }
}
