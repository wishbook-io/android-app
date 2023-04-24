package com.wishbook.catalog.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.adapters.MeetingReportAdapter;
import com.wishbook.catalog.home.models.Response_meeting_report;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_MeetingReport extends GATrackedFragment {

    private Toolbar toolbar;


    private RecyclerViewEmptySupport mRecyclerView;

    private MeetingReportAdapter meetingReportAdapter;

    ArrayList<Response_meeting_report>  response_meeting_reports_list = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
       // getReport();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.meetingreport, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("Wishbook");
      /*  toolbar.inflateMenu(R.menu.menu_notification);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_notification) {
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_Notifications()).addToBackStack(null).commit();
                    Fragment_Notifications supplier=new Fragment_Notifications();
                    Application_Singleton.CONTAINER_TITLE="Notifications";
                    Application_Singleton.CONTAINERFRAG = supplier;
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                }
                return true;
            }
        });*/
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));


        meetingReportAdapter=new MeetingReportAdapter((AppCompatActivity) getActivity(),response_meeting_reports_list);
        mRecyclerView.setAdapter(meetingReportAdapter);

        getReport();

        getCompanyData();

        return v;
    }

    private void getCompanyData() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    //parsing response data
                    try {
                        CompanyProfile companyProfile = new Gson().fromJson(response, CompanyProfile.class);
                        if (getUserVisibleHint() && getContext() != null) {
                            UserInfo userInfo = new UserInfo(getContext());
                            userInfo.setBuyerSplitSalesperson(companyProfile.getBuyers_assigned_to_salesman());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                    Toast.makeText(getContext(), "Request Failed!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReport() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(),"meetingsreport",""), null, headers, true, new HttpManager.customCallBack() {
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
                    Response_meeting_report[] response_meeting_reports = Application_Singleton.gson.fromJson(response, Response_meeting_report[].class);
                    response_meeting_reports_list.clear();
                    for (int i = 0; i < response_meeting_reports.length; i++) {
                        response_meeting_reports_list.add(response_meeting_reports[i]);
                    }
                    meetingReportAdapter.notifyDataSetChanged();
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
