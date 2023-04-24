package com.wishbook.catalog.frescoZoomable;


import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.stories.StoryStatusView;


public class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener implements ZoomableDraweeView.OnTouchListener  {
    private static final int DURATION_MS = 300;
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;

    private final ZoomableDraweeView mDraweeView;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1;
    private boolean mDoubleTapScroll = false;

    private static final int SWIPE_THRESHOLD = 300;
    private static final int SWIPE_VELOCITY_THRESHOLD = 500;

    private static final int LEFT_TAP_AREA_PERCENTAGE = 40;

    private boolean onPressed = false;

    private StoryStatusView storyStatusView;
    private UpdateActivityListener updateActivityListener;

    public DoubleTapGestureListener(ZoomableDraweeView zoomableDraweeView) {
        mDraweeView = zoomableDraweeView;
        this.onPressed = false;
    }

    public DoubleTapGestureListener(ZoomableDraweeView mDraweeView, StoryStatusView storyStatusView) {
        this.mDraweeView = mDraweeView;
        this.storyStatusView = storyStatusView;
        this.onPressed = false;
        Log.e("Gesture", "DoubleTap Constructor:  ");
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("Gesture", "onSingleTapConfirmed:  " + e);
        return super.onSingleTapConfirmed(e);
    }


    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("Gesture", "onShowPress: ");
        super.onShowPress(e);
        if(storyStatusView!=null){
            storyStatusView.pause();
            onPressed = true;
        }

    }

    public void onLongPress(MotionEvent e) {
        Log.d("Gesture", "onLongPress:  " + e);

    }





    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("Gesture", "onDown: ");

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(storyStatusView!=null){
                    storyStatusView.pause();
                }
                break;
        }

        return true;
    }






    public boolean onSingleTapUp(MotionEvent e) {

        //not in use
        Log.d("Gesture", "onSingleTapUp:  " + e);
        Log.d("Gesture", "onSingleTapUp: storyStatusView " + storyStatusView.toString());
        storyStatusView.resume();
        Log.d("Gesture", "X axis:  " + e.getX());
        Log.d("Gesture", "Left area:  " + (Application_Singleton.SCREEN_WIDTH * LEFT_TAP_AREA_PERCENTAGE / 100));
        if(!onPressed) {
            if (e.getX() < (Application_Singleton.SCREEN_WIDTH * LEFT_TAP_AREA_PERCENTAGE / 100)) {
                Log.d("Gesture", "Left Tap:  " + e.getX());
                if(storyStatusView!=null) {
                    storyStatusView.reverse();
                }
            } else {
                Log.d("Gesture", "Right Tap:  " + e.getX());
                if(storyStatusView!=null) {
                    storyStatusView.skip();
                }
            }
        }
        onPressed = false;
        return super.onSingleTapUp(e);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("Gesture", "onFling: e1" + e1 + "onFling: e2" + e2 + " velocityX:" + velocityX + "velocityY: " + velocityY);
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            Log.d("TAG", "onFling: diffx==>" + diffX + "\nDiffy==>" + diffY + "\n Velocity =>" + velocityY);
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        return true;
    }


    public void callOnResume() {
        if(storyStatusView!=null) {
            storyStatusView.resume();
        }
    }



/*

    public void onLongPress(MotionEvent e) {
        Log.d("Gesture", "onLongPress:  " + e);

    }


    public void onShowPress(MotionEvent e) {
        Log.d("Gesture", "onShowPress:  " + e);

    }



    public boolean onDoubleTap(MotionEvent e) {
        Log.d("Gesture", "onDoubleTap:  " + e);
        return super.onDoubleTap(e);
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("Gesture", "onDoubleTapEvent:  " + e);
        return super.onDoubleTapEvent(e);
    }





    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("Gesture", "onScroll: e1" + e1+ "onScroll: e2" + e2+ " distx:"+ distanceX + "disty: "+distanceY);
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("Gesture", "onFling: e1" + e1+ "onFling: e2" + e2+ " velocityX:"+ velocityX + "velocityY: "+velocityY);
        return true;
    }



    public boolean onDown(MotionEvent e) {
        Log.d("Gesture", "velocityY:  " + e);
        return super.onDown(e);
    }


    public boolean onContextClick(MotionEvent e) {
        Log.d("Gesture", "onContextClick:  " + e);
        return super.onContextClick(e);
    }*/











  /*  @Override
    public void onShowPress(MotionEvent e) {
        Log.d("TAG", "onShowPress: ");
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                storyStatusView.resume();
                break;

        }
        super.onShowPress(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("TAG", "onSingleTapUp: ");
        storyStatusView.resume();
        return super.onSingleTapUp(e);


    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("TAG", "onDown: ");
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                storyStatusView.pause();
                break;
            case MotionEvent.ACTION_UP:
                storyStatusView.resume();
                break;

        }

        return super.onDown(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("TAG", "onSingleTapConfirmed: ");
        storyStatusView.resume();
        return super.onSingleTapConfirmed(e);
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
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            Log.d("TAG", "onFling: diffx==>"+diffX  +"\nDiffy==>"+diffY + "\n Velocity =>"+velocityY);
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }*/

    public void onSwipeRight() {
        Log.d("TAG", "onSwipeRight: ===>");

    }

    public void onSwipeLeft() {
        Log.d("TAG", "onSwipeLeft: ===>");
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
        Log.d("TAG", "onSwipeBottom: ===>");
        if (updateActivityListener != null) {
            updateActivityListener.swipeGesture();
        }
    }


    private boolean shouldStartDoubleTapScroll(PointF viewPoint) {
        double dist = Math.hypot(
                viewPoint.x - mDoubleTapViewPoint.x,
                viewPoint.y - mDoubleTapViewPoint.y);
        return dist > DOUBLE_TAP_SCROLL_THRESHOLD;
    }

    private float calcScale(PointF currentViewPoint) {
        float dy = (currentViewPoint.y - mDoubleTapViewPoint.y);
        float t = 1 + Math.abs(dy) * 0.001f;
        return (dy < 0) ? mDoubleTapScale / t : mDoubleTapScale * t;
    }


    public void setUpdateActivityListener(UpdateActivityListener updateActivityListener) {
        this.updateActivityListener = updateActivityListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("Gesture", "onTouch:"+motionEvent.getActionMasked());

        if(motionEvent.getActionMasked() == MotionEvent.ACTION_UP){
            if(storyStatusView!=null) {
                storyStatusView.resume();
            }
        }
        return false;
    }

    public interface UpdateActivityListener {
        void swipeGesture();
    }
}
