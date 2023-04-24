package com.wishbook.catalog.home.models;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class Response_Brands implements Comparable<Response_Brands>,ParentListItem,Serializable {
    String id;
    String name;
    String company;
    String company_id;
    BrandImageObject image;
    int total_catalogs;
    List<Response_catalog> response_catalogs;
    Boolean isExpanded;
    Boolean isSelected;
    String is_followed;

    private NameValues.Discount_rules discount_rules;


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }



    public List<Response_catalog> getResponse_catalogs() {
        return response_catalogs;
    }

    public void setResponse_catalogs(List<Response_catalog> response_catalogs) {
        this.response_catalogs = response_catalogs;
    }

    public int getTotal_catalogs() {
        return total_catalogs;
    }

    public void setTotal_catalogs(int total_catalogs) {
        this.total_catalogs = total_catalogs;
    }

    public Response_Brands(String id,String name) {
        this.id=id;
        this.name = name;
    }

    public Response_Brands(String name) {
        this.name = name;
    }

    public Response_Brands(String id, String name, String company, BrandImageObject image, int total_catalogs, List<Response_catalog> catalogs) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.image = image;
        this.total_catalogs=total_catalogs;
       this.response_catalogs=catalogs;
    }

    public Response_Brands(String id, String name, String company, String is_followed) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.is_followed = is_followed;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public BrandImageObject getImage() {
        return image;
    }

    public void setImage(BrandImageObject image) {
        this.image = image;
    }

    public String getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(String is_followed) {
        this.is_followed = is_followed;
    }

    public NameValues.Discount_rules getDiscount_rules() {
        return discount_rules;
    }

    public void setDiscount_rules(NameValues.Discount_rules discount_rules) {
        this.discount_rules = discount_rules;
    }

    @Override
    public int compareTo(Response_Brands another) {
       return Integer.valueOf(this.getTotal_catalogs()).compareTo(Integer.valueOf(another.getTotal_catalogs()));
    }


    public String CheckEquality(String value){
        if(this.getName().equals(value))
        {
            return this.getName();
        }
        return "";
    }

    @Override
    public List<Response_catalog> getChildItemList() {

        return response_catalogs;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }


    public class Discount_rules implements Serializable {
        private String cash_discount;

        private String credit_discount;

        public String getCash_discount() {
            return cash_discount;
        }

        public void setCash_discount(String cash_discount) {
            this.cash_discount = cash_discount;
        }

        public String getCredit_discount() {
            return credit_discount;
        }

        public void setCredit_discount(String credit_discount) {
            this.credit_discount = credit_discount;
        }

        @Override
        public String toString() {
            return "ClassPojo [cash_discount = " + cash_discount + ", credit_discount = " + credit_discount + "]";
        }
    }




}
