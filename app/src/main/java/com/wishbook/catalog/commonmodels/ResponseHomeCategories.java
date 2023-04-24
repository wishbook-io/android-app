package com.wishbook.catalog.commonmodels;


import com.wishbook.catalog.commonmodels.responses.Image;

public class ResponseHomeCategories {

    private String id;

    private String category_name;

    private String is_home_display;

    private String parent_category;

    private Image image;

    private String[] child_category;

    private String sort_order;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIs_home_display() {
        return is_home_display;
    }

    public void setIs_home_display(String is_home_display) {
        this.is_home_display = is_home_display;
    }

    public String getParent_category() {
        return parent_category;
    }

    public void setParent_category(String parent_category) {
        this.parent_category = parent_category;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String[] getChild_category() {
        return child_category;
    }

    public void setChild_category(String[] child_category) {
        this.child_category = child_category;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
}
