package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.OtpClass;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_MyCompanyProfile extends GATrackedFragment implements OTPListener, View.OnClickListener {

    public final String POPUP_OTP_TITLE = "Mobile Verification";
    public final String POPUP_OTP_TEXT = "Please enter the One Time Password (OTP) sent to your registered mobile number and authenticate the registration.";

    private View view;

    @BindView(R.id.company_profile_name)
    EditText company_profile_name;

    @BindView(R.id.company_profile_email)
    EditText company_profile_email;

    @BindView(R.id.company_profile_mobile)
    EditText company_profile_mobile;

    @BindView(R.id.company_profile_spinner_city)
    Spinner company_profile_spinner_city;

    @BindView(R.id.company_profile_spinner_state)
    Spinner company_profile_spinner_state;

    @BindView(R.id.btn_save_company_profile)
    AppCompatButton btn_save_company_profile;

    @BindView(R.id.company_profile_pushdownstream_group)
    RadioGroup company_profile_pushdownstream_group;

    @BindView(R.id.appbar)
    Toolbar toolbar;

    @BindView(R.id.countrycodes)
    TextView countrycodes;



    private Countries[] countries;

    private SharedPreferences pref;

    private int selectedCountryId = 1;


    private Response_States[] responseStates;
    private Response_Cities[] responseCities;

    private MaterialDialog dialog;

    public static final int MODE_PRIVATE = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_MyCompanyProfile.this, Constants.SENDER_NUM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_my_company_profile, ga_container, true);

        ButterKnife.bind(this, view);

        toolbar.setVisibility(View.GONE);

        //getting local preferences
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, MODE_PRIVATE);

        //fetching countries
        getCountries(getActivity());

        //fetching states & cities
        getStates();


        //setting data from local
        settingLocalData(pref);


        //OnClick listener for countryCode
        countrycodes.setOnClickListener(this);

        //OnClick listener for Final Save button
        btn_save_company_profile.setOnClickListener(this);


        //getting Company Data from Server
        getCompanyData();

        return view;
    }

    private void getCompanyData() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                //parsing response data
                CompanyProfile companyProfile = new Gson().fromJson(response, CompanyProfile.class);

                //setting data from server
                settingServerData(companyProfile);

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Snackbar.make(view, "Request failed!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void settingLocalData(SharedPreferences pref) {

        //getting local data from preference and setting data

        company_profile_email.setText(pref.getString("company_profile_email", ""));
        company_profile_name.setText(pref.getString("companyname", ""));
        String local_company_mobile = pref.getString("company_profile_mobile", "");
        if (local_company_mobile.length() >= 10) {
            String phoneNumbertrim = local_company_mobile.substring(local_company_mobile.length() - 10, local_company_mobile.length());
            company_profile_mobile.setText(phoneNumbertrim);
        }

        if (pref.getString("company_profile_pushdownstream", "0").equals("0")) {
            company_profile_pushdownstream_group.check(R.id.radio_no);
        } else {
            company_profile_pushdownstream_group.check(R.id.radio_yes);
        }


    }

    private void settingServerData(CompanyProfile companyProfile) {

        //getting local data from preference and setting data

        company_profile_email.setText(companyProfile.getEmail());
        company_profile_name.setText(companyProfile.getName());
        String local_company_mobile = companyProfile.getPhone_number();
        if (local_company_mobile.length() >= 10) {
            String phoneNumbertrim = local_company_mobile.substring(local_company_mobile.length() - 10, local_company_mobile.length());
            company_profile_mobile.setText(phoneNumbertrim);
            pref.edit().putString("company_profile_mobile", getValue(company_profile_mobile)).apply();
        }

        //setting group data
        if (companyProfile.getPush_downstream().toLowerCase().equals("yes")) {
            company_profile_pushdownstream_group.check(R.id.radio_yes);
        } else {
            company_profile_pushdownstream_group.check(R.id.radio_no);
        }

        //for setting city state for first time
        pref.edit().putInt("company_profile_city", Integer.parseInt(companyProfile.getCity())).apply();
        pref.edit().putInt("company_profile_state", Integer.parseInt(companyProfile.getState())).apply();

        //setting in spinner for state
        getStates();


    }


    public void getCountries(final Context context) {
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.COUNTRIES, null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                countries = Application_Singleton.gson.fromJson(response, Countries[].class);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.countrycodes:
                final PopupMenu popup = new PopupMenu(getActivity(), countrycodes);
                if (countries != null && countries.length > 0) {

                    for (int i = 0; i < countries.length; i++) {
                        String country = countries[i].getName();
                        popup.getMenu().add(0, i, 0, country);

                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            selectedCountryId = Integer.parseInt(countries[item.getItemId()].getId());
                            Log.v("country selected", "" + selectedCountryId);
                            String country = countries[item.getItemId()].getName();
                            countrycodes.setText(country);
                            return true;
                        }
                    });
                    popup.show();


                }
                break;
            case R.id.btn_save_company_profile:

                //final submit
                sendEditedProfile();


                break;
        }
    }

    //sending profile to server
    private void sendEditedProfile() {
        int selectedRadioButton;
        if (company_profile_pushdownstream_group.getCheckedRadioButtonId() == R.id.radio_yes) {
            selectedRadioButton = 1;
        } else if (company_profile_pushdownstream_group.getCheckedRadioButtonId() == R.id.radio_no) {
            selectedRadioButton = 0;
        } else {
            Toast.makeText(getActivity(), "Please select 'Automatically share received catalogs' checkbox", Toast.LENGTH_LONG).show();
            return;
        }

        //checking for spinner
        if (company_profile_spinner_state.getSelectedItem() != null && company_profile_spinner_city.getSelectedItem() != null &&  ((Response_States) company_profile_spinner_state.getSelectedItem()).getId()!=null &&  ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId()!=null) {
            if (getValue(company_profile_mobile).equals(pref.getString("company_profile_mobile", "")) || getValue(company_profile_mobile).equals("+91" + pref.getString("company_profile_mobile", ""))) {
                saveProfile(getValue(company_profile_email), getValue(company_profile_mobile), getValue(company_profile_name), ((Response_States) company_profile_spinner_state.getSelectedItem()).getId(), ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId(), selectedRadioButton);
            } else {
                if(company_profile_mobile.getText().length()==10) {
                    registerOTP(getValue(company_profile_mobile));
                }else{
                    company_profile_mobile.setError("Number should be of 10 digits");
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please select state and city", Toast.LENGTH_LONG).show();
        }
    }


    private boolean areFieldsValid(String email, String number,
                                   String name, String state, String city) {

        if (email.equals("")) {
            company_profile_email.setError("Email cannot be empty!");
            company_profile_email.requestFocus();
            return false;
        }

        if (name.equals("")) {
            company_profile_name.setError("Company name cannot be empty!");
            company_profile_name.requestFocus();
            return false;
        }

        if (state != null && state.equals("")) {
            Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
            company_profile_spinner_state.requestFocus();
            return false;
        }

        if (city != null && city.equals("")) {
            Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
            company_profile_spinner_city.requestFocus();
            return false;
        }

        if (!ValidationUtils.validCellPhone(number) | number.length() < 10 | number.length() > 10) {
            company_profile_mobile.setError("invalid mobile number");
            company_profile_mobile.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void otpReceived(String messageText) {
        if (messageText.contains("OTP")) {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(messageText);
            while (matcher.find()) {
                String match = matcher.group();
                if(dialog!=null) {
                    try {
                        EditText otpEditText = dialog.getInputEditText();
                        if(otpEditText!=null){
                            otpEditText.setText(match);
                        }
                    } catch (NullPointerException e){
                        Log.d("Error","automatically setting otp");
                    }
                }
            }
        }
    }

    private void getStates() {
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                responseStates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (responseStates != null) {
                    SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem,R.id.spintext, responseStates);
                    company_profile_spinner_state.setAdapter(spinAdapter_state);

                    //setting local state
                    stateSelection(responseStates);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(getActivity())
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

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
                            responseCities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (responseCities != null) {
                                SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, responseCities);
                                company_profile_spinner_city.setAdapter(spinAdapter_city);

                                //setting local city
                                //setting local state
                                for (int i = 0; i < responseCities.length; i++) {
                                    if (pref.getInt("company_profile_city", 0) != 0 && responseCities[i].getId().equals(String.valueOf(pref.getInt("company_profile_city", 0)))) {
                                        company_profile_spinner_city.setSelection(i);
                                    }
                                }
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
    }

    private void stateSelection(Response_States[] responseStates) {
        for (int i = 0; i < responseStates.length; i++) {
            if (pref.getInt("company_profile_state", 0) != 0 && responseStates[i].getId().equals(String.valueOf(pref.getInt("company_profile_state", 0)))) {
                company_profile_spinner_state.setSelection(i);
            }
        }
    }

    //validating Otp
    private void validateOTP(final String mobile) {

         dialog = new MaterialDialog.Builder(getActivity())
                .title(POPUP_OTP_TITLE)
                .content(POPUP_OTP_TEXT)
                .inputRangeRes(2, 20, R.color.material_500_red)
                .positiveText(R.string.submit_text)
                .negativeText(R.string.cancel_text)
                .neutralText(R.string.resent_text)
                .input("", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        patchOtpServer(mobile, input.toString(), dialog);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                registerOTP(mobile);
            }
        }).build();
        dialog.show();
    }

    //patch otp to server
    private void patchOtpServer(String mobile, String inputOtp, final MaterialDialog dialog) {
        JsonObject jsonObject;
         OtpClass obj = new OtpClass(mobile, selectedCountryId, inputOtp);
        jsonObject = new Gson().fromJson(new Gson().toJson(obj), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_otp_url", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                dialog.dismiss();
                pref.edit().putString("company_profile_mobile", getValue(company_profile_mobile)).apply();
                sendEditedProfile();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(getActivity())
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    //when I Change the Number
    private void registerOTP(final String mobile) {

        JsonObject jsonObject;
        OtpClass obj = new OtpClass(mobile, selectedCountryId);
        jsonObject = new Gson().fromJson(new Gson().toJson(obj), JsonObject.class);

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_otp_url", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                validateOTP(mobile);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public String getValue(EditText editText) {
        return editText.getText().toString();
    }

    private void saveProfile(final String email, final String mobile,
                             final String name, final String state, final String city, final int radioButton) {

        if (areFieldsValid(email, mobile, name, state, city)) {

            JsonObject jsonObject;
            CompanyProfile profile = new CompanyProfile();
            profile.setCountry(String.valueOf(selectedCountryId));
            profile.setEmail(email);
            profile.setName(name);
            profile.setPhone_number(mobile);
            profile.setState(state);
            profile.setCity(city);
            if (radioButton == 0) {
                profile.setPush_downstream("no");
            } else {
                profile.setPush_downstream("yes");
            }

            jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    StaticFunctions.logger(response);


                    pref.edit().putString("company_profile_email", email).apply();
                    pref.edit().putString("companyname", name).apply();
                    pref.edit().putString("company_profile_mobile", mobile).apply();
                    pref.edit().putString("company_profile_pushdownstream", String.valueOf(radioButton)).apply();
                    pref.edit().putString("countrycode", String.valueOf(selectedCountryId)).apply();
                    pref.edit().putInt("company_profile_city", Integer.parseInt(city)).apply();
                    pref.edit().putInt("company_profile_state", Integer.parseInt(state)).apply();

                    Snackbar.make(view, "Profile updated successfully!", Snackbar.LENGTH_LONG).show();


                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.logger(error.getErrormessage());
                    Snackbar.make(view, "Request failed!", Snackbar.LENGTH_LONG).show();
                    //input_mobile.setError("mobile number does not exists");
                }
            });

        }

    }

}
