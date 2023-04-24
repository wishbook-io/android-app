package com.wishbook.catalog.Utils.networking.builder;

import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.future.ImageViewFuture;

/**
* Created by vignesh on 5/30/13.
*/
public interface LoadImageViewFutureBuilder {
    /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView
     * @param url
     * @return
     */
    ImageViewFuture load(String url);

    /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView
     * @param method
     * @param url
     * @return
     */
    Future<ImageView> load(String method, String url);
}
