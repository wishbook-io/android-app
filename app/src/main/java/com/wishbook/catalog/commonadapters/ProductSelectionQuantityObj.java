package com.wishbook.catalog.commonadapters;

import com.wishbook.catalog.commonmodels.responses.ProductsSelection;
import com.wishbook.catalog.home.models.ProductObj;

/**
 * Created by root on 31/1/17.
 */
public class ProductSelectionQuantityObj {
    ProductsSelection feedItemList;
    int quantity;
    float price;

    public ProductSelectionQuantityObj(ProductsSelection feedItemList, int quantity, float price) {
        this.feedItemList = feedItemList;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductsSelection getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(ProductsSelection feedItemList) {
        this.feedItemList = feedItemList;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}