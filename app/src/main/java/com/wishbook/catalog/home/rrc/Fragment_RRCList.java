package com.wishbook.catalog.home.rrc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.RRCRequest;
import com.wishbook.catalog.home.rrc.adapter.ReplacementRequestAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_RRCList extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener,RRCHandler.RRCHandlerListner {

    protected View view;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;

    @BindView(R.id.fab_create_request)
    FloatingActionButton fab_create_request;

    Context mContext;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    HashMap<String, String> params = new HashMap<>();

    ReplacementRequestAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RRCHandler.RRCREQUESTTYPE rrcrequesttype;




    public Fragment_RRCList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_rrc_request, ga_container, true);
        ButterKnife.bind(this, view);
        Application_Singleton.rrcHandlerMultipleListner= new HashSet<>();
        initView();
        initSwipeRefresh(view);
        new RRCHandler((Activity) mContext).setRrcHandlerListner(this);
        return view;
    }

    public void initView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        fab_create_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment_RRC_Eligible_Orders fragment_rrc_eligible_orders = new Fragment_RRC_Eligible_Orders();
                if(rrcrequesttype!=null) {
                    bundle.putSerializable("request_type",rrcrequesttype);
                    fragment_rrc_eligible_orders.setArguments(bundle);
                }
                Application_Singleton.CONTAINER_TITLE = "Choose a delivered order";
                Application_Singleton.CONTAINERFRAG = fragment_rrc_eligible_orders;

                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }
        });
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
        if (getArguments().getSerializable("RRC_TYPE") != null) {
            rrcrequesttype = (RRCHandler.RRCREQUESTTYPE) getArguments().getSerializable("RRC_TYPE");


            if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT)
                params.put("request_type", "replacement");
            else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION)
                params.put("request_type", "cancellation");
            else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN)
                params.put("request_type", "return");

            if(rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
                fab_create_request.hide();
            }

        }

        callRRCRequest(params);
    }


    public void callRRCRequest(final HashMap<String, String> param) {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "rrc-requests", ""), param, headers, isAllowCache, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ArrayList<RRCRequest> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<RRCRequest>>() {
                            }.getType());
                            if (items.size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                linear_empty.setVisibility(View.GONE);
                                adapter = new ReplacementRequestAdapter(mContext, items,rrcrequesttype);
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

    @Override
    public void onSuccessRequest() {
        onRefresh();
    }

    @Override
    public void onSuccessCancel() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(Application_Singleton.rrcHandlerMultipleListner!=null) {
            Application_Singleton.rrcHandlerMultipleListner.clear();
            Application_Singleton.rrcHandlerMultipleListner = null;
        }
    }
}
