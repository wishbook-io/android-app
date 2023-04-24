package com.wishbook.catalog.home.more;

import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.RejectedSuppliersAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;

import java.util.HashMap;

public class Fragment_SupplierRejected extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private RecyclerViewEmptySupport recyclerView;

    private Response_Suppliers[] response_suppliers;
    private RejectedSuppliersAdapter mAdapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipe_container;
    private boolean isFilter, isSearchFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    public Fragment_SupplierRejected() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        isAllowCache = false;
        getSuppliersList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rejected_suppliers, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("Rejected Suppliers");
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.setVisibility(View.GONE);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initSwipeRefresh(v);
        return v;
    }

    private void getSuppliersList() {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"sellers_rejected",""), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("res", response);
                response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                mAdapter = new RejectedSuppliersAdapter((AppCompatActivity) getActivity(), response_suppliers);
                recyclerView.setAdapter(mAdapter);
                Log.v("APPROVED_BUYERS1", "" + response_suppliers.toString());
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

//        NetworkManager.getInstance().HttpRequest(getActivity(), NetworkManager.GET,
//                URLConstants.companyUrl(getActivity(),"sellers_expand_true_rejected",""), null, new NetworkManager.customCallBack() {
//                    @Override
//                    public void onCompleted(int status, String response) {
//                        if (status == NetworkManager.RESPONSESUCCESS) {
//                            Log.v("res", response);
//                            response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
//                            mAdapter = new RejectedSuppliersAdapter((AppCompatActivity) getActivity(), response_suppliers);
//                            recyclerView.setAdapter(mAdapter);
//                            Log.v("APPROVED_BUYERS1", "" + response_suppliers.toString());
//
//                        }
//                    }
//                });

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
                getSuppliersList();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }
}
