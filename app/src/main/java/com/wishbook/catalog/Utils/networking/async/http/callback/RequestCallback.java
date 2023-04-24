package com.wishbook.catalog.Utils.networking.async.http.callback;

import com.wishbook.catalog.Utils.networking.async.callback.ResultCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpResponse;

public interface RequestCallback<T> extends ResultCallback<AsyncHttpResponse, T> {
    void onConnect();
    void onProgress(long downloaded, long total);
}
