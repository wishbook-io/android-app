package com.wishbook.catalog.home.orderNew.expandableRecyclerview;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;

import java.util.List;


public class CatalogListItem implements ParentListItem {

    private String catalog;
    private String brand;
    private String totalProducts;
    private int totalOrderPcs;
    private List<ProductObjectQuantity> feedItemList;


    public CatalogListItem(String catalog, String brand, String totalProducts, List<ProductObjectQuantity> feedItemList) {
        this.catalog = catalog;
        this.brand = brand;
        this.totalProducts = totalProducts;
        this.feedItemList = feedItemList;
    }

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

    public String getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(String totalProducts) {
        this.totalProducts = totalProducts;
    }

    public List<ProductObjectQuantity> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<ProductObjectQuantity> feedItemList) {
        this.feedItemList = feedItemList;
    }

    public int getTotalOrderPcs() {
        return totalOrderPcs;
    }

    public void setTotalOrderPcs(int totalOrderPcs) {
        this.totalOrderPcs = totalOrderPcs;
    }

    @Override
    public List<ProductObjectQuantity> getChildItemList() {
        return feedItemList;
    }

    public void setChildItemList(List<ProductObjectQuantity> list) {
        this.feedItemList = list;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
