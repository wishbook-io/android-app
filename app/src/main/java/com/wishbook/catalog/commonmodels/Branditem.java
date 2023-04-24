package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 20/03/16.
 */
public class Branditem {
    String id;
    String brandName;
    String brandImage;

    public Branditem(String id, String brandName, String brandImage) {
        this.id = id;
        this.brandName = brandName;
        this.brandImage = brandImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }
}
