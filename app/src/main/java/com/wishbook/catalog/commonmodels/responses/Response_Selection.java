package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 28/03/16.
 */
public class Response_Selection {
    String id;
    String user;
    String name;
    ArrayList<String> products;
    String image;
    String push_user_id;

    public Response_Selection(String id, String user, String name, ArrayList<String> products, String image, String push_user_id) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.products = products;
        this.image = image;
        this.push_user_id = push_user_id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPush_user_id() {
        return push_user_id;
    }

    public void setPush_user_id(String push_user_id) {
        this.push_user_id = push_user_id;
    }
}
