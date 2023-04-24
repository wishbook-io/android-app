package com.wishbook.catalog.Utils;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.facebook.drawee.backends.pipeline.BuildConfig;
import com.freshchat.consumer.sdk.Freshchat;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.Activity_Login;
import com.wishbook.catalog.rest.RetroFitServiceGenerator;

import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LogoutCommonUtils {

    public static void logout(final Activity activity, final boolean unfortunately) {
        Freshchat.resetUser(getApplicationContext());

        //Only for debug mode
        if (!BuildConfig.DEBUG)
            Application_Singleton.tutorial_pref.edit().clear().apply();

        LocaleHelper.setLocale(activity, "en");


        if (unfortunately) {
            if (!Application_Singleton.unfortunateLogoutCalled) {
                Application_Singleton.unfortunateLogoutCalled = true;
                new RetroFitServiceGenerator(activity).deleteRetrofitCache();
                new UserClientService(activity).logout();
                Application_Singleton.isAskedFirstTimeAfterApply = false;
                Application_Singleton.isChangeCreditScrore = false;
                Application_Singleton.IS_COMPANY_RATING_GET = false;
                PrefDatabaseUtils.clearPrefDatabase(activity);
                String lang_id = UserInfo.getInstance(activity).getLanguage();
                if (Activity_Home.pref != null) {
                    Activity_Home.pref.edit().clear().commit();
                }
                UserInfo.getInstance(activity).setLanguage(lang_id);

                PreferenceManager.getDefaultSharedPreferences(activity).edit().clear().commit();
                Application_Singleton.Token = "";
                clearAllService();


                Intent intent = new Intent(activity, Activity_Login.class);
                if (unfortunately) {
                    intent.putExtra("LOGOUT", true);
                } else {
                    intent.putExtra("LOGOUT", false);
                }
                intent.putExtra("from", "Logout");
                activity.startActivity(intent);
                    /*new MaterialDialog.Builder(activity)
                            .title("Oops!!")
                            .content("Your login has failed.Please ReLogin")
                            .positiveText("Ok")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();*/
                activity.finish();
                Application_Singleton.unfortunateLogoutCalled = true;
            } else if (Application_Singleton.unfortunateLogoutCalledCount < 3) {
                Application_Singleton.unfortunateLogoutCalledCount++;
                new RetroFitServiceGenerator(activity).deleteRetrofitCache();
                new UserClientService(activity).logout();
                Application_Singleton.isAskedFirstTimeAfterApply = false;
                Application_Singleton.isChangeCreditScrore = false;
                Application_Singleton.IS_COMPANY_RATING_GET = false;
                PrefDatabaseUtils.clearPrefDatabase(activity);
                String lang_id = UserInfo.getInstance(activity).getLanguage();
                if (Activity_Home.pref != null) {
                    Activity_Home.pref.edit().clear().commit();
                }
                PreferenceManager.getDefaultSharedPreferences(activity).edit().clear().commit();
                UserInfo.getInstance(activity).setLanguage(lang_id);

                Application_Singleton.Token = "";
                clearAllService();

                Intent intent = new Intent(activity, Activity_Login.class);
                if (unfortunately) {
                    intent.putExtra("LOGOUT", true);
                } else {
                    intent.putExtra("LOGOUT", false);
                }
                intent.putExtra("from", "Logout");
                activity.startActivity(intent);
                activity.finish();
            }


        } else if (!Application_Singleton.logoutCalled) {

            Application_Singleton.logoutCalled = true;


            HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
            HttpManager.getInstance(activity).requestwithOnlyHeaders(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGOUT_URL, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Application_Singleton.Token = "";
                    /* Branch.getInstance().logout();*/
                    new RetroFitServiceGenerator(activity).deleteRetrofitCache();
                    new UserClientService(activity).logout();

                    WishbookEvent wishbookEvent = new WishbookEvent();
                    wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                    wishbookEvent.setEvent_names("Logout");
                    HashMap<String, String> prop = new HashMap<>();
                    prop.put("logout", "true");
                    wishbookEvent.setEvent_properties(prop);
                    new WishbookTracker(activity, wishbookEvent);

                    Application_Singleton.isAskedFirstTimeAfterApply = false;
                    Application_Singleton.isChangeCreditScrore = false;
                    Application_Singleton.IS_COMPANY_RATING_GET = false;
                    clearAllService();

                  /*  Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();*/
                    PrefDatabaseUtils.clearPrefDatabase(activity);
                    String lang_id = UserInfo.getInstance(activity).getLanguage();
                    if (Activity_Home.pref != null) {
                        Activity_Home.pref.edit().clear().apply();
                    }
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().clear().apply();
                    UserInfo.getInstance(activity).setLanguage(lang_id);

                    Freshchat.resetUser(activity);

                    Intent intent = new Intent(activity, Activity_Login.class);
                    if (unfortunately) {
                        intent.putExtra("LOGOUT", true);
                    } else {
                        intent.putExtra("LOGOUT", false);
                    }
                    intent.putExtra("from", "Logout");
                    activity.startActivity(intent);
                        /*new MaterialDialog.Builder(activity)
                                .title("Oops!!")
                                .content("Your login has failed.Please ReLogin")
                                .positiveText("Ok")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).show();*/
                    activity.finish();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    Application_Singleton.logoutCalled = false;
                    Toast.makeText(activity, "Unable To Logout", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public static void clearAllService() {
        Application_Singleton.nomalService = null;
        Application_Singleton.longCacheService = null;
        Application_Singleton.pullToRefreshService = null;
        Application_Singleton.noCacheService = null;
    }
}
