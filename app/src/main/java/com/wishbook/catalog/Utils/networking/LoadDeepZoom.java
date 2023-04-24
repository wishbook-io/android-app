package com.wishbook.catalog.Utils.networking;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;

import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.util.FileCache;
import com.wishbook.catalog.Utils.networking.async.util.StreamUtility;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.gif.GifDecoder;
import com.wishbook.catalog.Utils.networking.gif.GifFrame;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

/**
 * Created by vignesh on 1/5/14.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class LoadDeepZoom extends LoadBitmapEmitter implements FutureCallback<Response<File>> {
    FileCache fileCache;
    public LoadDeepZoom(NetworkProcessor ion, String urlKey, boolean animateGif, FileCache fileCache) {
        super(ion, urlKey, true, animateGif);
        this.fileCache = fileCache;
    }

    @Override
    public void onCompleted(Exception e, final Response<File> response) {
        if (e == null)
            e = response.getException();

        if (e != null) {
            report(e, null);
            return;
        }

        final File tempFile = response.getResult();

        if (ion.bitmapsPending.tag(key) != this) {
//            Log.d("NetworkProcessorBitmapLoader", "Bitmap load cancelled (no longer needed)");
            return;
        }

        NetworkProcessor.getBitmapLoadExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                FileInputStream fin = null;
                try {
                    File file;
                    // file cache will be null if the file is on the local file system already
                    if (fileCache != null) {
                        fileCache.commitTempFiles(key, tempFile);
                        file = fileCache.getFile(key);
                    }
                    else {
                        // local file system, use the "temp" file as the source.
                        file = tempFile;
                    }
                    BitmapFactory.Options options = ion.getBitmapCache().prepareBitmapOptions(file, 0, 0);
                    final Point size = new Point(options.outWidth, options.outHeight);
                    if (animateGif && TextUtils.equals("image/gif", options.outMimeType)) {
                        fin = fileCache.get(key);

                        GifDecoder gifDecoder = new GifDecoder(ByteBuffer.wrap(StreamUtility.readToEndAsArray(fin)));
                        GifFrame frame = gifDecoder.nextFrame();
                        BitmapInfo info = new BitmapInfo(key, options.outMimeType, frame.image, size);
                        info.gifDecoder = gifDecoder;
                        report(null, info);
                        return;
                    }

                    BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(file.toString(), false);
                    Bitmap bitmap = decoder.decodeRegion(new Rect(0, 0, size.x, size.y), options);
                    if (bitmap == null)
                        throw new Exception("unable to load decoder");

                    BitmapInfo info = new BitmapInfo(key, options.outMimeType, bitmap, size);
                    info.decoder = decoder;
                    info.decoderFile = file;
                    info.servedFrom = response.getServedFrom();
                    report(null, info);
                } catch (Exception e) {
                    report(e, null);
                }
                finally {
                    StreamUtility.closeQuietly(fin);
                }
            }
        });
    }
}
