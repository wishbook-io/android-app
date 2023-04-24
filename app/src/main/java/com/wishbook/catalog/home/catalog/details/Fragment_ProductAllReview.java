package com.wishbook.catalog.home.catalog.details;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestRating;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.adapter.ProductAllReviewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ProductAllReview extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private Context mContext;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_total_rating)
    TextView txt_total_rating;

    @BindView(R.id.txt_avg_rating)
            TextView txt_avg_rating;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    String product_id;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    Response_catalog.Product_rating product_rating;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_all_product_review, ga_container, true);
        ButterKnife.bind(this, v);
        mContext = getActivity();
        initSwipeRefresh(v);
        initRecyclerView();
        return v;
    }


    public void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);

        if(getArguments()!=null && getArguments().getString("product_id")!=null) {
            product_id = getArguments().getString("product_id");
            product_rating = (Response_catalog.Product_rating) getArguments().getSerializable("product_rating");

            if(product_rating!=null && product_rating.getAvg_rating()!=null) {
                txt_avg_rating.setText(product_rating.getAvg_rating());
            }
            StringBuffer total_rating = new StringBuffer();
            if(Integer.parseInt(product_rating.getTotal_rating()) > 0) {
                total_rating.append(product_rating.getTotal_rating() + " rating ");
            }
            if(Integer.parseInt(product_rating.getTotal_review()) > 0) {
                total_rating.append( "& "+product_rating.getTotal_review()+" reviews");
            }

            txt_total_rating.setText(total_rating);
            callGetAllReview(product_id);
        }

    }

    public void callGetAllReview(String product_id) {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "product-ratings", "") + "?product="+product_id, null, headers, isAllowCache, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ArrayList<RequestRating> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<RequestRating>>() {
                            }.getType());
                            ProductAllReviewAdapter adapter= new ProductAllReviewAdapter(mContext, items);
                            mRecyclerView.setAdapter(adapter);
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
                callGetAllReview(product_id);
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
