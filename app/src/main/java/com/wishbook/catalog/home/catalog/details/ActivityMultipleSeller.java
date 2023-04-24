package com.wishbook.catalog.home.catalog.details;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.MultipleSupplierAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityMultipleSeller extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;

    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    boolean fromBroker = false;

    private static String TAG = Activity_ComapnyCatalogs.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ActivityMultipleSeller.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_multiple_seller_widget);
        ButterKnife.bind(this);
        setToolbar();
        initView();
        initSwipeRefresh();
    }

    private void setToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        if (getIntent().getStringExtra("catalog_name") != null) {
            //toolbar.setTitle(getIntent().getStringExtra("catalog_name"));
            toolbar.setTitle("Select Supplier");
        }
    }


    private void initView() {
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(getIntent().getStringExtra("catalog_id") !=null) {
            getAllSeller(getIntent().getStringExtra("catalog_id"));
        }

      /*  if (getIntent().getSerializableExtra("suppliers") != null) {
            ArrayList<MultipleSuppliers> multipleSupplierses = (ArrayList<MultipleSuppliers>) getIntent().getSerializableExtra("suppliers");
            if (multipleSupplierses.size() > 0) {
                txt_number_seller.setText(String.format(getResources().getString(R.string.multiple_seller_header), multipleSupplierses.size()));
                MultipleSupplierAdapter multipleSupplierAdapter = new MultipleSupplierAdapter(ActivityMultipleSeller.this, multipleSupplierses, getIntent().getStringExtra("action"));
                mRecyclerView.setAdapter(multipleSupplierAdapter);
            }
        }*/
    }


    private void getAllSeller(String catalogID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityMultipleSeller.this);
        StaticFunctions.showProgressbar(ActivityMultipleSeller.this);
        HttpManager.getInstance(ActivityMultipleSeller.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(ActivityMultipleSeller.this, "catalog_all_seller", catalogID), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

                try {
                    Log.v("sync response", response);
                    StaticFunctions.hideProgressbar(ActivityMultipleSeller.this);
                    MultipleSuppliers[] multipleSupplierses = Application_Singleton.gson.fromJson(response, MultipleSuppliers[].class);

                    if (multipleSupplierses.length > 0) {
                        ArrayList<MultipleSuppliers> responseArrayList = new ArrayList<MultipleSuppliers>(Arrays.asList(multipleSupplierses));
                    /*UserInfo userInfo = UserInfo.getInstance(ActivityMultipleSeller.this);
                    for (int i=0;i<responseArrayList.size();i++) {
                        if(responseArrayList.get(i).getCompany_id().equals(userInfo.getCompany_id())){
                            // remove own compnay
                            responseArrayList.remove(i);
                        }
                    }*/

                        if (getIntent().getStringExtra("action").equals(Fragment_CatalogsGallery.BROKERAGE)) {
                            fromBroker = true;
                            // show only connected buyers for brokerage order
                            Iterator<MultipleSuppliers> it = responseArrayList.iterator();
                            while (it.hasNext()) {
                                MultipleSuppliers temp = it.next();
                                if (temp.getRelation_id() == null) {
                                    it.remove();
                                }
                            }
                        } else {
                            fromBroker = false;
                        }


                        String companyCity = UserInfo.getInstance(ActivityMultipleSeller.this).getCompanyCity();
                        ArrayList<MultipleSuppliers> citySortedList = new ArrayList<MultipleSuppliers>();
                        ArrayList<MultipleSuppliers> cityUnSortedList = new ArrayList<MultipleSuppliers>();
                        for (int i = 0; i < responseArrayList.size(); i++) {
                            if (responseArrayList.get(i).getCity_name().equals(companyCity)) {
                                citySortedList.add(responseArrayList.get(i));
                            } else {
                                cityUnSortedList.add(responseArrayList.get(i));
                            }
                        }

                        responseArrayList.clear();
                        responseArrayList.addAll(citySortedList);
                        responseArrayList.addAll(cityUnSortedList);


                        // rating sorted
                    /*Collections.sort(responseArrayList, new Comparator<MultipleSuppliers>() {
                        @Override
                        public int compare(MultipleSuppliers obj1, MultipleSuppliers obj2) {

                            if (obj1.getSeller_score() == null) {
                                return (obj2.getSeller_score() == null) ? 0 : -1;
                            }
                            if (obj2.getSeller_score() == null) {
                                return 1;
                            }
                            float rating1 = Float.parseFloat(obj1.getSeller_score());
                            float rating2 = Float.parseFloat(obj2.getSeller_score());
                            return Float.compare(rating1,rating2);
                        }
                    });*/


                        if (responseArrayList.size() > 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            empty_linear.setVisibility(View.GONE);

                            if (getIntent().getStringExtra("catalog_price") != null) {
                                String price = getIntent().getStringExtra("catalog_price");
                                MultipleSupplierAdapter multipleSupplierAdapter = new MultipleSupplierAdapter(ActivityMultipleSeller.this, responseArrayList, getIntent().getStringExtra("action"), price, fromBroker);
                                mRecyclerView.setAdapter(multipleSupplierAdapter);
                            } else {
                                MultipleSupplierAdapter multipleSupplierAdapter = new MultipleSupplierAdapter(ActivityMultipleSeller.this, responseArrayList, getIntent().getStringExtra("action"), null, fromBroker);
                                mRecyclerView.setAdapter(multipleSupplierAdapter);
                            }


                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            empty_linear.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityMultipleSeller.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
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
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
               getAllSeller(getIntent().getStringExtra("catalog_id"));
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


}
