package com.wishbook.catalog.home.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.Fragment_RecievedCatalogs;
import com.wishbook.catalog.home.orderNew.details.Fragment_CreditPayment;

public class Activity_PaymentOrder_2 extends AppCompatActivity {

    private Context mContext;
    private TabLayout tabs;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.initializeAppsee();
        mContext = Activity_PaymentOrder_2.this;
        setContentView(R.layout.fragment_purchase_order_payment);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setTitle("Pay Now");
        getSupportActionBar().setTitle("Shipment & Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("fromPayment", true);
            setResult(50, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initView() {
        // tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        int NUM_ITEMS = 1;
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), NUM_ITEMS);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        // tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Payment Order ");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("fromPayment", true);
        setResult(50, intent);
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;

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
                    Fragment_CashPayment_2 cashPayment = new Fragment_CashPayment_2();
                    Bundle bundle = new Bundle();
                    if (getIntent().getBooleanExtra("isBrokerageOrder", false)) {
                        Log.i("TAG", "getIsBroker==>" + true);
                        bundle.putBoolean("isBrokerageOrder", getIntent().getBooleanExtra("isBrokerageOrder", false));
                    }
                    cashPayment.setArguments(bundle);
                    result = cashPayment;
                    break;
                case 1:
                    // First Fragment of second Tab
                    result = new Fragment_CreditPayment();
                    break;
            }
            return result;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Cash Payment";
                case 1:
                    return "Credit Payment";
                default:
                    return null;
            }

        }
    }

}
