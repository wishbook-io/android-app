package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 8/10/16.
 */
public class AllContactsChatModel {
    private String user_chat_id;
    private String user_name;

    public AllContactsChatModel(String user_chat_id, String user_name) {
        this.user_chat_id = user_chat_id;
        this.user_name = user_name;
    }

    public String getUser_chat_id() {
        return user_chat_id;
    }

    public void setUser_chat_id(String user_chat_id) {
        this.user_chat_id = user_chat_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
