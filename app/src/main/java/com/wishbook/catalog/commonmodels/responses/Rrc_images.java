package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class Rrc_images implements Serializable {

    private Image image;

    private String id;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
