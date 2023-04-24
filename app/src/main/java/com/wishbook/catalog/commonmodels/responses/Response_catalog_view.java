package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 3/9/16.
 */
public class Response_catalog_view {
    private String id;
    private String type;
    private String full_catalog_orders_only;
    private String is_disable;



    public Response_catalog_view(String id, String type, String full_catalog_orders_only) {
        this.id = id;
        this.type = type;
        this.full_catalog_orders_only = full_catalog_orders_only;
    }
    public Response_catalog_view(String id, String type) {
        this.id = id;
        this.type = type;

    }
    public String getIs_disable() {
        return is_disable;
    }

    public void setIs_disable(String is_disable) {
        this.is_disable = is_disable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }
}
