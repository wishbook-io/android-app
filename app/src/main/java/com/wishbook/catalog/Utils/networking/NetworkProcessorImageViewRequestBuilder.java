package com.wishbook.catalog.Utils.networking;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.builder.AnimateGifMode;
import com.wishbook.catalog.Utils.networking.builder.Builders;
import com.wishbook.catalog.Utils.networking.builder.ImageViewFutureBuilder;
import com.wishbook.catalog.Utils.networking.future.ImageViewFuture;

/**
 * Created by vignesh on 7/4/14.
 */
public class NetworkProcessorImageViewRequestBuilder extends NetworkProcessorBitmapRequestBuilder implements Builders.IV.F, ImageViewFutureBuilder {
    Drawable placeholderDrawable;
    int placeholderResource;
    Drawable errorDrawable;
    int errorResource;
    Animation inAnimation;
    Animation loadAnimation;
    int loadAnimationResource;
    int inAnimationResource;
    ContextReference.ImageViewContextReference imageViewPostRef;
    boolean fadeIn = true;
    boolean crossfade;
    BitmapDrawableFactory bitmapDrawableFactory = BitmapDrawableFactory.DEFAULT;

    public NetworkProcessorImageViewRequestBuilder(NetworkProcessorRequestBuilder builder) {
        super(builder);
    }

    public NetworkProcessorImageViewRequestBuilder(NetworkProcessor ion) {
        super(ion);
    }

    @Override
    void reset() {
        super.reset();
        fadeIn = true;
        crossfade = false;
        imageViewPostRef = null;
        placeholderDrawable = null;
        bitmapDrawableFactory = BitmapDrawableFactory.DEFAULT;
        placeholderResource = 0;
        errorDrawable = null;
        errorResource = 0;
        inAnimation = null;
        inAnimationResource = 0;
        loadAnimation = null;
        loadAnimationResource = 0;
    }

    @Override
    protected NetworkProcessorRequestBuilder ensureBuilder() {
        if (builder == null)
            builder = new NetworkProcessorRequestBuilder(ContextReference.fromContext(imageViewPostRef.getContext().getApplicationContext()), ion);
        return builder;
    }

    @Override
    public ImageViewFuture load(String uri) {
        ensureBuilder();
        builder.load(uri);
        return intoImageView(imageViewPostRef.get());
    }

    @Override
    public Future<ImageView> load(String method, String url) {
        ensureBuilder();
        builder.load(method, url);
        return intoImageView(imageViewPostRef.get());
    }

    NetworkProcessorImageViewRequestBuilder withImageView(ImageView imageView) {
        if (imageViewPostRef == null || imageViewPostRef.get() != imageView)
            imageViewPostRef = new ContextReference.ImageViewContextReference(imageView);
        return this;
    }

    private NetworkProcessorDrawable setNetworkProcessorDrawable(ImageView imageView, BitmapFetcher bitmapFetcher, ResponseServedFrom servedFrom) {
        BitmapInfo info = null;
        if (bitmapFetcher != null)
            info = bitmapFetcher.info;
        if (info != null)
            bitmapFetcher = null;

        NetworkProcessorDrawable ret = NetworkProcessorDrawable.getOrCreateNetworkProcessorDrawable(imageView)
        .ion(ion)
        .setBitmap(info, servedFrom)
        .setBitmapFetcher(bitmapFetcher)
        .setRepeatAnimation(animateGifMode == AnimateGifMode.ANIMATE)
        .setSize(resizeWidth, resizeHeight)
        .setError(errorResource, errorDrawable)
        .setPlaceholder(placeholderResource, placeholderDrawable)
        .setFadeIn(fadeIn || crossfade)
        .setBitmapDrawableFactory(bitmapDrawableFactory)
        .updateLayers();
        imageView.setImageDrawable(ret);
        return ret;
    }

    @Override
    public NetworkProcessorBitmapRequestBuilder fadeIn(boolean fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static boolean getAdjustViewBounds_16(ImageView imageView) {
        return imageView.getAdjustViewBounds();
    }

    private static boolean getAdjustViewBounds(ImageView imageView) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && getAdjustViewBounds_16(imageView);
    }

