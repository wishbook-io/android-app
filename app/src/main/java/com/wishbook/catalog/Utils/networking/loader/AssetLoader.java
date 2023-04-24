package com.wishbook.catalog.Utils.networking.loader;

import android.content.Context;
import android.net.Uri;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.stream.InputStreamDataEmitter;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.ResponseServedFrom;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;

import java.io.InputStream;

/**
 * Created by vignesh on 6/27/14.
 */
public class AssetLoader extends StreamLoader {
    @Override
    public Future<BitmapInfo> loadBitmap(final Context context, final NetworkProcessor ion, final String key, final String uri, final int resizeWidth, final int resizeHeight, final boolean animateGif) {
        if (!uri.startsWith("file:///android_asset/"))
            return null;

        return super.loadBitmap(context, ion, key, uri, resizeWidth, resizeHeight, animateGif);
    }

    @Override
    protected InputStream getInputStream(Context context, String uri) throws Exception {
        return context.getAssets().open(Uri.parse(uri).getPath().replaceFirst("^/android_asset/", ""));
    }

    @Override
    public Future<DataEmitter> load(final NetworkProcessor ion, final AsyncHttpRequest request, final FutureCallback<LoaderEmitter> callback) {
        if (!request.getUri().toString().startsWith("file:///android_asset/"))
            return null;

        final InputStreamDataEmitterFuture ret = new InputStreamDataEmitterFuture();
        ion.getHttpClient().getServer().post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = getInputStream(ion.getContext(), request.getUri().toString());
                    if (stream == null)
                        throw new Exception("Unable to load content stream");
                    int available = stream.available();
                    InputStreamDataEmitter emitter = new InputStreamDataEmitter(ion.getHttpClient().getServer(), stream);
                    ret.setComplete(emitter);
                    callback.onCompleted(null, new LoaderEmitter(emitter, available, ResponseServedFrom.LOADED_FROM_CACHE, null, null));
                }
                catch (Exception e) {
                    ret.setComplete(e);
                    callback.onCompleted(e, null);
                }
            }
        });
        return ret;
    }
}
