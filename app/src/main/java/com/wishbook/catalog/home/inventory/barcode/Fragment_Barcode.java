package com.wishbook.catalog.home.inventory.barcode;

import android.os.Bundle;
import androidx.annotation.Nullable;
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
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.inventory.barcode.expandableadapter.BrandAdapter;
import com.wishbook.catalog.home.inventory.barcode.expandableadapter.BrandAdapterExpandable;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 18/11/16.
 */
public class Fragment_Barcode extends GATrackedFragment {

    private ArrayList<Response_Brands> brands = new ArrayList<>();
    private ArrayList<Response_Brands> brandsnew = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    private BrandAdapter adapter;
    private BrandAdapterExpandable mBrandExpandableAdapter;
    private List<Response_catalog> catalogs =new ArrayList<>();
    private final int ITEM_TYPE_COMPANY = 1;
    private final int ITEM_TYPE_DEPARTMENT = 2;
    private final int ITEM_TYPE_EMPLOYEE = 3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.barcode_layout,container,false);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        getbrands();






        return v;


    }

    @Override
    public void onResume() {
        super.onResume();
     //getbrands();
    }

    private void getbrands() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"brands","") + "?device=app/", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    brands = new ArrayList<Response_Brands>(Arrays.asList(response_brands));
                    brandsnew.clear();
                    for (int i = 0; i < brands.size(); i++) {

                        brands.get(i).setExpanded(false);
                        if (brands.get(i).getTotal_catalogs() > 0) {
                            brandsnew.add(brands.get(i));
                        }

                    }
                    mBrandExpandableAdapter = new BrandAdapterExpandable(getActivity(), brandsnew, getActivity().getSupportFragmentManager(), Fragment_Barcode.this);
                    recyclerView.setAdapter(new AlphaInAnimationAdapter(mBrandExpandableAdapter));
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));



               /* mBaseExpandableAdapter.setExpandCollapseListener(new BaseExpandableAdapter.ExpandCollapseListener() {
                    @Override
                    public void onListItemExpanded(final int position) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"catalogs","") +"?brand="+brands.get(position).getId()+"",null, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                Log.v("cached response", response);
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Log.v("sync response ", response);
                                Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
                                if(response_catalog.length>0) {
                                    catalogs = new ArrayList<Response_catalog>(Arrays.asList(response_catalog));
                                    brands.get(position).setResponse_catalogs(catalogs);
                                    mBaseExpandableAdapter.modifyItem(position, brands);
                                }
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {

                            }
                        });
                    }
                    @Override
                    public void onListItemCollapsed(int position) {

                    }
                });*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

}
