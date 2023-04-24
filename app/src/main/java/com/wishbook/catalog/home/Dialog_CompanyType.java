package com.wishbook.catalog.home;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.CompanyTypePatch;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 1/10/16.
 */
public class Dialog_CompanyType extends DialogFragment {

    private SharedPreferences pref;
    @BindView(R.id.manufacturer)
    SwitchCompat manufacturer;

    @BindView(R.id.wholesaler_distributor)
    SwitchCompat wholesaler_distributor;

    @BindView(R.id.retailer)
    SwitchCompat retailer;

    @BindView(R.id.onlineretailer_reseller)
    SwitchCompat onlineretailer_reseller;

    @BindView(R.id.broker)
    SwitchCompat broker;

    @BindView(R.id.confirm)
    AppCompatButton save;

    @BindView(R.id.root_view)
    RelativeLayout relativeLayout;

    @BindView(R.id.company_profile_name)
    EditText company_profile_name;

    @BindView(R.id.company_profile_spinner_city)
    Spinner company_profile_spinner_city;

    @BindView(R.id.company_profile_spinner_state)
    Spinner company_profile_spinner_state;

    private Response_States[] responseStates;
    private Response_Cities[] responseCities;

    private String stateID = "";
    private String cityID = "";

    private Boolean manufacturer_value = false;
    private Boolean wholesaler_distributor_value = false;
    private Boolean retailer_value = false;
    private Boolean onlineretailer_reseller_value = false;
    private Boolean broker_value = false;


    private String CompanyGroupID;

