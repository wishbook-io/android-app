package com.wishbook.catalog.Utils.networking;

import android.content.Context;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.future.ResponseFuture;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 5/22/13.
 */
public interface Loader {
    class LoaderEmitter {
        public LoaderEmitter(DataEmitter emitter, long length, ResponseServedFrom servedFrom,
                             HeadersResponse headers,
                             AsyncHttpRequest request) {
            this.length = length;
            this.emitter = emitter;
            this.servedFrom = servedFrom;
            this.headers = headers;
            this.request = request;
        }
        DataEmitter emitter;
        long length;
        public DataEmitter getDataEmitter() {
            return emitter;
        }
        public long length() {
            return length;
        }
        ResponseServedFrom servedFrom;
        public ResponseServedFrom getServedFrom() {
            return servedFrom;
        }
        HeadersResponse headers;
        public HeadersResponse getHeaders() {
            return headers;
        }
        AsyncHttpRequest request;
        public AsyncHttpRequest getRequest() {
            return request;
        }
    }

    /**
     * returns a Future if this loader can handle a request
     * otherwise it returns null, and NetworkProcessor continues to the next loader.
     * @param ion
     * @param request
     * @param callback
     * @return
     */
    Future<DataEmitter> load(NetworkProcessor ion, AsyncHttpRequest request, FutureCallback<LoaderEmitter> callback);

    /**
     * returns a future if the laoder can handle the request as a bitmap
     * otherwise it returns null
     * @param ion
     * @param key
     * @param uri
     * @param resizeWidth
     * @param resizeHeight
     * @return
     */
    Future<BitmapInfo> loadBitmap(Context context, NetworkProcessor ion, String key, String uri, int resizeWidth, int resizeHeight, boolean animateGif);

    /**
     * Resolve a request into another request.
     * @param ion
     * @param request
     * @return
     */
    Future<AsyncHttpRequest> resolve(NetworkProcessor ion, AsyncHttpRequest request);

    <T> ResponseFuture<T> load(NetworkProcessor ion, AsyncHttpRequest request, Type type);
}
