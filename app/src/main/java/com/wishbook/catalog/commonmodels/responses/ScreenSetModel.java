package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.Utils.multipleimageselect.models.Image;

import java.util.ArrayList;
import java.util.Objects;

public class ScreenSetModel {

    String screen_name;

    String color_name;

    ArrayList<Image> images;

    boolean color_set_type;

    String expiry_date;


    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public boolean isColor_set_type() {
        return color_set_type;
    }

    public void setColor_set_type(boolean color_set_type) {
        this.color_set_type = color_set_type;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

   /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenSetModel that = (ScreenSetModel) o;
        return Objects.equals(screen_name, that.screen_name);
    }*/

}
