package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.BuyerSeg;
import com.wishbook.catalog.home.more.adapters.BuyerGroupsAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_BuyerGroups extends GATrackedFragment implements SearchView.OnQueryTextListener,SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private RecyclerView recyclerView;
   // private SearchView searchView;
    private BuyerGroupsAdapter mAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton fabaddbuyer;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter, isSearchFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;


    @Override
    public void onResume() {
        super.onResume();
        getBuyerGroups();
        ((OpenContainer)getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
        setHasOptionsMenu(true);
    }

    public Fragment_BuyerGroups() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_buyergroup, ga_container, true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);

        toolbar.setVisibility(View.GONE);
        ((OpenContainer)getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        //searchView = (SearchView) v.findViewById(R.id.group_search);
        fabaddbuyer=(FloatingActionButton)v.findViewById(R.id.fabaddbuyer);
        fabaddbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE = "Add Buyer Group";
                Application_Singleton.CONTAINERFRAG = new Fragment_AddBuyerGroup();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivityForResult(intent,2);


           /* Fragment_AddBuyerGroup addBuyergroupFragment =new Fragment_AddBuyerGroup();
                addBuyergroupFragment.show(getActivity().getSupportFragmentManager(),"add");*/
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        initSwipeRefresh(v);

        getBuyerGroups();
//        if (StaticFunctions.isOnline(getActivity())) {
//                progressDialog = StaticFunctions.showProgress(getActivity());
//                progressDialog.show();
//                NetworkProcessor.with(getActivity())
//                        .load( URLConstants.companyUrl(getActivity(),"buyergroups","")).addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
//                        .asString().setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        Log.v("res", "" + result);
//                        if(e==null) {
//                            ArrayList<BuyerSeg> response_buyerGroups = buyergroupstate.processBuyerGrRes(e, result);
//                            if (response_buyerGroups != null) {
//                                if (response_buyerGroups.size() > 0) {
//                                    mAdapter = new BuyerGroupsAdapter(getActivity(), response_buyerGroups);
//                                    recyclerView.setAdapter(mAdapter);
//
//                                }
//                            }
//                        }
//                        progressDialog.dismiss();
//                    }
//                });
//        }



       // searchView.setOnQueryTextListener(this);

       // mAdapter.setFilter(groups);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }
    private void getBuyerGroups() {
        Log.e("TAG", "getBuyerGroups: Call"+ isAllowCache );
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"buyergroups",""), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if(isAdded() && !isDetached()){
                    Type listOfProductObj = new TypeToken<ArrayList<BuyerSeg>>() {
                    }.getType();

                    ArrayList<BuyerSeg> response_buyerGroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                    if (response_buyerGroups != null) {
                        if (response_buyerGroups.size() > 0) {
                            mAdapter = new BuyerGroupsAdapter((AppCompatActivity) getActivity(), response_buyerGroups,Fragment_BuyerGroups.this);
                            recyclerView.setAdapter(mAdapter);

                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_new_group) {
            Application_Singleton.CONTAINER_TITLE = "Add Buyer Group";
            Application_Singleton.CONTAINERFRAG = new Fragment_AddBuyerGroup_Version2();
            Intent intent = new Intent(getActivity(), OpenContainer.class);
            Fragment_BuyerGroups.this.startActivityForResult(intent,2);
        } else if(item.getItemId() == android.R.id.home){
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult: Call==>"+requestCode +"Result Code==>"+resultCode );
        if(requestCode==2 && resultCode == Activity.RESULT_OK){
            isAllowCache = false;
            getBuyerGroups();
        }

        if(requestCode == 300 && resultCode == Activity.RESULT_OK){
            isAllowCache =false;
            getBuyerGroups();
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
                getBuyerGroups();

            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


}
