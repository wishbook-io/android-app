package com.wishbook.catalog.Utils.networking;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.future.SimpleFuture;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.future.ImageViewFuture;

class ImageViewFutureImpl extends TransformFuture<ImageView, NetworkProcessorDrawable> implements ImageViewFuture {
    public static final ImageViewFutureImpl FUTURE_IMAGEVIEW_NULL_URI = new ImageViewFutureImpl() {
        {
            setComplete(new NullPointerException("uri"));
        }
    };

    private ScaleMode scaleMode;
    private Animation inAnimation;
    private int inAnimationResource;
    private ContextReference.ImageViewContextReference imageViewRef;

    public static ImageViewFutureImpl getOrCreateImageViewFuture(ContextReference.ImageViewContextReference imageViewRef, NetworkProcessorDrawable drawable) {
        ImageViewFutureImpl ret;

        if (drawable.getLoadCallback() instanceof ImageViewFutureImpl)
            ret = (ImageViewFutureImpl)drawable.getLoadCallback();
        else
            ret = new ImageViewFutureImpl();

        drawable.setLoadCallback(ret);
        ret.imageViewRef = imageViewRef;
        return ret;
    }

    @Override
    protected void transform(NetworkProcessorDrawable result) throws Exception {
        // need to make sure that the NetworkProcessorDrawable calling this future
        // is still needed. It may have changed if the user manually
        // called setDrawable on the ImageView, etc.
        final ImageView imageView = imageViewRef.get();

        // check the imageview and the activity context
        if (null != imageViewRef.isAlive() || imageView == null) {
            cancelSilently();
            return;
        }

        if (imageView.getDrawable() != result) {
            // imageview is now waiting for something else now... cancel
            cancelSilently();
            return;
        }

        // retrigger the intrinsic dimension check on the drawable
        BitmapInfo info = result.getBitmapInfo();
        if (info != null && info.exception == null) {
            applyScaleMode(imageView, scaleMode);
        }
        NetworkProcessorBitmapRequestBuilder.doAnimation(imageView, inAnimation, inAnimationResource);
        imageView.setImageDrawable(null);
        imageView.setImageDrawable(result);
        setComplete(imageView);
    }

    @Override
    public Future<ImageViewBitmapInfo> withBitmapInfo() {
        final SimpleFuture<ImageViewBitmapInfo> ret = new SimpleFuture<ImageViewBitmapInfo>();
        setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                ImageViewBitmapInfo val = new ImageViewBitmapInfo();
                Drawable d = null;
                if (result != null)
                    d = result.getDrawable();
                if (d instanceof NetworkProcessorDrawable) {
                    NetworkProcessorDrawable id = (NetworkProcessorDrawable) d;
                    val.info = id.getBitmapInfo();
                }
                val.exception = e;
                val.imageView = result;
                ret.setComplete(val);
            }
        });
        ret.setParent(this);
        return ret;
    }

    public ImageViewFutureImpl setInAnimation(Animation inAnimation, int inAnimationResource) {
        this.inAnimation = inAnimation;
        this.inAnimationResource = inAnimationResource;
        return this;
    }

    public ImageViewFutureImpl setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        return this;
    }

    public static void applyScaleMode(ImageView imageView, ScaleMode scaleMode) {
        if (scaleMode == null)
            return;
        switch (scaleMode) {
            case CenterCrop:
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case FitCenter:
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case CenterInside:
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
            case FitXY:
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
        }
    }
}