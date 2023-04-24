package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.wishbook.catalog.commonmodels.PatchOtpObject;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.commonmodels.responses.Profile;
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

public class Fragment_MyProfile extends GATrackedFragment implements OTPListener {

    public final String POPUP_OTP_TITLE = "Mobile Verification";
    public final String POPUP_OTP_TEXT = "Please enter the One Time Password (OTP) sent to your registered mobile number and authenticate the registration.";
    private View v;
    private EditText firstNameEt, lastNameEt, userNameEt, mobileEt, companyEt;
    private AppCompatButton saveProfileBttn;
    private EditText inputotp;
    private MaterialDialog progressDialog;
    private String firstName, lastName, mobileNum, companyName, userName, companyName1,email;
    private SharedPreferences pref;
    private Toolbar toolbar;
    private EditText input_company_type;
    private EditText input_deputed_to;
    private TextInputLayout deputed_containter;
    private EditText input_email;
    private TextView countrycodes;
    private Countries[] countries;
    private int CountryId = 1;
    private LinearLayout share_recieved_layout;
    private Profile profile;
    private UserInfo userinfo;

    EditText company_profile_name;
    Spinner company_profile_spinner_city;
    Spinner company_profile_spinner_state;
    RadioGroup company_profile_pushdownstream_group;
    private Response_States[] responseStates;
    private Response_Cities[] responseCities;

    public Fragment_MyProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_MyProfile.this, Constants.SENDER_NUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_myprofile_companyprofile, ga_container, true);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("Profile");
        countrycodes = (TextView) v.findViewById(R.id.countrycodes);
        share_recieved_layout=v.findViewById(R.id.share_recieved_layout);
        if(UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")){
                share_recieved_layout.setVisibility(View.GONE);
        }
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        progressDialog = StaticFunctions.showProgress(getActivity());

        getCountries(getActivity());
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        firstNameEt = (EditText) v.findViewById(R.id.input_firstname);
        lastNameEt = (EditText) v.findViewById(R.id.input_lastname);
        userNameEt = (EditText) v.findViewById(R.id.input_username);
        mobileEt = (EditText) v.findViewById(R.id.input_mobile);
        companyEt = (EditText) v.findViewById(R.id.input_company);
        input_company_type = (EditText) v.findViewById(R.id.input_company_type);
        input_deputed_to = (EditText) v.findViewById(R.id.input_deputed_to);
        deputed_containter = (TextInputLayout) v.findViewById(R.id.deputed_containter);
        initMyCompanyField(v);
        input_email = (EditText) v.findViewById(R.id.input_email);
        firstNameEt.setText(pref.getString("firstName", ""));
        lastNameEt.setText(pref.getString("lastName", ""));
        userNameEt.setText(pref.getString("userName", ""));
        String phoneNumber1 = pref.getString("mobile", "");
        if (phoneNumber1.length() >= 10) {
            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
            mobileEt.setText(phoneNumbertrim);
        }
        settingCompanyLocalData(pref);
        getStates();
        getCompanyData(v);
        userinfo = UserInfo.getInstance(getContext());
        if (userinfo.getGroupstatus().equals("2")) {
            mobileEt.setClickable(false);
            mobileEt.setEnabled(false);
        }

        if (userinfo.getDeputed_to() != null && !userinfo.getDeputed_to().equals("") && !userinfo.getDeputed_to_name().equals("")) {
            input_deputed_to.setText(userinfo.getDeputed_to_name());
            deputed_containter.setVisibility(View.VISIBLE);
        }

        companyEt.setText(pref.getString("companyname", ""));
        input_email.setText(pref.getString("email", ""));
        input_company_type.setText(pref.getString("company_type", ""));
        saveProfileBttn = (AppCompatButton) v.findViewById(R.id.btn_save_profile);
        saveProfileBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstNameEt.getText().toString();
                lastName = lastNameEt.getText().toString();
                userName = userNameEt.getText().toString();
                mobileNum = mobileEt.getText().toString();
                companyName = companyEt.getText().toString();
                companyName1 = company_profile_name.getText().toString();
                email=input_email.getText().toString();
                if(email.isEmpty()){
                    input_email.setError("Please enter email-id");
                    input_email.requestFocus();
                    return;
                }

                if(!input_email.getText().toString().isEmpty()){
                    if (!ValidationUtils.isValidEmail(input_email.getText().toString())) {
                        input_email.setError("Invalid email");
                        return;
                    }
                }
                if (mobileNum.length() == 10) {
                    if (profile != null) {
                        if (mobileNum.equals(profile.getUserprofile().getPhone_number()) || mobileNum.equals("+91" + profile.getUserprofile().getPhone_number())) {
                            if (company_profile_spinner_state.getSelectedItem() != null && company_profile_spinner_city.getSelectedItem() != null && ((Response_States) company_profile_spinner_state.getSelectedItem()).getId() != null && ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId() != null) {
                                saveProfile(firstName, lastName, userName, mobileNum, companyName, companyName1, ((Response_States) company_profile_spinner_state.getSelectedItem()).getId(), ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId());
                            } else {
                                Toast.makeText(getActivity(), "Please select state and city", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            registerOTP(mobileNum);
                        }
                    }
                } else if(mobileNum.length()>0){
                    mobileEt.setError("Number should be of 10 digits");
                }else{
                    mobileEt.setError("This field cannot be empty ");
                }

            }
        });
        toolbar.setVisibility(View.GONE);
        countrycodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(getActivity(), countrycodes);
                if (countries != null && countries.length > 0) {

                    for (int i = 0; i < countries.length; i++) {
                        String country = countries[i].getName();
                        popup.getMenu().add(0, i, 0, country);

                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            CountryId = Integer.parseInt(countries[item.getItemId()].getId());
                            Log.v("country selected", "" + CountryId);
                            String country = countries[item.getItemId()].getName();
                            countrycodes.setText(country);
                            return true;
                        }
                    });
                    popup.show();


                }

            }
        });
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
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
                    firstNameEt.setText(profile.getFirst_name());
                    lastNameEt.setText(profile.getLast_name());
                    userNameEt.setText(profile.getUsername());
                    String phoneNumber2 = profile.getUserprofile().getPhone_number();
                    if (phoneNumber2.length() >= 10) {
                        String phoneNumbertrim = phoneNumber2.substring(phoneNumber2.length() - 10, phoneNumber2.length());
                        mobileEt.setText(phoneNumbertrim);
                    }

                    if (profile.getCompanyuser().getCompanyname() != null) {
                        companyEt.setText(profile.getCompanyuser().getCompanyname());
                    }

                    input_email.setText(profile.getEmail());
                    input_company_type.setText(profile.getCompanyuser().getCompany_type());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Snackbar.make(v, getResources().getString(R.string.error_null_cursor), Snackbar.LENGTH_LONG).show();
                }


