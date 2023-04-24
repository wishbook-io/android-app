package com.wishbook.catalog.Utils.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;

import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.NetworkProcessorBitmapCache;

/**
 * Created by vignesh on 1/29/14.
 */
public class LoadBitmapRegion extends BitmapCallback {
    public LoadBitmapRegion(final NetworkProcessor ion, final String key, final BitmapRegionDecoder decoder, final Rect region, final int inSampleSize) {
        super(ion, key, true);

        NetworkProcessor.getBitmapLoadExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = NetworkProcessorBitmapCache.loadRegion(decoder, region, inSampleSize);
                    if (bitmap == null)
                        throw new Exception("failed to load bitmap region");
                    BitmapInfo info = new BitmapInfo(key, null, bitmap, new Point(bitmap.getWidth(), bitmap.getHeight()));
                    report(null, info);
                }
                catch (Exception e) {
                    report(e, null);
                }
            }
        });
    }
}
