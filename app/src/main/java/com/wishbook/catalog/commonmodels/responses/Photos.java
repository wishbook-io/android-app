package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class Photos implements Serializable {

    private String id;

    private boolean set_default;

    private Image image;

    private String sort_order;

    public Photos(String id, Image image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getSet_default() {
        return set_default;
    }

    public void setSet_default(boolean set_default) {
        this.set_default = set_default;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
}
