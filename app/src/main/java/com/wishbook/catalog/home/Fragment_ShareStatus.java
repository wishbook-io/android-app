package com.wishbook.catalog.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.home.adapters.ShareStatusAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ShareStatus extends GATrackedFragment implements Paginate.Callbacks {

    private View v;
    @BindView(R.id.recycler_view)
    RecyclerViewEmptySupport mRecyclerView;
    ArrayList<Response_ShareStatus> shareStatusesList = new ArrayList<>();
    ShareStatusAdapter catalogsSharedAdapter;

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page=0;
    public Fragment_ShareStatus() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.mycatalogs, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        if (paginate != null) {
            paginate.unbind();
        }
        catalogsSharedAdapter = new ShareStatusAdapter((AppCompatActivity) getActivity(), shareStatusesList);
        mRecyclerView.setAdapter(catalogsSharedAdapter);
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();

        Loading=false;
        catalogsSharedAdapter.notifyDataSetChanged();
/*
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public Boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if(totalItemsCount%LIMIT==0) {
                    getShareStatus(LIMIT, totalItemsCount);
                }
              //  Toast.makeText(getActivity(),totalItemsCount+"",Toast.LENGTH_LONG).show();

                return true;
            }

            @Override
            public void stopLoading() {

            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);*/

      // getShareStatus(LIMIT,INITIAL_OFFSET,true);

        return v;
    }

    static void printArray(int arr[])
    {
        int n = arr.length;
        for (int i=0; i<n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    private void getShareStatus(int limit, final int offset, Boolean progress) {
        HttpManager.METHOD methodType;
        if(progress){
            methodType = HttpManager.METHOD.GETWITHPROGRESS;
        }
        else{
            methodType = HttpManager.METHOD.GET;
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(methodType, URLConstants.companyUrl(getActivity(),"mysent_catalog","")+"&&limit="+limit+"&&offset="+offset, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }
            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        Loading = false;
                        Response_ShareStatus[] response_shareStatuses = Application_Singleton.gson.fromJson(response, Response_ShareStatus[].class);
                        if (response_shareStatuses.length > 0) {

                            //checking if data updated on 2nd page or more
                            shareStatusesList = (ArrayList<Response_ShareStatus>) HttpManager.removeDuplicateIssue(offset, shareStatusesList, dataUpdated, LIMIT);


                            if (response_shareStatuses.length % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }
                            for (int i = 0; i < response_shareStatuses.length; i++) {
                                shareStatusesList.add(response_shareStatuses[i]);
                            }

                            page = (int) Math.ceil((double) shareStatusesList.size() / LIMIT);


                            if (shareStatusesList.size() <= LIMIT) {
                                catalogsSharedAdapter.notifyDataSetChanged();
                            } else {
                                try {
                                    catalogsSharedAdapter.notifyItemRangeChanged(offset, shareStatusesList.size() - 1);
                                } catch (Exception e) {
                                    catalogsSharedAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            hasLoadedAllItems = true;
                            catalogsSharedAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    @Override
    public void onLoadMore() {
        Loading=true;
        if(page==0){
            getShareStatus(LIMIT,INITIAL_OFFSET,true);

        }else {
            getShareStatus(LIMIT, page * LIMIT, false);
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

}
