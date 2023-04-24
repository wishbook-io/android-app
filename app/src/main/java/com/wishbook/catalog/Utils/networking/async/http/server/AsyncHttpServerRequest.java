package com.wishbook.catalog.Utils.networking.async.http.server;

import com.wishbook.catalog.Utils.networking.async.AsyncSocket;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.http.Headers;
import com.wishbook.catalog.Utils.networking.async.http.Multimap;
import com.wishbook.catalog.Utils.networking.async.http.body.AsyncHttpRequestBody;

import java.util.regex.Matcher;

public interface AsyncHttpServerRequest extends DataEmitter {
    Headers getHeaders();
    Matcher getMatcher();
    AsyncHttpRequestBody getBody();
    AsyncSocket getSocket();
    String getPath();
    Multimap getQuery();
    String getMethod();
}
