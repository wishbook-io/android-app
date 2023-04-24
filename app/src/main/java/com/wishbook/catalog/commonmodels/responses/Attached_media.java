package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class Attached_media implements Serializable {

    public Attached_media(String media_fbid) {
        this.media_fbid = media_fbid;
    }

    String media_fbid;

    public String getMedia_fbid() {
        return media_fbid;
    }

    public void setMedia_fbid(String media_fbid) {
        this.media_fbid = media_fbid;
    }
}
