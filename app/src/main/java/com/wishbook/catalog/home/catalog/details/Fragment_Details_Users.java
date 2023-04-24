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
import com.wishbook.catalog.commonadapters.ResponseUserDetailsAdapter;
import com.wishbook.catalog.commonmodels.responses.Buyers;
import com.wishbook.catalog.commonmodels.responses.Response_User_Details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by root on 10/11/16.
 */
public class Fragment_Details_Users extends GATrackedFragment {
    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private String id=null;
    private ResponseUserDetailsAdapter responseUserDetailsAdapter;
    private Response_User_Details response_user_details;
    private ArrayList<Buyers> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.push_user_details, ga_container, true);
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
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"pushes_with_id",id) + "buyerwise/", null, headers, true, new HttpManager.customCallBack() {
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
                        response_user_details = new Gson().fromJson(response, Response_User_Details.class);
                        if (response_user_details != null) {
                            Buyers[] buyers = response_user_details.getBuyers();
                            list = new ArrayList<Buyers>(Arrays.asList(buyers));
                            responseUserDetailsAdapter = new ResponseUserDetailsAdapter(getActivity(), list);
                            mRecyclerView.setAdapter(responseUserDetailsAdapter);
                        }
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

    return v;
    }
}
