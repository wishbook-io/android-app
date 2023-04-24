package com.wishbook.catalog.Utils.networking.builder;

import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.future.ImageViewFuture;

/**
* Created by vignesh on 5/30/13.
*/
public interface ImageViewFutureBuilder {
    /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView
     * @param imageView ImageView to set once the request completes
     * @return
     */
    ImageViewFuture intoImageView(ImageView imageView);
}
