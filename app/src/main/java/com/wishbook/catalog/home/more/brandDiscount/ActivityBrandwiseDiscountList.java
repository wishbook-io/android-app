package com.wishbook.catalog.home.more.brandDiscount;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscountExpand;
import com.wishbook.catalog.home.more.brandDiscount.adapter.MyBrandWiseDiscountListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityBrandwiseDiscountList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.add_discount)
    FloatingActionButton add_discount;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_empty_discount)
    LinearLayout txt_empty_discount;

    private RecyclerView.LayoutManager mLayoutManager;

    private Context mContext;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    public static int ADD_EDIT_DISCOUNT_REQUEST = 200;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_brandwise_discount_list);
        mContext = ActivityBrandwiseDiscountList.this;
        ButterKnife.bind(this);
        setUpToolbar();
        initView();
        initListener();
        initCall();
    }

    private void initView() {
        txt_empty_discount.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initSwipeRefresh();
    }

    private void setUpToolbar() {
        toolbar.setTitle("My Discount");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initListener() {
        add_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.CONTAINER_TITLE = "Set Discount";
                Application_Singleton.CONTAINERFRAG = new FragmentAddBrandDiscountVersion2();
                startActivityForResult(new Intent(ActivityBrandwiseDiscountList.this,OpenContainer.class),ADD_EDIT_DISCOUNT_REQUEST);
            }
        });
    }


    public void getDiscountList() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBrandwiseDiscountList.this);
        StaticFunctions.showProgressbar(ActivityBrandwiseDiscountList.this);
        HttpManager.getInstance(ActivityBrandwiseDiscountList.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.DISCOUNT_RULE + "?expand=true", null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityBrandwiseDiscountList.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityBrandwiseDiscountList.this);
                    try {
                        ArrayList<ResponseBrandDiscountExpand> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseBrandDiscountExpand>>() {
                        }.getType());
                        if (responseData.size() > 0) {
                            Log.e("TAG", "onServerResponse: ===IF" );
                            txt_empty_discount.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            MyBrandWiseDiscountListAdapter myBrandWiseDiscountListAdapter = new MyBrandWiseDiscountListAdapter(mContext, responseData);
                            mRecyclerView.setAdapter(myBrandWiseDiscountListAdapter);
                        } else {
                            Log.e("TAG", "onServerResponse: ===Else" );
                            txt_empty_discount.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBrandwiseDiscountList.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void initCall() {
        getDiscountList();
    }

    public void initSwipeRefresh() {
        swipe_container = findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
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
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_DISCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            onRefresh();
        }
    }
}
