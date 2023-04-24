package com.wishbook.catalog.Utils.networking.builder;

import com.wishbook.catalog.Utils.networking.bitmap.PostProcess;
import com.wishbook.catalog.Utils.networking.bitmap.Transform;

/**
* Created by vignesh on 5/30/13.
*/
public interface BitmapBuilder<B extends BitmapBuilder<?>> {
    /**
     * Apply a transformation to a Bitmap
     * @param transform Transform to apply
     * @return
     */
    B transform(Transform transform);

    /**
     * Resize the bitmap to the given dimensions.
     * @param width
     * @param height
     * @return
     */
    B resize(int width, int height);

    /**
     * Resize the bitmap to the given width dimension, maintaining
     * the aspect ratio of the height.
     * @param width
     * @return
     */
    B resizeWidth(int width);

    /**
     * Resize the bitmap to the given height dimension, maintaining
     * the aspect ratio of the width.
     * @param height
     * @return
     */
    B resizeHeight(int height);

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that it fills the bounds, and crops
     * the extra.
     * @return
     */
    B centerCrop();

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that one dimension is as large as the requested
     * bounds.
     * @return
     */
    B fitCenter();

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation.
     * @return
     */
    B centerInside();

    /**
     * Fit the image inside the bounds specified by the ImageView or the resize
     * operation. This will scale the image so that both dimensions are as large as the
     * requested bounds.
     * @return
     */
    B fitXY();

    /**
     * Enable/disable automatic resizing to the dimensions of the device when loading the image.
     * @param smartSize
     * @return
     */
    B smartSize(boolean smartSize);

    /**
     * Process the bitmap on a background thread.
     * @param postProcess
     * @return
     */
    B postProcess(PostProcess postProcess);
}
