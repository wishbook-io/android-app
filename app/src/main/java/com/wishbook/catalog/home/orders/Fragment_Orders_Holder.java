package com.wishbook.catalog.home.orders;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.Activity_Home;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.tabs.TabLayout.MODE_FIXED;

public class Fragment_Orders_Holder extends GATrackedFragment {

    private View v;
    private TabLayout tabLayout;
    private FloatingActionButton fabaddbuyer;
    private Toolbar toolbar;
    public static ArrayList<String> pendingstatuses = new ArrayList<>();
    public static ArrayList<String> dispatchstatuses = new ArrayList<>();
    public static ArrayList<String> cancelstatuses = new ArrayList<>();
    public Fragment_Orders_Holder() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
       // StaticFunctions.setUpselectedProdCounter((AppCompatActivity) getActivity(), toolbar);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TAG", "onCreateView: Order Holder");

        v = inflater.inflate(R.layout.fragment_orders, container, false);
        Activity_Home.support_chat_fab.clearAnimation();
        Activity_Home.support_chat_fab.setVisibility(View.VISIBLE);
        Activity_Home.support_chat_fab.show();

        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        tabLayout = (TabLayout)v. findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);





        if (viewPager != null) {
            setupViewPager(viewPager);
        }
       /* if(getArguments()!=null)
        {
            viewPager.setCurrentItem(0);
        }
        else {
            viewPager.setCurrentItem(1);
        }*/

        viewPager.setCurrentItem(Application_Singleton.selectedInnerTabOrders);
        tabLayout.setTabMode(MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        pendingstatuses.clear();
        dispatchstatuses.clear();
        cancelstatuses.clear();
        pendingstatuses.add("Pending");
        pendingstatuses.add("ordered");
        pendingstatuses.add("Accepted");
        pendingstatuses.add("In Progress");
        dispatchstatuses.add("Dispatched");
        dispatchstatuses.add("Delivered");
        dispatchstatuses.add("Closed");
        cancelstatuses.add("Cancelled");

        //Order Tutorial
       /* if(!Application_Singleton.tutorial_pref.getBoolean("order_tutorial",false) && !UserInfo.getInstance(getContext()).getGroupstatus().equals("2")){
            Intent intent = new Intent(getActivity(), IntroActivity.class);
            ArrayList<AppIntroModel> models = new ArrayList<>();
            models.add(new AppIntroModel(getString(R.string.intro_orders_title),getString(R.string.intro_orders_desc),R.drawable.intro_orders_new));
            intent.putParcelableArrayListExtra("list",models);
            startActivity(intent);
            Application_Singleton.tutorial_pref.edit().putBoolean("order_tutorial",true).apply();
        }*/


        return v;
    }



    private void setupViewPager(ViewPager viewPager) {
        if(Activity_Home.pref.getString("groupstatus", "0") .equals("2")){
            tabLayout.setVisibility(View.GONE);
            Adapter adapter = new Adapter(getChildFragmentManager());
            adapter.addFragment(new Fragment_SalesOrders_Holder(), "Sales orders");
            viewPager.setAdapter(adapter);
        }
        else  if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {

            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller") ) {
                tabLayout.setVisibility(View.GONE);

                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new Fragment_SalesOrders_Holder(), "Sales orders");

                viewPager.setAdapter(adapter);
            }
            else if(UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")){
                tabLayout.setVisibility(View.GONE);

                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new Fragment_PurchaseOrders_Holder(), "Purchase orders");

                viewPager.setAdapter(adapter);
            }
            else{

                tabLayout.setTabMode(MODE_FIXED);

                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new Fragment_PurchaseOrders_Holder(), "Purchase");
                adapter.addFragment(new Fragment_SalesOrders_Holder(), "Sales");
                if(UserInfo.getInstance(getActivity()).getBroker()!=null && UserInfo.getInstance(getActivity()).getBroker()) {
                    adapter.addFragment(new Fragment_BrokerageOrder_Holders(), "Brokerage");
                }


                viewPager.setAdapter(adapter);
            }



        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private  List<GATrackedFragment> mFragments = new ArrayList<>();
        private  List<String> mFragmentTitles = new ArrayList<>();

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
