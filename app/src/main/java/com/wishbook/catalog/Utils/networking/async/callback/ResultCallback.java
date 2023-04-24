package com.wishbook.catalog.Utils.networking.async.callback;

public interface ResultCallback<S, T> {
    void onCompleted(Exception e, T result);
}
