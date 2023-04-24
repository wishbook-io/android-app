package com.wishbook.catalog.home.contacts.details;

import android.os.Bundle;
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
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BuyerMeetingAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Fragment_Details_Meetings extends GATrackedFragment {

    private View v;
    private RecyclerView mRecyclerView;
    private BuyerMeetingAdapter buyerMeetingAdapter;


    public Fragment_Details_Meetings() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bdetorders, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        if (getArguments() != null) {
            if (getArguments().getString("buyerComapnyId") != null) {
                getMeetings(getArguments().getString("buyerComapnyId"));
            }
        }
        return v;
    }


    private void getMeetings(String buyerComapnyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.userUrl(getActivity(), "meetings", "") + "?buying_company_ref=" + buyerComapnyId+"&user="+ UserInfo.getInstance(getActivity()).getUserId();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS,url , null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_meeting[] temp = Application_Singleton.gson.fromJson(response, Response_meeting[].class);
                final ArrayList<Response_meeting> response_meetingsList = new ArrayList<Response_meeting>();
                if (temp.length > 0) {
                    Collections.addAll(response_meetingsList, temp);
                    BuyerMeetingAdapter buyerMeetingAdapter = new BuyerMeetingAdapter(getActivity(), response_meetingsList);
                    mRecyclerView.setAdapter(buyerMeetingAdapter);
                } else {

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }

        });

    }
}
