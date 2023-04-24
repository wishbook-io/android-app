package com.wishbook.catalog.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.home.adapters.CatalogsSharedAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class Fragment_SharedCatalogs extends GATrackedFragment {

    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private boolean isFirstTimeCalled;

    public Fragment_SharedCatalogs() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.recentcatalogs, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    // Recycle view scrolling up...
/*
                    Activity_Home.tabs.setVisibility(View.VISIBLE);
*/

                } else if (dy > 0) {
                    // Recycle view scrolling down...
/*
                    Activity_Home.tabs.setVisibility(View.GONE);
*/
                }
            }
        });

        fetchSharedByMe();

       return v;
    }


    public void fetchSharedByMe() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.RECENTSHAREDCATAPP, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    ArrayList<CatalogMinified> respoCatalogMinifiedArrayList = new ArrayList<>();
                    CatalogMinified[] response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified[].class);
                    for (int i = 0; i < response_catalogs.length; i++) {
                        respoCatalogMinifiedArrayList.add(response_catalogs[i]);
                    }
                    CatalogsSharedAdapter catalogsSharedAdapter = new CatalogsSharedAdapter((AppCompatActivity) getActivity(), respoCatalogMinifiedArrayList, "Sales", Fragment_SharedCatalogs.this);
                    mRecyclerView.setAdapter(catalogsSharedAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();

            }
        });
    }
}
