package com.wishbook.catalog.home.contacts.details;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.contacts.adapter.CatalogGridAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Fragment_Details_Catalog extends GATrackedFragment {

    private View v;

    RecyclerView recyclerCatalog;
    private String sellerCompanyid = null;

    TextView empty_txt;

    private boolean isAllowCache ;
    public Fragment_Details_Catalog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.bdetorders, ga_container, true);
        recyclerCatalog = (RecyclerView) v.findViewById(R.id.recycler_view);
        empty_txt = v.findViewById(R.id.empty_txt);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerCatalog.setLayoutManager(gridLayoutManager);
       // recyclerCatalog.setHasFixedSize(true);
       // recyclerCatalog.setNestedScrollingEnabled(false);



        if (getArguments().getString("sellerCompanyid") != null) {
            sellerCompanyid = getArguments().getString("sellerCompanyid");
            isAllowCache = getArguments().getBoolean("isAllowCache");
            if(getArguments().getString("isLinear")!=null){
                if(getArguments().getString("isLinear").equals("true")){
                    getCatalogsLinear(sellerCompanyid);
                } else {
                    getCatalogs(sellerCompanyid);
                }
            } else {
                getCatalogs(sellerCompanyid);
            }
        }
        return v;
    }


    private void getCatalogs(String companyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?company=" + companyId, null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(isAdded() && !isDetached()){
                    hideProgress();
                    Log.v("sync response", response);
                    final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>();
                    if (response_catalogMinis.length > 0) {
                        Collections.addAll(catalogMinis, response_catalogMinis);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
                        recyclerCatalog.setLayoutManager(gridLayoutManager);
                        CatalogGridAdapter gridAdapter = new CatalogGridAdapter(catalogMinis, getActivity());
                        recyclerCatalog.setAdapter(gridAdapter);
                    } else {
                        // Toast.makeText(getActivity(),"There is no catalog in product",Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }

        });

    }


    private void getCatalogsLinear(String companyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?company=" + companyId, null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(isAdded() && !isDetached()){
                    hideProgress();
                    Log.v("sync response", response);
                    final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>();
                    if (response_catalogMinis.length > 0) {
                        recyclerCatalog.setVisibility(View.VISIBLE);
                        empty_txt.setVisibility(View.GONE);
                        Collections.addAll(catalogMinis, response_catalogMinis);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerCatalog.setLayoutManager(linearLayoutManager);
                        BrowseCatalogsAdapter gridAdapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), catalogMinis);
                        recyclerCatalog.setAdapter(gridAdapter);
                    } else {
                        recyclerCatalog.setVisibility(View.GONE);
                        empty_txt.setVisibility(View.VISIBLE);
                        // Toast.makeText(getActivity(),"There is no catalog in product",Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }

        });

    }

}
