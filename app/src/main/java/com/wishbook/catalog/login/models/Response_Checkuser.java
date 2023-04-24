package com.wishbook.catalog.login.models;

/**
 * Created by Vigneshkarnika on 25/03/16.
 */
public class Response_Checkuser {
    String success;

    public Response_Checkuser(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
