package com.wishbook.catalog.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wishbook.catalog.commonmodels.ImageLoadIssueDevices;
import com.wishbook.catalog.frescoZoomable.DoubleTapGestureListener;
import com.wishbook.catalog.frescoZoomable.ProductDoubleTabListner;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;
import com.wishbook.catalog.stories.CircleProgressBarDrawable;
import com.wishbook.catalog.stories.StoryActivity;
import com.wishbook.catalog.stories.StoryStatusView;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

public class ImageUtils {



    public static void loadImage(Context context, String imagepath, SimpleDraweeView image) {
        try {

            if(isExceptionDevices()) {
                if(imagepath.contains("https://")) {
                    imagepath = imagepath.replace("https://","http://");
                }
            }

            if (imagepath.contains(".gif")) {
                DraweeController controller =
                        Fresco.newDraweeControllerBuilder()
                                .setUri(imagepath)
                                .setAutoPlayAnimations(true)
                                .build();
                image.setController(controller);
            } else {
                Uri uri = Uri.parse(imagepath);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(image.getController())
                        .build();
                image.setController(controller);
                image.setImageURI(uri);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFresco1(Context context, String imagepath, ZoomableDraweeView zoomableDraweeView, StoryStatusView statusView) {
        try {
            if(isExceptionDevices()) {
                if(imagepath.contains("https://")) {
                    imagepath = imagepath.replace("https://","http://");
                }
            }
            Uri uri = Uri.parse(imagepath);
            zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
            // needed for double tap to zoom
            zoomableDraweeView.setIsLongpressEnabled(false);
            zoomableDraweeView.setIsZoomResetAvailable(true);
            DoubleTapGestureListener doubleTapGestureListener = new DoubleTapGestureListener(zoomableDraweeView, statusView);
            doubleTapGestureListener.setUpdateActivityListener((StoryActivity) context);

            zoomableDraweeView.setTapListener(doubleTapGestureListener);
            zoomableDraweeView.setOnTouchListener(doubleTapGestureListener);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setImageRequest(request)
                    .setCallerContext("ZoomableApp-MyPagerAdapter")
                    .build();
            zoomableDraweeView.setController(controller);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadFrescoZoombable(Context context, String imagepath, ZoomableDraweeView zoomableDraweeView) {
        try {
            if(isExceptionDevices()) {
                if(imagepath.contains("https://")) {
                    imagepath = imagepath.replace("https://","http://");
                }
            }
            Uri uri = Uri.parse(imagepath);
            zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
            zoomableDraweeView.setIsLongpressEnabled(false);
            zoomableDraweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(null, context));
            zoomableDraweeView.setIsZoomResetAvailable(false);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setImageRequest(request)
                    .setCallerContext("ZoomableApp-MyPagerAdapter")
                    .build();
            zoomableDraweeView.setController(controller);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadFrescoZoomableProductCarousel(Context context, String imagepath,
                                                         ZoomableDraweeView zoomableDraweeView,
                                                         RelativeLayout relativeLayout, Toolbar toolbar, ImageButton left, ImageButton right, SharedPreferences pref) {
        try {
            if(isExceptionDevices()) {
                if(imagepath.contains("https://")) {
                    imagepath = imagepath.replace("https://","http://");
                }
            }
            Uri uri = Uri.parse(imagepath);
            zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
            zoomableDraweeView.setIsLongpressEnabled(false);
            zoomableDraweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(null, context));
            zoomableDraweeView.setIsZoomResetAvailable(false);
            ProductDoubleTabListner productDoubleTabListner = new ProductDoubleTabListner(zoomableDraweeView);
            productDoubleTabListner.setOtherLayout(relativeLayout, toolbar, left, right, pref);
            zoomableDraweeView.setTapListener1(productDoubleTabListner);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setImageRequest(request)
                    .setCallerContext("ZoomableApp-MyPagerAdapter")
                    .build();
            zoomableDraweeView.setController(controller);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static boolean isExceptionDevices() {
        ArrayList<ImageLoadIssueDevices> imageLoadIssueDevicesArrayList = new ArrayList<>();
        ImageLoadIssueDevices imageLoadIssueDevices = new ImageLoadIssueDevices("CPH1909","oppo");
        imageLoadIssueDevicesArrayList.add(imageLoadIssueDevices);

        String brand = Build.BRAND;
        String model = Build.MODEL;

        for (ImageLoadIssueDevices infected_device:
             imageLoadIssueDevicesArrayList) {
            if(brand!=null && model!=null && infected_device.getBrand().equalsIgnoreCase(brand) && infected_device.getDevice_model().equalsIgnoreCase(model)){
                return true;
            }
        }
        return false;
    }
}
