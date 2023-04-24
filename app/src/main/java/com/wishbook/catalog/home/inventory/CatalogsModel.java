package com.wishbook.catalog.home.inventory;

import com.wishbook.catalog.commonmodels.responses.ProductsSelection;
import com.wishbook.catalog.commonmodels.postpatchmodels.InventoryAddStock;

import java.util.ArrayList;

/**
 * Created by root on 28/11/16.
 */
public class CatalogsModel {
    String title;
    String id;
    ArrayList<InventoryAddStock> products;

    ProductsSelection[] productsSelections;
    Boolean isExpanded;

    public Boolean getExpanded() {

        return isExpanded;
    }

    public ProductsSelection[] getProductsSelections() {
        return productsSelections;
    }

    public void setProductsSelections(ProductsSelection[] productsSelections) {
        this.productsSelections = productsSelections;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public CatalogsModel() {
    }

    public CatalogsModel(String title, String id, ArrayList<InventoryAddStock> products) {
        this.title = title;
        this.id = id;
        this.products = products;
    }

    public CatalogsModel(String title, String id) {
        this.title = title;
        this.id = id;

    }



    public CatalogsModel(String title, String id, ProductsSelection[] products,Boolean isExpanded) {
        this.title = title;
        this.id = id;
        this.productsSelections = products;
        this.isExpanded =isExpanded;
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

    public ArrayList<InventoryAddStock> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<InventoryAddStock> products) {
        this.products = products;
    }
}
