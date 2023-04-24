package com.wishbook.catalog.home.more;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.RejectedBuyersAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

import java.util.HashMap;

public class Fragment_BuyersRejected extends GATrackedFragment implements SearchView.OnQueryTextListener,SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private RecyclerViewEmptySupport recyclerView;
    private SearchView searchView;
    private RejectedBuyersAdapter mAdapter;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipe_container;
    private boolean isFilter, isSearchFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    public Fragment_BuyersRejected() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_buyers_rejected, ga_container, true);
        toolbar=(Toolbar)v.findViewById(R.id.appbar);
        toolbar.setTitle("Rejected Buyers");
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        searchView = (SearchView) v.findViewById(R.id.group_search);
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchView.setOnQueryTextListener(this);
        searchView.setVisibility(View.GONE);
        initSwipeRefresh(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        isAllowCache = false;
        getBuyersList();
    }

    private void getBuyersList() {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"buyers_rejected",""), null, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    Log.v("sync response", response);
                    Gson gson = new Gson();
                        Response_Buyer[] response_buyer = gson.fromJson(response, Response_Buyer[].class);

                                mAdapter = new RejectedBuyersAdapter((AppCompatActivity) getActivity(), response_buyer);
                                recyclerView.setAdapter(mAdapter);
                                Log.v("REJECTED_BUYERS1", "" + response_buyer.toString());
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });

//        if (StaticFunctions.isOnline(getActivity())) {
//            progressDialog.show();
//
//            NetworkProcessor.with(getActivity())
//                    .load( URLConstants.companyUrl(getActivity(),"buyers_expand_true_rejected",""))
//                    .addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
//                    .asString().setCallback(new FutureCallback<String>() {
//                @Override
//                public void onCompleted(Exception e, String result) {
//
//                    Log.v("REJECTED_BUYERS", "" + result);
//                    if (e == null) {
//                        Gson gson = new Gson();
//                        Response_Buyer[] response_buyer = gson.fromJson(result, Response_Buyer[].class);
//                        if (response_buyer.length > 0) {
//                            if (response_buyer[0].getId() != null) {
//                                mAdapter = new RejectedBuyersAdapter((AppCompatActivity) getActivity(), response_buyer);
//                                recyclerView.setAdapter(mAdapter);
//                                Log.v("REJECTED_BUYERS1", "" + response_buyer.toString());
//                            }
//                        }
//                    }
//                    progressDialog.dismiss();
//                }
//
//            });
//
//        } else {
//            StaticFunctions.showNetworkAlert(getActivity());
//        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // final List<RejectedBuyersModel> filteredGroupList = filter(rejectedBuyersList, newText);
        // mAdapter.setFilter(filteredGroupList);
        return false;
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
                getBuyersList();
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
