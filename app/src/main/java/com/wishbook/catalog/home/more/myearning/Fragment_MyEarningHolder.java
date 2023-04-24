package com.wishbook.catalog.home.more.myearning;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MyEarningHolder extends GATrackedFragment {

    View view;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private ViewPagerAdapter adapter;

    private static String TAG = Fragment_MyEarningHolder.class.getSimpleName();

    String from ="";

    public Fragment_MyEarningHolder() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_earning_holder, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof OpenContainer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((OpenContainer) getActivity()).toolbar.setElevation(0);
            }
        }
        if(getArguments()!=null && getArguments().getString("from")!=null) {
            from = getArguments().getString("from");
        }
        setupViewPager();
        return view;
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getChildFragmentManager(), 3);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);

        if (getArguments() != null && getArguments().getString("position") != null) {
            if (getArguments().getString("position").equals("wbmoney")) {
                viewpager.setCurrentItem(0);
                sendScreenEvent("WB Money");
            } else if (getArguments().getString("position").equals("reward")) {
                viewpager.setCurrentItem(1);
                sendScreenEvent("WB Reward");
            }else if (getArguments().getString("position").equals("incentive")) {
                sendScreenEvent("Incentive");
                viewpager.setCurrentItem(2);
            }
        }

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;

        public ViewPagerAdapter(FragmentManager fragmentManager, int NUM_ITEMS) {
            super(fragmentManager);
            this.NUM_ITEMS = NUM_ITEMS;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public GATrackedFragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Fragment_WBMoney();
                case 1:
                    return new Fragment_RewardPoint();
                case 2:
                    return new Fragment_Incentives();
                default:
                    return new Fragment_WBMoney();
            }
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "WB Money";
                case 1:
                    return "WB Rewards";
                case 2:
                    return "Incentives";

                default:
                    return "";
            }
        }


    }



    private void sendScreenEvent(String tab) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("MyEarning_Screen");
        HashMap<String,String> param = new HashMap<>();
        param.put("visited","true");
        param.put("tab",tab);
        param.put("from",from);
        wishbookEvent.setEvent_properties(param);
        new WishbookTracker(getActivity(), wishbookEvent);
    }
}
