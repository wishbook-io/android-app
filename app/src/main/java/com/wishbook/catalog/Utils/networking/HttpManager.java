package com.wishbook.catalog.Utils.networking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.models.Response_Key;
import com.wishbook.catalog.rest.RetroFitManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Vigneshkarnika on 03/04/16.
 */
public class HttpManager {

    public static int RESPONSE_SKIP = -128;
    public static int RESPONSE_ADD = -1;
    private static HttpManager instance;
    private final Activity context;
    MaterialDialog progressDialog =null;

    public HttpManager(Activity context) {
        this.context = context;
    }

    public static ArrayList<?> removeDuplicateIssue(int offset, ArrayList<?> response_buyers, boolean dataUpdated, int LIMIT) {
        if (dataUpdated && (offset + LIMIT) > LIMIT) {
            if (response_buyers.size() > offset) {
                response_buyers.subList(offset, response_buyers.size()).clear();
            }
        } else if (dataUpdated) {
            response_buyers.clear();
        }

        return response_buyers;
    }

    public static ArrayList<?> removeDuplicateIssue(int offset, ArrayList allDatalist, ArrayList responseDataList, boolean dataUpdated, int LIMIT) {
        if (dataUpdated && (offset + LIMIT) > LIMIT) {
            if (allDatalist.size() > offset) {

                for (int i = 0; i < responseDataList.size(); i++) {
                    allDatalist.set( i + offset, responseDataList.get(i));

                }
            }
        } else if (dataUpdated) {
            allDatalist.clear();
        }

        return allDatalist;
    }

    public static synchronized HttpManager getInstance(Activity context) {
        instance = new HttpManager(context);
        return instance;
    }

    public static boolean isOnline(Context context) {
        if(context!=null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } else {
            return false;
        }

    }

    public static void hideKeyboard() {
//        try {
//            View view = activity.getCurrentFocus();
//            if (view != null) {
//                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        }
//
    }

    public static MaterialDialog showProgress(Context context, boolean cancelable) {
        if (context == null) {
            context = Activity_Home.context;
        }
        return new MaterialDialog.Builder(context)
                .title("Loading")
                .content("Please wait..")
                .cancelable(cancelable)
                .progress(true, 0).build();
    }

    public static void showNetworkAlert(Context activity) {
        /*final AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);
        alert.setTitle("Network unavailable");
        alert.setMessage("Please check your Internet connection!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();*/
        new MaterialDialog.Builder(activity)
                .title("Network unavailable")
                .content("Please check your Internet connection!")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();

        /*if (!Application_Singleton.errorNoInternetShown) {
            Application_Singleton.errorNoInternetShown = true;
            new MaterialDialog.Builder(activity)
                    .title("Network unavailable")
                    .content("Please check your Internet connection!")
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();

        }*/

    }

