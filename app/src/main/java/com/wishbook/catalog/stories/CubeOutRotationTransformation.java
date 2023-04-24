package com.wishbook.catalog.stories;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class CubeOutRotationTransformation implements ViewPager.PageTransformer {
    /*@Override
    public void transformPage(View page, float position) {

        if (position < -1) {    // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        } else if (position <= 0) {    // [-1,0]
            page.setAlpha(1);
            page.setPivotX(page.getWidth());
            page.setRotationY(-90 * Math.abs(position));

        } else if (position <= 1) {    // (0,1]
            page.setAlpha(1);
            page.setPivotX(0);
            page.setRotationY(90 * Math.abs(position));

        } else {
            page.setAlpha(0);

        }
    }*/


    protected void onTransform(View view, float position) {
        try {
            view.setPivotX(position < 0f ? view.getWidth() : 0f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(45f * position);
        }
        catch (Exception e){

        }
    }




    @Override
    public void transformPage(View view, float position) {

        try {
            onPreTransform(view, position);
            onTransform(view, position);
            onPostTransform(view, position);
        }
        catch (Exception e){

        }
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * visibility of the fragment to {@link android.view.View#GONE}. Returning false will force the fragment to {@link android.view.View#VISIBLE}.
     *
     * @return
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected boolean isPagingEnabled() {
        return true;
    }

    /**
     * Called each {@link #transformPage(android.view.View, float)} before {{@link #onTransform(android.view.View, float)} is called.
     *
     * @param view
     * @param position
     */
    protected void onPreTransform(View view, float position) {
        final float width = view.getWidth();

        view.setRotationX(0);
        view.setRotationY(0);
        view.setRotation(0);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setPivotX(0);
        view.setPivotY(0);
        view.setTranslationY(0);
        view.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            view.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
        } else {
            view.setAlpha(1f);
        }
    }

    /**
     * Called each {@link #transformPage(android.view.View, float)} call after {@link #onTransform(android.view.View, float)} is finished.
     *
     * @param view
     * @param position
     */
    protected void onPostTransform(View view, float position) {
    }
}

