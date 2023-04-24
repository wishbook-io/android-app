package com.wishbook.catalog.Utils.networking.gson;

import com.google.gson.JsonObject;

import java.nio.charset.Charset;

/**
 * Created by vignesh on 6/23/14.
 */
public class GsonObjectParser extends GsonParser<JsonObject> {
    public GsonObjectParser() {
        super(JsonObject.class);
    }
    public GsonObjectParser(Charset charset) {
        super(JsonObject.class, charset);
    }
}
