package com.wishbook.catalog.home.more;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.Userprofile;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseLanguages;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.AppIntroActivity;
import com.wishbook.catalog.home.catalog.add.CategorySelectAdapter;
import com.wishbook.catalog.login.models.Response_User;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityLanguage extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_save)
    AppCompatButton btnSave;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Context context;
    CategorySelectAdapter languageAdapter;

    ResponseLanguages[] languages;
    SharedPreferences pref;

    // backEnable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ActivityLanguage.this;
        pref = StaticFunctions.getAppSharedPreferences(ActivityLanguage.this);
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);
        setToolbar();
        initView();
    }

    private void setToolbar() {
        toolbar.setTitle(getResources().getString(R.string.language_title));
        if (getIntent().getBooleanExtra("backDisable", false)) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        } else {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }

    }

    private void initView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerview.setLayoutManager(mLayoutManager);
        getLanguages();
        //getLanguage();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageAdapter != null) {
                    int id = 0;
                    if (languageAdapter.getSelectedItem() != null) {
                        if (languageAdapter.getSelectedItem().size() > 0) {
                            UserInfo.getInstance(context).setLanguage(languageAdapter.getSelectedItem().get(0));
                            LocaleHelper.setLocale(context, languageAdapter.getSelectedItem().get(0));
                            UserInfo.getInstance(context).setLanguageSet(true);

                            if (languages != null) {
                                // save Data to server
                                for (int i = 0; i < languages.length; i++) {
                                    if (languages[i].getCode().equals(languageAdapter.getSelectedItem().get(0))) {
                                        patchUserDetails(languages[i].getId());
                                        break;
                                    }

                                }
                            }
                        }
                    }

                }
                if (getIntent().getBooleanExtra("backDisable", false)) {

                    if (pref.getString("groupstatus", "0").equals("1")) {
                        // Normal Wishbook User

                        if (!PrefDatabaseUtils.getPrefAppintro(ActivityLanguage.this)) {
                            startActivity(new Intent(context, AppIntroActivity.class));
                            overridePendingTransition(R.anim.animation_enter,
                                    R.anim.animation_leave);
                            finish();
                        } else {
                            StaticFunctions.switchActivity(ActivityLanguage.this, Activity_Home.class);
                            finish();
                        }
                    } else {
                        // Sales Person
                        StaticFunctions.switchActivity(ActivityLanguage.this, Activity_Home.class);
                        finish();
                    }

                } else {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra("backDisable", false)) {
            if (pref.getString("groupstatus", "0").equals("1")) {
                if (!PrefDatabaseUtils.getPrefAppintro(ActivityLanguage.this)) {
                    startActivity(new Intent(context, AppIntroActivity.class));
                    finish();
                } else {
                    StaticFunctions.switchActivity(ActivityLanguage.this, Activity_Home.class);
                    finish();
                }
            } else {
                // Sales man User
                StaticFunctions.switchActivity(ActivityLanguage.this, Activity_Home.class);
                finish();
            }

        } else {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public void getLanguages() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityLanguage.this);
        StaticFunctions.showProgressbar(ActivityLanguage.this);
        HttpManager.getInstance(ActivityLanguage.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.LANGUAGE, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityLanguage.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityLanguage.this);
                languages = new Gson().fromJson(response, ResponseLanguages[].class);
                ArrayList<String> selectedLanguages = new ArrayList<>();
                if (languages.length > 0) {
                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<>();
                    for (int i = 0; i < languages.length; i++) {
                        enumGroupResponses.add(new EnumGroupResponse(languages[i].getCode(), languages[i].getName()));
                    }
                    selectedLanguages.add(UserInfo.getInstance(context).getLanguage());
                    languageAdapter = new CategorySelectAdapter(context, enumGroupResponses, selectedLanguages);
                    recyclerview.setAdapter(languageAdapter);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityLanguage.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void patchUserDetails(String langugaeId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityLanguage.this);
        Userprofile userprofile = new Userprofile();
        userprofile.setLanguage(langugaeId);
        Response_User response_user = new Response_User();
        response_user.setUserprofile(userprofile);
        HttpManager.getInstance(ActivityLanguage.this).requestPatch(HttpManager.METHOD.PATCH, URLConstants.PROFILE, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(response_user), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
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
