package com.wishbook.catalog.home.cart;

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
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;
import com.wishbook.catalog.home.cart.adapter.CouponListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Coupon_List extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private Context mContext;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_empty_view)
    LinearLayout linear_empty_view;

    String cart_id;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_user_coupon_list, ga_container, true);
        ButterKnife.bind(this, v);
        mContext = getActivity();
        initSwipeRefresh(v);
        initRecyclerView();
        return v;
    }


    public void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (getArguments() != null && getArguments().getString("cart_id") != null) {
            cart_id = getArguments().getString("cart_id");
            callGetUserCouponList();
        }
    }

    public void callGetUserCouponList() {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "coupons", ""), null, headers, isAllowCache, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ArrayList<ResponseCouponList> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseCouponList>>() {
                            }.getType());
                            if (items.size() > 0) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                linear_empty_view.setVisibility(View.GONE);
                                CouponListAdapter adapter = new CouponListAdapter(mContext, items, cart_id);
                                mRecyclerView.setAdapter(adapter);
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                                linear_empty_view.setVisibility(View.VISIBLE);
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
            e.printStackTrace();
        }
    }

    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                callGetUserCouponList();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 500);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
