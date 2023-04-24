package com.wishbook.catalog.Utils.networking.async.http.body;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;

public interface AsyncHttpRequestBody<T> {
    void write(AsyncHttpRequest request, DataSink sink, CompletedCallback completed);
    void parse(DataEmitter emitter, CompletedCallback completed);
    String getContentType();
    boolean readFullyOnRequest();
    int length();
    T get();
}
