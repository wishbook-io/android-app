package com.wishbook.catalog.Utils.networking.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.future.SimpleFuture;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.stream.InputStreamDataEmitter;
import com.wishbook.catalog.Utils.networking.async.util.StreamUtility;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.ResponseServedFrom;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.NetworkProcessorBitmapCache;

import java.io.InputStream;

/**
 * Created by vignesh on 6/20/14.
 */
public class ResourceLoader extends StreamLoader {
    private static class Resource {
        Resources res;
        int id;
    }

    private static Resource lookupResource(Context context, String uri) throws Exception {
        Uri u = Uri.parse(uri);
        if (u.getPathSegments() == null)
            throw new IllegalArgumentException("uri is not a valid resource uri");
        String pkg = u.getAuthority();
        Context ctx = context.createPackageContext(pkg, 0);
        Resources res = ctx.getResources();
        int id;
        if (u.getPathSegments().size() == 1)
            id = Integer.valueOf(u.getPathSegments().get(0));
        else if (u.getPathSegments().size() == 2) {
            String type = u.getPathSegments().get(0);
            String name = u.getPathSegments().get(1);
            id = res.getIdentifier(name, type, pkg);
            if (id == 0)
                throw new IllegalArgumentException("resource not found in given package");
        }
        else {
            throw new IllegalArgumentException("uri is not a valid resource uri");
        }
        Resource ret = new Resource();
        ret.res = res;
        ret.id = id;
        return ret;
    }

    @Override
    public Future<BitmapInfo> loadBitmap(final Context context, final NetworkProcessor ion, final String key, final String uri, final int resizeWidth, final int resizeHeight, final boolean animateGif) {
        if (uri == null || !uri.startsWith("android.resource:/"))
            return null;

        final SimpleFuture<BitmapInfo> ret = new SimpleFuture<BitmapInfo>();

//        Log.d("FileLoader", "Loading file bitmap " + uri + " " + resizeWidth + "," + resizeHeight);
        NetworkProcessor.getBitmapLoadExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Resource res = lookupResource(context, uri);
                    BitmapFactory.Options options = ion.getBitmapCache().prepareBitmapOptions(res.res, res.id, resizeWidth, resizeHeight);
                    Point size = new Point(options.outWidth, options.outHeight);
                    BitmapInfo info;
                    if (animateGif && TextUtils.equals("image/gif", options.outMimeType)) {
                        InputStream in = res.res.openRawResource(res.id);
                        try {
                            info = loadGif(key, size, in, options);
                        }
                        finally {
                            StreamUtility.closeQuietly(in);
                        }
                    }
                    else {
                        Bitmap bitmap = NetworkProcessorBitmapCache.loadBitmap(res.res, res.id, options);
                        if (bitmap == null)
                            throw new Exception("Bitmap failed to load");
                        info = new BitmapInfo(key, options.outMimeType, bitmap, size);
                    }
                    info.servedFrom = ResponseServedFrom.LOADED_FROM_CACHE;
                    ret.setComplete(info);
                }
                catch (OutOfMemoryError e) {
                    ret.setComplete(new Exception(e), null);
                }
                catch (Exception e) {
                    ret.setComplete(e);
                }
            }
        });

        return ret;
    }

    @Override
    public Future<DataEmitter> load(final NetworkProcessor ion, final AsyncHttpRequest request, final FutureCallback<LoaderEmitter> callback) {
        if (!request.getUri().getScheme().startsWith("android.resource:/"))
            return null;

        final InputStreamDataEmitterFuture ret = new InputStreamDataEmitterFuture();
        ion.getHttpClient().getServer().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Resource res = lookupResource(ion.getContext(), request.getUri().toString());
                    InputStream stream = res.res.openRawResource(res.id);
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