    @Override
    public ImageViewFuture intoImageView(ImageView imageView) {
        assert Thread.currentThread() == Looper.getMainLooper().getThread();
        if (imageView == null)
            throw new NullPointerException("imageView");

        // no uri? just set a placeholder and bail
        if (builder.uri == null) {
            setNetworkProcessorDrawable(imageView, null, ResponseServedFrom.LOADED_FROM_NETWORK).cancel();
            return ImageViewFutureImpl.FUTURE_IMAGEVIEW_NULL_URI;
        }

        withImageView(imageView);

        if (crossfade) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof NetworkProcessorDrawable) {
                NetworkProcessorDrawable ionDrawable = (NetworkProcessorDrawable)drawable;
                drawable = ionDrawable.getCurrentDrawable();
            }
            placeholder(drawable);
        }

        int sampleWidth = resizeWidth;
        int sampleHeight = resizeHeight;
        // see if we need default transforms, or this if the imageview
        // will request the actual size on measure
        if (resizeHeight == 0 && resizeWidth == 0 && !getAdjustViewBounds(imageView)) {
            // set the sample size hints from the current dimensions
            // but don't actually apply a transform.
            // this may be zero, in which case NetworkProcessorDrawable
            // will eventually try again with real dimensions
            // during draw.
            sampleWidth = imageView.getMeasuredWidth();
            sampleHeight = imageView.getMeasuredHeight();
        }
        else {
            addDefaultTransform();
        }

        // executeCache the request, see if we get a bitmap from cache.
        BitmapFetcher bitmapFetcher = executeCache(sampleWidth, sampleHeight);
        if (bitmapFetcher.info != null) {
            doAnimation(imageView, null, 0);
            NetworkProcessorDrawable drawable = setNetworkProcessorDrawable(imageView, bitmapFetcher, ResponseServedFrom.LOADED_FROM_MEMORY);
            drawable.cancel();
            ImageViewFutureImpl imageViewFuture = ImageViewFutureImpl.getOrCreateImageViewFuture(imageViewPostRef, drawable)
            .setInAnimation(inAnimation, inAnimationResource)
            .setScaleMode(scaleMode);
            ImageViewFutureImpl.applyScaleMode(imageView, scaleMode);
            imageViewFuture.reset();
            imageViewFuture.setComplete(bitmapFetcher.info.exception, imageView);
            return imageViewFuture;
        }

        NetworkProcessorDrawable drawable = setNetworkProcessorDrawable(imageView, bitmapFetcher, ResponseServedFrom.LOADED_FROM_NETWORK);
        doAnimation(imageView, loadAnimation, loadAnimationResource);
        ImageViewFutureImpl imageViewFuture = ImageViewFutureImpl.getOrCreateImageViewFuture(imageViewPostRef, drawable)
        .setInAnimation(inAnimation, inAnimationResource)
        .setScaleMode(scaleMode);
        imageViewFuture.reset();

        return imageViewFuture;
    }

    private Drawable getImageViewDrawable() {
        ImageView iv = imageViewPostRef.get();
        if (iv == null)
            return null;
        return iv.getDrawable();
    }

    @Override
    public Bitmap getBitmap() {
        Drawable d = getImageViewDrawable();
        if (d == null)
            return null;
        if (d instanceof BitmapDrawable)
            return ((BitmapDrawable)d).getBitmap();
        if (!(d instanceof NetworkProcessorDrawable))
            return null;
        NetworkProcessorDrawable id = (NetworkProcessorDrawable)d;
        d = id.getCurrentDrawable();
        if (d instanceof BitmapDrawable)
            return ((BitmapDrawable)d).getBitmap();
        return null;
    }

    @Override
    public BitmapInfo getBitmapInfo() {
        Drawable d = getImageViewDrawable();
        if (d == null)
            return null;
        if (!(d instanceof NetworkProcessorDrawable))
            return null;
        NetworkProcessorDrawable id = (NetworkProcessorDrawable)d;
        return id.getBitmapInfo();
    }


    @Override
    public NetworkProcessorImageViewRequestBuilder crossfade(boolean crossfade) {
        this.crossfade = crossfade;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder placeholder(Drawable drawable) {
        placeholderDrawable = drawable;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder placeholder(int resourceId) {
        placeholderResource = resourceId;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder error(Drawable drawable) {
        errorDrawable = drawable;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder error(int resourceId) {
        errorResource = resourceId;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder animateIn(Animation in) {
        inAnimation = in;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder animateLoad(Animation load) {
        loadAnimation = load;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder animateLoad(int animationResource) {
        loadAnimationResource = animationResource;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder animateIn(int animationResource) {
        inAnimationResource = animationResource;
        return this;
    }

    @Override
    public NetworkProcessorImageViewRequestBuilder bitmapDrawableFactory(BitmapDrawableFactory bitmapDrawableFactory) {
        this.bitmapDrawableFactory = bitmapDrawableFactory;
        return this;
    }
}
