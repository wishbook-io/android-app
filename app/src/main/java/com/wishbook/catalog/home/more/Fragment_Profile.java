package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedDialogFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.PinCodeZone;
import com.wishbook.catalog.commonmodels.responses.Profile;
import com.wishbook.catalog.commonmodels.responses.ResponseNotificationPreference;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.address.ManageDeliveryAddressBottomDialog;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Profile extends GATrackedDialogFragment {


    @BindView(R.id.txt_manage_address)
    TextView txt_manage_address;

    @BindView(R.id.edit_pincode)
    EditText edit_pincode;

    @BindView(R.id.input_pincode)
    TextInputLayout input_pincode;

    @BindView(R.id.edit_phone)
    EditText edit_phone;

    @BindView(R.id.input_phone)
    TextInputLayout input_phone;

    @BindView(R.id.edit_companyname)
    EditText edit_companyname;

    @BindView(R.id.input_companyname)
    TextInputLayout input_companyname;

    @BindView(R.id.input_name)
    TextInputLayout input_name;

    @BindView(R.id.edit_name)
    EditText edit_name;

    @BindView(R.id.edit_email)
    EditText edit_email;

    @BindView(R.id.input_email)
    TextInputLayout input_email;

    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    @BindView(R.id.spinner_city)
    Spinner spinner_city;

    @BindView(R.id.edit_gst)
    EditText edit_gst;

    @BindView(R.id.input_gst)
    TextInputLayout input_gst;

    @BindView(R.id.edit_address)
    EditText edit_address;

    @BindView(R.id.input_address)
    TextInputLayout input_address;

    @BindView(R.id.btn_save)
    AppCompatButton btn_save;

    @BindView(R.id.relative_progress)
    RelativeLayout relative_progress;

    @BindView(R.id.linear_dialog_cancel_ok)
    LinearLayout linear_dialog_cancel_ok;

    @BindView(R.id.btn_negative)
    LinearLayout btn_negative;

    @BindView(R.id.btn_positive)
    LinearLayout btn_positive;

    @BindView(R.id.linear_whatsapp_container)
    LinearLayout linear_whatsapp_container;

    @BindView(R.id.check_whatsapp_mycatalog)
    CheckBox check_whatsapp_mycatalog;

    @BindView(R.id.check_whatsapp_promotion)
    CheckBox check_whatsapp_promotion;


    // ##### Start Global variable #####
    private View view;
    private SharedPreferences preferencesUtils;
    private UserInfo userinfo;
    private Response_States[] responseStates;
    private Response_Cities[] responseCities;
    private Profile profile;
    private boolean isGSTDataPresent;
    String gstId;
    String billing_address_id = null;
    boolean isShowAsDialog = false;
    MaterialDialog progressDialog;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getBoolean("showAsDialog")) {
            isShowAsDialog = true;
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fragment_profile);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View view = View.inflate(getContext(), R.layout.fragment_profile, null);
            ButterKnife.bind(this, view);
            return dialog;
        } else {
            final Dialog dialog = new Dialog(getActivity());
            return dialog;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_profile, container, true);
        view = View.inflate(getContext(), R.layout.fragment_profile, null);
        ButterKnife.bind(this, view);
        preferencesUtils = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        userinfo = UserInfo.getInstance(getContext());
        initView();
        initListener();
        return view;
    }


    public void initView() {
        check_whatsapp_mycatalog.setChecked(true);
        check_whatsapp_promotion.setChecked(true);
        edit_name.setText(preferencesUtils.getString("firstName", "") + " " + preferencesUtils.getString("lastName", ""));
        String phoneNumber1 = preferencesUtils.getString("mobile", "");
        if (phoneNumber1.length() >= 10) {
            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
            edit_phone.setText(phoneNumbertrim);
        }
        edit_companyname.setText(preferencesUtils.getString("companyname", ""));

        edit_email.setText(preferencesUtils.getString("email", ""));
        getUserProfile();
        getCompanyProfile();
        getStates();
        getGST();
        fetchAddress();
        getNotificationPreference();
        bindTextWatcher();
        if (isShowAsDialog) {
            linear_whatsapp_container.setVisibility(View.GONE);
            linear_dialog_cancel_ok.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.GONE);
            txt_manage_address.setVisibility(View.GONE);
        } else {
            linear_whatsapp_container.setVisibility(View.VISIBLE);
            linear_dialog_cancel_ok.setVisibility(View.GONE);
            btn_save.setVisibility(View.VISIBLE);
            txt_manage_address.setVisibility(View.VISIBLE);
        }
    }

    public void initListener() {
        txt_manage_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isReadOnly",true);
                ManageDeliveryAddressBottomDialog addressBottomDialog = ManageDeliveryAddressBottomDialog.newInstance(bundle);
                addressBottomDialog.setTargetFragment(Fragment_Profile.this, 1600);
                addressBottomDialog.show(getFragmentManager(), "ManageAddress");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    checkPinCode(getContext(),edit_pincode.getText().toString(),((Response_Cities) spinner_city.getSelectedItem()).getId());
                }

            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    checkPinCode(getContext(),edit_pincode.getText().toString(),((Response_Cities) spinner_city.getSelectedItem()).getId());
                }
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
                if(isAdded() && !isDetached()) {
                    responseStates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    if (responseStates != null) {
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem, R.id.spintext, responseStates);
                        spinner_state.setAdapter(spinAdapter_state);
                        stateSelection(responseStates);
                    }
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

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                            if (isAdded() && !isDetached()) {
                                responseCities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                                if (responseCities != null) {
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, responseCities);
                                    spinner_city.setAdapter(spinAdapter_city);
                                    for (int i = 0; i < responseCities.length; i++) {
                                        if (preferencesUtils.getInt("company_profile_city", 0) != 0 && responseCities[i].getId().equals(String.valueOf(preferencesUtils.getInt("company_profile_city", 0)))) {
                                            spinner_city.setSelection(i);
                                        }
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

    private void callSaveUserProfile(final String firstName,
                                     final String email, final String mobileNum, final String companyName1, final String state, final String city) {
        if (isValid()) {
            JsonObject jsonObject;
            String[] splited = firstName.trim().split(" ");
            String firstname_split = "", lastname_split = "";
            if (splited.length == 1) {
                firstname_split = splited[0];
            } else if (splited.length == 2) {
                firstname_split = splited[0];
                lastname_split = splited[1];
            } else if (splited.length > 2) {
                firstname_split = (splited[0]);
                ArrayList<String> temp = new ArrayList<>();
                String lastName = "";
                for (int i = 1; i < splited.length; i++) {
                    temp.add(splited[i]);
                }
                lastName = StaticFunctions.ArrayListToString(temp, " ");
                lastname_split = lastName;
            }

            Profile profile = new Profile();
            profile.setFirst_name(firstname_split);
            if (lastname_split != null && !lastname_split.isEmpty()) {
                profile.setLast_name(lastname_split);
            } else {
                profile.setLast_name("");
            }
            /* if (email != null && !email.isEmpty())*/
            profile.setEmail(email);
            jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);

            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            final String finalFirstname_split = firstname_split;
            final String finalLastname_split = lastname_split;


            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if(isAdded() && !isDetached()) {
                        preferencesUtils.edit().putString("firstName", finalFirstname_split).apply();
                        preferencesUtils.edit().putString("lastName", finalLastname_split).apply();
                        preferencesUtils.edit().putString("mobile", mobileNum).apply();
                        preferencesUtils.edit().putString("email", email).apply();
                        patchAddress(billing_address_id,state,city,companyName1,1);
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    StaticFunctions.logger(error.getErrormessage());
                    new MaterialDialog.Builder(getActivity())
                            .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                            .content(error.getErrormessage())
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });

        }

    }

    private void bindTextWatcher() {

        edit_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_name.isErrorEnabled()) {
                    input_name.setError(null);
                    input_name.setErrorEnabled(false);
                }
            }
        });

        edit_companyname.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_companyname.isErrorEnabled()) {
                    input_companyname.setError(null);
                    input_companyname.setErrorEnabled(false);
                }
            }
        });

        edit_phone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_phone.isErrorEnabled()) {
                    input_phone.setError(null);
                    input_phone.setErrorEnabled(false);
                }
            }
        });

        edit_email.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_email.isErrorEnabled()) {
                    input_email.setError(null);
                    input_email.setErrorEnabled(false);
                }
            }
        });


        edit_address.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_address.isErrorEnabled()) {
                    input_address.setError(null);
                    input_address.setErrorEnabled(false);
                }
            }
        });

        edit_pincode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_pincode.isErrorEnabled()) {
                    input_pincode.setError(null);
                    input_pincode.setErrorEnabled(false);
                }
            }
        });


        edit_gst.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_gst.isErrorEnabled()) {
                    input_gst.setError(null);
                    input_gst.setErrorEnabled(false);
                }
            }
        });

    }

    private boolean isValid() {

        if (edit_name.getText().toString().trim().isEmpty()) {
            input_name.setError("Name cannot be empty!");
            edit_name.requestFocus();
            return false;
        }


        if (edit_companyname.getText().toString().trim().isEmpty()) {
            input_companyname.setError("Company name cannot be empty!");
            edit_companyname.requestFocus();
            return false;
        } else {
            if (!edit_companyname.getText().toString().trim().isEmpty() && edit_companyname.getText().toString().equalsIgnoreCase("-")) {
                input_companyname.setError("Please enter valid company name");
                edit_companyname.requestFocus();
                return false;
            }
        }


        /*if (edit_email.getText().toString().isEmpty()) {
            input_email.setError("Please enter email-id");
            edit_email.requestFocus();
            return false;
        }*/

        if (!edit_email.getText().toString().isEmpty()) {
            if (!ValidationUtils.isValidEmail(edit_email.getText().toString())) {
                input_email.setError("Invalid email");
                return false;
            }
        }

        if (edit_address.getText().toString().isEmpty()) {
            input_address.setError("Address cannot be empty!");
            edit_address.requestFocus();
            return false;
        } else {
            if (edit_address.getText().toString().length() < 15) {
                edit_address.requestFocus();
                edit_address.setError("Hmmm... How is your address this small? Please enter a valid address.");
                return false;
            }
        }

        if (((Response_States) spinner_state.getSelectedItem()) == null) {
            Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
            spinner_state.requestFocus();
            return false;
        } else {
            String state = ((Response_States) spinner_state.getSelectedItem()).getId();
            String stateName = ((Response_States) spinner_state.getSelectedItem()).getState_name();
            if (state != null && state.equals("")) {
                Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
                spinner_state.requestFocus();
                return false;
            } else if (stateName.equals("-")) {
                Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
                spinner_state.requestFocus();
                return false;
            }
        }


        if (((Response_Cities) spinner_city.getSelectedItem()) == null) {
            Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
            spinner_city.requestFocus();
            return false;
        } else {
            String city = ((Response_Cities) spinner_city.getSelectedItem()).getId();
            String cityName = ((Response_Cities) spinner_city.getSelectedItem()).getCity_name();
            if (city != null && city.equals("")) {
                Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
                spinner_city.requestFocus();
                return false;
            } else if (cityName.equals("-")) {
                Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
                spinner_city.requestFocus();
                return false;
            }
        }


        if (edit_phone.getText().toString().isEmpty()) {
            input_phone.setError("Mobile number cannot be empty!");
            edit_phone.requestFocus();
            return false;
        }

        if (edit_phone.getText().toString().isEmpty()) {
            if (edit_phone.getText().toString().length() != 10) {
                input_phone.setError("invalid mobile number");
                edit_phone.requestFocus();
                return false;
            }
        }


        if (edit_pincode.getText().toString().isEmpty()) {
            input_pincode.setError("Pincode cannot be empty");
            edit_pincode.requestFocus();
            return false;
        } else if (edit_pincode.getText().toString().length() != 6) {
            input_pincode.setError("Please enter valid pincode");
            edit_pincode.requestFocus();
            return false;
        }


        return true;
    }

    private void stateSelection(Response_States[] responseStates) {
        for (int i = 0; i < responseStates.length; i++) {
            if (preferencesUtils.getInt("company_profile_state", 0) != 0 && responseStates[i].getId().equals(String.valueOf(preferencesUtils.getInt("company_profile_state", 0)))) {
                spinner_state.setSelection(i);
            }
        }
    }

    public void callSaveCompanyProfile(final int selectedCountryId, final String companyName, final String state, final String city) {
        JsonObject jsonObject;
        CompanyProfile profile = new CompanyProfile();
        profile.setName(companyName);
     /*   profile.setCountry(String.valueOf(selectedCountryId));
        profile.setState(state);
        profile.setCity(city);*/
        jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    preferencesUtils.edit().putString("companyname", companyName).apply();
                    preferencesUtils.edit().putInt("company_profile_city", Integer.parseInt(city)).apply();
                    preferencesUtils.edit().putInt("company_profile_state", Integer.parseInt(state)).apply();
                    if (!edit_gst.getText().toString().isEmpty()) {
                        if (gstId != null) {
                            patchGST();
                        } else {
                            postGST();
                        }

                    } else {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        if (!isShowAsDialog) {
                            getActivity().finish();
                        } else {
                            Log.e("TAG", "Dialog Finish: ");
                            Intent intent = new Intent();
                            if (getTargetFragment() != null) {
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                            }
                            getDialog().dismiss();
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    StaticFunctions.logger(error.getErrormessage());
                    new MaterialDialog.Builder(getActivity())
                            .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                            .content(error.getErrormessage())
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void patchGST() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.METHOD method;
            String url;
            method = HttpManager.METHOD.PATCHWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "company_kyc", "") + gstId + "/";

            PostKycGst postKycGst = new PostKycGst();
            postKycGst.setGstin(edit_gst.getText().toString());
            HttpManager.getInstance(getActivity()).requestPatch(method, url, new Gson().fromJson(new Gson().toJson(postKycGst), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if(isAdded() && !isDetached()) {
                        PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);
                        if (resPostKycGst != null) {
                            UserInfo.getInstance(getActivity()).setKyc(Application_Singleton.gson.toJson(resPostKycGst));
                        }
                        if (Activity_Home.pref != null) {
                            Activity_Home.pref.edit().putString("entered_gst", null).commit();
                        } else {
                            Activity_Home.pref = StaticFunctions.getAppSharedPreferences(getActivity());
                            Activity_Home.pref.edit().putString("entered_gst", null).commit();
                        }

                        Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        if (!isShowAsDialog) {
                            getActivity().finish();
                        } else {
                            try {
                                Intent intent = new Intent();
                                if (getTargetFragment() != null) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                                }
                                dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void postGST() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.METHOD method;
            String url;
            method = HttpManager.METHOD.POSTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "company_kyc", "");

            PostKycGst postKycGst = new PostKycGst();
            postKycGst.setGstin(edit_gst.getText().toString());

            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(postKycGst), HashMap.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);
                    if (resPostKycGst != null) {
                        UserInfo.getInstance(getActivity()).setKyc(Application_Singleton.gson.toJson(resPostKycGst));
                    }
                    if (Activity_Home.pref != null) {
                        Activity_Home.pref.edit().putString("entered_gst", null).commit();
                    } else {
                        Activity_Home.pref = StaticFunctions.getAppSharedPreferences(getActivity());
                        Activity_Home.pref.edit().putString("entered_gst", null).commit();
                    }

                    Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    if (!isShowAsDialog) {
                        getActivity().finish();
                    } else {
                        Log.e("TAG", "Dialog Finish: ");
                        Intent intent = new Intent();
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                        }
                        dismiss();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getGST() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    PostKycGst[] resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst[].class);
                    if (resPostKycGst.length > 0 && resPostKycGst[0] != null) {
                        isGSTDataPresent = true;
                        edit_gst.setText(resPostKycGst[0].getGstin());
                        gstId = resPostKycGst[0].getId();
                    } else {
                        isGSTDataPresent = false;
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserProfile() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                StaticFunctions.logger(response);
                try {
                    profile = new Gson().fromJson(response, Profile.class);
                    if(profile.getLast_name()!=null && !profile.getLast_name().trim().isEmpty()) {
                        edit_name.setText(profile.getFirst_name() + " " + profile.getLast_name());
                    } else {
                        edit_name.setText(profile.getFirst_name());
                    }
                    String phoneNumber2 = profile.getUserprofile().getPhone_number();
                    if (phoneNumber2.length() >= 10) {
                        String phoneNumbertrim = phoneNumber2.substring(phoneNumber2.length() - 10, phoneNumber2.length());
                        edit_phone.setText(phoneNumbertrim);
                    }
                    if (profile.getCompanyuser().getCompanyname() != null) {
                        edit_companyname.setText(profile.getCompanyuser().getCompanyname());
                    }
                    edit_email.setText(profile.getEmail());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.logger(error.getErrormessage());
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getCompanyProfile() {
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
                CompanyProfile companyProfile = new Gson().fromJson(response, CompanyProfile.class);
                settingCompanyData(companyProfile);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Snackbar.make(view, "Request failed!", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void settingCompanyData(CompanyProfile companyProfile) {
        edit_companyname.setText(companyProfile.getName());
        preferencesUtils.edit().putInt("company_profile_city", Integer.parseInt(companyProfile.getCity())).apply();
        preferencesUtils.edit().putInt("company_profile_state", Integer.parseInt(companyProfile.getState())).apply();
        getStates();
    }


    private void postNotificationPreference() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.METHOD method;
            String url;
            method = HttpManager.METHOD.POSTWITHPROGRESS;
            url = URLConstants.userUrl(getActivity(), "notification-preference", "");
            ResponseNotificationPreference requestPreference = new ResponseNotificationPreference();
            requestPreference.setWhatsapp_notifications(check_whatsapp_mycatalog.isChecked());
            requestPreference.setWhatsapp_promotions(check_whatsapp_promotion.isChecked());

            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(requestPreference), HashMap.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {


                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void getNotificationPreference () {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(), "notification-preference", ""), null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseNotificationPreference[] responseNotificationPreferences = Application_Singleton.gson.fromJson(response, ResponseNotificationPreference[].class);
                    if (responseNotificationPreferences.length > 0) {
                        check_whatsapp_mycatalog.setChecked(responseNotificationPreferences[0].isWhatsapp_notifications());
                        check_whatsapp_promotion.setChecked(responseNotificationPreferences[0].isWhatsapp_promotions());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fetchAddress() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", ""), null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ShippingAddressResponse[] shippingAddressResponses = Application_Singleton.gson.fromJson(response, ShippingAddressResponse[].class);
                    if (shippingAddressResponses.length > 0) {
                        ArrayList<ShippingAddressResponse> addressResponses = new ArrayList<ShippingAddressResponse>(Arrays.asList(shippingAddressResponses));
                        setBillingAddress(addressResponses);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setBillingAddress(ArrayList<ShippingAddressResponse> addressResponses) {
        if (addressResponses != null && addressResponses.size() > 0) {
            for (int i = 0; i < addressResponses.size(); i++) {
                if (addressResponses.get(i).is_default()) {
                    edit_address.setText(addressResponses.get(i).getStreet_address());
                    edit_pincode.setText(addressResponses.get(i).getPincode());
                    billing_address_id = addressResponses.get(i).getId();
                    return;
                }
            }
        }
    }

    public void patchAddress(String billing_address_id,String state,String city,String companyName1,int selectedCountryId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("street_address", edit_address.getText().toString());
        params.put("pincode", edit_pincode.getText().toString());
        params.put("name", edit_name.getText().toString());
        params.put("country", String.valueOf(selectedCountryId));
        params.put("state", state);
        params.put("city", city);


        // Decide to post or patch
        String url;
        HttpManager.METHOD method = HttpManager.METHOD.PATCHWITHPROGRESS;
        method = HttpManager.METHOD.PATCHWITHPROGRESS;
        url = URLConstants.companyUrl(getActivity(), "address", "") + billing_address_id + "/";
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(method, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                AddressResponse newAddress = Application_Singleton.gson.fromJson(response, AddressResponse.class);
                try {
                    if(!isShowAsDialog) {
                        postNotificationPreference();
                    }
                    callSaveCompanyProfile(1, companyName1, state, city);
                    Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });


    }

    public void showProgress() {
        if (relative_progress != null) {
            relative_progress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (relative_progress != null) {
            relative_progress.setVisibility(View.GONE);
        }
    }

    /**
     * WB-4262 mapped city,state with pincode
     */
    public void checkPinCode(Context context, String pincode, final String selected_city) {
        final boolean[] isSamecity = {true};
        final String state_id = ((Response_States) spinner_state.getSelectedItem()).getId();
        final String city_id = ((Response_Cities) spinner_city.getSelectedItem()).getId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> params = new HashMap<>();
        params.put("pincode", pincode);
        progressDialog = StaticFunctions.showProgress(getContext());
        progressDialog.show();
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PINCODE_ZONE, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                if(isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    final ArrayList<PinCodeZone> pinCodeZones = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<PinCodeZone>>() {
                    }.getType());

                    if (pinCodeZones != null && pinCodeZones.size() > 0) {
                        if (!pinCodeZones.get(0).getCity().equalsIgnoreCase(selected_city)) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(getActivity());
                            }
                            builder.setTitle("")
                                    .setMessage("We have updated the city since the pincode you entered is of a different city.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            isSamecity[0] = false;
                                            callSaveUserProfile(edit_name.getText().toString(),
                                                    edit_email.getText().toString(),
                                                    edit_phone.getText().toString(),
                                                    edit_companyname.getText().toString(),pinCodeZones.get(0).getState(),pinCodeZones.get(0).getCity());
                                        }
                                    })
                                    .show();
                        }  else {
                            // continue old state
                            callSaveUserProfile(edit_name.getText().toString(),
                                    edit_email.getText().toString(),
                                    edit_phone.getText().toString(),
                                    edit_companyname.getText().toString(),state_id,city_id);
                        }
                    } else {
                        // continue old state
                        callSaveUserProfile(edit_name.getText().toString(),
                                edit_email.getText().toString(),
                                edit_phone.getText().toString(),
                                edit_companyname.getText().toString(),state_id,city_id);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    callSaveUserProfile(edit_name.getText().toString(),
                            edit_email.getText().toString(),
                            edit_phone.getText().toString(),
                            edit_companyname.getText().toString(),state_id,city_id);
                }
            }
        });
    }

}
