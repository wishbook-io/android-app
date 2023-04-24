package com.wishbook.catalog.stories;

import android.graphics.drawable.Animatable;
import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

public class MyControllerListener extends BaseControllerListener<ImageInfo> {


    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);
    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        Log.i("DraweeUpdate", "Image is partly loaded! (maybe it's a progressive JPEG?)");
        if (imageInfo != null) {
            int quality = imageInfo.getQualityInfo().getQuality();
            Log.i("DraweeUpdate", "Image quality (number scans) is: " + quality);
        }
        super.onIntermediateImageSet(id, imageInfo);
    }

    @Override
    public void onFailure(String id, Throwable throwable) {
        super.onFailure(id, throwable);
    }
}
