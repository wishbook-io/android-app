package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.CatalogsAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog;

import java.util.HashMap;

public class Fragment_Catalogs_Spare extends GATrackedFragment {

    private View v;
    private CatalogsAdapter adapter;
    private RelativeLayout filter;
    private RecyclerViewEmptySupport mRecyclerView;
    private String filtertype = null;
    private String filtervalue = null;
    private ProgressDialog progressDialog;

    public Fragment_Catalogs_Spare() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_catalogs, container, false);
/*
        Activity_Home.tabs.setVisibility(View.VISIBLE);
*/
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        filter = (RelativeLayout) v.findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Fragment_AddFilter addBuyergroupFragment =new Fragment_AddFilter();
                Bundle bundle = new Bundle();
                bundle.putBoolean("from_public",false);
                addBuyergroupFragment.setArguments(bundle);
                addBuyergroupFragment.setTargetFragment(Fragment_Catalogs_Spare.this, 1);
                addBuyergroupFragment.show(getActivity().getSupportFragmentManager(),"add");*/

                Intent intent = new Intent(getActivity(), ActivityFilter.class);
                intent.putExtra("from_public", false);
                startActivityForResult(intent,1);
            }
        });
        final Bundle filter1 = getArguments();
        if (filter1 != null) {
            filtertype = filter1.getString("filtertype");
            filtervalue = filter1.getString("filtervalue");

        }
        //catalogs();
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
                    filter.setVisibility(View.VISIBLE);

                } else if (dy > 5) {
                    // Recycle view scrolling down...
/*
                    Activity_Home.tabs.setVisibility(View.GONE);
*/
                    filter.setVisibility(View.GONE);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        if(Activity_Home.pref.getString("groupstatus", "0") .equals("2")){
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getActivity(). getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_main, new AddCatalog()).addToBackStack("addcatalog").commit();
                Application_Singleton.CONTAINER_TITLE="Add Catalog";
                Application_Singleton.CONTAINERFRAG=new Fragment_AddCatalog();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        catalogs();
    }

    private void showCatalogs(HashMap<String, String> params, Context context,String URL) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URL, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        //    adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), response_catalog);
                            mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
//        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.GET_CATALOGS_URL, null, new NetworkManager.customCallBack() {
//            @Override
//            public void onCompleted(int status, String response) {
//                if(status==NetworkManager.RESPONSESUCCESS){
//                    Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
//                    if(response_catalog.length>0) {
//                        if(response_catalog.length>0) {
//                            if (response_catalog[0].getId() != null) {
//                                adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), response_catalog);
//                                mRecyclerView.setAdapter(adapter);
//                            }
//                        }
//                    }
//                }
//            }
//        });


    }

   /* private void showCatalogsFiltered(String key, String value,HashMap<String, String> params, Context context) {


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"catalogs","")+ "?" + filtertype + "=" + filtervalue, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
                if(response_catalog.length>0) {
                    if(response_catalog.length>0) {
                        if (response_catalog[0].getId() != null) {
                            adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), response_catalog);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });*/

//        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.GET_CATALOGS_URL+ "?" + filtertype + "=" + filtervalue, null, new NetworkManager.customCallBack() {
//            @Override
//            public void onCompleted(int status, String response) {
//                if(status==NetworkManager.RESPONSESUCCESS){
//                    Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
//                    if(response_catalog.length>0) {
//                        if(response_catalog.length>0) {
//                            if (response_catalog[0].getId() != null) {
//                                adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), response_catalog);
//                                mRecyclerView.setAdapter(adapter);
//                            }
//                        }
//                    }
//                }
//            }
//        });
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    HashMap<String,String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                    if(params!=null)
                    {
                        if (filtertype == null | filtervalue == null) {
                            String URL = URLConstants.companyUrl(getActivity(),"catalogs","");
                            params.put("view_type","mycatalogs");
                            showCatalogs(params,getActivity(),URL);
                        } else {
                            String URL =  URLConstants.companyUrl(getActivity(),"catalogs","");
                            params.put(filtertype,filtervalue);
                            showCatalogs(params,getActivity(),URL);
                        }
                    }

                }
                break;
        }
    }
    private void catalogs() {
        if (filtertype == null | filtervalue == null) {
            HashMap<String, String> params=new HashMap<>();
            params.put("view_type","mycatalogs");
            //  String URL = URLConstants.companyUrl(getActivity(),"catalogs","")+"?view_type=mycatalogs";
            String URL = URLConstants.companyUrl(getActivity(),"catalogs","");
            showCatalogs(params,getActivity(),URL);
        } else {
            HashMap<String, String> params=new HashMap<>();
            params.put(filtertype,filtervalue);
            String URL = URLConstants.companyUrl(getActivity(),"catalogs","");
            // String URL =  URLConstants.companyUrl(getActivity(),"catalogs","")+ "?" + filtertype + "=" + filtervalue;
            showCatalogs(params,getActivity(),URL);
        }
    }
}