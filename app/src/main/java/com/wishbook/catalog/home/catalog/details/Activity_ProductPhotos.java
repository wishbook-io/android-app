package com.wishbook.catalog.home.catalog.details;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.commonmodels.RequestRating;
import com.wishbook.catalog.commonmodels.responses.Image;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.home.catalog.adapter.PhotoPagerAdapter;
import com.wishbook.catalog.home.catalog.adapter.ProductReviewPagerAdapter;
import com.wishbook.catalog.home.catalog.adapter.ReviewPhotosPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_ProductPhotos extends AppCompatActivity {


    private Context mContext;

    @BindView(R.id.pager)
    ExtendedViewPager mViewPager;

    @BindView(R.id.toolbar_top)
    Toolbar toolbar;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.txt_current_image_count)
    TextView txt_current_image_count;

    public SharedPreferences pref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_ProductPhotos.this;
        setContentView(R.layout.activity_product_photos);
        ButterKnife.bind(this);
        initView();
        initListner();
        pref = StaticFunctions.getAppSharedPreferences(Activity_ProductPhotos.this);
        pref.edit().putString("hidden", "no").apply();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.my_statusbar_color));
        }
    }

    public void initView() {

    }

    public void initListner() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getSerializableExtra("photos") != null) {
           ArrayList<Photos> photosArrayList =  (ArrayList<Photos>) getIntent().getSerializableExtra("photos");
            final PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(Activity_ProductPhotos.this, mViewPager, toolbar, (ArrayList<Photos>) getIntent().getSerializableExtra("photos"));
            mViewPager.setAdapter(photoPagerAdapter);
            int selected_position = 0;
            if (getIntent() != null) {
                selected_position = getIntent().getIntExtra("position", 0);
            }
            mViewPager.setCurrentItem(selected_position);
            txt_current_image_count.setVisibility(View.VISIBLE);
            try {
                txt_current_image_count.setText(""+(selected_position+1)+"/"+photosArrayList.size()+"");
            } catch (Exception e) {
                e.printStackTrace();
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    txt_current_image_count.setText(""+(position+1)+"/"+photosArrayList.size()+"");
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else if (getIntent().getSerializableExtra("review") != null) {
            final ProductReviewPagerAdapter reviewPagerAdapter = new ProductReviewPagerAdapter(Activity_ProductPhotos.this, mViewPager, toolbar, (RequestRating) getIntent().getSerializableExtra("review"));
            mViewPager.setAdapter(reviewPagerAdapter);
            int selected_position = 0;
            if (getIntent() != null) {
                selected_position = getIntent().getIntExtra("position", 0);
            }
            mViewPager.setCurrentItem(selected_position);
        } else if (getIntent().getSerializableExtra("review_photos") != null) {
            final ReviewPhotosPagerAdapter reviewPhotosPagerAdapter = new ReviewPhotosPagerAdapter(Activity_ProductPhotos.this, mViewPager, toolbar, (ArrayList<Image>) getIntent().getSerializableExtra("review_photos"));
            mViewPager.setAdapter(reviewPhotosPagerAdapter);
            int selected_position = 0;
            if (getIntent() != null) {
                selected_position = getIntent().getIntExtra("position", 0);
            }
            mViewPager.setCurrentItem(selected_position);
        }
    }


}
