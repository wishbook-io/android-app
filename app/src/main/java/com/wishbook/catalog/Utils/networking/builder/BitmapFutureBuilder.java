package com.wishbook.catalog.Utils.networking.builder;

import android.graphics.Bitmap;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.LocallyCachedStatus;

/**
* Created by vignesh on 5/30/13.
*/
public interface BitmapFutureBuilder {
    /**
     * Perform the request and get the result as a Bitmap
     * @return
     */
    Future<Bitmap> asBitmap();

    /**
     * Attempt to immediately retrieve the cached Bitmap info from the memory cache
     * @return
     */
    BitmapInfo asCachedBitmap();

    /**
     * Check whether the Bitmap can be loaded from either the file or memory cache
     * @return
     */
    LocallyCachedStatus isLocallyCached();
}
