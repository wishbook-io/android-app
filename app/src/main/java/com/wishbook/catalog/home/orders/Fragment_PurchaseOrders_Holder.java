package com.wishbook.catalog.home.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.home.orders.add.Fragment_CreatePurchaseOrder;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_PurchaseOrders_Holder extends GATrackedFragment {

    private View v;
    private TabLayout tabs;
    private FloatingActionButton orderfab;
    // public static CustomViewPager viewPager;
    private Toolbar toolbar;
    private ViewPager viewPager;
    public ArrayList<String> pendingstatuses = new ArrayList<>();
    public ArrayList<String> dispatchstatuses = new ArrayList<>();
    public ArrayList<String> cancelstatuses = new ArrayList<>();
    Response_buyingorder[] response_buyingorders;
    ViewPagerAdapter adapter;

    public static Fragment fragment;

    public Fragment_PurchaseOrders_Holder() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    //    getPurchaseOrder(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragment = null;
    }

    //todo rejected sellers approve only
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TAG", "onCreateView: Purchase Order Fragment");
       // v = super.onCreateView(inflater, container, savedInstanceState);
       // ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
         v = inflater.inflate(R.layout.fragment_purchaseorder_holder, container, false);
      /*  toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Orders");*/
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        orderfab = (FloatingActionButton) v.findViewById(R.id.orderfab);
        orderfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                Bundle bundle = new Bundle();
                purchase.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                Application_Singleton.CONTAINERFRAG = purchase;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivity(intent);
            }
        });

        fragment = this;

        if(UserInfo.getInstance(getActivity()).getGroupstatus().equals("1"))
        {
           orderfab.setVisibility(View.VISIBLE);
        }else
        {
            orderfab.setVisibility(View.GONE);
        }
        orderfab.setVisibility(View.GONE);
     //   getPurchaseOrder(getActivity());

        viewPager.setCurrentItem(0);
        pendingstatuses.clear();
        dispatchstatuses.clear();
        cancelstatuses.clear();

        pendingstatuses.add("Draft");
        pendingstatuses.add("Pending");
        pendingstatuses.add("ordered");
        pendingstatuses.add("Accepted");
        pendingstatuses.add("In Progress");
        dispatchstatuses.add("Dispatched");
        dispatchstatuses.add("Delivered");
        dispatchstatuses.add("Closed");
        dispatchstatuses.add("Partially Dispatched");
        cancelstatuses.add("Cancelled");
        cancelstatuses.add("Transferred");
        // indicator.setViewPager(pager);
        // setupViewPager(viewPager);

        adapter = new ViewPagerAdapter(getChildFragmentManager(),response_buyingorders);
        if(adapter!=null) {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(Application_Singleton.selectedInnerSubTabOrders);
            tabs.setupWithViewPager(viewPager);
            tabs.setTabMode(TabLayout.MODE_FIXED);
        }
        return v;
    }

    private void getPurchaseOrder(FragmentActivity activity) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        showProgress();
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"purchaseorder",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Log.v("res", "" + response);

                Gson gson = new Gson();
                response_buyingorders = gson.fromJson(response, Response_buyingorder[].class);
                adapter = new ViewPagerAdapter(getChildFragmentManager(),response_buyingorders);
                if(adapter!=null) {
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(Application_Singleton.selectedInnerSubTabOrders);
                    tabs.setupWithViewPager(viewPager);
                    tabs.setTabMode(TabLayout.MODE_FIXED);
                }
//
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        Response_buyingorder[] response_buyingorders;
        public ViewPagerAdapter(FragmentManager fm, Response_buyingorder[] response_buyingorders) {
            super(fm);
            this.response_buyingorders=response_buyingorders;
        }

        @Override
        public GATrackedFragment getItem(int position) {
            GATrackedFragment result= new Fragment_Tab_Purchase_Orders("pending",false);;
            switch (position) {
                case 0:
                    // First Fragment of First Tab
                    result = new Fragment_Tab_Purchase_Orders("pending",false);
                    break;
                case 1:
                    // First Fragment of Second Tab
                    result = new Fragment_Tab_Purchase_Orders("dispatch",false);
                    break;
                case 2:
                    // First Fragment of Third Tab
                    result = new Fragment_Tab_Purchase_Orders("cancel",false);
                    break;

            }

            return result;
        }



        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pending";
                case 1:
                    return "Dispatched";
                case 2:
                    return "Cancelled";
                default:
                    return "Pending";
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode== ResponseCodes.PurchaseResponse){
            getPurchaseOrder(getActivity());
        }
    }
}
