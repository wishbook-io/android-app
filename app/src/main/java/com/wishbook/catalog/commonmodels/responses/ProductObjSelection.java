package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.CatalogObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class ProductObjSelection implements Comparator<ProductObjSelection> {

    String id;
    String sku;
    String title;
    String fabric;
    String work;
    CatalogObj catalog;
    ThumbnailObj image;
    String final_price;
    String selling_price;
    String price;
    String push_user_product_id;
    String product_likes;
    public String product_like_id;
    String barcode;

    public ProductObjSelection(String id, String sku, String title, String fabric, String work, CatalogObj catalog, ThumbnailObj image, String final_price, String selling_price, String price, String push_user_product_id, String product_likes, String product_like_id) {
        this.id = id;
        this.sku = sku;
        this.title = title;
        this.fabric = fabric;
        this.work = work;
        this.catalog = catalog;
        this.image = image;
        this.final_price = final_price;
        this.selling_price = selling_price;
        this.price = price;
        this.push_user_product_id = push_user_product_id;
        this.product_likes = product_likes;
        this.product_like_id = product_like_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public CatalogObj getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogObj catalog) {
        this.catalog = catalog;
    }

    public ThumbnailObj getImage() {
        return image;
    }

    public void setImage(ThumbnailObj image) {
        this.image = image;
    }

    public String getFinal_price() {
        return final_price;
    }

    public void setFinal_price(String final_price) {
        this.final_price = final_price;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPush_user_product_id() {
        return push_user_product_id;
    }

    public void setPush_user_product_id(String push_user_product_id) {
        this.push_user_product_id = push_user_product_id;
    }

    public String getProduct_likes() {
        return product_likes;
    }

    public void setProduct_likes(String product_likes) {
        this.product_likes = product_likes;
    }

    public String getProduct_like_id() {
        return product_like_id;
    }

    public void setProduct_like_id(String product_like_id) {
        this.product_like_id = product_like_id;
    }


    @Override
    public int compare(ProductObjSelection lhs, ProductObjSelection rhs) {
        return lhs.getFinal_price().compareTo(rhs.getFinal_price());
    }
}
