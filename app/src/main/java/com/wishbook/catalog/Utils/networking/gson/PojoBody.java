package com.wishbook.catalog.Utils.networking.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.Util;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.http.body.AsyncHttpRequestBody;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

/**
 * Created by vignesh on 6/5/13.
 */
public class PojoBody<T> implements AsyncHttpRequestBody<T> {
    T pojo;
    byte[] bodyBytes;
    Type type;
    Gson gson;
    public PojoBody(Gson gson, T pojo, TypeToken<T> token) {
        this.pojo = pojo;
        if (token != null)
            this.type = token.getType();
        this.gson = gson;

        if (pojo.getClass().isPrimitive() || pojo instanceof String)
            throw new AssertionError("must provide a non-primitive type");
    }

    byte[] getBodyBytes() {
        if (bodyBytes != null)
            return bodyBytes;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(bout);
        if (type == null)
            gson.toJson(pojo, out);
        else
            gson.toJson(pojo, type, out);
        try {
            out.flush();
            bout.flush();
        }
        catch (Exception e) {

        }
        bodyBytes = bout.toByteArray();
        return bodyBytes;
    }

    @Override
    public void write(AsyncHttpRequest request, DataSink sink, final CompletedCallback completed) {
        Util.writeAll(sink, getBodyBytes(), completed);
    }

    @Override
    public void parse(DataEmitter emitter, CompletedCallback completed) {

    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        return getBodyBytes().length;
    }

    @Override
    public T get() {
        return pojo;
    }

    public static final String CONTENT_TYPE = "application/json";
}
