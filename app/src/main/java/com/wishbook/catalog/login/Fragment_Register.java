package com.wishbook.catalog.login;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonadapters.AutoCompleteCompanyAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.models.CompanyList;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_Key;
import com.wishbook.catalog.login.models.Response_OTP;
import com.wishbook.catalog.login.models.Response_States;
import com.wishbook.catalog.login.models.Response_User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class Fragment_Register extends GATrackedFragment implements OTPListener, Activity_Login.OnBackPressedListener {
    public final String POPUP_OTP_TITLE = "Mobile Verification";
    public final String POPUP_OTP_TEXT = "Please enter the One Time Password (OTP) sent to your registered mobile number and authenticate the registration.";
    public final String OTP_HINT = "Recieved OTP";
    private View view;
    private EditText inputotp;
    private boolean termscheck = false;
    private boolean approvecheck = true;
    private boolean discovercheck = true;
    @BindView(R.id.appbar)
    Toolbar toolbar;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.input_mobile)
    EditText input_mobile;
    @BindView(R.id.input_confpassword)
    EditText input_confpassword;
    @BindView(R.id.input_username)
    EditText input_username;
    @BindView(R.id.countrycodes)
    TextView countrycodes;
    @BindView(R.id.input_name)
    EditText input_name;
    @BindView(R.id.check_terms)
    AppCompatCheckBox check_terms;
    /*  @BindView(R.id.check_approve)
      AppCompatCheckBox check_approve;
      @BindView(R.id.check_discoverable)
      AppCompatCheckBox check_discoverable;*/
    @BindView(R.id.newcompanycheck)
    AppCompatCheckBox newcompanycheck;
    @BindView(R.id.btn_register)
    TextView btn_register;
    @BindView(R.id.spinner_state)
    Spinner spinner_state;
    @BindView(R.id.spinner_city)
    Spinner spinner_city;
    @BindView(R.id.spinner_usertype)
    Spinner spinner_usertype;
    @BindView(R.id.donotcreate)
    LinearLayout donotcreate;
    @BindView(R.id.companyautocomp)
    CustomAutoCompleteTextView companyautocomp;
    @BindView(R.id.tnc)
    TextView tnc;
    private ArrayList<CompanyList> suggestions = new ArrayList<>();
    private UserInfo userInfo;
    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private AutoCompleteCompanyAdapter companyListadapter;
    private ArrayList<CompanyList> companies;
    private String stateId = "";
    private String cityId = "";
    private boolean isNewCompany = true;
    private String companyId;
    public CompanyList selectedCompany;
    private String mobile = "";
    private String State = "";
    private String City = "";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Boolean gps_enabled;
    private Countries[] countries;
    private String CountryId = "1";

    @BindView(R.id.linear_step_one)
    LinearLayout linear_step_one;

    @BindView(R.id.linear_step_two)
    LinearLayout linear_step_two;


    @BindView(R.id.input_companyname)
    EditText input_companyname;

    @BindView(R.id.txt_input_companyname)
    TextInputLayout txt_input_companyname;

    @BindView(R.id.img_check_password)
    ImageView img_check_password;


    @BindView(R.id.linear_city)
    LinearLayout linear_city;
    @BindView(R.id.linear_state)
    LinearLayout linear_state;

    @BindView(R.id.txt_scan_dialog)
    TextView txt_scan_dialog;
    ClickableSpan clickableSpan;


    int step;
    JSONObject facebookJsonObject;
    private ZXingScannerView mScannerView;
    private String TAG = Fragment_Register.class.getSimpleName();

    public Fragment_Register() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_Register.this, Constants.SENDER_NUM);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(getActivity(), CallbackLocationFetchingActivity.class);
            startActivityForResult(intent, 1);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onStop() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();

        // getStateFromLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.frgament_signup_version_2, ga_container, true);
        ButterKnife.bind(this, view);
        fetchMobileNumber();
        companies = new ArrayList<>();
        donotcreate.setVisibility(View.GONE);
        if (getActivity() instanceof Activity_Login)
            ((Activity_Login) getActivity()).setOnBackPressedListener(this);
        step = 1;
        // check_approve.setVisibility(View.GONE);
        //check_discoverable.setVisibility(View.GONE);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE = "Terms & Condition";
                Application_Singleton.CONTAINERFRAG = new Fragment_TermsConditions();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.getIntExtra("toolbarCategory", OpenContainer.TNC);
                startActivity(intent);
            }
        });
        getCountries(getActivity());


        //SPANNABLE STRING
        SpannableString ss = new SpannableString(getString(R.string.register_scan_label));
        String s = getString(R.string.register_scan_label);
        int start_link = s.indexOf("enter");
        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String username = input_username.getText().toString().equals("") ? input_mobile.getText().toString() : input_username.getText().toString();
                input_username.setText(username);
                String companyname = input_companyname.getText().toString();
                String name = input_name.getText().toString();
                final String mobile = input_mobile.getText().toString();
                String password = input_password.getText().toString();
                String confPswd = input_confpassword.getText().toString();
                if (!checkValidations(companyname, username, mobile, password, confPswd)) {
                    new MaterialDialog.Builder(getActivity())
                            .content("Please fill up all details")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                } else {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 500);
                    } else {
                        Intent intent = new Intent(getActivity(), ReferralScanActivity.class);
                        startActivityForResult(intent, ReferralScanActivity.REQUEST_REGISTER_REFERRAL);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, start_link, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_scan_dialog.setText(ss);
        txt_scan_dialog.setMovementMethod(LinkMovementMethod.getInstance());
        txt_scan_dialog.setClickable(true);


        //added by Abu
        if (!Fragment_login.password.equals("") || !Fragment_login.username.equals("")) {
            if (Fragment_login.username.toString().matches("[0-9]+")) {
                input_mobile.setText(Fragment_login.username.toString());
            }
            input_password.setText(Fragment_login.password.toString());
        }


        companyautocomp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                selectedCompany = (CompanyList) parent.getItemAtPosition(position);
            }
        });


        companyautocomp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isNewCompany) {
                    selectedCompany = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        userInfo = UserInfo.getInstance(getActivity());

        input_password.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input_password.setTransformationMethod(new PasswordTransformationMethod());
        input_confpassword.setTransformationMethod(new PasswordTransformationMethod());
        input_confpassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        input_confpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!input_confpassword.getText().toString().isEmpty()
                        && !input_password.getText().toString().isEmpty()) {
                    if (input_password.getText().toString().equals(input_confpassword.getText().toString())) {
                        img_check_password.setVisibility(View.VISIBLE);
                    } else {
                        img_check_password.setVisibility(View.GONE);
                    }
                }
            }
        });
        getstates();

        check_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                termscheck = isChecked;
            }
        });
     /*   check_approve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                approvecheck = isChecked;
            }
        });
        check_discoverable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                discovercheck = isChecked;
            }
        });*/
        newcompanycheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNewCompany = false;
                    txt_input_companyname.setVisibility(View.GONE);
                    if (checkIfLocationSelected()) {
                        donotcreate.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Please select State and City ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isNewCompany = true;
                    donotcreate.setVisibility(View.GONE);
                    txt_input_companyname.setVisibility(View.VISIBLE);
                }
            }
        });


        //Added by abu
        input_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && input_mobile.getText().toString().length() == 10) {
                    NumberExist();
                    if (!input_mobile.getText().toString().isEmpty()) {
                        String code = "+91";
                        if (countries != null && countries.length > 0) {
                            code = countries[Integer.parseInt(CountryId) - 1].getPhone_code();
                        }
                        input_username.setText(code.substring(1) + input_mobile.getText().toString().trim());
                    }

                }
            }
        });

        if (getArguments() != null) {
            if (getArguments().getString("registerDetailFb") != null) {
                try {
                    String userProfileString = getArguments().getString("registerDetailFb");
                    facebookJsonObject = new JSONObject(userProfileString);
                    if (facebookJsonObject.has("name")) {
                        input_name.setText(facebookJsonObject.getString("name"));
                    }
                    if (facebookJsonObject.has("email")) {
                        input_email.setText(facebookJsonObject.getString("email"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        updateUI();
        return view;
    }


    private void NumberExist() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", input_mobile.getText().toString());
        //for now Static
        params.put("country", CountryId);
        params.put("field", "is_exist");

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.USERS, params, null, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.d("CHECk", response.toString());
                String code = "+91";
                if (countries != null && countries.length > 0) {
                    code = countries[Integer.parseInt(CountryId) - 1].getPhone_code();
                }
                input_username.setText(code.substring(1) + input_mobile.getText().toString().trim());
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (error != null && error.getErrormessage() != null && getActivity() != null) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
                input_mobile.setError("Phone Number Already exist");
            }
        });

    }

    private void checkEmailExist() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", input_email.getText().toString());
        params.put("field", "is_exist");
        showProgress();

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.USERS, params, null, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.d("CHECk", response.toString());
                step = step + 1;
                updateUI();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (error != null && error.getErrormessage() != null && getActivity() != null) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
                input_email.setError("Email-Id Already exist");
            }
        });
    }

    private boolean checkIfLocationSelected() {
        boolean locationselected = false;
        if (!cityId.equals("") && !stateId.equals("")) {
            locationselected = true;
        }
        return locationselected;
    }

    private void getstates() {
        Log.i(TAG, "getstates: Called");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.i(TAG, "getstates: Called Set Adapter");
                allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (getActivity() != null) {
                    if (allstates != null) {
                        linear_state.setVisibility(View.VISIBLE);
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem, R.id.spintext, allstates);
                        spinner_state.setAdapter(spinAdapter_state);
                    }
                    if (allstates != null) {
                        if (State != null) {
                            for (int i = 0; i < allstates.length; i++) {
                                if (State.equals(allstates[i].getState_name())) {
                                    try {
                                        spinner_state.setSelection(i);
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
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
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    State = allstates[position].getState_name();
                    Log.i(TAG, "getCity: Called");
                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Log.i(TAG, "getcity: set Data");
                            spinner_city.setVisibility(View.VISIBLE);
                            allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (allcities != null) {
                                if (getActivity() != null) {
                                    linear_city.setVisibility(View.VISIBLE);
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, allcities);
                                    spinner_city.setAdapter(spinAdapter_city);
                                    if (State != null && State != "") {
                                        if (City != null) {
                                            for (int i = 0; i < allcities.length; i++) {
                                                if (City.equals(allcities[i].getCity_name())) {
                                                    try {
                                                        spinner_city.setSelection(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                }

                                            }
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
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                cityId = allcities[position].getId();
                City = allcities[position].getCity_name();
                HashMap<String, String> params = new HashMap<>();
                params.put("city", cityId);
                params.put("state", stateId);

                companyListadapter = new AutoCompleteCompanyAdapter(getActivity(), R.layout.spinneritem, companies, params);
                companyautocomp.setAdapter(companyListadapter);
                companyautocomp.setThreshold(1);
                companyautocomp.setLoadingIndicator(
                        (android.widget.ProgressBar) view.findViewById(R.id.progress_bar));



                /*HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(),"companylist",""), params, null, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        //change by abu
                        companies.clear();
                        CompanyList[] companyLists = new Gson().fromJson(response, CompanyList[].class);
                        for (CompanyList companyList : companyLists) {
                            companies.add(companyList);
                        }
                        companyListadapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
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
                });*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.btn_register)
    public void onRegister() {
        if (step == 1) {
            if (input_name.getText().toString().isEmpty()) {
                input_name.requestFocus();
                input_name.setError("Name cannot be empty !");
                return;
            }
            if (input_mobile.getText().toString().isEmpty()) {
                input_mobile.requestFocus();
                input_mobile.setError("mobile number cannot be empty !");
                return;
            }
            if (input_mobile.getText().toString().length() != 10) {
                input_mobile.requestFocus();
                input_mobile.setError("Invalid Mobile Number");
                return;
            }

            if (!input_mobile.getText().toString().isEmpty()) {
                if (input_mobile.getText().toString().startsWith("6") ||
                        input_mobile.getText().toString().startsWith("7") ||
                        input_mobile.getText().toString().startsWith("8") ||
                        input_mobile.getText().toString().startsWith("9")) {
                } else {
                    input_mobile.requestFocus();
                    input_mobile.setError("Invalid Mobile Number");
                    return;
                }
            }

            if (State != null) {
                if (State.equals("-") || State.equals("")) {
                    Toast.makeText(getActivity(), "Please Select State", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (City != null) {
                if (City.equals("-") || City.equals("")) {
                    Toast.makeText(getActivity(), "Please Select City", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String email = input_email.getText().toString();
            if (email != null) {
                if (email.isEmpty()) {
                    String code = "+91";
                    if (countries != null && countries.length > 0) {
                        code = countries[Integer.parseInt(CountryId) - 1].getPhone_code();
                    }
                    input_email.setText(code.substring(1) + input_mobile.getText().toString() + "@wishbook.io");
                    email = code.substring(1) + input_email.getText().toString();
                }
                if (email.equals("")) {
                    String code = "+91";
                    if (countries != null && countries.length > 0) {
                        code = countries[Integer.parseInt(CountryId) - 1].getPhone_code();
                    }
                    input_email.setText(code.substring(1) + input_mobile.getText().toString() + "@wishbook.io");
                    email = code.substring(1) + input_email.getText().toString();
                }
                if (email.equals("@wishbook.io")) {
                    input_email.setText("");
                    String code = "+91";
                    if (countries != null && countries.length > 0) {
                        code = countries[Integer.parseInt(CountryId) - 1].getPhone_code();
                    }
                    input_email.setText(code + input_mobile.getText().toString() + "@wishbook.io");
                }


                if (!input_email.getText().toString().isEmpty()) {
                    if (!isValidEmail(input_email.getText().toString())) {
                        input_email.setError("Enter valid Email Address");
                        return;
                    }
                    checkEmailExist();
                }
            }
        } else if (step == 2) {
            String username = input_username.getText().toString().equals("") ? input_mobile.getText().toString() : input_username.getText().toString();
            input_username.setText(username);
            String companyname = input_companyname.getText().toString();
            String name = input_name.getText().toString();
            final String mobile = input_mobile.getText().toString();
            String password = input_password.getText().toString();
            String confPswd = input_confpassword.getText().toString();
            if (checkValidations(companyname, username, mobile, password, confPswd)) {
                actionSetThree(null);
            }
        }

    }

    public void actionSetThree(String referral) {
        String username = input_username.getText().toString().equals("") ? input_mobile.getText().toString() : input_username.getText().toString();
        input_username.setText(username);
        String companyname = input_companyname.getText().toString();
        String name = input_name.getText().toString();
        final String mobile = input_mobile.getText().toString();
        //final String mobile = input_mobile.getText().toString();
        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        String confPswd = input_confpassword.getText().toString();
        //signUp(companyname, name, mobile, email, password, confPswd, username);
        registerUser(companyname, username, mobile, email, password, confPswd, name, referral);
    }

    @OnClick(R.id.countrycodes)
    public void onCountrySelected() {
        final PopupMenu popup = new PopupMenu(getActivity(), countrycodes);
        if (countries != null && countries.length > 0) {
            for (int i = 0; i < countries.length; i++) {
                String country = countries[i].getName();
                popup.getMenu().add(0, i, 0, country);

            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    CountryId = countries[item.getItemId()].getId();
                    Log.v("country selected", "" + CountryId);
                    String country = countries[item.getItemId()].getName();
                    countrycodes.setText(country);
                    return true;
                }
            });
            popup.show();


        }
    }


    /*private void signUp(final String companyname, final String name, final String mobile, final String email,
                        final String password, final String confPswd, final String username) {

        if (checkValidations(companyname,name, mobile, password, confPswd)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("email", email);

            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.USER_EXISTENCE_CHECK, params, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Response_Checkuser response_checkuser = Application_Singleton.gson.fromJson(response, Response_Checkuser.class);
                    if (response_checkuser.getSuccess() != null) {
                        registerUser(companyname, username, mobile, email, password, confPswd);
                       // generateOTP(companyname, mobile, name, email, password, confPswd);
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
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });


        }
    }
*/
   /* private void generateOTP(final String companyname, final String mobile, final String username, final String email, final String password, final String confPswd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", mobile);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.REGISTRATION_OTP, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Response_OTP response_otp = Application_Singleton.gson.fromJson(response, Response_OTP.class);
                if (response_otp.getCreated_date() != null) {
                    validateOTP(companyname, username, mobile, email, password, confPswd);
                } else {
                    StaticFunctions.showError(view, "error requesting OTP !");
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
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });


    }*/

//changed by Abu
    /*private void validateOTP(final String username,final String password) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        alert.setTitle(POPUP_OTP_TITLE);
        alert.setMessage(POPUP_OTP_TEXT);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_input, (ViewGroup) getView(), false);
        inputotp = (EditText) viewInflated.findViewById(R.id.input_otp);
        alert.setView(viewInflated);
        alert.setPositiveButton("Submit", null);
        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Resend", null);

        final AlertDialog alertModal = alert.create();
        alertModal.getWindow().setLayout(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        alertModal.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button submit_but = alertModal.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg_but = alertModal.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        HashMap<String, String> params = new HashMap<>();
                        //params.put("phone_number", mobile);
                        params.put("username", username);
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.RESENDOTP_URL, params, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Response_OTP response_otp = Application_Singleton.gson.fromJson(response, Response_OTP.class);
                               *//* if (response_otp.getCreated_date() != null) {
                                    StaticFunctions.showMessage(view, "request processed");
                                } else {
                                    StaticFunctions.showError(view, "error requesting OTP !");
                                }*//*
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
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
                });

                submit_but.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(final View v) {
                                                      HashMap<String, String> params = new HashMap<>();
                                                      //params.put("phone_number", mobile);
                                                      params.put("username", username);
                                                      params.put("otp", inputotp.getText().toString());

                                                      HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.CHECKOTP_URL, params, new HttpManager.customCallBack() {
                                                          @Override
                                                          public void onCacheResponse(String response) {

                                                          }

                                                          @Override
                                                          public void onServerResponse(String response, boolean dataUpdated) {
                                                              Response_Success response_success = Application_Singleton.gson.fromJson(response, Response_Success.class);
                                                              if (response_success.getSuccess() != null) {
                                                                  Log.d("OTP",response_success.getSuccess().toString());
                                                                  alertModal.dismiss();
                                                                  HashMap<String, String> paramslogin = new HashMap<>();
                                                                  paramslogin.put("username", username);
                                                                  paramslogin.put("password", password);
                                                                  Fragment_login loginObject = new Fragment_login();
                                                                  loginObject.loginUser(paramslogin,getActivity(),"fromReg",userInfo,password);

                                                                //  registerUser(companyname, username, mobile, email, password, confPswd);
                                                              } else {
                                                                  StaticFunctions.showError(view, "invalid OTP");
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
                                                                          public void onClick(MaterialDialog dialog, DialogAction which) {
                                                                              dialog.dismiss();
                                                                          }
                                                                      })
                                                                      .show();
                                                          }
                                                      });

                                                  }
                                              }

                );
            }
        });
        alertModal.setCancelable(false);
        alertModal.show();
    }*/


    private void validateOTP(final String username, final String password) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        alert.setTitle(POPUP_OTP_TITLE);
        alert.setMessage(POPUP_OTP_TEXT);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_input, (ViewGroup) getView(), false);
        inputotp = (EditText) viewInflated.findViewById(R.id.input_otp);
        alert.setView(viewInflated);
        alert.setPositiveButton("Submit", null);
        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (getActivity() != null) {
                    dialog.dismiss();
                    getActivity().getSupportFragmentManager().popBackStack();
                    new MaterialDialog.Builder(getActivity())
                            .content(getResources().getString(R.string.otp_cancel_register))
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
        alert.setNegativeButton("Resend", null);

        final AlertDialog alertModal = alert.create();
        alertModal.getWindow().setLayout(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        alertModal.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button submit_but = alertModal.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg_but = alertModal.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        HashMap<String, String> params = new HashMap<>();
                        //params.put("phone_number", mobile);
                        params.put("name_or_number", username);
                        params.put("otp", "");
                        params.put("password", password);
                        params.put("country", CountryId);
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGIN_URL, params, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Response_OTP response_otp = Application_Singleton.gson.fromJson(response, Response_OTP.class);
                               /* if (response_otp.getCreated_date() != null) {
                                    StaticFunctions.showMessage(view, "request processed");
                                } else {
                                    StaticFunctions.showError(view, "error requesting OTP !");
                                }*/
                                StaticFunctions.showMessage(view, "Request processed");
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                               /* new MaterialDialog.Builder(getActivity())
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
                                StaticFunctions.showMessage(view, "Request processed");

                            }
                        });


                    }
                });

                submit_but.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(final View v) {
                                                      final HashMap<String, String> params = new HashMap<>();
                                                      //params.put("phone_number", mobile);
                                                      params.put("name_or_number", username);
                                                      params.put("otp", inputotp.getText().toString());
                                                      params.put("password", password);
                                                      params.put("country", CountryId);
                                                      HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGIN_URL, params, new HttpManager.customCallBack() {
                                                          @Override
                                                          public void onCacheResponse(String response) {

                                                          }

                                                          @Override
                                                          public void onServerResponse(String response, boolean dataUpdated) {
                                                              final HashMap<String, String> param = new HashMap<>();
                                                              param.put("username", username);
                                                              param.put("password", password);
                                                              Fragment_login loginObject = new Fragment_login();
                                                              loginObject.loginUser(params, getActivity(), "fromReg", userInfo, password);

                                                          }

                                                          @Override
                                                          public void onResponseFailed(ErrorString error) {
                                                              if (!isRemoving()) {
                                                                  StaticFunctions.showResponseFailedDialog(error);
                                                                  alertModal.dismiss();
                                                                  getActivity().getSupportFragmentManager().popBackStack();
                                                              }
                                                          }
                                                      });

                                                  }
                                              }

                );
            }
        });
        alertModal.setCancelable(false);
        alertModal.show();
    }


    /*   private void validateOTP(final String username,final String password) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        alert.setTitle(POPUP_OTP_TITLE);
        alert.setMessage(POPUP_OTP_TEXT);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_input, (ViewGroup) getView(), false);
        inputotp = (EditText) viewInflated.findViewById(R.id.input_otp);
        alert.setView(viewInflated);
        alert.setPositiveButton("Submit", null);
        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Resend", null);

        final AlertDialog alertModal = alert.create();
        alertModal.getWindow().setLayout(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        alertModal.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button submit_but = alertModal.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg_but = alertModal.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        HashMap<String, String> params = new HashMap<>();
                        //params.put("phone_number", mobile);
                        params.put("username", username);
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.RESENDOTP_URL, params, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Response_OTP response_otp = Application_Singleton.gson.fromJson(response, Response_OTP.class);
                               *//* if (response_otp.getCreated_date() != null) {
                                    StaticFunctions.showMessage(view, "request processed");
                                } else {
                                    StaticFunctions.showError(view, "error requesting OTP !");
                                }*//*
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
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
                });

                submit_but.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(final View v) {
                                                      HashMap<String, String> params = new HashMap<>();
                                                      //params.put("phone_number", mobile);
                                                      params.put("username", username);
                                                      params.put("otp", inputotp.getText().toString());

                                                      HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.CHECKOTP_URL, params, new HttpManager.customCallBack() {
                                                          @Override
                                                          public void onCacheResponse(String response) {

                                                          }

                                                          @Override
                                                          public void onServerResponse(String response, boolean dataUpdated) {
                                                              Response_Success response_success = Application_Singleton.gson.fromJson(response, Response_Success.class);
                                                              if (response_success.getSuccess() != null) {
                                                                  Log.d("OTP",response_success.getSuccess().toString());
                                                                  alertModal.dismiss();
                                                                  HashMap<String, String> paramslogin = new HashMap<>();
                                                                  paramslogin.put("username", username);
                                                                  paramslogin.put("password", password);
                                                                  Fragment_login loginObject = new Fragment_login();
                                                                  loginObject.loginUser(paramslogin,getActivity(),"fromReg",userInfo,password);

                                                                //  registerUser(companyname, username, mobile, email, password, confPswd);
                                                              } else {
                                                                  StaticFunctions.showError(view, "invalid OTP");
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
                                                                          public void onClick(MaterialDialog dialog, DialogAction which) {
                                                                              dialog.dismiss();
                                                                          }
                                                                      })
                                                                      .show();
                                                          }
                                                      });

                                                  }
                                              }

                );
            }
        });
        alertModal.setCancelable(false);
        alertModal.show();
    }*/

    private void registerUser(final String companyname, final String username, final String mobile, String email, final String password, final String confPswd, final String firstName, final String referral) {

        if (checkValidations(companyname, username, mobile, password, confPswd)) {
            HashMap<String, String> params = new HashMap<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sharedPreferences.getString("gcmtoken", "");

            if (email.equals("")) {
                email = mobile + "@wishbook.io";
                input_email.setText(email);
            }
            params.put("first_name", firstName);
            params.put("username", username);
            params.put("phone_number", mobile);
            params.put("email", email);
            params.put("password1", password);
            params.put("password2", confPswd);
            params.put("connections_preapproved", "true");
            params.put("discovery_ok", "true");
            params.put("registration_id", token);
            params.put("city", cityId);
            params.put("state", stateId);
            params.put("number_verified", "no");
            params.put("country", CountryId);
            if (referral != null && !referral.isEmpty()) {
                params.put("refered_by", referral);
            }

            //changed by abu
            if (isNewCompany == false) {
                if (selectedCompany == null) {
                    Toast.makeText(getActivity(), "Please Select Valid Company", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                    params.put("usertype", "administrator");
                } else {
                    params.put("usertype", "salesperson");
                }

                params.put("company", getCompanyId());
            } else {
                params.put("company_name", companyname);
            }
            final String finalEmail = email;
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.REGISTER_USER, params, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    StaticFunctions.logger(response);
                    //generateOTP(companyname, mobile, username, finalEmail, password, confPswd);
                    Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                    if (response_key.getKey() != null) {
                        userInfo.setKey(response_key.getKey());
                        validateOTP(username, password);


                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });

        }
    }


    private void getUserDetails() {
        StaticFunctions.clearAllService();
        Application_Singleton.Token = userInfo.getKey();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestwithOnlyHeaders(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PROFILE, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                Response_User response_user = Application_Singleton.gson.fromJson(response, Response_User.class);
                StaticFunctions.storeUserData(response_user, userInfo, input_password.getText().toString());
                StaticFunctions.switchActivity(getActivity(), Activity_Home.class);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);


            }
        });
    }

    private boolean checkValidations(String companyname, String name, String mobile, String password, String confPswd) {


        if (isNewCompany) {
            if (companyname.equals("")) {
                input_companyname.requestFocus();
                input_companyname.setError("Compnay name cannot be empty !");
                return false;
            }
        }
        if (mobile.equals("")) {
            input_mobile.requestFocus();
            input_mobile.setError("mobile number cannot be empty !");
            return false;
        }
        if (input_mobile.getText().toString().length() != 10) {
            input_mobile.requestFocus();
            input_mobile.setError("Invalid Mobile Number");
            return false;
        }

        if (password.equals("")) {
            input_password.requestFocus();
            input_password.setError("password cannot be empty !");
            return false;
        }

        if (password.length() < 6) {
            input_password.requestFocus();
            input_password.setError("password too short !");
            return false;
        }

        if (confPswd.equals("")) {
            input_confpassword.requestFocus();
            input_confpassword.setError("confirm password cannot be empty !");
            return false;
        }

        if (!password.equals(confPswd)) {
            input_confpassword.requestFocus();
            input_confpassword.setError("password mismatch");
            return false;
        }
        if (!termscheck) {
            Toast.makeText(getActivity(), "Should agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isNewCompany) {
            if (companyautocomp.getText().toString().trim().isEmpty()) {
                new MaterialDialog.Builder(getActivity())
                        .content("Company name is required")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            } else {
                if (selectedCompany == null) {
                    new MaterialDialog.Builder(getActivity())
                            .content("Company name is Invalid")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    // Toast.makeText(getActivity(), "Company name is required", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        }
        return true;

    }

    public void setUpToolbar(final AppCompatActivity context, Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Register");
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.intro_text_color));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.intro_text_color), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step == 1) {
                    context.getSupportFragmentManager().popBackStack();
                } else {
                    step = step - 1;
                    updateUI();
                }

            }
        });
    }

    @Override
    public void otpReceived(String messageText) {
        if (messageText.contains("OTP")) {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(messageText);
            while (matcher.find()) {
                String match = matcher.group();
                inputotp.setText(match);
            }
        }
    }

    public String getCompanyId() {

        return selectedCompany.getId();
    }


    //Getting State Logic From GPS

    private void getStateFromLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            // notify user
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.LightDialogTheme);
            dialog.setMessage("Gps not enabled");
            dialog.setPositiveButton("Open Location Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                }
            });
            dialog.show();
        } else {
            getGoogleApi();
        }
    }

    private void getGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d("Getting Location", "");
                            int count = 0;
                            getLocation(count);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void getLocation(int count) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d("Fetched Location", "");
        count++;
        if (count < 10) {
            if (mLastLocation != null) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
                if (geocoder != null) {
                    try {
                        /**
                         * Geocoder.getFromLocation - Returns an array of Addresses
                         * that are known to describe the area immediately surrounding the given latitude and longitude.
                         */
                        Log.d("Getting Address", "");
                        List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

                        if (addresses.get(0).getAdminArea() != null) {
                            State = addresses.get(0).getAdminArea().toString();
                        } else {
                            State = "-";
                        }

                        if (addresses.get(0).getLocality() != null) {
                            City = addresses.get(0).getLocality().toString();
                        } else {
                            City = "-";
                        }
                        if (allstates != null) {
                            if (State != null) {
                                for (int i = 0; i < allstates.length; i++) {
                                    if (State.equals(allstates[i].getState_name())) {
                                        try {
                                            spinner_state.setSelection(i);
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        getstates();
                    } catch (IOException e) {
                        //e.printStackTrace();
                        Log.e("CAtched", "Impossible to connect to Geocoder", e);
                    }
                }
            } else {
                getLocation(count);
            }
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Double latitude = bundle.getDouble("latitude");
                Double longitude = bundle.getDouble("longitude");
                if (latitude != null && longitude != null) {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
                    if (geocoder != null) {
                        try {
                            /**
                             * Geocoder.getFromLocation - Returns an array of Addresses
                             * that are known to describe the area immediately surrounding the given latitude and longitude.
                             */
                            Log.d("Getting Address", "");
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            Log.d("Address", addresses.toString());
                            Log.i(TAG, "onActivityResult: Geo Coder Not Null");
                            if (addresses != null) {
                                if (addresses.size() > 0) {

                                    if (addresses.get(0).getAdminArea() != null) {
                                        State = addresses.get(0).getAdminArea().toString();
                                    } else {
                                        Log.i(TAG, "onActivityResult:Sate Null set");
                                        State = "-";
                                    }

                                    if (addresses.get(0).getLocality() != null) {
                                        City = addresses.get(0).getLocality().toString();
                                    } else {
                                        Log.i(TAG, "onActivityResult:City Null set");
                                        City = "-";
                                    }
                                }
                            }
                            if (allstates != null) {
                                if (State != null) {
                                    for (int i = 0; i < allstates.length; i++) {
                                        if (State.equals(allstates[i].getState_name())) {
                                            try {
                                                spinner_state.setSelection(i);
                                            } catch (IndexOutOfBoundsException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                            getstates();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            Log.e("CAtched", "Impossible to connect to Geocoder", e);
                        }
                    }
                }
            }

            if (getArguments() != null) {
                if (getArguments().getString("registerDetailFb") != null) {
                    if (facebookJsonObject.has("location")) {
                        try {
                            String location = facebookJsonObject.get("location").toString();
                            JSONObject locationJson = new JSONObject(location);
                            if (locationJson.has("city")) {
                                City = locationJson.getString("city");
                            }
                            if (locationJson.has("state")) {
                                State = locationJson.getString("state");
                                if (allstates != null) {
                                    if (State != null) {
                                        for (int i = 0; i < allstates.length; i++) {
                                            if (State.equals(allstates[i].getState_name())) {
                                                try {
                                                    spinner_state.setSelection(i);
                                                } catch (IndexOutOfBoundsException e) {
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                getstates();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        if (requestCode == ReferralScanActivity.REQUEST_REGISTER_REFERRAL &&
                resultCode == ReferralScanActivity.RESPONSE_REGISTER_REFERRAL) {
            if (data.getStringExtra("content") != null) {
                actionSetThree(data.getStringExtra("content"));
            } else {
                actionSetThree(null);
            }

        }
    }

    public void stepOne() {
        setUpToolbar((AppCompatActivity) getActivity(), toolbar);
        linear_step_one.setVisibility(View.VISIBLE);
        linear_step_two.setVisibility(View.GONE);
        //linear_step_three.setVisibility(View.GONE);
        btn_register.setText("Next");

    }

    public void stepTwo() {
        setUpToolbar((AppCompatActivity) getActivity(), toolbar);
        linear_step_one.setVisibility(View.GONE);
        linear_step_two.setVisibility(View.VISIBLE);
        // linear_step_three.setVisibility(View.GONE);
        btn_register.setText("Done");
    }

   /* public void stepThree() {
        toolbar.setVisibility(View.GONE);
        linear_step_one.setVisibility(View.GONE);
        linear_step_two.setVisibility(View.GONE);
        linear_step_three.setVisibility(View.VISIBLE);
        input_mobile_scan.setText("");
        btn_register.setText("Skip");
        mScannerView.setResultHandler(Fragment_Register.this);
        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();
    }*/

    public void updateUI() {
        if (step == 1) {
            stepOne();
        } else if (step == 2) {
            stepTwo();
        } else if (step == 3) {
           /* if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 500);
            }
            stepThree();*/
        }
    }

    @Override
    public void doBack() {
        if (step == 1) {
            step = step - 1;
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                if (getActivity() instanceof Activity_Login)
                    ((Activity_Login) getActivity()).setOnBackPressedListener(null);
            }
        } else {
            step = step - 1;
            updateUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 500) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), ReferralScanActivity.class);
                    startActivityForResult(intent, ReferralScanActivity.REQUEST_REGISTER_REFERRAL);
                }
            }
        }

    }

    public void fetchMobileNumber() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            Log.i("TAG", "fetchMobileNumber: " + mPhoneNumber);
            if (mPhoneNumber != null && !mPhoneNumber.equals("")) {
                if (mPhoneNumber != null && mPhoneNumber.length() > 0 && !(mPhoneNumber.charAt(0) == '+')) {
                    mPhoneNumber = mPhoneNumber.replace("+", "");
                }
                if (mPhoneNumber.length() > 10) {
                    int countrycodelength = mPhoneNumber.length() - 10;
                    mPhoneNumber = mPhoneNumber.substring(countrycodelength, mPhoneNumber.length());
                }
                Log.i("TAG", "fetchMobileNumber opetation: " + mPhoneNumber);
                input_mobile.setText(mPhoneNumber);
                NumberExist();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}
