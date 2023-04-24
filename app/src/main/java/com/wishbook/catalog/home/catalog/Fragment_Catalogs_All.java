package com.wishbook.catalog.home.catalog;

import android.os.Bundle;

import com.wishbook.catalog.GATrackedFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.commonadapters.AllSectionAdapter;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.Fragment_RecievedCatalogs;
import com.wishbook.catalog.home.Fragment_SharedCatalogs;
import com.wishbook.catalog.commonmodels.AllDataModel;
import com.wishbook.catalog.commonmodels.Response_catalogapp;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.SharedByMe;

public class Fragment_Catalogs_All extends GATrackedFragment {

    private View v;
    private TextView textview;
    private RecyclerViewEmptySupport mRecyclerView;
    private Toolbar toolbar;
    private AllSectionAdapter allItemAdapter;
    private ArrayList<AllDataModel> alldata;
    private ArrayList<Response_Brands> brandsData;
    private ArrayList<Response_catalogapp> catalogsData;
    private ArrayList<Response_Selection> selData;
    private ArrayList<SharedByMe> sharedByme;
    private ArrayList<CatalogMinified> sharedWithMe;

    public Fragment_Catalogs_All() {
    }

    @Override
    public void onResume() {
        super.onResume();
        //StaticFunctions.setUpselectedProdCounter((AppCompatActivity) getActivity(), toolbar);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.allitemsfrag, container, false);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("Catalog");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        alldata = new ArrayList<>();
        allItemAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), alldata);
        mRecyclerView.setAdapter(allItemAdapter);

        //    getbrands();
        getsharedwithMe();
        //   getcatalogs();
        //      getSelections();
//        getsharedbyMe();

        return v;

    }

    private void getsharedwithMe() {
        sharedWithMe = new ArrayList<>();
        alldata.add(new AllDataModel("Received Catalogs", sharedWithMe, new Fragment_RecievedCatalogs()));

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs_received", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                CatalogMinified[] response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified[].class);
                sharedWithMe.clear();
                for (CatalogMinified resb : response_catalogs) {
                    sharedWithMe.add(resb);
                }
                allItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    private void getsharedbyMe() {
        sharedByme = new ArrayList<>();
        alldata.add(new AllDataModel("Shared By Me", sharedByme, new Fragment_SharedCatalogs()));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.RECENTSHAREDCATAPP, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                SharedByMe[] response_catalogs = Application_Singleton.gson.fromJson(response, SharedByMe[].class);
                sharedByme.clear();
                for (SharedByMe resb : response_catalogs) {
                    sharedByme.add(resb);
                }
                allItemAdapter.notifyDataSetChanged();
            }

            @Override

            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getbrands() {
        brandsData = new ArrayList<>();
        alldata.add(new AllDataModel("Brands", brandsData, new Fragment_Brands()));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                brandsData.clear();
                for (Response_Brands resb : response_brands) {
                    brandsData.add(resb);
                }
                allItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });


    }

    private void getcatalogs() {
        catalogsData = new ArrayList<>();
        alldata.add(new AllDataModel("My Catalogs", catalogsData, new Fragment_Catalogs()));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "mycatalogs", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                catalogsData.clear();
                Response_catalogapp[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogapp[].class);
                for (Response_catalogapp resb : response_catalog) {
                    catalogsData.add(resb);
                }
                allItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    private void getSelections() {
        selData = new ArrayList<>();
        alldata.add(new AllDataModel("My Selections", selData, new Fragment_MySelections()));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "selections", "") + "?type=my", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

                selData.clear();
                Response_Selection[] response_selections = Application_Singleton.gson.fromJson(response, Response_Selection[].class);
                for (Response_Selection resb : response_selections) {
                    selData.add(resb);
                }
                allItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


}
