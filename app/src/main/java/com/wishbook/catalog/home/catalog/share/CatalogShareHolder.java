package com.wishbook.catalog.home.catalog.share;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

public class CatalogShareHolder extends AppCompatActivity {

    private TabLayout tabs;
    public static Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private String product_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_share_holder);
        StaticFunctions.initializeAppsee();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.share_title));
        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Application_Singleton.shareCatalogHolder = null;
            }
        });
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        if(getIntent()!=null && getIntent().getStringExtra("product_id")!=null) {
            product_id = getIntent().getStringExtra("product_id");
        }
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
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:
                    Fragment_CatalogShare_Version2 fragemnt_group = new Fragment_CatalogShare_Version2();
                    bundle.putString("type", "group");
                    bundle.putString("product_id",product_id);
                    fragemnt_group.setArguments(bundle);
                    return fragemnt_group;

                case 1:
                    Fragment_CatalogShare_Version2 fragemnt_individual = new Fragment_CatalogShare_Version2();
                    bundle.putString("type", "single");
                    bundle.putString("product_id",product_id);
                    fragemnt_individual.setArguments(bundle);
                    return fragemnt_individual;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.groupshare);
                case 1:
                    return getResources().getString(R.string.individualshare);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Application_Singleton.shareCatalogHolder = null;
    }
}