    public static int contains(ArrayList<? extends Object> listOfCachedObject, Object singleObjectFromServer) {
        Log.d("Cache issue", " Entering listOfCachedObject Loop for checking against each object");

        for (int i = 0; i < listOfCachedObject.size(); i++) {

            //if data already in cache and not changed than do not add in arraylist
            try {

                //id from arraylist
                Field arrayListData = listOfCachedObject.get(i).getClass().getDeclaredField("id");
                arrayListData.setAccessible(true);
                Object idFromList = arrayListData.get(listOfCachedObject.get(i));


                //id from compared objects
                Field singleObjectData = singleObjectFromServer.getClass().getDeclaredField("id");
                singleObjectData.setAccessible(true);
                Object singleId = singleObjectData.get(singleObjectFromServer);

                Log.d("Cache issue", " Checking against each id");
                if (idFromList.equals(singleId)) {
                    Log.d("Cache issue", " Found Id equal");
                    /*//data till now from cache
                    String alreadyContainData = new Gson().toJson(listOfCachedObject.get(i));

                    //changed data from server
                    String dataToBeChecked = new Gson().toJson(singleObjectFromServer);

                    Log.d("Cache issue","Checking for data changed");
                    if (alreadyContainData.equals(dataToBeChecked)) {
                        return RESPONSE_SKIP;
                    } else {
                        return i;
                    }*/

                    return i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        Log.d("Cache issue", "If no id found than adding new object");
        return RESPONSE_ADD;
    }

    public void request(METHOD method, String url, HashMap<String, String> params, final customCallBack customcallBack) {
        request(method, url, params, null, false, customcallBack);
    }

    public void request(METHOD method, String url, final customCallBack customcallBack) {
        request(method, url, null, null, false, customcallBack);
    }

    public void requestwithOnlyHeaders(METHOD method, String url, HashMap<String, String> headers, final customCallBack customcallBack) {
        request(method, url, null, headers, false, customcallBack);
    }

    public void request(METHOD method, String url, HashMap<String, String> params, HashMap<String, String> headers, boolean cache, final customCallBack customcallBack) {
        Log.v("requestsending:", url + "params:" + new Gson().toJson(params) + "headers:" + new Gson().toJson(headers));
        switch (method) {
            case GET:
            case GETWITHPROGRESS:
                RetroFitManager.getInstance(context).request(method, url, params, headers, cache, customcallBack);
                //methodGet(method, url, params, headers, cache, customcallBack);
                break;
            case POST:
            case POSTWITHPROGRESS:
                RetroFitManager.getInstance(context).request(method, url, params, headers, cache, customcallBack);
               // methodPost(method, url, params, headers, cache, customcallBack);
                break;
            case PUT:
            case PUTWITHPROGRESS:
                methodPut(method, url, params, headers, cache, customcallBack);
                break;
            case DELETE:
            case DELETEWITHPROGRESS:
                RetroFitManager.getInstance(context).request(method, url, params, headers, cache, customcallBack);
                break;
            case POSTJSON:
            case POSTJSONWITHPROGRESS:
                methodPostJson(method, url, params, headers, cache, customcallBack);
                break;
            case HEAD:
                methodHead(method, url, params, headers, cache, customcallBack);
                break;


        }
    }

    public void requestPatch(METHOD method, String url, JsonObject params, HashMap<String, String> headers, final customCallBack customcallBack) {
        Log.v("requestsending:", url + "params:" + new Gson().toJson(params) + "headers:" + new Gson().toJson(headers));

        switch (method) {
            case PATCH:
            case PATCHWITHPROGRESS:
                methodPatch(method, url, params, headers, false, customcallBack);
                break;
        }
    }

    public void requestwithArray(METHOD method, String url, HashMap<String, List<String>> paramslist, HashMap<String, String> headers, boolean cache, final customCallBack customcallBack) {
        boolean cachecheck = false;
//        if (cache) {
//
//            cachecheck = checkIfCache(url + paramslistToGetQuery(paramslist), customcallBack);
//        }
        switch (method) {
            case POSTARRAY:
            case POSTARRAYWITHPROGRESS:
                methodPostArray(method, url, paramslist, headers, customcallBack);
                break;
        }
    }

    public void requestwithArrayParams(METHOD method, String url, HashMap<String, List<String>> paramslist, HashMap<String, String> params, HashMap<String, String> headers, boolean cache, final customCallBack customcallBack) {
        boolean cachecheck = false;
        if (cache) {
            cachecheck = checkIfCache(url + paramslistToGetQuery(paramslist), customcallBack);
        }
        switch (method) {
            case POSTARRAYPARAMS:
            case POSTARRAYPARAMSWITHPROGRESS:
                methodPostArrayParams(method, url, paramslist, params, headers, customcallBack);
                break;
        }
    }

    private void methodPostArrayParams(final METHOD method, final String url, final HashMap<String, List<String>> paramslist, final HashMap<String, String> params, HashMap<String, String> headers, final customCallBack customcallBack) {
        hideKeyboard();
        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.POSTARRAYPARAMSWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(paramslist)).setMultipartParameters(convertMap(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResultList(url, e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.POSTARRAYPARAMSWITHPROGRESS) {
                    try {
                        progressDialog.dismiss();

                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void requestwithObject(METHOD method, String url, JsonObject jsonObject, HashMap<String, String> headers, boolean cache, final customCallBack customcallBack) {
        switch (method) {
            case POSTJSONOBJECT:
            case POSTJSONOBJECTWITHPROGRESS:
            case PATCHJSONOBJECTWITHPROGRESS:
                methodPostJsonObject(method, url, jsonObject, headers, cache, customcallBack);
                break;
            case PUTWITHPROGRESS:
                methodPutJsonObject(method, url, jsonObject, headers, cache, customcallBack);
                break;
            case PATCHWITHPROGRESS:
                methodPatch(method, url, jsonObject, headers, cache, customcallBack);
                break;
        }
    }

    private void methodPostJsonObject(final METHOD method, final String url, final JsonObject jsonObject, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        Log.v("cache", "" + cache);
        boolean cacheAvailable = false;
        if (cache) {
            String getMd5 = MD5(processURL(url, jsonObject));
            cacheAvailable = checkIfCache(getMd5, customcallBack);
            Log.v("getMD5", "" + getMd5);
        }
        Log.v("cacheavailable", "" + cacheAvailable);
        hideKeyboard();
        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.POSTJSONOBJECTWITHPROGRESS || method == METHOD.PATCHJSONOBJECTWITHPROGRESS) {
            if (!cacheAvailable) {
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    if (progressDialog != null)
                        progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        final boolean finalCacheAvailable = cacheAvailable;
        if (method == METHOD.PATCHJSONOBJECTWITHPROGRESS) {
            NetworkProcessor.with(context).load("PATCH", url).addHeaders(convertMap(headers)).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    try {
                        processResult(cache, url, jsonObject, e, result, customcallBack);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if (e == null) {
                        if (result != null) {
                            if (result.getResult() != null) {
                                if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPostJsonObject(method, url, jsonObject, headers, cache, customcallBack);
                                    }
                                });*/
                                    //                         progressDialog.dismiss();
                                    LogoutCommonUtils.logout(context, true);
                                }
                            }
                        }
                    }
                    if (method == METHOD.POSTJSONOBJECTWITHPROGRESS || method == METHOD.PATCHJSONOBJECTWITHPROGRESS) {
                        try {
                            if (!finalCacheAvailable) {
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        } catch (Exception e1) {
                        }
                    }
                }
            });
        }else {
            NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers)).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    try {
                        processResult(cache, url, jsonObject, e, result, customcallBack);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if (e == null) {
                        if (result != null) {
                            if (result.getResult() != null) {
                                if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPostJsonObject(method, url, jsonObject, headers, cache, customcallBack);
                                    }
                                });*/
                                    //                         progressDialog.dismiss();
                                    LogoutCommonUtils.logout(context, true);
                                }
                            }
                        }
                    }
                    if (method == METHOD.POSTJSONOBJECTWITHPROGRESS || method == METHOD.PATCHJSONOBJECTWITHPROGRESS) {
                        try {
                            if (!finalCacheAvailable) {
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        } catch (Exception e1) {
                        }
                    }
                }
            });
        }
    }

    private void methodPutJsonObject(final METHOD method, final String url, final JsonObject jsonObject, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        Log.v("cache", "" + cache);
        boolean cacheAvailable = false;
        if (cache) {
            String getMd5 = MD5(processURL(url, jsonObject));
            cacheAvailable = checkIfCache(getMd5, customcallBack);
            Log.v("getMD5", "" + getMd5);
        }
        Log.v("cacheavailable", "" + cacheAvailable);
        hideKeyboard();
        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.PUTWITHPROGRESS) {
            if (!cacheAvailable) {
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    progressDialog.show();
                } catch (Exception e) {
                }
            }
        }
        final boolean finalCacheAvailable = cacheAvailable;
        NetworkProcessor.with(context).load("PUT", url).addHeaders(convertMap(headers)).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, jsonObject, e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (e == null) {
                    if (result != null) {
                        if (result.getResult() != null) {
                            if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPostJsonObject(method, url, jsonObject, headers, cache, customcallBack);
                                    }
                                });*/
                                progressDialog.dismiss();
                                LogoutCommonUtils.logout(context, true);
                            }
                        }
                    }
                }
                if (method == METHOD.PUTWITHPROGRESS) {
                    try {
                        if (!finalCacheAvailable) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void requestwithFile(METHOD method, String url, HashMap paramslist, HashMap<String, String> headers, String filename, String contentType, File file, boolean cache, final customCallBack customcallBack) {
        switch (method) {
            case FILEUPLOAD:
            case FILEUPLOADWITHPROGRESS:
                methodFileUpload(method, url, paramslist, headers, cache, customcallBack, filename, contentType, file);
                break;

            case FILEUPLOADPUTWITHPROGRESS:
                methodFileUploadPATCH(method, url, paramslist, headers, cache, customcallBack, filename, contentType, file);
                break;

            case PUTFILEWITHPROGRESS:
                methodPutFileUpload(method, url, paramslist, headers, cache, customcallBack, filename, contentType, file);
                break;

            case FILEUPLOADPATCHWITHPROGRESS:
                methodFileUploadPatch(method, url, paramslist, headers, cache, customcallBack, filename, contentType, file);

        }
    }

    private boolean checkIfCache(String md5, customCallBack customcallBack) {
        String cacheresponseStr = getCacheifExists(md5);
        if (cacheresponseStr != null) {
            //  JSONObject cacheresponse = null;
            try {
                //   cacheresponse = new Gson().fromJson(cacheresponseStr, JSONObject.class);
                if (cacheresponseStr != null) {
                    try {
                        Log.v("rescached:", "" + md5 + "---" + cacheresponseStr);
                        customcallBack.onCacheResponse(cacheresponseStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getCacheifExists(String md5) {
    /*    ResponseCache res = (Realm.getDefaultInstance().where(ResponseCache.class)).equalTo("key", md5).findFirst();
        if (res != null) {
            return res.getResponse();
        }*/

        return null;

    }

    public void methodGet(final METHOD method, final String url, final HashMap<String, String> params, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        Log.v("cache", "" + cache);
        boolean cacheAvailable = false;

        Log.v("url + params", url + "");

        if (cache) {
            //changed by abu
            String getMd5 = MD5(processURL(url, converttojson(params)));
            cacheAvailable = checkIfCache(getMd5, customcallBack);
            Log.v("getMD5", "" + getMd5);
            // Changed According JIRA (WB-1179)
            if(cacheAvailable && url.contains("/api/v1/shares/?view_type=mysent")) {
                return;
            }
        }
        Log.v("cacheavailable", "" + cacheAvailable);
        hideKeyboard();
        MaterialDialog progressDialog = null;
        if (method == METHOD.GETWITHPROGRESS) {
            progressDialog = showProgress(context, true);
            if (!cacheAvailable) {
                Log.v("newreq", "" + url);
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    //progressDialog.show();
                } catch (Exception e) {
                }
            } else {
                Log.v("oldreq", "" + url);
            }
        }
        final String buildUrl = url + paramsToGetQuery(params);
        final boolean finalCacheAvailable = cacheAvailable;
        final MaterialDialog finalProgressDialog = progressDialog;
        NetworkProcessor.with(context).load("GET", buildUrl).addHeaders(convertMap(headers)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, converttojson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (e == null) {
                    if (result != null) {
                        if (result.getResult() != null) {
                            if (result.getResult().contains("Invalid token")) {
                               /* regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodGet(method, url, params, headers, cache, customcallBack);
                                    }
                                });*/
                               if(finalProgressDialog!=null){
                                   finalProgressDialog.dismiss();
                               }

                                LogoutCommonUtils.logout(context, true);
                            }
                        }
                    }
                }
                if (method == METHOD.GETWITHPROGRESS) {
                    try {
                        if (!finalCacheAvailable) {
                            if(finalProgressDialog!=null){
                                finalProgressDialog.dismiss();
                            }
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    private void methodHead(final METHOD method, final String url, final HashMap<String, String> params, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        Log.v("cache", "" + cache);
        boolean cacheAvailable = false;
        if (cache) {
            //changed by abu
            String getMd5 = MD5(processURL(url, converttojson(params)));
            cacheAvailable = checkIfCache(getMd5, customcallBack);
            Log.v("getMD5", "" + getMd5);
        }
        Log.v("cacheavailable", "" + cacheAvailable);
        hideKeyboard();
        MaterialDialog progressDialog = null;
        if (method == METHOD.GETWITHPROGRESS) {
            progressDialog = showProgress(context, false);
            if (!cacheAvailable) {
                Log.v("newreq", "" + url);
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    progressDialog.show();
                } catch (Exception e) {
                }
            } else {
                Log.v("oldreq", "" + url);
            }
        }
        final String buildUrl = url + paramsToGetQuery(params);
        final boolean finalCacheAvailable = cacheAvailable;
        final MaterialDialog finalProgressDialog = progressDialog;
        NetworkProcessor.with(context).load("HEAD", buildUrl).addHeaders(convertMap(headers)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResultHead(cache, url, converttojson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (e == null) {
                    if (result != null) {
                        if (result.getResult() != null) {
                            if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodHead(method, url, params, headers, cache, customcallBack);
                                    }
                                });*/

                                if(finalProgressDialog!=null){
                                    finalProgressDialog.dismiss();
                                }

                                LogoutCommonUtils.logout(context, true);
                            }
                        }
                    }
                }
                if (method == METHOD.GETWITHPROGRESS) {
                    try {
                        if (!finalCacheAvailable) {
                            if(finalProgressDialog!=null){
                                finalProgressDialog.dismiss();
                            }

                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    private JsonObject converttojson(HashMap<String, String> params) {
        if (params == null) {
            return new JsonObject();
        }
        Gson gson = new Gson();
        String processed = gson.toJson(params);
        return gson.fromJson(processed, JsonObject.class);
    }

    public void regenrateToken(Activity context, final Listener listener) {
        Log.v("token failed", "getting new token");

        final SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        HashMap<String, String> params = new HashMap<>();
        params.put("username", pref.getString("userName", ""));
        params.put("password", pref.getString("password", ""));
        HttpManager.getInstance(context).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGIN_URL, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                Log.v("token failed", "got new token" + response);
                Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                if (response_key.getKey() != null) {
                    pref.edit().putString("key", response_key.getKey()).apply();
                    Application_Singleton.Token = response_key.getKey();
                    listener.onSuccess();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                Log.v("token failed", "unable to get new token");
            }
        });

    }

    private String processURL(String url, HashMap<String, String> params) {
        String processed;
        if (params == null) {
            processed = new Gson().toJson(new JsonObject());
        } else {
            processed = new Gson().toJson(params);
        }

        return processed + url;
    }

    private String processURL(String url, JsonObject params) {
        String processed;
        if (params == null) {
            processed = new Gson().toJson(new JsonObject());
        } else {
            processed = new Gson().toJson(params);
        }
        return processed + url;
    }

    private String paramslistToGetQuery(HashMap<String, List<String>> paramslist) {
        StringBuilder sb = new StringBuilder();
        if (paramslist != null) {
            sb.append("?");
            try {
                String[] keysets = (String[]) paramslist.keySet().toArray();
                for (int i = 0; i < paramslist.size(); i++) {
                    String key = keysets[i];
                    List<String> currentparam = paramslist.get(i);
                    for (String paramvalue : currentparam) {
                        sb.append(key + "=" + paramvalue + "&");
                    }
                }
            } catch (Exception e) {

            }
            Iterator it = paramslist.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                sb.append(pair.getKey() + "=" + pair.getValue() + "&");
                it.remove();
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();

    }

    public void methodPost(final METHOD method, final String url, final HashMap<String, String> params, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        hideKeyboard();
        RetroFitManager.getInstance(context).request(method, url, params, headers, cache, customcallBack);
        /*MaterialDialog progressDialog = showProgress(context, false);
        if(url.contains("onwishbook")){
            progressDialog = showProgress(context, true);
        }


        if (method == METHOD.POSTWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final MaterialDialog finalProgressDialog = progressDialog;
        Log.d("TAG", "methodPost: Called");
        NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                processResult(cache, url, converttojson(params), e, result, customcallBack);
                if (e == null) {
                    if (result != null) {
                        if (result.getResult() != null) {
                            if (result.getResult().contains("Invalid token")) {
                                *//*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPost(method, url, params, headers, cache, customcallBack);
                                    }
                                });*//*
                                if(finalProgressDialog!=null){
                                    finalProgressDialog.dismiss();
                                }
                                LogoutCommonUtils.logout(context, true);
                            }
                        }
                    }
                }
                if (method == METHOD.POSTWITHPROGRESS) {
                    try {
                        if(finalProgressDialog!=null){
                            finalProgressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
    }

    public void methodPut(final METHOD method, final String url, final HashMap<String, String> params, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        headers.put("X-HTTP-Method-Override", "PUT");
        hideKeyboard();
        if(Application_Singleton.canUseCurrentAcitivity()){
            progressDialog = showProgress(Application_Singleton.getCurrentActivity(),false);
        }
        if (method == METHOD.PUTWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                if(progressDialog!=null){
                    progressDialog.show();
                }
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("PUT", url).addHeaders(convertMap(headers)).setJsonObjectBody(convertJson(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, convertJson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (e == null) {
                    if (result != null) {
                        if (result.getResult() != null) {
                            if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPut(method, url, params, headers, cache, customcallBack);
                                    }
                                });*/
                                if(progressDialog!=null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }

                                LogoutCommonUtils.logout(context, true);
                            }
                        }
                    }
                }
                if (method == METHOD.PUTWITHPROGRESS) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void methodPatch(final METHOD method, final String url, final JsonObject params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        headers.put("X-HTTP-Method-Override", "PATCH");
        hideKeyboard();
        //   final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.PATCHWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                //           progressDialog.show();
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("PATCH", url).addHeaders(convertMap(headers)).setJsonObjectBody(params).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, params, e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.PATCHWITHPROGRESS) {
                    try {
                        //                     progressDialog.dismiss();
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void methodDelete(final METHOD method, final String url, final HashMap<String, String> params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        headers.put("X-HTTP-Method-Override", "DELETE");
        hideKeyboard();

        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.DELETEWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                //          progressDialog.show();
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("DELETE", url).addHeaders(convertMap(headers)).setJsonObjectBody(convertJson(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, converttojson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.DELETEWITHPROGRESS) {
                    try {
                        //                progressDialog.dismiss();
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void methodPutFileUpload(final METHOD method, final String url, final HashMap<String, String> params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack, String filename, String contentType, File file) {
        hideKeyboard();
        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.PUTFILEWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("PUT", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(params)).setMultipartFile(filename, contentType, file).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, convertJson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.PUTFILEWITHPROGRESS) {
                    try {
                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void methodFileUpload(final METHOD method, final String url, final HashMap params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack, String filename, String contentType, File file) {
        hideKeyboard();
       /* try {
            RetroFitManager.getInstance(context).request(method, url, params, headers,filename,contentType,file, cache, customcallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if(Application_Singleton.canUseCurrentAcitivity()){
           progressDialog = showProgress(context, false);
        }

        if (method == METHOD.FILEUPLOADWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers))
                .setTimeout(Constants.FILE_UPLOAD_TIME_OUT_MILLISECOND)
                .setMultipartParameters(convertMap(params)).setMultipartFile(filename, contentType, file).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, convertJson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    Log.e("FILEUPLOAD", "Exception " );
                    e1.printStackTrace();
                }
                if (method == METHOD.FILEUPLOADWITHPROGRESS) {
                    try {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                        Log.e("FILEUPLOAD", "Exception " );
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void methodFileUploadPatch(final METHOD method, final String url, final HashMap params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack, String filename, String contentType, File file) {
        hideKeyboard();

        if(file == null) {
            methodPatch(METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, false, customcallBack);
            return;
        }
       /* try {
            RetroFitManager.getInstance(context).request(method, url, params, headers,filename,contentType,file, cache, customcallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if(Application_Singleton.canUseCurrentAcitivity()){
            progressDialog = showProgress(context, false);
        }

        if (method == METHOD.FILEUPLOADPATCHWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        NetworkProcessor.with(context).load("PATCH", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(params)).setMultipartFile(filename, contentType, file).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, convertJson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    Log.e("FILEUPLOAD", "Exception " );
                    e1.printStackTrace();
                }
                if (method == METHOD.FILEUPLOADPATCHWITHPROGRESS) {
                    try {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                        Log.e("FILEUPLOAD", "Exception " );
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void methodFileUploadPATCH(final METHOD method, final String url, final HashMap params, HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack, String filename, String contentType, File file) {
        hideKeyboard();
        final MaterialDialog progressDialog = showProgress(context, false);
        if (method == METHOD.FILEUPLOADWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
        NetworkProcessor.with(context).load("PUT", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(params)).setMultipartFile(filename, contentType, file).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResult(cache, url, convertJson(params), e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.FILEUPLOADWITHPROGRESS) {
                    try {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    public void methodPostJson(final METHOD method, final String url, final HashMap<String, String> params, final HashMap<String, String> headers, final boolean cache, final customCallBack customcallBack) {
        hideKeyboard();
        Log.v("cache", "" + cache);
        boolean cacheAvailable = false;
        if (cache) {

            String getMd5 = MD5(processURL(url, params));
            cacheAvailable = checkIfCache(getMd5, customcallBack);
            Log.v("getMD5", "" + getMd5);
        }
        Log.v("cacheavailable", "" + cacheAvailable);
        MaterialDialog progressDialog = null;
        if(Application_Singleton.canUseCurrentAcitivity()){
           progressDialog = showProgress(Application_Singleton.getCurrentActivity(), false);
        }

        if (method == METHOD.POSTJSONWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                if (progressDialog != null) {
                    progressDialog.show();
                }
            } catch (Exception e) {
            }
        }

        final MaterialDialog finalProgressDialog = progressDialog;
        NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers)).setJsonObjectBody(convertJson(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    try {
                        processResult(cache, url, converttojson(params), e, result, customcallBack);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if (e == null) {
                        if (result != null) {
                            if (result.getResult() != null) {
                                if (result.getResult().contains("Invalid token")) {
                                /*regenrateToken(context, new Listener() {
                                    @Override
                                    public void onSuccess() {
                                        methodPostJson(method, url, params, headers, cache, customcallBack);
                                    }
                                });*/
                                    if (finalProgressDialog != null && finalProgressDialog.isShowing()) {
                                        finalProgressDialog.dismiss();
                                    }
                                    LogoutCommonUtils.logout(context, true);
                                }
                            }
                        }
                    }
                    if (method == METHOD.POSTJSONWITHPROGRESS) {
                        try {
                            finalProgressDialog.dismiss();
                        } catch (Exception e1) {
                        }
                    }
                }
            });

    }

    public void methodPostArray(final METHOD method, final String url, final HashMap<String, List<String>> params, HashMap<String, String> headers, final customCallBack customcallBack) {
        hideKeyboard();
        MaterialDialog progressDialog = null;
        if(Application_Singleton.canUseCurrentAcitivity()){
            progressDialog = showProgress(Application_Singleton.getCurrentActivity(), false);
        }

        if (method == METHOD.POSTARRAYWITHPROGRESS) {
            if (!isOnline(context)) {
                showNetworkAlert(context);
                return;
            }
            try {
                if (progressDialog != null) {
                    progressDialog.show();
                }

            } catch (Exception e) {

            }
        }
        final MaterialDialog finalProgressDialog = progressDialog;
        NetworkProcessor.with(context).load("POST", url).addHeaders(convertMap(headers)).setMultipartParameters(convertMap(params)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    processResultList(url, e, result, customcallBack);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (method == METHOD.POSTARRAYWITHPROGRESS) {
                    try {
                        if(finalProgressDialog !=null){
                            finalProgressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                    }
                }
            }
        });
    }

    private void processResultList(String url, Exception e, Response<String> result, customCallBack customcallBack) throws ParseException {
        if (e == null) {
            if (result != null) {
                int headerCode = result.getHeaders().code();
                switch (headerCode) {
                    case 401:
                    case 403:
                        LogoutCommonUtils.logout(context, true);
                        /*if(!Application_Singleton.errorLogoutpopshown) {
                            Application_Singleton.errorLogoutpopshown = true;
                            LogoutCommonUtils.logout(context,true);
                            new MaterialDialog.Builder(context)
                                    .title("Oops!!")
                                    .content("Someone just logout with your username! You have to relogin.")
                                    .positiveText("Re-Login")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            Application_Singleton.errorLogoutpopshown = true;
                                            dialog.dismiss();
                                            LogoutCommonUtils.logout(context, true);
                                        }
                                    }).show();
                        }*/
                        //  returnCallBack(customcallBack, null, "Someone just logout with your username! You have to relogin.");
                        break;
                    case 404:
                        if(Application_Singleton.canUseCurrentAcitivity()){
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("Not Found!!")
                                    .content("Data not found!!")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();

                                        }
                                    }).show();
                        }

                        //returnCallBack(customcallBack, null, "Data not found");
                        break;
                  //  case 502:
                    case 500:
                        if(Application_Singleton.canUseCurrentAcitivity()) {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("Server Down")
                                    .content("Server is Down for a while!! Please try again after sometime.")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                        // returnCallBack(customcallBack, null, "Server is Down for a while!! Please try again after sometime.");
                        break;
                    case 400:
                        Log.v("resulterror", url);

                        String error = result.getResult();

                        if (!formatResult(error).equals("")
                                || !error.contains("handshake")
                                || !error.contains("cache")
                                || !error.contains("failed to rename")
                                || !error.contains("connect")
                                ) {
                            if (!url.contains("device/gcm/")) {
                                returnCallBack(customcallBack, formatResult(result.getResult()));
                            }
                        }
                        /*if (!formatResult(result.getResult()).equals("")) {
                            if (!url.contains("device/gcm/")) {
                                returnCallBack(customcallBack, formatResult(result.getResult()));
                            }
                        }*/

                        break;
                    default:
                        Log.v("result", "" + result.getResult());
                        returnCallBack(customcallBack, result, "");
                        break;
                }
            } else {
                try {
                    returnCallBack(customcallBack, null, "Unknown Error");
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        } else {

            if (!e.getMessage().contains("Unable to resolve host")) {
                try {
                    returnCallBack(customcallBack, null, e.getMessage());
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }


    }

    private void insertNewCache(String md5gen, String result) {
        /*ResponseCache responseCache = new ResponseCache();
        responseCache.setKey(md5gen);
        responseCache.setResponse(result);
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealmOrUpdate(responseCache);
        Realm.getDefaultInstance().commitTransaction();*/
    }

    public void removeCacheParams(String url, HashMap<String, String> params) {
      /*  String md5gen = MD5(processURL(url, params));
        Realm.getDefaultInstance().beginTransaction();

        ResponseCache data = (Realm.getDefaultInstance().where(ResponseCache.class)).equalTo("key", md5gen).findFirst();
        if (data != null) {
            data.deleteFromRealm();
        }
        Realm.getDefaultInstance().commitTransaction();*/
    }

    public void removeCacheJsonObject(String url, JsonObject jsonobject) {
       /* String md5gen = MD5(processURL(url, jsonobject));
        ;
        Realm.getDefaultInstance().beginTransaction();

        ResponseCache data = (Realm.getDefaultInstance().where(ResponseCache.class)).equalTo("key", md5gen).findFirst();
        if (data != null) {
            data.deleteFromRealm();
        }

        Realm.getDefaultInstance().commitTransaction();*/
    }

    private JsonObject convertJson(HashMap<String, String> params) {
        if (params == null) {
            return new JsonObject();
        }
        HashMap<String, String> params1 = new HashMap<>();
        params1.putAll(params);
        JsonObject jsonObject = new JsonObject();
        if (params1 != null) {
            Iterator it = params1.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                jsonObject.addProperty("" + pair.getKey(), "" + pair.getValue());
                it.remove();
            }
        }
        return jsonObject;
    }

    public Map<String, List<String>> convertMap(HashMap params) {
        Map<String, List<String>> paramstopost = new HashMap<>();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ArrayList<String> stringMap = new ArrayList<>();
                stringMap.add(pair.getValue().toString());
                paramstopost.put(pair.getKey().toString(), stringMap);
                it.remove();
            }
        }
        return paramstopost;
    }

    public void returnCallBack(customCallBack customcallBack, Response<String> result, String message) throws ParseException {
        if (customcallBack != null) {
            if (result == null) {


                if (!HttpManager.formatResult(message).equals("")
                        || !message.contains("handshake")
                        || !message.contains("cache")
                        || !message.contains("failed to rename")
                        || !message.contains("connect")
                        ) {
                    customcallBack.onResponseFailed(new ErrorString("Error", message));
                }


            } else {
                customcallBack.onServerResponse(result.getResult(), true);
            }

        }
    }

    public void returnCallBack(customCallBack customcallBack, ErrorString message) {
        if (customcallBack != null) {


            customcallBack.onResponseFailed(message);
        }
    }

    void processResult(boolean saveToCache, String url, JsonObject params, Exception e, Response<String> result, customCallBack customcallBack) throws ParseException {
        if (e == null) {
            if (result != null) {
                int headerCode = result.getHeaders().code();
                switch (headerCode) {
                    case 401:
                    case 403:
                        LogoutCommonUtils.logout(context, true);
                        /*if(!Application_Singleton.errorLogoutpopshown) {
                            Application_Singleton.errorLogoutpopshown = true;
                            LogoutCommonUtils.logout(context,true);
                        }*/
                        //  returnCallBack(customcallBack, null, "Someone just logout with your username! You have to relogin.");
                        break;
                    case 404:
                        if(Application_Singleton.canUseCurrentAcitivity()){
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("404")
                                    .content("Data not found")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();

                                        }
                                    }).show();
                        }
                        //returnCallBack(customcallBack, null, "Data not found");
                        break;
                   // case 502:
                    case 500:
                        if(Application_Singleton.canUseCurrentAcitivity()){
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("500")
                                    .content("Server is down for a while!! Please try again after sometime.")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                        if(url.contains("manifest-images") || url.contains("seller-invoice-images") || url.contains("imagesearch")) {
                            returnCallBack(customcallBack, formatResult(result.getResult()));
                        }
                        // returnCallBack(customcallBack, null, "Server is Down for a while!! Please try again after sometime.");
                        break;
                    case 400:
                        Log.v("resulterror", url);

                        String error = result.getResult();

                        if (!formatResult(error).equals("")
                                || !error.contains("handshake")
                                || !error.contains("cache")
                                || !error.contains("failed to rename")
                                || !error.contains("connect")
                                ) {
                            if (!url.contains("device/gcm/")) {
                                returnCallBack(customcallBack, formatResult(result.getResult()));
                            }
                        }

                        break;
                    default:
                        Log.v("result", "" + result.getResult());
                        if (saveToCache) {
                            String md5gen = MD5(processURL(url, params));
                            String cacheresponse = getCacheifExists(md5gen);
                            if (cacheresponse != null) {
                                if (cacheresponse.equals(result.getResult())) {
                                    Log.v("md5insertsamechanges", cacheresponse);
                                    return;
                                } else {
                                    try {
                                        insertNewCache(md5gen, result.getResult());
                                        returnCallBack(customcallBack, result, "");
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            try {
                                insertNewCache(md5gen, result.getResult());
                                returnCallBack(customcallBack, result, "");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            returnCallBack(customcallBack, result, "");
                        }
                        break;
                }
            } else {
                try {
                    returnCallBack(customcallBack, null, "Unknown Error");
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            if (e.getMessage() != null) {
                if (!e.getMessage().contains("Unable to resolve host")) {
                    returnCallBack(customcallBack, null, e.getMessage());
                }
            }
        }
    }

    public static ErrorString formatResult(String result) {
        ErrorString errorString = new ErrorString();
        errorString.setErrormessage("");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                errorString.setErrorkey(StringUtils.capitalize(key.toLowerCase().trim()));
                try {
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray) {
                        errorString.setErrormessage(((JSONArray) value).getString(0));
                    } else {
                        errorString.setErrormessage(value.toString());
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            errorString.setErrormessage("");
            e.printStackTrace();
        }
        return errorString;
    }

    private void processResultHead(boolean saveToCache, String url, JsonObject params, Exception e, Response<String> result, customCallBack customcallBack) throws ParseException {

        if (result.getHeaders() != null) {
            int headerCode = result.getHeaders().code();
            switch (headerCode) {
                case 404:
                    returnCallBack(customcallBack, null, "");
                    break;
                default:
                    returnCallBack(customcallBack, result, "");
                    break;

            }
        }
    }

    private String paramsToGetQuery(HashMap params) {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            sb.append("?");
            HashMap localparams = params;
            Iterator it = localparams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                sb.append(pair.getKey() + "=" + pair.getValue() + "&");
                //CHANGED BY ABU
                //  it.remove();
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    enum STATUS {
        SUCCESS, FAIL
    }

    public enum METHOD {
        GET,
        GETWITHPROGRESS,
        HEAD,
        POST,
        POSTWITHPROGRESS,
        PUT,
        PUTWITHPROGRESS,
        PUTFILEWITHPROGRESS,
        PATCH,
        PATCHWITHPROGRESS,
        DELETE,
        DELETEWITHPROGRESS,
        FILEUPLOAD,
        FILEUPLOADWITHPROGRESS,
        FILEUPLOADPATCHWITHPROGRESS,
        FILEUPLOADPUTWITHPROGRESS,
        POSTJSON,
        POSTJSONWITHPROGRESS,
        POSTARRAY,
        POSTARRAYPARAMS,
        POSTARRAYWITHPROGRESS,
        POSTARRAYPARAMSWITHPROGRESS,
        POSTJSONOBJECT,
        POSTJSONOBJECTWITHPROGRESS,
        PATCHJSONOBJECTWITHPROGRESS
    }

    interface Listener {
        void onSuccess();
    }

    public interface customCallBack {
        void onCacheResponse(String response);

        void onServerResponse(String response, boolean dataUpdated) throws ParseException;


        void onResponseFailed(ErrorString error);
    }

}
