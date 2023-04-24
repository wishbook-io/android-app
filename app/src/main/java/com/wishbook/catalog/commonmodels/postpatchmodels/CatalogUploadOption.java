package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 1/8/17.
 */

public class CatalogUploadOption {
    String id;
    String private_single_price;
    String public_single_price;
    String fabric;
    String work;
    Boolean without_price;
    Boolean without_sku;
    String catalog;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivate_single_price() {
        return private_single_price;
    }

    public void setPrivate_single_price(String private_single_price) {
        this.private_single_price = private_single_price;
    }

    public String getPublic_single_price() {
        return public_single_price;
    }

    public void setPublic_single_price(String public_single_price) {
        this.public_single_price = public_single_price;
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

    public Boolean getWithout_price() {
        return without_price;
    }

    public void setWithout_price(Boolean without_price) {
        this.without_price = without_price;
    }

    public Boolean getWithout_sku() {
        return without_sku;
    }

    public void setWithout_sku(Boolean without_sku) {
        this.without_sku = without_sku;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
