package com.wishbook.catalog.Utils.networking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.commonmodels.responses.Request_CreateOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vigneshkarnika on 03/04/16.
 */
public class NetworkManager {
    public static final String PUT = "PUT";
    public static int GET = 1;
    public static int POST = 2;
    public static int POSTJSON = 3;
    public static int RESPONSEFAIL = 7;
    public static int RESPONSESUCCESS = 8;
    private static NetworkManager instance;
    public static synchronized NetworkManager getInstance() {
        if (instance == null)
            instance=new NetworkManager();
        return instance;
    }
    public interface customCallBack {
        void onCompleted(int status, String response);
    }

    public void HttpRequest(final Activity context, int method, String url, HashMap<String, String> params, final customCallBack customcallBack) {
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();
        switch (method) {
            case 1:
                StringBuilder sb = new StringBuilder();
                if (params != null) {
                    sb.append("?");
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        sb.append(pair.getKey() + "=" + pair.getValue() + "&");
                        it.remove();
                    }
                    if(sb.length()>0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                String buildUrl = url + sb.toString();
                NetworkProcessor.with(context).load("GET", buildUrl).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;

            case 2:
                Map<String, List<String>> paramstopost=new HashMap<>();
                if (params != null) {
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        ArrayList<String> stringMap=new ArrayList<>();
                        stringMap.add(pair.getValue().toString());
                        paramstopost.put(pair.getKey().toString(),stringMap);
                        it.remove();
                    }
                }
                NetworkProcessor.with(context).load("POST", url).setMultipartParameters(paramstopost).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case 3:
                JsonObject jsonObject=new JsonObject();
                if (params != null) {
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        jsonObject.addProperty(""+pair.getKey(),""+pair.getValue());
                        it.remove();
                    }
                }
                NetworkProcessor.with(context).load("POST", url).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }
    public void HttpRequestwithHeader(final Activity context, int method, String url, HashMap<String, String> params, final customCallBack customcallBack) {
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();
        switch (method) {
            case 1:
                StringBuilder sb = new StringBuilder();
                if (params != null) {
                    sb.append("?");
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        sb.append(pair.getKey() + "=" + pair.getValue() + "&");
                        it.remove();
                    }
                    if(sb.length()>0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                String buildUrl = url + sb.toString();
                NetworkProcessor.with(context).load("GET", buildUrl).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;

            case 2:
                Map<String, List<String>> paramstopost=new HashMap<>();
                if (params != null) {
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        ArrayList<String> stringMap=new ArrayList<>();
                        stringMap.add(pair.getValue().toString());
                        paramstopost.put(pair.getKey().toString(),stringMap);
                        it.remove();
                    }
                }
                NetworkProcessor.with(context).load("POST", url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setMultipartParameters(paramstopost).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case 3:
                JsonObject jsonObject=new JsonObject();
                if (params != null) {
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        jsonObject.addProperty(""+pair.getKey(),""+pair.getValue());
                        it.remove();
                    }
                }
                NetworkProcessor.with(context).load("POST", url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }



    public void HttpRequestwithHeader(final Activity context, String method, String url, HashMap<String, String> params, final customCallBack customcallBack) {
        if (!StaticFunctions.isOnline(context)) {
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                jsonObject.addProperty("" + pair.getKey(), "" + pair.getValue());
                it.remove();
            }
        }
        if(method.equals("PATCH")) {
            NetworkProcessor.with(context).load("POST", url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).addHeader("X-HTTP-Method-Override", "PATCH").setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    if (e == null) {
                        if (result != null) {
                            if (result.getHeaders().code() >= 200 && result.getHeaders().code() < 300) {
                                if (customcallBack != null) {
                                    customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                    } else {
                        Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                    }
                    progressDialog.dismiss();
                }
            });
        }
        else{

            NetworkProcessor.with(context).load(method, url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setJsonObjectBody(jsonObject).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    if (e == null) {
                        if (result != null) {
                            if (result.getHeaders().code() >= 200 && result.getHeaders().code() < 300) {
                                if (customcallBack != null) {
                                    customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                    } else {
                        Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                    }
                    progressDialog.dismiss();
                }
            });
        }
        }




    public void HttpRequestArraywithHeader(final Activity context, String url, HashMap<String, List<String>> params, final customCallBack customcallBack) {
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();

                NetworkProcessor.with(context).load("POST", url).setMultipartParameters(params).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e == null) {
                            if (result != null) {
                                if (result.getHeaders().code() >= 200 && result.getHeaders().code() < 300) {
                                    if (customcallBack != null) {
                                        customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                                    }
                                } else {
                                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                                }
                            } else {
                                Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                        progressDialog.dismiss();
                    }
                });


    }





    public void HttpRequest(final Activity context,String method,String url,HashMap params,final customCallBack customcallBack){
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();
        Map<String, List<String>> paramstopost=new HashMap<>();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ArrayList<String> stringMap=new ArrayList<>();
                stringMap.add(pair.getValue().toString());
                paramstopost.put(pair.getKey().toString(),stringMap);
                it.remove();
            }
        }
        NetworkProcessor.with(context).load(method, url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setMultipartParameters(paramstopost).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null) {
                    if (result != null) {
                        if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                            if (customcallBack != null) {
                                customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                    } else {
                        Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                    }
                } else {
                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                }
                progressDialog.dismiss();
            }
        });
    }

    public void HttpRequestwithFile(final Activity context,String url,HashMap params,String filename,String contentType,File file,final customCallBack customcallBack){
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();
        Map<String, List<String>> paramstopost=new HashMap<>();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ArrayList<String> stringMap=new ArrayList<>();
                stringMap.add(pair.getValue().toString());
                paramstopost.put(pair.getKey().toString(),stringMap);
                it.remove();
            }
        }
        NetworkProcessor.with(context).load("POST", url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setMultipartParameters(paramstopost).setMultipartFile(filename, contentType, file).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null) {
                    if (result != null) {
                        if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                            if (customcallBack != null) {
                                customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                    } else {
                        Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                    }
                } else {
                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                }
                progressDialog.dismiss();
            }
        });
    }
    public void HttpRequestwithJSON(final Activity context,String url,Request_CreateOrder jsonObject,final customCallBack customcallBack){
        if(!StaticFunctions.isOnline(context)){
            StaticFunctions.showNetworkAlert(context);
            return;
        }
        KeyboardUtils.hideKeyboard(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, Context.MODE_PRIVATE);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(context);
        progressDialog.show();
        TypeToken<Request_CreateOrder> token = new TypeToken<Request_CreateOrder>(){};

        NetworkProcessor.with(context).load("POST", url).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setJsonPojoBody(jsonObject,token).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null) {
                    if (result != null) {
                        if (result.getHeaders().code() >= 200&&result.getHeaders().code()<300) {
                            if (customcallBack != null) {
                                customcallBack.onCompleted(RESPONSESUCCESS, result.getResult());
                            }
                        } else {
                            Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                        }
                    } else {
                        Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                    }
                } else {
                    Toast.makeText(context,"Request Failed",Toast.LENGTH_SHORT).show(); customcallBack.onCompleted(RESPONSEFAIL, null);
                }
                progressDialog.dismiss();
            }
        });
    }
}
