package com.wishbook.catalog.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.CompanyTypeResponse;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.Userprofile;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.CompanyTypePatch;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseLanguages;
import com.wishbook.catalog.home.AppIntroActivity;
import com.wishbook.catalog.home.catalog.add.CategorySelectAdapter;
import com.wishbook.catalog.login.models.Response_User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_Register_Version2 extends AppCompatActivity {

    @BindView(R.id.recyclerviewLanguage)
    RecyclerView recyclerviewLanguage;
    @BindView(R.id.linear_container)
    LinearLayout linear_container;

    @BindView(R.id.manufacturer)
    CheckBox manufacturer;

    @BindView(R.id.onlineretailer_reseller)
    CheckBox onlineretailer_reseller;

    @BindView(R.id.retailer)
    CheckBox retailer;

    @BindView(R.id.wholesaler_distributor)
    CheckBox wholesaler_distributor;

    @BindView(R.id.broker)
    CheckBox broker;

    @BindView(R.id.btn_save)
    AppCompatButton btn_save;

    @BindView(R.id.img_close)
    ImageView img_close;


    // #### Global Class Variable Initialize Start ########
    private Context mContext;
    private String from = "";
    CategorySelectAdapter languageAdapter;
    ResponseLanguages[] languages;
    SharedPreferences pref;
    String companyGroupId = null;
    JSONObject object;

    MaterialDialog progressDialog;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_pre_register);
        mContext = Activity_Register_Version2.this;
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Registration_screen");
        HashMap<String, String> prop = new HashMap<>();
        if (getIntent().getStringExtra("from") != null) {
            from = "Home";
        }
        prop.put("source", from);
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);
        object = new JSONObject();
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(Activity_Register.class.getSimpleName(), mContext);
        ButterKnife.bind(this);
        initLayout();
        initListner();
    }


    public void initLayout() {
        if (getIntent().getStringExtra("company_group_id") != null) {
            companyGroupId = getIntent().getStringExtra("company_group_id");
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        recyclerviewLanguage.setLayoutManager(mLayoutManager);
        getLanguages();

        if(getIntent().getExtras()!=null && getIntent().getStringExtra("temp_company_selection")!=null) {
            try {
                object = new JSONObject(getIntent().getStringExtra("temp_company_selection"));
                if(object.has("online_retailer_reseller")) {
                    if(object.getString("online_retailer_reseller").equals("true")) {
                        onlineretailer_reseller.setChecked(true);
                    }
                }
                if(object.has("retailer")) {
                    if(object.getString("retailer").equals("true")) {
                        retailer.setChecked(true);
                    }
                }

                if(object.has("wholesaler_distributor")) {
                    if(object.getString("wholesaler_distributor").equals("true")){
                        wholesaler_distributor.setChecked(true);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void initListner() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(Activity_Register_Version2.this)
                        .title("Logout")
                        .content("Logout from app?")
                        .positiveText("YES")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                LogoutCommonUtils.logout((Activity) mContext, false);
                            }
                        })
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .neutralText("Exit app")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finishAffinity();
                                Intent intent = new Intent();
                                setResult(1801,intent);
                                finish();


                            }
                        })
                        .show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCompanyTypeObject();
                if (isValidData()) {
                    postRegister();
                }
            }
        });
    }


    public void getLanguages() {
        Log.e("TAG", "getLanguages: Called" );
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        StaticFunctions.showProgressbar((Activity) mContext);
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.LANGUAGE, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar((Activity) mContext);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar((Activity) mContext);
                languages = new Gson().fromJson(response, ResponseLanguages[].class);
                ArrayList<String> selectedLanguages = new ArrayList<>();
                if (languages.length > 0) {
                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<>();
                    for (int i = 0; i < languages.length; i++) {
                        enumGroupResponses.add(new EnumGroupResponse(languages[i].getCode(), languages[i].getName()));
                    }
                    selectedLanguages.add(UserInfo.getInstance(mContext).getLanguage());
                    Log.e("TAG", "getLanguages: Adapter Bind Called" );
                    languageAdapter = new CategorySelectAdapter(mContext, enumGroupResponses, selectedLanguages);
                    recyclerviewLanguage.setAdapter(languageAdapter);
                    languageAdapter.notifyDataSetChanged();
                    languageAdapter.setOnCheckedListener(new CategorySelectAdapter.OnCheckedListener() {
                        @Override
                        public void onChecked(String categoryId, boolean eventFire) {
                            if(eventFire && LocaleHelper.getLanguage(mContext)!=null && !LocaleHelper.getLanguage(mContext).equals(languageAdapter.getSelectedItem().get(0))){
                                LocaleHelper.setLocale(mContext, languageAdapter.getSelectedItem().get(0));
                                UserInfo.getInstance(mContext).setLanguage(languageAdapter.getSelectedItem().get(0));
                                recreate();
                                /*Intent refresh = new Intent(mContext, Activity_Register_Version2.class);
                                saveCompanyTypeObject();
                                refresh.putExtra("temp_company_selection",object.toString());
                                refresh.putExtra("company_group_id",companyGroupId);
                                startActivity(refresh);
                                finish();
                                overridePendingTransition(0,0);*/
                            }
                        }
                    });

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar((Activity) mContext);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(mContext, "Please press start using wishbook", Toast.LENGTH_SHORT).show();
    }

    public void postRegister() {
        if (languageAdapter != null) {
            int id = 0;
            if (languageAdapter.getSelectedItem() != null) {
                if (languageAdapter.getSelectedItem().size() > 0) {
                    UserInfo.getInstance(mContext).setLanguage(languageAdapter.getSelectedItem().get(0));
                    LocaleHelper.setLocale(mContext, languageAdapter.getSelectedItem().get(0));
                    UserInfo.getInstance(mContext).setLanguageSet(true);
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
    }

    private void patchUserDetails(String langugaeId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        Userprofile userprofile = new Userprofile();
        userprofile.setLanguage(langugaeId);
        Response_User response_user = new Response_User();
        response_user.setUserprofile(userprofile);
        progressDialog = StaticFunctions.showProgress(mContext);
        progressDialog.show();

        HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.PROFILE, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(response_user), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                patchCompany(object);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(progressDialog!=null) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void patchCompany(final JSONObject jsonObject) {
        try {
            if (companyGroupId != null) {
                CompanyTypePatch patchcompanytype = new CompanyTypePatch(jsonObject.getBoolean("wholesaler_distributor"), jsonObject.getBoolean("retailer"), jsonObject.getBoolean("online_retailer_reseller"));
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
                HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "companytype", companyGroupId), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchcompanytype), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            CompanyTypeResponse companyProfile = new Gson().fromJson(response, CompanyTypeResponse.class);
                            if(companyProfile!=null) {
                                String allCompanyType = null;
                                if (companyProfile.isManufacturer()) {
                                    allCompanyType = "Manufacturer";
                                }

                                if (companyProfile.isWholesaler_distributor()) {
                                    if (allCompanyType != null) {
                                        allCompanyType += ", Wholesaler Distributor";
                                    } else {
                                        allCompanyType = "Wholesaler Distributor";
                                    }
                                }

                                if (companyProfile.isOnline_retailer_reseller()) {
                                    if (allCompanyType != null) {
                                        allCompanyType += ", Online-Retailer Reseller";
                                    } else {
                                        allCompanyType = "Online-Retailer Reseller";
                                    }
                                }

                                if (companyProfile.getBroker()) {
                                    if (allCompanyType != null) {
                                        allCompanyType += ", Broker";
                                    } else {
                                        allCompanyType = "Broker";
                                    }

                                }
                                if (companyProfile.isRetailer()) {
                                    if (allCompanyType != null) {
                                        allCompanyType += ", Retailer";
                                    } else {
                                        allCompanyType = "Retailer";
                                    }

                                }
                                UserInfo.getInstance(mContext).setAllCompanyType(allCompanyType);
                            }


                            /*if (jsonObject.getBoolean("manufacturer") && !jsonObject.getBoolean("online_retailer_reseller") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker")) {
                                UserInfo.getInstance(mContext).setCompany_type("manufacturer");
                                UserInfo.getInstance(mContext).setCompanyType("seller");
                            } else if (!jsonObject.getBoolean("manufacturer") && !jsonObject.getBoolean("online_retailer_reseller") && jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker") ||
                                    !jsonObject.getBoolean("manufacturer") && jsonObject.getBoolean("online_retailer_reseller") && !jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker") ||
                                    !jsonObject.getBoolean("manufacturer") && jsonObject.getBoolean("online_retailer_reseller") && jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker")) {
                                UserInfo.getInstance(mContext).setCompanyType("buyer");
                            } else {
                                UserInfo.getInstance(mContext).setCompany_type("nonmanufacturer");
                                UserInfo.getInstance(mContext).setCompanyType("all");
                            }*/
                        } catch (Exception e) {

                        }
                        UserInfo.getInstance(mContext).setGuest(false);
                        sendRegistrationData();

                        startActivity(new Intent(mContext,AppIntroActivity.class));
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isValidData() {

        if (languageAdapter == null) {
            Toast.makeText(mContext, "Please select Language", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(onlineretailer_reseller.isChecked()
                || retailer.isChecked() || wholesaler_distributor.isChecked())) {
            Toast.makeText(mContext, "Please select user type", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void saveCompanyTypeObject() {
        try {
           /* if (manufacturer.isChecked()) {
                object.put("manufacturer", true);
            } else {
                object.put("manufacturer", false);
            }*/
            if (onlineretailer_reseller.isChecked()) {
                object.put("online_retailer_reseller", true);
            } else {
                object.put("online_retailer_reseller", false);
            }
            if (retailer.isChecked()) {
                object.put("retailer", true);
            } else {
                object.put("retailer", false);
            }
            if (wholesaler_distributor.isChecked()) {
                object.put("wholesaler_distributor", true);
            } else {
                object.put("wholesaler_distributor", false);
            }

          /*  if (broker.isChecked()) {
                object.put("broker", true);
            } else {
                object.put("broker", false);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }


    public void sendRegistrationData() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Registration");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("name",   UserInfo.getInstance(getApplicationContext()).getMobile());
        prop.put("company_name", "");
        prop.put("information_source","Email");
        prop.put("status", "done");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);

        sendUserAttributes(mContext);

    }

    public void sendUserAttributes(Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.USER_PROPERTIES);
        wishbookEvent.setEvent_names(WishbookEvent.USER_PROPERTIES);
        new WishbookTracker(mContext, wishbookEvent);
    }
}
