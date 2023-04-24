package com.wishbook.catalog.Utils.networking;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.util.FileCache;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.NetworkProcessorBitmapCache;
import com.wishbook.catalog.Utils.networking.bitmap.PostProcess;

abstract class BitmapCallback {
    final String key;
    final NetworkProcessor ion;

    public static void saveBitmapSnapshot(NetworkProcessor ion, BitmapInfo info) {
        if (info.bitmap == null)
            return;
        FileCache cache = ion.responseCache.getFileCache();
        if (cache == null)
            return;
        File tempFile = cache.getTempFile();
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            Bitmap.CompressFormat format = info.bitmap.hasAlpha() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            info.bitmap.compress(format, 100, out);
            out.close();
            cache.commitTempFiles(info.key, tempFile);
        }
        catch (Exception ex) {
        }
        finally {
            tempFile.delete();
        }
    }

    public static void getBitmapSnapshot(final NetworkProcessor ion, final String transformKey, final ArrayList<PostProcess> postProcess) {
        // don't do this if this is already loading
        if (ion.bitmapsPending.tag(transformKey) != null)
            return;
        final BitmapCallback callback = new LoadBitmapBase(ion, transformKey, true);
        NetworkProcessor.getBitmapLoadExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (ion.bitmapsPending.tag(transformKey) != callback) {
//                    Log.d("NetworkProcessorBitmapLoader", "Bitmap cache load cancelled (no longer needed)");
                    return;
                }

                try {
                    File file = ion.responseCache.getFileCache().getFile(transformKey);
                    Bitmap bitmap = NetworkProcessorBitmapCache.loadBitmap(file, null);
                    if (bitmap == null)
                        throw new Exception("Bitmap failed to load");
                    BitmapInfo info = new BitmapInfo(transformKey, "image/jpeg", bitmap, null);
                    info.servedFrom =  ResponseServedFrom.LOADED_FROM_CACHE;

                    if (postProcess != null) {
                        for (PostProcess p: postProcess) {
                            p.postProcess(info);
                        }
                    }

                    callback.report(null, info);
                }
                catch (OutOfMemoryError e) {
                    callback.report(new Exception(e), null);
                }
                catch (Exception e) {
                    callback.report(e, null);
                    try {
                        ion.responseCache.getFileCache().remove(transformKey);
                    } catch (Exception ex) {
                    }
                }
            }
        });
    }

    protected BitmapCallback(NetworkProcessor ion, String key, boolean put) {
        this.key = key;
        this.put = put;
        this.ion = ion;

        ion.bitmapsPending.tag(key, this);
    }

    final boolean put;

    boolean put() {
        return put;
    }

    protected void onReported() {
        ion.processDeferred();
    }

    protected void report(final Exception e, final BitmapInfo info) {
        AsyncServer.post(NetworkProcessor.mainHandler, new Runnable() {
            @Override
            public void run() {
                BitmapInfo result = info;
                if (result == null) {
                    // cache errors, unless they were cancellation exceptions
                    result = new BitmapInfo(key, null, null, new Point());
                    result.exception = e;
                    if (!(e instanceof CancellationException))
                        ion.getBitmapCache().put(result);
                } else if (put()) {
                    ion.getBitmapCache().put(result);
                }
                else {
                    ion.getBitmapCache().putSoft(result);
                }

                final ArrayList<FutureCallback<BitmapInfo>> callbacks = ion.bitmapsPending.remove(key);
                if (callbacks == null || callbacks.size() == 0) {
                    onReported();
                    return;
                }

                for (FutureCallback<BitmapInfo> callback : callbacks) {
                    callback.onCompleted(e, result);
                }
                onReported();
            }
        });

        // attempt to smart cache stuff to disk
        if (info == null || info.originalSize == null || info.decoder != null
            // don't cache anything that requests not to be cached
            || !put
            // don't cache dead bitmaps
            || info.bitmap == null
            // don't cache gifs
            || info.gifDecoder != null
            // too big
            || info.sizeOf() > 512 * 512 * 4) {
            return;
        }

        saveBitmapSnapshot(ion, info);
    }
}
