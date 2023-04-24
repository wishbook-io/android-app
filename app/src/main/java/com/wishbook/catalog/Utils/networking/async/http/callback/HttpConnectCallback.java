package com.wishbook.catalog.Utils.networking.async.http.callback;


import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpResponse;

public interface HttpConnectCallback {
    void onConnectCompleted(Exception ex, AsyncHttpResponse response);
}
