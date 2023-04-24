package com.wishbook.catalog.Utils.networking;

import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;

/**
 * Created by vignesh on 7/1/14.
 */
public class ImageViewBitmapInfo {
    Exception exception;
    public Exception getException() {
        return exception;
    }

    ImageView imageView;
    public ImageView getImageView() {
        return imageView;
    }

    BitmapInfo info;
    public BitmapInfo getBitmapInfo() {
        return info;
    }
}
