package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 13/4/17.
 */
public class MultiSelectModel {
    String id;
    String name;
    Boolean isSelected;

    public MultiSelectModel() {
    }

    public MultiSelectModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
