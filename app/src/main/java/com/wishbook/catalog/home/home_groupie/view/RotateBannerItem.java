package com.wishbook.catalog.home.home_groupie.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.widget.LoopViewPager;
import com.wishbook.catalog.commonmodels.responses.ImageBanner;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.home.Fragment_Home2;
import com.wishbook.catalog.rest.CampaignLogApi;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class RotateBannerItem extends Item<RotateBannerItem.RotateViewHolder> {


    Fragment fragment;
    Context mContext;
    private ArrayList<Response_Promotion> data;
    Timer timer;
    Handler handler;
    int currentPage = 0;


    public RotateBannerItem(ArrayList<Response_Promotion> data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.groupie_rotate_banner_item;
    }


    @NonNull
    @Override
    public RotateBannerItem.RotateViewHolder createViewHolder(@NonNull View itemView) {
        return new RotateBannerItem.RotateViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull RotateViewHolder viewHolder, int position) {
        viewHolder.viewPager.setVisibility(View.VISIBLE);
        final ArrayList<ImageBanner> imageBanners = new ArrayList<ImageBanner>();
        for (Response_Promotion response_promotion : data) {
            imageBanners.add(response_promotion.getImage());
        }

        final MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, imageBanners, data);
        viewHolder.viewPager.setAdapter(myCustomPagerAdapter);
        viewHolder.viewPager.setCurrentItem(0, false); // set current item in the adapter to middle
        viewHolder.indicator.setViewPager(viewHolder.viewPager);
        final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 6000; // time in milliseconds between successive task executions.
        handler = new Handler();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imageBanners.size()) {
                    currentPage = 0;
                }
                viewHolder.viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public class RotateViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.viewPager)
        LoopViewPager viewPager;

        @BindView(R.id.indicator)
        CircleIndicator indicator;


        private BannerItem.ItemClickListener clickListener;

        public RotateViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(BannerItem.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<ImageBanner> images = new ArrayList<>();
        LayoutInflater layoutInflater;
        private ArrayList<Response_Promotion> response_promotions;

        public MyCustomPagerAdapter(Context context, ArrayList<ImageBanner> images, ArrayList<Response_Promotion> promotion) {
            this.context = context;
            this.images = images;
            this.response_promotions = promotion;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);

            final int newposition = position % response_promotions.size();
            SimpleDraweeView imageView = (SimpleDraweeView) itemView.findViewById(R.id.imageView);
            if (images.get(newposition).getFull_size().contains(".gif")) {
                StaticFunctions.loadFresco(context, images.get(newposition).getFull_size(), imageView);
            } else {
                StaticFunctions.loadFresco(context, images.get(newposition).getBanner(), imageView);
            }

            container.addView(itemView);
            //listening to image click
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (response_promotions.get(position).getLanding_page_type() != null) {
                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "Top Banner");
                        if (response_promotions.get(position).getLanding_page_type() != null) {
                            prop.put("landing_page_type", response_promotions.get(position).getLanding_page_type());
                        }

                        if (response_promotions.get(position).getLanding_page() != null) {
                            prop.put("landing_page", response_promotions.get(position).getLanding_page());
                        }
                        if (fragment instanceof Fragment_Home2)
                            ((Fragment_Home2) fragment).sendBannerClickAnalytics(prop);
                        new CampaignLogApi((Activity) mContext, response_promotions.get(position).getCampaign_name());
                        Application_Singleton singleton = new Application_Singleton();
                       /* Tracker t = singleton.getGoogleAnalyticsTracker();
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(mContext.getString(R.string.banner))
                                .setAction(mContext.getString(R.string.banner_click))
                                .setLabel(response_promotions.get(position).getLanding_page() != null ? response_promotions.get(position).getLanding_page().toLowerCase() : "")
                                .setValue(response_promotions.get(position).getId())
                                .build());*/
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("type", response_promotions.get(position).getLanding_page_type().toLowerCase());
                        if (!response_promotions.get(position).getLanding_page_type().equals("Webview")) {
                            hashMap.put("page", response_promotions.get(position).getLanding_page() != null ? response_promotions.get(position).getLanding_page().toLowerCase() : "");
                        } else {
                            hashMap.put("page", response_promotions.get(position).getLanding_page());
                        }
                        if (response_promotions.get(position).getLanding_page_type().toLowerCase().equals("deep_link")) {
                            if (response_promotions.get(position).getLanding_page() != null) {
                                if (response_promotions.get(position).getLanding_page().contains("/?")) {
                                    hashMap.putAll(SplashScreen.getQueryString(Uri.parse(response_promotions.get(position).getLanding_page())));
                                } else {
                                    String deep_link = response_promotions.get(position).getLanding_page().toLowerCase();
                                    for (final String entry : deep_link.split("&")) {
                                        final String[] parts = entry.split("=");
                                        hashMap.put(parts[0], parts[1]);
                                    }
                                }
                            }
                        }
                        new DeepLinkFunction(hashMap, context);
                    }
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void unbind(@NonNull RotateViewHolder viewHolder) {
        super.unbind(viewHolder);
        if (timer != null) {
            timer.cancel();
        }

        currentPage = 0;
    }
}
