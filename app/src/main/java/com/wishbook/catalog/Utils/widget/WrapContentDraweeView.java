package com.wishbook.catalog.Utils.widget;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by tech4 on 18/12/17.
 */

public class WrapContentDraweeView extends SimpleDraweeView {

    // we set a listener and update the view's aspect ratio depending on the loaded image
    private final ControllerListener listener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            updateViewSize(imageInfo);
        }

        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
            updateViewSize(imageInfo);
        }
    };

    public WrapContentDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public WrapContentDraweeView(Context context) {
        super(context);
    }

    public WrapContentDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapContentDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageURI(Uri uri, Object callerContext) {
        DraweeController controller = ((PipelineDraweeControllerBuilder) getControllerBuilder())
                .setControllerListener(listener)
                .setCallerContext(callerContext)
                .setUri(uri)
                .setOldController(getController())
                .build();
        setController(controller);
    }

    void updateViewSize(@Nullable ImageInfo imageInfo) {
        if (imageInfo != null) {
           // Log.i("Fresco", "updateViewSize: Width=>"+imageInfo.getWidth()+"\n Height=>"+imageInfo.getHeight());
            setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
        }
    }
}
