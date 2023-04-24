package com.wishbook.catalog.reseller;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ResellerHolder extends GATrackedFragment {

    View view;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private ViewPagerAdapter adapter;

    private static String TAG = Fragment_ResellerHolder.class.getSimpleName();

    public Fragment_ResellerHolder() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reseller_holder, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof OpenContainer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((OpenContainer) getActivity()).toolbar.setElevation(0);
            }
        }
        setupViewPager();
        return view;
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getChildFragmentManager(), 4);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);

        if (getArguments() != null && getArguments().getString("position") != null) {
            if (getArguments().getString("position").equals("payout")) {
                viewpager.setCurrentItem(0);
            } else if (getArguments().getString("position").equals("sharedbyme")) {
                viewpager.setCurrentItem(1);
            } else if (getArguments().getString("position").equals("address")) {
                viewpager.setCurrentItem(2);
            } else if (getArguments().getString("position").equals("bankaccount")) {
                viewpager.setCurrentItem(3);
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
                    return new Fragment_Reseller_Payout();
                case 1:
                    return new Fragment_Reseller_Shared_ByMe();
                case 2:
                    return new Fragment_Reseller_Address();
                case 3:
                    return new Fragment_Reseller_BankAccount();
                default:
                    return new Fragment_Reseller_Payout();
            }
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Payouts";
                case 1:
                    return "Shared by me";
                case 2:
                    return "Customer Addresses";
                case 3:
                    return "Bank Details";

                default:
                    return "";
            }
        }
    }
}
