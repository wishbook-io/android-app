package com.wishbook.catalog.commonmodels.responses;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.wishbook.catalog.commonmodels.NameValues;

import java.util.List;

public class Brand implements ParentListItem
{
    private String id;

    private String company;

    private String name;

    private Image image;

    private boolean isChecked;

    private boolean isDisable;

    private NameValues.Discount_rules discount_rules;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCompany ()
    {
        return company;
    }

    public void setCompany (String company)
    {
        this.company = company;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Image getImage ()
    {
        return image;
    }

    public void setImage (Image image)
    {
        this.image = image;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public NameValues.Discount_rules getDiscount_rules() {
        return discount_rules;
    }

    public void setDiscount_rules(NameValues.Discount_rules discount_rules) {
        this.discount_rules = discount_rules;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", company = "+company+", name = "+name+", image = "+image+"]";
    }

    @Override
    public List<?> getChildItemList() {
        return null;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

}