package com.wishbook.catalog.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class AppIntroActivity extends AppCompatActivity {

    private AppIntroActivity mContext;

    private ViewPager viewPager;
    private PagerAdapter pAdapter;
    private LayoutInflater inflater;

    AppCompatButton btn_step;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);




        mContext = this;
        btn_step = findViewById(R.id.btn_step);
        inflater = LayoutInflater.from(this);
        viewPager = (ViewPager) findViewById(R.id.vp_step);
        pAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(pAdapter);
        initPoints(pAdapter.getCount());
        viewPager.addOnPageChangeListener(new PageListener());

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 4 - 1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        sendScreenEvent();
    }

    private LinearLayout layout_points;
    private List<ImageView> points;

    private void initPoints(int pagers) {

        layout_points = (LinearLayout) findViewById(R.id.layout_point);
        layout_points.removeAllViews();
        points = new ArrayList<ImageView>();
        int width = StaticFunctions.dpToPx(mContext, 16);
        int height = StaticFunctions.dpToPx(mContext, 8);
        for (int i = 0; i < pagers; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
                    height);
            iv.setImageResource(R.drawable.pager_point_light_grey);
            if (i == 0) {
                iv.setImageResource(R.drawable.pager_point_blue);
            } else {
                iv.setImageResource(R.drawable.pager_point_light_grey);
            }
            layout_points.addView(iv, lp);
            points.add(iv);
        }

        btn_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (ImageView iv : points) {
                iv.setImageResource(R.drawable.pager_point_light_grey);
            }
            try {
                points.get(arg0).setImageResource(R.drawable.pager_point_blue);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private List<View> mListViews;

        public MyViewPagerAdapter() {
            mListViews = new ArrayList<View>();
            View step0 = inflater.inflate(R.layout.item_step1, null);
            ImageView iv0 = (ImageView) step0.findViewById(R.id.iv_step);
            iv0.setImageResource(R.drawable.tutorial_one);
            TextView title_0 = step0.findViewById(R.id.iv_step_title);
            title_0.setText(getResources().getString(R.string.tutorial_one_text));
            TextView sub_title_0 = step0.findViewById(R.id.iv_step_subtitle);
            sub_title_0.setText(getResources().getString(R.string.tutorial_one_sub_text));


            View step1 = inflater.inflate(R.layout.item_step1, null);
            ImageView iv1 = (ImageView) step1.findViewById(R.id.iv_step);
            iv1.setImageResource(R.drawable.tutorial_two);
            TextView title_1 = step1.findViewById(R.id.iv_step_title);
            title_1.setText(getResources().getString(R.string.tutorial_two_text));
            TextView sub_title_1 = step1.findViewById(R.id.iv_step_subtitle);
            sub_title_1.setText(getResources().getString(R.string.tutorial_two_sub_text));


            View step2 = inflater.inflate(R.layout.item_step1, null);
            ImageView iv2 = (ImageView) step2.findViewById(R.id.iv_step);
            iv2.setImageResource(R.drawable.tutorial_three);
            TextView title_2 = step2.findViewById(R.id.iv_step_title);
            title_2.setText(getResources().getString(R.string.tutorial_three_text));
            TextView sub_title_2 = step2.findViewById(R.id.iv_step_subtitle);
            sub_title_2.setText(getResources().getString(R.string.all_over_india_subtext));

            mListViews.add(step0);
            mListViews.add(step1);
            mListViews.add(step2);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        try {
            PrefDatabaseUtils.setPrefAppintro(mContext, true);
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent homeintent = new Intent(mContext, Activity_Home.class);
        homeintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
        finish();
        overridePendingTransition(R.anim.animation_enter,
                R.anim.animation_leave);
    }


    // ############# Send Wishbook Tracker Analysis Data ##############################//

    public void sendScreenEvent() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Onboarding_screen");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("visited","true");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);
    }
}
