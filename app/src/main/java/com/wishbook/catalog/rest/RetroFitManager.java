package com.wishbook.catalog.rest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.wishbook.catalog.Utils.networking.HttpManager.isOnline;
import static com.wishbook.catalog.Utils.networking.HttpManager.showNetworkAlert;
import static com.wishbook.catalog.Utils.networking.HttpManager.showProgress;


public class RetroFitManager {

    private static RetroFitManager instance;
    private final Activity context;
    private static String TAG = RetroFitManager.class.getSimpleName();
    MaterialDialog progressDialog;


    public RetroFitManager(Activity context) {
        this.context = context;
    }


    public static synchronized RetroFitManager getInstance(Activity context) {
        instance = new RetroFitManager(context);
        return instance;
    }


    public void request(final HttpManager.METHOD method, final String url, HashMap<String, String> params, final HashMap<String, String> headers, boolean cache, final HttpManager.customCallBack customcallBack) {

        Call<ResponseBody> call = null;

        if (method == HttpManager.METHOD.GET || method == HttpManager.METHOD.GETWITHPROGRESS) {
            if (params == null)
                call = getService(cache, params, url).getData(url);
            else
                call = getService(cache, params, url).getData(url, params);


        } else if (method == HttpManager.METHOD.POST || method == HttpManager.METHOD.POSTWITHPROGRESS) {
            if (Application_Singleton.canUseCurrentAcitivity()) {
                progressDialog = showProgress(Application_Singleton.getCurrentActivity(), false);
            }

            if (url.contains("onwishbook")) {
                if (Application_Singleton.canUseCurrentAcitivity()) {
                    progressDialog = showProgress(Application_Singleton.getCurrentActivity(), true);
                }
            }
            if (method == HttpManager.METHOD.POSTWITHPROGRESS) {
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            call = getNoCacheService().postData(url, converttojson(params));
        } else if (method == HttpManager.METHOD.DELETE || method == HttpManager.METHOD.DELETEWITHPROGRESS) {
            if (Application_Singleton.canUseCurrentAcitivity()) {
                progressDialog = showProgress(Application_Singleton.getCurrentActivity(), false);
            }
            if (method == HttpManager.METHOD.DELETEWITHPROGRESS) {
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            call = getNoCacheService().deleteData(url);
        }


        if (call != null) {

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                    StaticFunctions.hideOfflineSnackBar(context);
                    if (method == HttpManager.METHOD.POSTWITHPROGRESS
                            || method == HttpManager.METHOD.DELETEWITHPROGRESS) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e1) {

                        }
                    }
                    if (rawResponse.isSuccessful()) {
                        String response = null;
                        try {
                            if (context != null && !context.isFinishing()) {
                                if (rawResponse != null && rawResponse.body() != null)
                                    response = rawResponse.body().string();
                            } else {
                                return;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        try {
                            customcallBack.onServerResponse(response, true);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handleFailed(url, method, rawResponse, customcallBack, call);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Retorfit", "onFailure");
                    t.printStackTrace();

                    if (method == HttpManager.METHOD.POSTWITHPROGRESS
                            || method == HttpManager.METHOD.DELETEWITHPROGRESS) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (t instanceof SocketTimeoutException || t instanceof UnknownHostException) {
                        if (!isOnline(context)) {
                            StaticFunctions.showOfflineSnackBar(context);
                        } else {
                            try {
                                if (context != null && !context.isFinishing() && (method == HttpManager.METHOD.POSTWITHPROGRESS
                                        || method == HttpManager.METHOD.DELETEWITHPROGRESS)) {
                                    new MaterialDialog.Builder(context)
                                            .title("Connection Timeout")
                                            .content("Retry after few seconds")
                                            .positiveText("OK")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            } catch (WindowManager.BadTokenException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (context != null && !context.isFinishing()) {
                            Log.e(TAG, "onFailure: instance of SocketTimeoutException Else not Finis" );
                            t.printStackTrace();
                            String error = t.getMessage();
                            if (!HttpManager.formatResult(error).getErrormessage().equals("")
                                    && !error.contains("cache")
                                    && !error.contains("failed to rename")
                                    && !error.contains("connect")) {
                                customLogException(url, call.request().method(), t.getMessage(), call.request().body().toString(), 0);
                            } else if(error!=null && error.contains("handshake aborted")) {
                                try {
                                    showErrorConnectionPopupDialog(error);
                                    customLogException(url, call.request().method(), t.getMessage(), "", 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }

    }


    public void request(final HttpManager.METHOD method, final String url, HashMap params, final HashMap<String, String> headers, String filename, String contentType, File file, boolean cache, final HttpManager.customCallBack customcallBack) {
        Call<ResponseBody> call = null;
        Log.e(TAG, "request: ====>" + url);
        if (method == HttpManager.METHOD.FILEUPLOAD || method == HttpManager.METHOD.FILEUPLOADWITHPROGRESS) {
            if (Application_Singleton.canUseCurrentAcitivity()) {
                progressDialog = showProgress(Application_Singleton.getCurrentActivity(), false);
            }
            if (method == HttpManager.METHOD.FILEUPLOADWITHPROGRESS) {
                if (!isOnline(context)) {
                    showNetworkAlert(context);
                    return;
                }
                try {
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData(filename, file.getName(), reqFile);
            call = getNoCacheService().postImageUpload(url, body, converttoRequestBody(params));
        }


        // Sync Task
        /*if (call != null) {
            try {
                retrofit2.Response<ResponseBody> rawResponse = call.execute();
                StaticFunctions.hideOfflineSnackBar(context);
                if (method == HttpManager.METHOD.FILEUPLOADWITHPROGRESS) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (rawResponse.isSuccessful()) {
                    String response = null;
                    try {
                        if (context != null && !context.isFinishing()) {
                            if (rawResponse != null && rawResponse.body() != null)
                                response = rawResponse.body().string();
                        } else {
                            return;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    try {
                        customcallBack.onServerResponse(response, true);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleFailed(url, method, rawResponse, customcallBack,call);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (method == HttpManager.METHOD.POSTWITHPROGRESS
                        || method == HttpManager.METHOD.DELETEWITHPROGRESS) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                    if (!isOnline(context)) {
                        StaticFunctions.showOfflineSnackBar(context);
                    } else {
                      // handleFailed(url,method,rawResponse,customcallBack,call);
                    }
                } else {
                    if (context != null && !context.isFinishing()) {
                        e.printStackTrace();
                        String error = e.getMessage();
                        if (!HttpManager.formatResult(error).getErrormessage().equals("")
                                && !error.contains("handshake")
                                && !error.contains("cache")
                                && !error.contains("failed to rename")
                                && !error.contains("connect")
                                ) {
                            //  customcallBack.onResponseFailed(HttpManager.formatResult(error));
                        }

                    }
                }
            }*/


        // Async Task
        if (call != null) {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                    StaticFunctions.hideOfflineSnackBar(context);
                    if (method == HttpManager.METHOD.FILEUPLOADWITHPROGRESS) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e1) {

                        }
                    }
                    if (rawResponse.isSuccessful()) {
                        Log.e("Retorfit", "On Sucess FILE UPLOAD");
                        String response = null;
                        try {
                            if (context != null && !context.isFinishing()) {
                                if (rawResponse != null && rawResponse.body() != null)
                                    response = rawResponse.body().string();
                            } else {
                                return;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        try {
                            customcallBack.onServerResponse(response, true);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Retorfit", "onFailure FILE UPLOAD");
                        handleFailed(url, method, rawResponse, customcallBack, call);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Retorfit", "onFailure");
                    if (method == HttpManager.METHOD.POSTWITHPROGRESS
                            || method == HttpManager.METHOD.DELETEWITHPROGRESS) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (t instanceof SocketTimeoutException || t instanceof UnknownHostException) {
                        if (!isOnline(context)) {
                            StaticFunctions.showOfflineSnackBar(context);
                        } else {
                            try {
                                if (context != null && !context.isFinishing() && (method == HttpManager.METHOD.POSTWITHPROGRESS
                                        || method == HttpManager.METHOD.DELETEWITHPROGRESS)) {
                                    new MaterialDialog.Builder(context)
                                            .title("Connection Timeout")
                                            .content("Retry after few seconds")
                                            .positiveText("OK")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            } catch (WindowManager.BadTokenException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (context != null && !context.isFinishing()) {
                            t.printStackTrace();
                            String error = t.getMessage();
                            if (!HttpManager.formatResult(error).getErrormessage().equals("")
                                    && !error.contains("cache")
                                    && !error.contains("failed to rename")
                                    && !error.contains("connect")) {
                                customLogException(url, call.request().method(), t.getMessage(), call.request().body().toString(), 0);
                            } else if( error!=null && error.contains("handshake aborted")) {
                                try {
                                    showErrorConnectionPopupDialog(error);
                                    customLogException(url, call.request().method(), t.getMessage(), "", 0);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }


    }


    private JsonObject converttojson(HashMap<String, String> params) {
        if (params == null) {
            return new JsonObject();
        }
        Gson gson = new Gson();
        String processed = gson.toJson(params);
        return gson.fromJson(processed, JsonObject.class);
    }

    private Map<String, RequestBody> converttoRequestBody(HashMap<String, Object> params) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        MediaType plain_text = MediaType.parse("text/plain");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            requestBodyMap.put(key, RequestBody.create(plain_text, String.valueOf(entry.getValue())));
        }

        return requestBodyMap;


    }

    private void handleFailed(String url, HttpManager.METHOD method, Response<ResponseBody> rawResponse, HttpManager.customCallBack customcallBack, Call<ResponseBody> call) {
        try {

            int headerCode = rawResponse.code();
            switch (headerCode) {
                case 401:
                case 403:
                    LogoutCommonUtils.logout(context, true);
                    break;
                case 404:
                    try {
                        if (url != null && url.contains("carts/")) {
                            clearCartPref(context);
                            String error = rawResponse.errorBody().string().toString();
                            customcallBack.onResponseFailed(HttpManager.formatResult(error));
                        } else {
                            if (Application_Singleton.canUseCurrentAcitivity()) {
                                new MaterialDialog.Builder(context)
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 500:
                    if (method != HttpManager.METHOD.GET
                            && method != HttpManager.METHOD.GETWITHPROGRESS) {
                        new MaterialDialog.Builder(context)
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
                case 400: {
                    Log.v("resulterror", url);
                    String error = rawResponse.errorBody().string().toString();
                    if (!HttpManager.formatResult(error).getErrormessage().equals("")
                            && !error.contains("handshake")
                            && !error.contains("cache")
                            && !error.contains("failed to rename")
                            && !error.contains("connect")
                    ) {
                        if (!url.contains("device/gcm/")) {
                            customcallBack.onResponseFailed(HttpManager.formatResult(error));
                            customLogException(url, call.request().method(), rawResponse.toString(), call.request().body().toString(), headerCode);
                        }
                    } else {
                        customLogException(url, call.request().method(), rawResponse.toString(), call.request().body().toString(), headerCode);
                    }
                }
                break;
                default:
                    String error = rawResponse.errorBody().string().toString();
                    if (!HttpManager.formatResult(error).getErrormessage().equals("") &&
                            (!error.contains("cache")
                                    && !error.contains("failed to rename")
                                    && !error.contains("connect"))) {
                        customcallBack.onResponseFailed(HttpManager.formatResult(error));
                        customLogException(url, call.request().method(), rawResponse.toString(), call.request().body().toString(), headerCode);
                    } else {
                        customLogException(url, call.request().method(), rawResponse.toString(), call.request().body().toString(), headerCode);
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void customLogException(String url, String method, String errorbody, String requestbody, int errorcode) {
        try {
            StringBuffer custom_message = new StringBuffer();
            custom_message.append("User Id===>" + UserInfo.getInstance(context).getUserId() + "\n");
            custom_message.append("Request URL==>" + url + "\n");
            custom_message.append("Request Method==>" + method + "\n");
            custom_message.append("Error code==>" + errorcode + "\n");
            custom_message.append("Error Body==>" + errorbody + "\n");
            Crashlytics.logException(new Exception(custom_message.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearCartPref(Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            preferences.edit().putInt("cartcount", 0).commit();
            preferences.edit().putString("cartId", "").commit();
            preferences.edit().putString("cartdata", "").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WishbookClient getService(boolean cache, HashMap<String, String> params, String url) {
        if (cache) {
            if (params != null) {
                if (params.containsKey("most_viewed") || params.containsKey("most_ordered")) {
                    return getLongCacheService();
                } else {
                    if (Application_Singleton.nomalService == null) {
                        Application_Singleton.nomalService = new RetroFitServiceGenerator(context).createService(WishbookClient.class);
                        return Application_Singleton.nomalService;
                    } else {
                        return Application_Singleton.nomalService;
                    }
                }
            } else {
                if (url != null && url.equalsIgnoreCase(URLConstants.USER_FEED_LIST + "?page=2")) {
                    return getLongCacheService();
                } else {
                    return getDefaultService();
                }
            }

        } else {
            if (Application_Singleton.pullToRefreshService == null) {
                Application_Singleton.pullToRefreshService = new RetroFitServiceGenerator(context).updateService(WishbookClient.class);
                return Application_Singleton.pullToRefreshService;
            } else {
                return Application_Singleton.pullToRefreshService;
            }

        }
    }

    private WishbookClient getNoCacheService() {
        if (Application_Singleton.noCacheService == null) {
            Application_Singleton.noCacheService = new RetroFitServiceGenerator(context).createServiceNoCache(WishbookClient.class);
            return Application_Singleton.noCacheService;
        } else {
            return Application_Singleton.noCacheService;
        }
    }

    private WishbookClient getLongCacheService() {
        if (Application_Singleton.longCacheService == null) {
            Application_Singleton.longCacheService = new RetroFitServiceGenerator(context).createLongCacheService(WishbookClient.class);
            return Application_Singleton.longCacheService;
        } else {
            return Application_Singleton.longCacheService;
        }
    }

    private WishbookClient getDefaultService() {
        if (Application_Singleton.nomalService == null) {
            Application_Singleton.nomalService = new RetroFitServiceGenerator(context).createService(WishbookClient.class);
            return Application_Singleton.nomalService;
        } else {
            return Application_Singleton.nomalService;
        }
    }

    private void showErrorConnectionPopupDialog(String erroMessage) {
        try {
            new MaterialDialog.Builder(context).title("Connection Error")
                    .content(erroMessage)
                    .positiveText("OK")
                    .build()
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
