package com.wishbook.catalog.Utils.networking.mock;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.future.SimpleFuture;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.http.Headers;
import com.wishbook.catalog.Utils.networking.HeadersResponse;
import com.wishbook.catalog.Utils.networking.Response;
import com.wishbook.catalog.Utils.networking.ResponseServedFrom;
import com.wishbook.catalog.Utils.networking.future.ResponseFuture;

/**
 * Created by vignesh on 3/6/15.
 */
public class MockResponseFuture<T> extends SimpleFuture<T> implements ResponseFuture<T> {
    private AsyncHttpRequest request;
    public MockResponseFuture(AsyncHttpRequest request) {
        this.request = request;
    }

    protected Headers getHeaders() {
        return new Headers();
    }

    protected HeadersResponse getHeadersResponse() {
        return new HeadersResponse(200, "OK", getHeaders());
    }

    private Response<T> getResponse(Exception e, T result) {
        return new Response<T>(request, ResponseServedFrom.LOADED_FROM_NETWORK, getHeadersResponse(), e, result);
    }

    @Override
    public Future<Response<T>> withResponse() {
        final SimpleFuture<Response<T>> ret = new SimpleFuture<Response<T>>();
        setCallback(new FutureCallback<T>() {
            @Override
            public void onCompleted(Exception e, T result) {
                ret.setComplete(getResponse(e, result));
            }
        });
        ret.setParent(this);
        return ret;
    }
}
