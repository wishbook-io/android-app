package com.wishbook.catalog.home.inventory.barcode.adaptermodel;

import com.wishbook.catalog.commonmodels.responses.Response_catalog;


import java.util.List;

/**
 * Created by root on 22/11/16.
 */
public class Brand  {

    public boolean mExpanded = false;
    public String id;
    public String name;
    public String company;
    public List<Response_catalog> mCatalogs;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


}
