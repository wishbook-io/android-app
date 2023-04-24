package com.wishbook.catalog.Utils.networking.bitmap;

import android.graphics.Bitmap;

/**
 * Created by vignesh on 5/23/13.
 */
public interface Transform {
    Bitmap transform(Bitmap b);
    String key();
}
