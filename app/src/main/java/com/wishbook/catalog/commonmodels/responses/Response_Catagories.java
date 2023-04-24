package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.BrandImageObject;

/**
 * Created by root on 6/9/16.
 */
public class Response_Catagories {

    String id;
    String[] child_category;
    String category_name;
    String parent_category;
    String Image;

    public Response_Catagories(String id, String[] child_category, String category_name, String parent_category,String image) {
        this.id = id;
        this.child_category = child_category;
        this.category_name = category_name;
        this.parent_category = parent_category;
        this.Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getChild_category() {
        return child_category;
    }

    public void setChild_category(String[] child_category) {
        this.child_category = child_category;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getParent_category() {
        return parent_category;
    }

    public void setParent_category(String parent_category) {
        this.parent_category = parent_category;
    }
}
