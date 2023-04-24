package com.wishbook.catalog.Utils.networking.loader;

import android.content.Context;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.Loader;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.future.ResponseFuture;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 12/22/13.
 */
public class SimpleLoader implements Loader {
    @Override
    public Future<DataEmitter> load(NetworkProcessor ion, AsyncHttpRequest request, FutureCallback<LoaderEmitter> callback) {
        return null;
    }

    @Override
    public Future<BitmapInfo> loadBitmap(Context context, NetworkProcessor ion, String key, String uri, int resizeWidth, int resizeHeight, boolean animateGif) {
        return null;
    }

    @Override
    public Future<AsyncHttpRequest> resolve(NetworkProcessor ion, AsyncHttpRequest request) {
        return null;
    }

    @Override
    public <T> ResponseFuture<T> load(NetworkProcessor ion, AsyncHttpRequest request, Type type) {
        return null;
    }
}
