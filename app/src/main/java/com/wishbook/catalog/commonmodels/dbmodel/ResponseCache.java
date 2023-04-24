package com.wishbook.catalog.commonmodels.dbmodel;


/**
 * Created by Vigneshkarnika on 12/06/16.
 */
public class ResponseCache/* extends RealmObject*/{
  /*  @PrimaryKey*/
    String key;
    String response;

    public ResponseCache() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
