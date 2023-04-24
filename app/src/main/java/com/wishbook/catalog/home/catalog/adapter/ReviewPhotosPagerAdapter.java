package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.commonmodels.responses.Image;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewPhotosPagerAdapter extends PagerAdapter {


    @BindView(R.id.left_nav)
    ImageButton left;

    @BindView(R.id.right_nav)
    ImageButton right;

    private Context context;
    private ExtendedViewPager mViewPager;
    private ArrayList<Image> images;
    LayoutInflater mLayoutInflater;
    Toolbar toolbar;

    private SharedPreferences pref;

    public ReviewPhotosPagerAdapter(Context context, ExtendedViewPager mViewPager, Toolbar toolbar, ArrayList<Image> images) {
        this.context = context;
        this.mViewPager = mViewPager;
        this.images = images;
        this.toolbar = toolbar;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = mLayoutInflater.inflate(R.layout.photo_item, container, false);
        ButterKnife.bind(this, view);
        pref = StaticFunctions.getAppSharedPreferences(context);
        final ZoomableDraweeView imageView = (ZoomableDraweeView) view.findViewById(R.id.prod_img);
        String image = images.get(position).getThumbnail_medium();

        if (image != null && !image.equals("")) {
            ImageUtils.loadFrescoZoomableProductCarousel(context, image, imageView, null, toolbar, left, right, pref);
        }
        if (pref.getString("hidden", "no").equals("no")) {
            toolbar.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
        }

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = mViewPager.getCurrentItem();
                if (tab > 0) {

                    tab--;
                    mViewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    mViewPager.setCurrentItem(tab);
                }
            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = mViewPager.getCurrentItem();
                tab++;
                mViewPager.setCurrentItem(tab);
            }
        });

        bindViewPagerListener(left, right);
        container.addView(view);
        return view;
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int selectedProductPostion, Object object) {
        container.removeView((FrameLayout) object);
    }

    public void bindViewPagerListener(ImageButton left, ImageButton right) {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    if (position == 0) {
                        left.setVisibility(View.GONE);
                    } else {
                        left.setVisibility(View.VISIBLE);
                    }

                    if (position == (images.size() - 1)) {
                        right.setVisibility(View.GONE);
                    } else {
                        right.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
