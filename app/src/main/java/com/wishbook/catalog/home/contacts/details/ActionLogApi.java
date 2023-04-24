package com.wishbook.catalog.home.contacts.details;


import android.app.Activity;

import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;

import java.util.HashMap;

public class ActionLogApi {

    public static String RELATION_TYPE_ENQUIRY = "Enquiry";
    public static String RELATION_TYPE_BUYER = "Buyer";
    public static String RELATION_TYPE_SUPPLIER = "Supplier";

    public static String ACTION_TYPE_CALL = "Call";
    public static String ACTION_TYPE_CHAT = "Chat";


    private Activity context;
    private String type;
    private String action;
    private String receiptCompany;

    public ActionLogApi(Activity context, String type, String action, String receiptCompany) {
        this.context = context;
        this.type = type;
        this.action = action;
        this.receiptCompany = receiptCompany;
        postAsynchData(type,action,receiptCompany);
    }

    public void postAsynchData(String type, String action, String receiptCompanyId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("relationship_type", type);
        params.put("action_type", action);
        params.put("recipient_company", receiptCompanyId);


        HttpManager.getInstance(context).methodPost(HttpManager.METHOD.POST, URLConstants.ACTIONLOG, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // ToDo after Post Device Information
            }

            @Override
            public void onResponseFailed(ErrorString error) {
            }
        });
    }


}
