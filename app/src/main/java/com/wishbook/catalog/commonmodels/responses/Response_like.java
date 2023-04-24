package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by Vigneshkarnika on 27/03/16.
 */
public class Response_like {
    String id;
    String user;
    String product;

    public Response_like(String id, String user, String product) {
        this.id = id;
        this.user = user;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
