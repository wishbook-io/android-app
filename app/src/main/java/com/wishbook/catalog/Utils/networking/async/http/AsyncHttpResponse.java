package com.wishbook.catalog.Utils.networking.async.http;

import com.wishbook.catalog.Utils.networking.async.AsyncSocket;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;

public interface AsyncHttpResponse extends DataEmitter {
    String protocol();
    String message();
    int code();
    Headers headers();
    AsyncSocket detachSocket();
    AsyncHttpRequest getRequest();
}
