package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.home.models.ProductObj;

/**
 * Created by Vigneshkarnika on 19/04/16.
 */
public class ProductObjectQuantity {
    ProductObj feedItemList;
    int quantity;
    float price;
    boolean isFullCatalog;
    String packing_type;
    private int isSetDesign;


    public ProductObjectQuantity(ProductObj feedItemList, int quantity, float price) {
        this.feedItemList = feedItemList;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductObjectQuantity(ProductObj feedItemList, int quantity, float price,boolean isFullCatalog,String packing_type ) {
        this.feedItemList = feedItemList;
        this.quantity = quantity;
        this.price = price;
        this.isFullCatalog = isFullCatalog;
        this.packing_type = packing_type;
    }

    public ProductObj getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(ProductObj feedItemList) {
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

    public boolean isFullCatalog() {
        return isFullCatalog;
    }

    public void setFullCatalog(boolean fullCatalog) {
        isFullCatalog = fullCatalog;
    }

    public String getPacking_type() {
        return packing_type;
    }

    public void setPacking_type(String packing_type) {
        this.packing_type = packing_type;
    }

    public int getSetDesign() {
        return isSetDesign;
    }

    public void setSetDesign(int setDesign) {
        isSetDesign = setDesign;
    }


    @Override
    public boolean equals(Object o) {
        return (((ProductObjectQuantity) o).getFeedItemList().equals(getFeedItemList()) &&
                ((ProductObjectQuantity) o).getQuantity()== getQuantity());
    }
}
