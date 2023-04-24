package com.wishbook.catalog.home.inventory.barcode.adaptermodel;

import com.wishbook.catalog.home.models.ProductObj;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/11/16.
 */
public class Catalog {
    String id;
    String title;
    private boolean mExpand = false;
    ArrayList<ProductObj> products;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
