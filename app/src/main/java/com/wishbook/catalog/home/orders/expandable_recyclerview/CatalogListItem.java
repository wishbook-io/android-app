package com.wishbook.catalog.home.orders.expandable_recyclerview;

import android.widget.ImageView;
import android.widget.TextView;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.wishbook.catalog.commonmodels.responses.Response_Product;

import java.util.List;

/**
 * Created by root on 24/9/16.
 */
public class CatalogListItem implements ParentListItem {

    private String catalog;
    private String add_sub;
    private String brand;
    private String pcs;
    private List<Response_Product> feedItemList;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPcs() {
        return pcs;
    }

    public void setPcs(String pcs) {
        this.pcs = pcs;
    }

    public List<Response_Product> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<Response_Product> feedItemList) {
        this.feedItemList = feedItemList;
    }

    public CatalogListItem(String catalog, String brand, String pcs, List<Response_Product> feedItemList) {
        this.catalog = catalog;
        this.brand = brand;
        this.pcs = pcs;
        this.feedItemList = feedItemList;
    }

    @Override
    public List<Response_Product> getChildItemList() {
        return feedItemList;
    }
    public void setChildItemList(List<Response_Product> list) {
        this.feedItemList = list;
    }
    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
