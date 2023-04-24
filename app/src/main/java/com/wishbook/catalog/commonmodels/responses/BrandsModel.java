package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

/**
 * Created by root on 28/11/16.
 */
public class BrandsModel {
    String title;
    String id;
    ArrayList<CatalogsModel> catalogs;
    Boolean isExpanded;


    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }


    public BrandsModel(String title, String id, Boolean isExpanded) {
        this.title = title;
        this.id = id;
        this.isExpanded = isExpanded;
    }

    public BrandsModel(String title, String id, ArrayList<CatalogsModel> catalogs) {
        this.title = title;
        this.id = id;
        this.catalogs = catalogs;
    }

    public BrandsModel(String title, String id, ArrayList<CatalogsModel> catalogs, Boolean isExpanded) {
        this.title = title;
        this.id = id;
        this.catalogs = catalogs;
        this.isExpanded = isExpanded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CatalogsModel> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(ArrayList<CatalogsModel> catalogs) {
        this.catalogs = catalogs;
    }
}
