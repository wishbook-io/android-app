package com.wishbook.catalog.home.catalog.details;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.wishbook.catalog.commonadapters.ResponseDesignDetailsAdapter;
import com.wishbook.catalog.commonmodels.responses.Products;
import com.wishbook.catalog.commonmodels.responses.Response_Design_Details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by root on 10/11/16.
 */
public class Fragment_Details_Design extends GATrackedFragment {
    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private ResponseDesignDetailsAdapter responseDesignDetailsAdapter;
    private String id=null;
    private Response_Design_Details response_design_details;
    private ArrayList<Products> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.push_design_details, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        Bundle filter1 = getArguments();
        if (filter1 != null) {
            id = filter1.getString("id");
        }
        if(id!=null) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"pushes_with_id",id)+ "designwise/", null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    Log.v("sync response", response);
                    response_design_details = new Gson().fromJson(response, Response_Design_Details.class);
                    if(response_design_details!=null) {
                        Products[] product = response_design_details.getProducts();
                        list = new ArrayList<Products>(Arrays.asList(product));
                        responseDesignDetailsAdapter = new ResponseDesignDetailsAdapter(getActivity(), list);
                        mRecyclerView.setAdapter(responseDesignDetailsAdapter);
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });

        }
        return v;
    }
}
