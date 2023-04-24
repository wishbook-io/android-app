package com.wishbook.catalog.Utils.networking;

/**
 * Created by Vigneshkarnika on 06/07/16.
 */
public class ErrorString {
    String errormessage;
    String errorkey;

    public ErrorString(String errorkey,String errormessage) {
        this.errormessage = errormessage;
        this.errorkey = errorkey;
    }

    public ErrorString() {
        this.errormessage = "";
        this.errorkey = "";
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public String getErrorkey() {
        return errorkey;
    }

    public void setErrorkey(String errorkey) {
        this.errorkey = errorkey;
    }
}
