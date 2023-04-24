package com.wishbook.catalog.Utils.networking.async.http.server;


public interface HttpServerRequestCallback {
    void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
