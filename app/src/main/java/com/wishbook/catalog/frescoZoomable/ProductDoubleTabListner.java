package com.wishbook.catalog.frescoZoomable;

import android.content.SharedPreferences;
import android.graphics.PointF;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class ProductDoubleTabListner extends GestureDetector.SimpleOnGestureListener implements ZoomableDraweeView.OnTouchListener {

    private ZoomableDraweeView mDraweeView;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1;
    private boolean mDoubleTapScroll = false;
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;
    private static final int DURATION_MS = 300;
    private boolean onPressed = false;

    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private ImageButton left, right;
    private SharedPreferences pref;

    public ProductDoubleTabListner(ZoomableDraweeView mDraweeView) {
        this.mDraweeView = mDraweeView;
        this.onPressed = false;
    }


    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        AbstractAnimatedZoomableController zc =
                (AbstractAnimatedZoomableController) mDraweeView.getZoomableController();
        PointF vp = new PointF(e.getX(), e.getY());
        PointF ip = zc.mapViewToImage(vp);
        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                mDoubleTapViewPoint.set(vp);
                mDoubleTapImagePoint.set(ip);
                mDoubleTapScale = zc.getScaleFactor();
                break;
            case MotionEvent.ACTION_MOVE:
                mDoubleTapScroll = mDoubleTapScroll || shouldStartDoubleTapScroll(vp);
                if (mDoubleTapScroll) {
                    float scale = calcScale(vp);
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
                hideShow();
                if (mDoubleTapScroll) {
                    float scale = calcScale(vp);
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                } else {
                    final float maxScale = zc.getMaxScaleFactor();
                    final float minScale = zc.getMinScaleFactor();
                    if (zc.getScaleFactor() < (maxScale + minScale) / 2) {
                        zc.zoomToPoint(
                                maxScale,
                                ip,
                                vp,
                                DefaultZoomableController.LIMIT_ALL,
                                DURATION_MS,
                                null);
                    } else {
                        zc.zoomToPoint(
                                minScale,
                                ip,
                                vp,
                                DefaultZoomableController.LIMIT_ALL,
                                DURATION_MS,
                                null);
                    }
                }
                mDoubleTapScroll = false;
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        hideShow();
        return super.onSingleTapUp(e);
    }

    private boolean shouldStartDoubleTapScroll(PointF viewPoint) {
        double dist = Math.hypot(
                viewPoint.x - mDoubleTapViewPoint.x,
                viewPoint.y - mDoubleTapViewPoint.y);
        return dist > DOUBLE_TAP_SCROLL_THRESHOLD;
    }

    public void hideShow() {
        try {
            if (relativeLayout.getVisibility() == View.INVISIBLE) {
                relativeLayout.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                pref.edit().putString("hidden", "no").commit();
            } else {
                relativeLayout.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.GONE);
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                pref.edit().putString("hidden", "yes").apply();
                pref.edit().putString("hidden", "yes").commit();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOtherLayout(RelativeLayout relativeLayout, Toolbar toolbar, ImageButton left, ImageButton right, SharedPreferences pref) {
        this.relativeLayout = relativeLayout;
        this.toolbar = toolbar;
        this.left = left;
        this.right = right;
        this.pref = pref;
    }

    private float calcScale(PointF currentViewPoint) {
        float dy = (currentViewPoint.y - mDoubleTapViewPoint.y);
        float t = 1 + Math.abs(dy) * 0.001f;
        return (dy < 0) ? mDoubleTapScale / t : mDoubleTapScale * t;
    }
}
