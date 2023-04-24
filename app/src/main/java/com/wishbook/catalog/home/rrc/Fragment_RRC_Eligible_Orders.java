package com.wishbook.catalog.home.rrc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.home.orders.adapters.PurchaseOrderAdapterNew;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_RRC_Eligible_Orders extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected View view;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;


    Context mContext;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = false;


    PurchaseOrderAdapterNew adapter;
    LinearLayoutManager linearLayoutManager;


    RRCHandler.RRCREQUESTTYPE rrcrequesttype;




    public Fragment_RRC_Eligible_Orders() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_generic_recyclerview, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initSwipeRefresh(view);
        return view;
    }

    public void initView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        initCall();
    }

    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void initCall() {
        if(getArguments().getSerializable("request_type")!=null) {
             rrcrequesttype = (RRCHandler.RRCREQUESTTYPE) getArguments().getSerializable("request_type");
        }
        callRRCOrdersList();
    }


    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 500);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void callRRCOrdersList() {
        try {
            showProgress();
            String url = URLConstants.companyUrl(getActivity(), "rrc-eligible-order-list", "") + "?for_rrc=true";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, isAllowCache, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ArrayList<Response_buyingorder> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_buyingorder>>() {
                            }.getType());
                            if (items.size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                linear_empty.setVisibility(View.GONE);
                                adapter = new PurchaseOrderAdapterNew(mContext, items,true,rrcrequesttype);
                                recyclerview.setAdapter(adapter);
                            } else {
                                linear_empty.setVisibility(View.VISIBLE);
                                recyclerview.setVisibility(View.GONE);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } catch (Exception e) {
            hideProgress();
            e.printStackTrace();
        }
    }
}

