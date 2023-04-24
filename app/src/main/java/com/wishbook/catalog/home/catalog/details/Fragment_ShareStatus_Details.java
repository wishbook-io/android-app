package com.wishbook.catalog.home.catalog.details;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/11/16.
 */
public class Fragment_ShareStatus_Details extends GATrackedFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String id = null;
    private String catalog = null;
    private TextView share_id;
    private TextView catalog_name;

    private TextView cat_name;
    private TextView npeople;
    private TextView nprods;
    private TextView nopened;
    private SimpleDraweeView cat_img;
    private TextView productsviewed;
    private TextView catalog_brand_name;
    private TextView catalog_by;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.share_status_details, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
       /* share_id = (TextView) v.findViewById(R.id.share_id);
        catalog_name = (TextView) v.findViewById(R.id.catalog_name);*/

        cat_name = (TextView) v.findViewById(R.id.cat_name);
        catalog_brand_name = (TextView) v.findViewById(R.id.catalog_brand_name);
        catalog_by = (TextView) v.findViewById(R.id.catalog_by);
        npeople = (TextView) v.findViewById(R.id.npeople);
        nprods = (TextView) v.findViewById(R.id.nprods);
        nopened = (TextView) v.findViewById(R.id.nopened);
        productsviewed = (TextView) v.findViewById(R.id.productsviewed);
        cat_img = (SimpleDraweeView) v.findViewById(R.id.cat_img);


      /*  Bundle filter1 = getArguments();
        if (filter1 != null) {
            id = filter1.getString("id");
            catalog = filter1.getString("catalog");
        }*/

        if (Application_Singleton.response_shareStatus != null) {
            id = Application_Singleton.response_shareStatus.getId();
            catalog = Application_Singleton.response_shareStatus.getTitle();

            cat_name.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getTitle().toLowerCase().trim()));
            npeople.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getTotal_users().toLowerCase().trim()));
            nprods.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getTotal_products().toLowerCase().trim()));
            nopened.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getTotal_viewed().toLowerCase().trim()));
            productsviewed.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getTotal_products_viewed().toLowerCase().trim()));
            if(Application_Singleton.response_shareStatus.getBrand_name() != null && !Application_Singleton.response_shareStatus.getBrand_name().equals("")) {
                catalog_brand_name.setText(StringUtils.capitalize(Application_Singleton.response_shareStatus.getBrand_name().toLowerCase().trim()));
            }
            else
            {
                catalog_brand_name.setVisibility(View.GONE);
                catalog_by.setVisibility(View.GONE);
            }
            String image = Application_Singleton.response_shareStatus.getImage();
            if (image != null & !image.equals("")) {
                //StaticFunctions.loadImage(getContext(), image, cat_img, R.drawable.uploadempty);
                StaticFunctions.loadFresco(getContext(), image, cat_img);
                //Picasso.with(mContext).load(image).into(customViewHolder.cat_img);
            }

        }

        if (viewPager != null) {
            if (id != null) {
                setupViewPager(viewPager, id);
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                tabLayout.setupWithViewPager(viewPager);
            }

        }

      /*  if(id!=null)
        {
            share_id.setText(id);
        }
        if(catalog!=null) {
            catalog_name.setText(catalog);
        }*/
        return v;
    }

    private void setupViewPager(ViewPager viewPager, final String id) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment_Details_Users bDet_users = new Fragment_Details_Users();
        if (!id.equals("") && !id.equals(null)) {
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bDet_users.setArguments(bundle);
        }
        adapter.addFragment(bDet_users, "Users");
        Fragment_Details_Design bDes_orders = new Fragment_Details_Design();
        if (!id.equals("") && !id.equals(null)) {
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bDes_orders.setArguments(bundle);
        }
        adapter.addFragment(bDes_orders, "Designs");
        viewPager.setAdapter(adapter);
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
