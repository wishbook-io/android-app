package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.commonmodels.RequestRating;
import com.wishbook.catalog.commonmodels.responses.Image;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductReviewPagerAdapter extends PagerAdapter {


    @BindView(R.id.left_nav)
    ImageButton left;

    @BindView(R.id.right_nav)
    ImageButton right;

    @BindView(R.id.txt_buyer_name)
    TextView txt_buyer_name;

    @BindView(R.id.txt_review)
    TextView txt_review;

    @BindView(R.id.txt_rating)
    TextView txt_rating;


    @BindView(R.id.descontent)
    RelativeLayout descontent;


    private Context context;
    private ExtendedViewPager mViewPager;
    private ArrayList<Image> photos;
    private RequestRating rating;
    LayoutInflater mLayoutInflater;
    Toolbar toolbar;

    private SharedPreferences pref;

    public ProductReviewPagerAdapter(Context context, ExtendedViewPager mViewPager, Toolbar toolbar, RequestRating rating) {
        this.context = context;
        this.mViewPager = mViewPager;
        this.rating = rating;
        this.photos = rating.getReview_photos();
        this.toolbar = toolbar;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = mLayoutInflater.inflate(R.layout.review_pager_item, container, false);
        ButterKnife.bind(this, view);
        pref = StaticFunctions.getAppSharedPreferences(context);
        final ZoomableDraweeView imageView = (ZoomableDraweeView) view.findViewById(R.id.prod_img);
        String image = photos.get(position).getThumbnail_medium();

        if (image != null && !image.equals("")) {
            ImageUtils.loadFrescoZoomableProductCarousel(context, image, imageView, descontent, toolbar, left, right, pref);
        }
        bindRatingView(view);
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
        return photos.size();
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

                    if (position == (photos.size() - 1)) {
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

    public void bindRatingView(View view) {
        StringBuffer buyername = new StringBuffer();
        if (rating.getFirst_name() != null && !rating.getFirst_name().isEmpty()) {
            buyername.append(rating.getFirst_name());
        }
        if (rating.getLast_name() != null && !rating.getLast_name().isEmpty()) {
            buyername.append(" " + rating.getLast_name());
        }
        txt_buyer_name.setText(buyername);
        txt_rating.setText(rating.getRating());
        if (rating.getReview() != null && !rating.getReview().isEmpty()) {
            txt_review.setVisibility(View.VISIBLE);
            txt_review.setText(rating.getReview());
        } else {
            txt_review.setVisibility(View.GONE);
        }

    }
}
