package com.wishbook.catalog.Utils.networking.future;

import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.ImageViewBitmapInfo;

/**
 * Created by vignesh on 7/1/14.
 */
public interface ImageViewFuture extends Future<ImageView> {
    Future<ImageViewBitmapInfo> withBitmapInfo();
}