//                pref.edit().putString("firstName", firstName).apply();
//                pref.edit().putString("lastName", lastName).apply();
//                pref.edit().putString("userName", userName).apply();
//                pref.edit().putString("mobile", mobileNum).apply();
//                pref.edit().putString("email", input_email.getText().toString()).apply();

                // pref.edit().putString("companyname", companyName).apply();
                //  Snackbar.make(v, "profile updated successfully!", Snackbar.LENGTH_LONG).show();


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.logger(error.getErrormessage());
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
        return v;
    }

    private void saveProfile(final String firstName, final String lastName,
                             final String userName, final String mobileNum, final String companyName, final String companyName1, final String state, final String city) {
        if (areFieldsValid(firstName, lastName, userName, mobileNum, companyName, companyName1, state, city)) {
            JsonObject jsonObject;
            if (!mobileNum.equals(profile.getUserprofile().getPhone_number()) || !mobileNum.equals("+91" + profile.getUserprofile().getPhone_number())) {
                profile.setFirst_name(firstName);
                profile.setLast_name(lastName);
                profile.setUsername(userName);
                if(email!=null && !email.isEmpty())
                     profile.setEmail(email);
                profile.getCompanyuser().setCompany(companyName);
                profile.getUserprofile().setPhone_number(mobileNum);
                profile.getUserprofile().setCountry(String.valueOf(CountryId));
                profile.getCompanyuser().setCompany(companyName);
                jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
            } else {
                profile.setFirst_name(firstName);
                profile.setLast_name(lastName);
                profile.setUsername(userName);
                if(email!=null && !email.isEmpty())
                    profile.setEmail(email);
                profile.getCompanyuser().setCompany(companyName);
                profile.getUserprofile().setPhone_number(mobileNum);
                profile.getUserprofile().setCountry(String.valueOf(CountryId));
                jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
            }
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    pref.edit().putString("firstName", firstName).apply();
                    pref.edit().putString("lastName", lastName).apply();
                    pref.edit().putString("userName", userName).apply();
                    pref.edit().putString("mobile", mobileNum).apply();
                    pref.edit().putString("email", email).apply();
                    int selectedRadioButton;
                    if (company_profile_pushdownstream_group.getCheckedRadioButtonId() == R.id.radio_yes) {
                        selectedRadioButton = 1;
                    } else {
                        selectedRadioButton = 0;
                    }

                    saveCompanyProfile(1, companyName1, state, city, selectedRadioButton);

                    // pref.edit().putString("companyname", companyName).apply();
                    Snackbar.make(v, "Profile updated successfully!", Snackbar.LENGTH_LONG).show();


                }

                @Override
                public void onResponseFailed(ErrorString error) {
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

    private boolean areFieldsValid(String firstName, String lastName,
                                   String userName, String mobileNum, String companyName, String companyName1, String state, String city) {

        if (firstName.equals("")) {
            firstNameEt.setError("Firstname cannot be empty!");
            firstNameEt.requestFocus();
            return false;
        }

        if (lastName.equals("")) {
            lastNameEt.setError("Lastname cannot be empty!");
            lastNameEt.requestFocus();
            return false;
        }

        if (userName.equals("")) {
            userNameEt.setError("Username cannot be empty!");
            userNameEt.requestFocus();
            return false;
        }
        if (companyName1.equals("")) {
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

        if (mobileNum.equals("")) {
            mobileEt.setError("Mobile number cannot be empty!");
            mobileEt.requestFocus();
            return false;
        }

        if (companyName.equals("")) {
            companyEt.setError("Company Name cannot be empty!");
            companyEt.requestFocus();
            return false;
        }

        if (!ValidationUtils.validCellPhone(mobileNum) | mobileNum.length() < 10 | mobileNum.length() > 10) {
            mobileEt.setError("invalid mobile number");
            mobileEt.requestFocus();
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
                inputotp.setText(match);
            }
        }
    }


    private void validateOTP(final String mobile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        alert.setTitle(POPUP_OTP_TITLE);
        alert.setMessage(POPUP_OTP_TEXT);
        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_input, (ViewGroup) getView(), false);
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
            public void onShow(final DialogInterface dialog) {
                Button submit_but = alertModal.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg_but = alertModal.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        alertModal.dismiss();
                        registerOTP(mobile);
                    }
                });

                submit_but.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(final View v) {
                                                     /* HashMap<String, String> params = new HashMap<>();
                                                      params.put("phone_number", mobile);
                                                      params.put("country","1");
                                                      params.put("otp", inputotp.getText().toString());*/

                                                      JsonObject jsonObject;
                                                      PatchOtpObject otp = new PatchOtpObject();
                                                      OtpClass obj = new OtpClass(mobile, CountryId, inputotp.getText().toString());
                                                      otp.setUserprofile(obj);
                                                      jsonObject = new Gson().fromJson(new Gson().toJson(otp), JsonObject.class);
                                                      HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

                                                      HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
                                                          @Override
                                                          public void onCacheResponse(String response) {

                                                          }

                                                          @Override
                                                          public void onServerResponse(String response, boolean dataUpdated) {
                                                              alertModal.dismiss();
                                                              saveProfile(firstName, lastName, userName, mobileNum, companyName,companyName1,((Response_States) company_profile_spinner_state.getSelectedItem()).getId(), ((Response_Cities) company_profile_spinner_city.getSelectedItem()).getId());
                                                          }

                                                          @Override
                                                          public void onResponseFailed(ErrorString error) {
                                                              StaticFunctions.showResponseFailedDialog(error);
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

    //when I Change the Number
    private void registerOTP(final String mobile) {

        JsonObject jsonObject;
        PatchOtpObject otp = new PatchOtpObject();
        OtpClass obj = new OtpClass(mobile, CountryId);
        otp.setUserprofile(obj);
        jsonObject = new Gson().fromJson(new Gson().toJson(otp), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
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

    private void getCompanyData(final View view) {
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
                settingServerData(companyProfile);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Snackbar.make(view, "Request failed!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void initMyCompanyField(View view) {
        company_profile_name = view.findViewById(R.id.company_profile_name);
        company_profile_spinner_city = view.findViewById(R.id.company_profile_spinner_city);
        company_profile_spinner_state = view.findViewById(R.id.company_profile_spinner_state);
        company_profile_pushdownstream_group = view.findViewById(R.id.company_profile_pushdownstream_group);

    }

    private void stateSelection(Response_States[] responseStates) {
        for (int i = 0; i < responseStates.length; i++) {
            if (pref.getInt("company_profile_state", 0) != 0 && responseStates[i].getId().equals(String.valueOf(pref.getInt("company_profile_state", 0)))) {
                company_profile_spinner_state.setSelection(i);
            }
        }
    }

    private void settingCompanyLocalData(SharedPreferences pref) {
        company_profile_name.setText(pref.getString("companyname", ""));
        if (pref.getString("company_profile_pushdownstream", "0").equals("0")) {
            company_profile_pushdownstream_group.check(R.id.radio_no);
        } else {
            company_profile_pushdownstream_group.check(R.id.radio_yes);
        }


    }

    private void settingServerData(CompanyProfile companyProfile) {
        company_profile_name.setText(companyProfile.getName());
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
                        company_profile_spinner_state.setAdapter(spinAdapter_state);

                        //setting local state
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

    public void saveCompanyProfile(final int selectedCountryId, final String companyName, final String state, final String city, final int radioButton) {
        JsonObject jsonObject;
        CompanyProfile profile = new CompanyProfile();
        profile.setCountry(String.valueOf(selectedCountryId));
        profile.setName(companyName);
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
                pref.edit().putString("companyname", companyName).apply();
                pref.edit().putString("company_profile_pushdownstream", String.valueOf(radioButton)).apply();
                pref.edit().putString("countrycode", String.valueOf(selectedCountryId)).apply();
                pref.edit().putInt("company_profile_city", Integer.parseInt(city)).apply();
                pref.edit().putInt("company_profile_state", Integer.parseInt(state)).apply();
                Snackbar.make(v, "Profile updated successfully!", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                Snackbar.make(v, "Request failed!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

}
