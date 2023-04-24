package com.wishbook.catalog.rest;

import android.app.Activity;

import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;

import java.util.HashMap;

public class CampaignLogApi {

    private Activity context;
    private String campaign;

    public CampaignLogApi(Activity context, String campaign) {
        this.context = context;
        this.campaign = campaign;
        if (campaign != null && !campaign.isEmpty())
            postAsynchData(campaign);
    }

    public void postAsynchData(String campaign) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HttpManager.getInstance(context).methodPost(HttpManager.METHOD.GET, URLConstants.CAMPAIGNLOG + "?campaign=" + campaign, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

            }

            @Override
            public void onResponseFailed(ErrorString error) {
            }
        });
    }
}
