package com.wishbook.catalog.Utils.networking.gson;

import com.google.gson.JsonArray;

import java.nio.charset.Charset;

/**
 * Created by vignesh on 6/23/14.
 */
public class GsonArrayParser extends GsonParser<JsonArray> {
    public GsonArrayParser() {
        super(JsonArray.class);
    }

    public GsonArrayParser(Charset charset) {
        super(JsonArray.class, charset);
    }
}
