package com.wishbook.catalog.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import com.wishbook.catalog.GATrackedFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.CustomViewPager;
import com.wishbook.catalog.commonmodels.UserInfo;

public class Fragment_HomeHolder extends GATrackedFragment {

    private View view;

    public static CustomViewPager viewPager;
    @BindView(R.id.tabs)  TabLayout tabLayout;
    @BindView(R.id.appbar)  Toolbar toolbar;
    private UserInfo userInfo;

    public Fragment_HomeHolder() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        userInfo=UserInfo.getInstance(getActivity());
        viewPager= (CustomViewPager) view.findViewById(R.id.view_pager);
/*
        Activity_Home.tabs.setVisibility(View.VISIBLE);
*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
       // StaticFunctions.setUpselectedProdCounter((AppCompatActivity) getActivity(), toolbar);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle("Wishbook");
        viewPager.setOffscreenPageLimit(4);
        if (userInfo.getGroupstatus().equals("2")) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            Adapter adapter = new Adapter(getChildFragmentManager());
            adapter.addFragment(new Fragment_MeetingReport(), "VISITS");
            viewPager.setAdapter(adapter);
        } else if (userInfo.getGroupstatus().equals("1")) {
            if (userInfo.getCompanyType().equals("seller")) {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new Fragment_Summary(), "SUMMARY");
                adapter.addFragment(new Fragment_ShareStatus(), "SHARE STATUS");
                adapter.addFragment(new Fragment_SharedCatalogs(), "SHARED CATALOGS");
                viewPager.setAdapter(adapter);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new Fragment_Summary(), "SUMMARY");
                adapter.addFragment(new Fragment_ShareStatus(), "SHARED");
                adapter.addFragment(new Fragment_RecievedCatalogs(), "RECEIVED");
                viewPager.setAdapter(adapter);
            }


        }
        tabLayout.setupWithViewPager(viewPager);

    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<GATrackedFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(GATrackedFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public GATrackedFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}

