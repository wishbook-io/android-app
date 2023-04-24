package com.wishbook.catalog.home.catalog;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.home.Fragment_RecievedCatalogs;

/**
 * Created by root on 9/9/16.
 */
public class SupplierViewCatalogHolder extends GATrackedFragment {
    private View v;
    private TabLayout tabs;

    // public static CustomViewPager viewPager;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    String SupplierCompanyID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.buyer_share_catalog_holder, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        int NUM_ITEMS = 1;
        adapter = new ViewPagerAdapter(getChildFragmentManager(), NUM_ITEMS);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        if(getArguments()!=null){
            SupplierCompanyID = getArguments().getString("filtervalue");
        }
        return v;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;
        SparseArray<GATrackedFragment> registeredFragments = new SparseArray<GATrackedFragment>();

        public ViewPagerAdapter(FragmentManager fragmentManager, int NUM_ITEMS) {
            super(fragmentManager);
            this.NUM_ITEMS = NUM_ITEMS;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public GATrackedFragment getItem(int position) {
            GATrackedFragment result = new Fragment_RecievedCatalogs();
            switch (position) {
                case 0:
                    // First Fragment of First Tab
                    Fragment_BrowseCatalogs browseCatalogs = new Fragment_BrowseCatalogs();
                    Bundle bundleBrowse = new Bundle();
                    bundleBrowse.putString("filtertype","company");
                    bundleBrowse.putString("filtervalue",SupplierCompanyID);
                    browseCatalogs.setArguments(bundleBrowse);
                    result = browseCatalogs;
                    break;
                case 1:
                    // First Fragment of Third Tab
                    Fragment_RecievedCatalogs brCatalogs = new Fragment_RecievedCatalogs();
                    Bundle bundleReceived = new Bundle();
                    bundleReceived.putString("filtertype","company");
                    bundleReceived.putString("filtervalue",SupplierCompanyID);
                    brCatalogs.setArguments(bundleReceived);
                    result = brCatalogs;
                    break;
            }
            return result;
        }


        public GATrackedFragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Public";
                default:
                    return "Public";
            }

        }
    }

}
