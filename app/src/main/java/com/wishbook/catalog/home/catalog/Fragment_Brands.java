package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BrandsAdapter;
import com.wishbook.catalog.commonmodels.Branditem;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
public class Fragment_Brands extends GATrackedFragment implements
        Paginate.Callbacks,SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private View v;
    final ArrayList<Branditem> feedsList = new ArrayList<>();
    private BrandsAdapter adapter;

    private RecyclerViewEmptySupport mRecyclerView;
    private ProgressDialog progressDialog;
    BrandsAdapter brandsAdapter;
    ArrayList<Response_Brands> response_brandsArrayList = new ArrayList<Response_Brands>();
    private String searchText = "";
    SearchView search_view;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page=0;
    int lastFirstVisiblePosition = 0;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    /**
     * Show Only Catalog Brands(Gaurav sir)
     * Changed Feb-6 2019 by Bhavik Gandhi
     */
    public Fragment_Brands() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_brands, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        search_view = v.findViewById(R.id.search_view);

        SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete)search_view.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        search_view.setOnQueryTextListener(this);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(layoutManager);

        brandsAdapter = new BrandsAdapter((AppCompatActivity) getActivity(), response_brandsArrayList,this);
        mRecyclerView.setAdapter(brandsAdapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();

        Loading=false;
        brandsAdapter.notifyDataSetChanged();


        initSwipeRefresh(v);

        CatalogHolder.toggleFloating(mRecyclerView,getActivity());

        if(getArguments().getString("focus_position")!=null){
            lastFirstVisiblePosition = Integer.parseInt(getArguments().getString("focus_position"));
        }

        return v;

    }

    private void getBrands(int limit, final int offset, Boolean progress,String searchText, boolean isAllowCacheData) {
        HttpManager.METHOD methodType;
        if(progress){
            if(getUserVisibleHint()) {
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
               // showProgress();
            }else{
                methodType = HttpManager.METHOD.GET;
            }
        }
        else{
            methodType = HttpManager.METHOD.GET;
        }

        if(offset == 0){
            page = 0;
            hasLoadedAllItems = false;
            response_brandsArrayList.clear();
            if(brandsAdapter!=null) {
                brandsAdapter.notifyDataSetChanged();
            }
        }

        String url = null;
        if(getArguments()!=null){
            if(getArguments().getBoolean("isFromBrandsIFollow")){
                url = URLConstants.companyUrl(getActivity(),"brands","")+"?limit="+limit+"&offset="+offset +"&type=public" +"&brand_i_follow=true"+ "&name="+searchText;
            } else {
                url = URLConstants.companyUrl(getActivity(),"brands","")+"?limit="+limit+"&offset="+offset +"&type=public" +"&product_type=catalog"+ "&name="+searchText;
            }
        } else {
            url = URLConstants.companyUrl(getActivity(),"brands","")+"?limit="+limit+"&offset="+offset +"&type=public" +"&product_type=catalog"+ "&name="+searchText;
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(methodType, url, null, headers, isAllowCacheData, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    hideProgress();
                    Loading = false;
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    if (response_brands.length > 0) {

                        //checking if data updated on 2nd page or more
                        if (offset == 0) {
                            response_brandsArrayList.clear();
                        }
                        response_brandsArrayList = (ArrayList<Response_Brands>) HttpManager.removeDuplicateIssue(offset, response_brandsArrayList, dataUpdated, LIMIT);


                        for (int i = 0; i < response_brands.length; i++) {
                            response_brandsArrayList.add(response_brands[i]);
                        }
                        page = (int) Math.ceil((double) response_brandsArrayList.size() / LIMIT);


                /*    if(response_brandsArrayList.size()>0){
                        Collections.sort(response_brandsArrayList,Collections.<Response_Brands>reverseOrder());
                    }*/

                        if (response_brands.length % LIMIT != 0) {
                            hasLoadedAllItems = true;
                        }
                        if (response_brandsArrayList.size() <= LIMIT) {
                            brandsAdapter.notifyDataSetChanged();
                        } else {
                            brandsAdapter.notifyItemRangeInserted(brandsAdapter.getItemCount(), response_brandsArrayList.size() - 1);
                        }

                    } else {
                        if (offset == 0) {
                            response_brandsArrayList.clear();
                        }
                        hasLoadedAllItems = true;
                        brandsAdapter.notifyDataSetChanged();
                    }

                    if (brandsAdapter.getItemCount() <= LIMIT) {

                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);

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
    public void onLoadMore() {
        Loading=true;
        if(page==0){
            getBrands(LIMIT,INITIAL_OFFSET,true,"",isAllowCache);
        }else {
            getBrands(LIMIT, page * LIMIT, false,searchText,isAllowCache);
        }
    }

    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode== ResponseCodes.Brand_Response){
            getBrands(LIMIT,INITIAL_OFFSET,true,"",true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtils.hideKeyboard(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchText = "";

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        KeyboardUtils.hideKeyboard(getActivity());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final Boolean[] canRun = {true};
        searchText = newText.toLowerCase();
        if (newText.length() > 2) {

            if (canRun[0]) {
                canRun[0] = false;
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canRun[0] = true;
                        if (getUserVisibleHint() && getContext() != null)
                            getBrands(LIMIT, INITIAL_OFFSET, true, searchText,true);
                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            if (getUserVisibleHint() && getContext() != null)
                getBrands(LIMIT, INITIAL_OFFSET, true, searchText,true);
        }

        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
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
                swipe_container.setRefreshing(false);
                isAllowCache = false;
                searchText ="";
                search_view.setQuery("", false);
                search_view.clearFocus();
                getBrands(LIMIT,INITIAL_OFFSET,true,"",isAllowCache);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }
}