    public Dialog_CompanyType() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialogTheme);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dialog.setContentView(R.layout.company_type);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        ButterKnife.bind(this, dialog);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((Response_States) company_profile_spinner_state.getSelectedItem())!=null){
                }else{
                    Toast.makeText(getContext(), "Please select state", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(((Response_Cities) company_profile_spinner_city.getSelectedItem())!=null ){
                }else{
                    Toast.makeText(getContext(), "Please select city", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (company_profile_name.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Company name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (((Response_States) company_profile_spinner_state.getSelectedItem()).getState_name().equals("-")) {
                    Toast.makeText(getContext(), "Please select state", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (((Response_Cities) company_profile_spinner_city.getSelectedItem()).getCity_name().equals("-")) {
                    Toast.makeText(getContext(), "Please select city", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!manufacturer.isChecked() && !onlineretailer_reseller.isChecked() && !retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked()) {
                    Snackbar.make(relativeLayout, "Select atleast one company type", Snackbar.LENGTH_LONG).show();
                } /*else if (manufacturer.isChecked() && !onlineretailer_reseller.isChecked() && !retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked()) {
                    new MaterialDialog.Builder(getActivity())
                            .title("You have chosen Manufacturer Only, You cannot:")
                            .content(
                                    "1. Receive Catalogs/Selections\n" +
                                            "2. Have suppliers")
                            .negativeText("Change")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    PatchCompanyType();
                                }
                            })
                            .show();
                } */else {
                    PatchCompanyType();
                }
            }
        });

        //fetching data
        getCompanyData();


        company_profile_spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (responseStates != null) {
                    String stateId = responseStates[position].getId();

                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                responseCities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                                if (responseCities != null) {
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, responseCities);
                                    company_profile_spinner_city.setAdapter(spinAdapter_city);

                                    //setting local state
                                    for (int i = 0; i < responseCities.length; i++) {
                                        if (responseCities[i].getId().equals(cityID)) {
                                            company_profile_spinner_city.setSelection(i);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return dialog;
    }

    public void PatchCompanyType() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();

        if (manufacturer.isChecked()) {
            manufacturer_value = true;
        } else {
            manufacturer_value = false;
        }
        if (onlineretailer_reseller.isChecked()) {
            onlineretailer_reseller_value = true;
        } else {
            onlineretailer_reseller_value = false;
        }
        if (retailer.isChecked()) {
            retailer_value = true;
        } else {
            retailer_value = false;
        }
        if (wholesaler_distributor.isChecked()) {
            wholesaler_distributor_value = true;
        } else {
            wholesaler_distributor_value = false;
        }

        if (broker.isChecked()) {
            broker_value = true;
        } else {
            broker_value = false;
        }


        if(manufacturer.isChecked() && !onlineretailer_reseller.isChecked() && !retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked()){
            UserInfo.getInstance(getActivity()).setCompany_type("manufacturer");
            UserInfo.getInstance(getActivity()).setCompanyType("seller");
        }
        else if(!manufacturer.isChecked() && !onlineretailer_reseller.isChecked() && retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked() ||
                !manufacturer.isChecked() && onlineretailer_reseller.isChecked() && !retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked() ||
                !manufacturer.isChecked() && onlineretailer_reseller.isChecked() && retailer.isChecked() && !wholesaler_distributor.isChecked() && !broker.isChecked()){
            UserInfo.getInstance(getActivity()).setCompanyType("buyer");
        }
        else {
            UserInfo.getInstance(getActivity()).setCompany_type("nonmanufacturer");
            UserInfo.getInstance(getActivity()).setCompanyType("all");
        }

        CompanyTypePatch patchcompanytype = new CompanyTypePatch(manufacturer_value, wholesaler_distributor_value, retailer_value, onlineretailer_reseller_value, broker_value);
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getContext(), "companytype", CompanyGroupID), gson.fromJson(gson.toJson(patchcompanytype), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    saveProfile(((Response_States) company_profile_spinner_state.getSelectedItem()).getId(), ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    private void saveProfile(final String state, final String city) {
        JsonObject jsonObject;
        CompanyProfile profile = new CompanyProfile();
        profile.setName(company_profile_name.getText().toString());
        profile.setState(state);
        profile.setCity(city);

        jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.logger(response);
                    pref.edit().putString("companyname", company_profile_name.getText().toString()).apply();
                    pref.edit().putInt("company_profile_city", Integer.parseInt(city)).apply();
                    pref.edit().putInt("company_profile_state", Integer.parseInt(state)).apply();
                    Activity_Home.pref.edit().putString("is_profile_set", "true").apply();
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                Toast.makeText(getContext(), "Request Failed!", Toast.LENGTH_LONG).show();
                //input_mobile.setError("mobile number does not exists");
            }
        });
    }

    private void getCompanyData() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //parsing response data
                try {
                    CompanyProfile companyProfile = new Gson().fromJson(response, CompanyProfile.class);
                    if (companyProfile != null) {
                        company_profile_name.setText(companyProfile.getName());
                        stateID = companyProfile.getState();
                        cityID = companyProfile.getCity();

                        //fetching states
                        getStates();
                        if (companyProfile.getCompany_group_flag() != null)
                            CompanyGroupID = companyProfile.getCompany_group_flag().getId();

                        if (companyProfile.getCompany_type_filled()) {
                            manufacturer.setChecked(companyProfile.getCompany_group_flag().getManufacturer());
                            wholesaler_distributor.setChecked(companyProfile.getCompany_group_flag().getWholesaler_distributor());
                            broker.setChecked(companyProfile.getCompany_group_flag().getBroker());
                            retailer.setChecked(companyProfile.getCompany_group_flag().getRetailer());
                            onlineretailer_reseller.setChecked(companyProfile.getCompany_group_flag().getOnline_retailer_reseller());
                        }
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
    }

    private void getStates() {
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    responseStates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    if (responseStates != null) {
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem, R.id.spintext, responseStates);
                        company_profile_spinner_state.setAdapter(spinAdapter_state);

                        //setting local state
                        for (int i = 0; i < responseStates.length; i++) {
                            if (responseStates[i].getId().equals(stateID)) {
                                company_profile_spinner_state.setSelection(i);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


}


