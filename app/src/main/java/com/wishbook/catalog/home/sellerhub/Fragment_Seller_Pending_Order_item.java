package com.wishbook.catalog.home.sellerhub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPendingOrderItems;
import com.wishbook.catalog.home.catalog.details.FilterBottomDialog;
import com.wishbook.catalog.home.sellerhub.adapter.SellerPendingOrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Seller_Pending_Order_item extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener,SellerPendingOrderAdapter.PendingOrderItemChangeListener {


    private View view;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;

    LinearLayoutManager linearLayoutManager;
    SellerPendingOrderAdapter adapter;
    Context mContext;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    Menu menu;

    HashMap<String,String> params=new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_seller_pending_order, ga_container, true);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        if (getActivity() instanceof OpenContainer) {
            ((OpenContainer) getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
        }
        setHasOptionsMenu(true);
        initView();
        initSwipeRefresh(view);
        return view;
    }

    public void initView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        callGetSellerPendingOrder(params);
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
    public void onResume() {
        super.onResume();
        ((OpenContainer) getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_seller_pending_item, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_sort:
                openSortBottom("pending_order_items");
                break;
        }
        return true;
    }

    private void openSortBottom(final String filtertype) {
        try {
            FilterBottomDialog filterBottomDialog = null;
            Bundle bundle = new Bundle();
            bundle.putString("type", filtertype);
            if (params != null) {
                if(params.containsKey("ordering")){
                    bundle.putString("previous_selected_tag",params.get("ordering"));
                }

            }
            filterBottomDialog = filterBottomDialog.newInstance(bundle);
            filterBottomDialog.show(getFragmentManager(), filterBottomDialog.getTag());
            filterBottomDialog.setFilterBottomSelectListener(new FilterBottomDialog.FilterBottomSelectListener() {
                @Override
                public void onCheck(String check) {
                    if (check != null) {
                        params.put("ordering",check);
                        callGetSellerPendingOrder(params);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callGetSellerPendingOrder(HashMap<String,String> param) {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "pending-purchase-product-status", ""), param, headers, isAllowCache, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ArrayList<ResponseSellerPendingOrderItems> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseSellerPendingOrderItems>>() {
                            }.getType());
                            if(items.size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                linear_empty.setVisibility(View.GONE);
                                adapter = new SellerPendingOrderAdapter(mContext, items);
                                adapter.setPendingOrderItemChangeListener(Fragment_Seller_Pending_Order_item.this);
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
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                callGetSellerPendingOrder(params);
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

    @Override
    public void onUpdate() {
        onRefresh();
    }
}
