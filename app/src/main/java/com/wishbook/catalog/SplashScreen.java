package com.wishbook.catalog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clevertap.android.sdk.CleverTapAPI;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatConfig;
import com.freshchat.consumer.sdk.exception.MethodNotAllowedException;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.gcm.GCMPreferences;
import com.wishbook.catalog.Utils.gcm.RegistrationIntentService;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.ResponseShortUrl;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.Activity_Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    SharedPreferences pref;
    Application_Singleton singleton;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //CountingIdlingResource espressoTestIdlingResource = new CountingIdlingResource("Network_Call");

    public static HashMap<String, String> getQueryString(Uri uri) {
        HashMap<String, String> map = new HashMap<>();
        for (String paramName : uri.getQueryParameterNames()) {
            if (paramName != null) {
                String paramValue = uri.getQueryParameter(paramName);
                if (paramValue != null) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (UserInfo.getInstance(getApplicationContext()).isBranchReferral()) {
                Log.i(StaticFunctions.BRANCHIOTAG, "This is branch referral session");
            }

            final Branch branch = Branch.getInstance();
            // Branch init
            branch.initSession(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error == null) {
                        Log.i(StaticFunctions.BRANCHIOTAG, "Init Success===>" + referringParams.toString());
                        setUserFromReferral(referringParams);
                        if (pref.getBoolean("isLoggedIn", false)) {
                            try {
                                final Uri intent_uri = Uri.parse(referringParams.getString("deep_link"));
                                if(isFinishing()) {
                                    HashMap<String, String> map = SplashScreen.getQueryString(intent_uri);
                                    new DeepLinkFunction(map, SplashScreen.this);
                                } else {

                                    if (referringParams.get("deep_link") != null) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                HashMap<String, String> map = SplashScreen.getQueryString(intent_uri);
                                                new DeepLinkFunction(map, SplashScreen.this);
                                            }
                                        },3500);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.i(StaticFunctions.BRANCHIOTAG, "Init Failed==>" + error.getMessage());
                    }
                }
            }, this.getIntent().getData(), this);

            // latest
            JSONObject sessionParams = Branch.getInstance().getLatestReferringParams();

            // first
            JSONObject installParams = Branch.getInstance().getFirstReferringParams();
            try {
                if (installParams != null) {
                    Log.e(StaticFunctions.BRANCHIOTAG, "onStart: Intsall Params==>" + installParams.toString());
                    if (installParams.has("deep_link")) {
                        setUserFromReferral(installParams);
                    }
                }

                if (sessionParams != null) {
                    Log.e(StaticFunctions.BRANCHIOTAG, "onStart: Session Param===>" + sessionParams.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);

        // CleverTapApi Handle
        try {
            CleverTapAPI.getDefaultInstance(this).event.pushNotificationEvent(intent.getExtras());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_splash_version2);
        singleton = new Application_Singleton();
        singleton.trackScreenView(getLocalClassName(), SplashScreen.this);
        StaticFunctions.initializeAppsee();
        pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        Application_Singleton.Token = "";
        pref.edit().putString("FromSplashScreen", "yes").apply();

        if(BuildConfig.DEBUG) {
            generateKeyHash();
        }

        FreshchatConfig freshchatConfig = new FreshchatConfig(BuildConfig.FRESHCHAT_ID, BuildConfig.FRESHCHAT_KEY);
        freshchatConfig.setCameraCaptureEnabled(true);
        freshchatConfig.setGallerySelectionEnabled(true);
        try {
            Freshchat.getInstance(getApplicationContext()).init(freshchatConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (pref.getBoolean("isLoggedIn", false)) {

            if (getIntent().getData() != null) {
                Uri intentUri = getIntent().getData();
                Log.v("intentURI", "" + intentUri.getPath());
                Log.v("intentURI", "" + intentUri.getQueryParameter("type"));
                Log.v("intentURI", "" + intentUri.getQueryParameter("id"));
                String[] segments = intentUri.getPath().split("/");
                boolean isShortUrl = false;

                if (intentUri.getQueryParameter("utm_source") != null
                        || intentUri.getQueryParameter("utm_medium") != null
                        || intentUri.getQueryParameter("utm_campaign") != null
                        || intentUri.getQueryParameter("utm_content") != null
                        || intentUri.getQueryParameter("gclid") != null) {
                    Application_Singleton.trackCampaign(String.valueOf(intentUri),this);
                } else {

                    String commonCampaign = "https://app.wishbook.io/?utm_source=androidapp&utm_medium=otheropen";
                    Application_Singleton.trackCampaign(String.valueOf(commonCampaign),this);

                }


                for (int i = 0; i < segments.length; i++) {
                    if (segments[i].equals("s")) {
                        isShortUrl = true;
                    }
                }

                if (isShortUrl) {
                    Log.v("intentURI 1", intentUri.getLastPathSegment());
                    getFullURL(intentUri.getLastPathSegment());
                    return;
                }
                callDeepLinkIntent(intentUri);
            } else {
                String commonCampaign = "https://app.wishbook.io/?utm_source=androidapp&utm_medium=appopen";
                Application_Singleton.trackCampaign(String.valueOf(commonCampaign),this);
                callHomeIntent();
            }

        } else {

            if (getIntent().getData() != null) {
                Uri intentUri = getIntent().getData();
                Log.v("intentURI", "" + intentUri.getPath());
                Log.v("intentURI", "" + intentUri.getQueryParameter("type"));
                Log.v("intentURI", "" + intentUri.getQueryParameter("id"));

                if (intentUri.getQueryParameter("utm_source") != null
                        || intentUri.getQueryParameter("utm_medium") != null
                        || intentUri.getQueryParameter("utm_campaign") != null
                        || intentUri.getQueryParameter("utm_content") != null
                        || intentUri.getQueryParameter("gclid") != null) {
                    Application_Singleton.trackCampaign(String.valueOf(intentUri),this);
                } else {
                    String commonCampaign = "https://app.wishbook.io/?utm_source=androidapp&utm_medium=otheropen";
                    Application_Singleton.trackCampaign(String.valueOf(commonCampaign),this);
                }
            } else {
                String commonCampaign = "https://app.wishbook.io/?utm_source=androidapp&utm_medium=appopen";
                Application_Singleton.trackCampaign(String.valueOf(commonCampaign),this);
            }


            initReciever();
            registerReceiver();
            if (StaticFunctions.checkPlayServices(SplashScreen.this)) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    /**
     * Click from any deeplink intent URI than navigate that screen
     * @param intentUri
     */
    public void callDeepLinkIntent(Uri intentUri) {
        String type = null;
        if (intentUri.getQueryParameter("type") != null) {
            type = intentUri.getQueryParameter("type");
        } else if (intentUri.getQueryParameter("t") != null) {
            type = intentUri.getQueryParameter("t");
        }
        String id = intentUri.getQueryParameter("id");
        if (type != null) {
            Intent intent = new Intent(SplashScreen.this, Activity_Home.class);
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            intent.putExtra("otherPara", getQueryString(intentUri));
            sendEvent(getString(R.string.sms_event_category),getString(R.string.sms_event_click),type.toLowerCase());

            startActivity(intent);
            finish();


        } else {
            Intent intent = new Intent(SplashScreen.this, Activity_Home.class);
            String catalog = intentUri.getPath().replaceAll("\\D+", "");
            String supplier = intentUri.getQueryParameter("supplier");
            if (supplier != null && supplier.isEmpty()) {
                intent.putExtra("catalog", catalog);
                intent.putExtra("supplier", supplier);
                sendEvent(getString(R.string.qr_event_category),getString(R.string.qr_event_scan),"catalog_" + catalog.toLowerCase());
                startActivity(intent);
                finish();
            } else {
                startActivity(intent);
                finish();
            }

        }
    }

    public void callHomeIntent() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String restoreId = Freshchat.getInstance(getApplicationContext()).getUser().getRestoreId();
                try {
                    Freshchat.getInstance(getApplicationContext()).identifyUser(UserInfo.getInstance(SplashScreen.this).getUserId(), restoreId);
                } catch (MethodNotAllowedException e) {
                    e.printStackTrace();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(Freshchat.FRESHCHAT_USER_RESTORE_ID_GENERATED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);

        Log.d("Splash", "Splash Finish Home Loaded");


        StaticFunctions.switchActivityDelayed(SplashScreen.this, Activity_Home.class);
    }

    /**
     * Broadcast Receiver initialize for check GCM/FCM is register
     */
    private void initReciever() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                String gcmtoken = sharedPreferences.getString("gcmtoken", "");
                // if (sentToken) {
                if (getIntent().getData() != null) {
                    Uri intentUri = getIntent().getData();
                    String type = intentUri.getQueryParameter("type");
                    String id = intentUri.getQueryParameter("id");
                    sharedPreferences.edit().putString("link_type", type).commit();
                    sharedPreferences.edit().putString("link_id", id).commit();
                }
                Log.d("Splash", "Splash Finish Login Loaded");
                StaticFunctions.switchActivityDelayed(SplashScreen.this, Activity_Login.class);
                //    Log.i(TAG, "gcm success."+ gcmtoken);
                // } else {
                //     Log.i(TAG, "Failed");
                //     Toast.makeText(SplashScreen.this,"Oops ! You have No Internet Connection",Toast.LENGTH_LONG).show();
                // }
            }
        };
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Call API for get Full URL from short_url specify in SMS
     * @param short_url
     */
    public void getFullURL(final String short_url) {
        final SharedPreferences pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(SplashScreen.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("short_url", short_url);
        HttpManager.getInstance(SplashScreen.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.SHORT_URL, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseShortUrl shortUrl = Application_Singleton.gson.fromJson(response, ResponseShortUrl.class);
                    if (shortUrl.getUrl().equals(URLConstants.APP_URL)) {
                        // Redirect Home Page if return root url
                        callHomeIntent();
                    } else {
                        // Call DeepLink Intent
                        Application_Singleton singleton = new Application_Singleton();
                        Uri intentUri = Uri.parse(shortUrl.getUrl());
                        callDeepLinkIntent(intentUri);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

                callHomeIntent();
              /*  new MaterialDialog.Builder(SplashScreen.this)
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();*/
            }
        });
    }

    /**
     * Set Branch.io referral link in preference
     * @param referringParams
     */
    public void setUserFromReferral(JSONObject referringParams) {
        boolean clickedBranchLink = false;
        try {
            clickedBranchLink = referringParams.getBoolean("+clicked_branch_link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (clickedBranchLink) {
            try {
                UserInfo.getInstance(getApplicationContext()).setBranchReferral(true);

                String link = referringParams.getString("~referring_link");

                Log.i(StaticFunctions.BRANCHIOTAG, "This is new branch referral session with clicked link: " + clickedBranchLink);
                Log.i(StaticFunctions.BRANCHIOTAG, "This is new branch referral session with referred link: " + link);

                if (link != null && !link.isEmpty()) {
                    UserInfo.getInstance(getApplicationContext()).setBranchUserReferralLink(link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.wishbook.catalog",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * Send GA Analytics event
     * @param categoryId - set as event names
     * @param actionId - set for what type of event done
     * @param labelId - set event name
     */
    public void sendEvent(String categoryId,String actionId,String labelId) {
        HashMap<String,String> prop = new HashMap<>();
        prop.put("action",actionId);
        prop.put("label",labelId);
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_names(categoryId);
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(this,wishbookEvent);
    }
}





