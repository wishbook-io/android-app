package com.wishbook.catalog.Utils.networking;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by vignesh on 4/25/15.
 */
public interface BitmapDrawableFactory {
    BitmapDrawableFactory DEFAULT = new BitmapDrawableFactory() {
        @Override
        public Drawable fromBitmap(Resources resources, Bitmap bitmap) {
            return new BitmapDrawable(resources, bitmap);
        }
    };
    Drawable fromBitmap(Resources resources, Bitmap bitmap);
}
