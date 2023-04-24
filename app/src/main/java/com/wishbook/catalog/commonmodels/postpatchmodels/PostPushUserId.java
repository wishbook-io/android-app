package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

public class PostPushUserId {
    ArrayList<String> push_user;
    ArrayList<String> push_user_product;

    public PostPushUserId(ArrayList<String> push_user, ArrayList<String> push_user_product) {
        this.push_user = push_user;
        this.push_user_product = push_user_product;
    }

    public ArrayList<String> getPush_user() {
        return push_user;
    }

    public void setPush_user(ArrayList<String> push_user) {
        this.push_user = push_user;
    }

    public ArrayList<String> getPush_user_product() {
        return push_user_product;
    }

    public void setPush_user_product(ArrayList<String> push_user_product) {
        this.push_user_product = push_user_product;
    }
}
