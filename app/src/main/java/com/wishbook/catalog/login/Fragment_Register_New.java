package com.wishbook.catalog.login;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_suppliertypes;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.commonmodels.responses.RequestAddSuppliers;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.login.adapters.AutoCompleteCompanyAdapter;
import com.wishbook.catalog.login.models.CompanyList;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;


public class Fragment_Register_New extends GATrackedFragment {
    private View view;
    private EditText inputotp;
    private boolean termscheck = false;
    private boolean approvecheck = true;
    private boolean discovercheck = true;

    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.txtinput_password)
    TextInputLayout txtinput_password;
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
    @BindView(R.id.check_approve)
    AppCompatCheckBox check_approve;
    @BindView(R.id.check_discoverable)
    AppCompatCheckBox check_discoverable;
    @BindView(R.id.newcompanycheck)
    AppCompatCheckBox newcompanycheck;
    @BindView(R.id.invite_as_container)
    LinearLayout invite_as_container;

    @BindView(R.id.btn_register)
    AppCompatButton btn_register;
    @BindView(R.id.spinner_state)
    Spinner spinner_state;
    @BindView(R.id.spinner_city)
    Spinner spinner_city;
    @BindView(R.id.spinner_usertype)
    Spinner spinner_usertype;
    @BindView(R.id.buyer_supplier)
    Spinner inviteas;
    @BindView(R.id.group_type)
    Spinner group_type;
    @BindView(R.id.donotcreate)
    LinearLayout donotcreate;
    @BindView(R.id.companyautocomp)
    AppCompatAutoCompleteTextView companyautocomp;
    @BindView(R.id.tnc)
    TextView tnc;

    @BindView(R.id.linear_broker_user_container)
    LinearLayout linear_broker_user_container;
    @BindView(R.id.btn_connect_supplier)
    TextView btn_connect_supplier;
    @BindView(R.id.flexbox_suppliers)
    FlexboxLayout flexbox_suppliers;
    @BindView(R.id.txt_supplier_label)
    TextView txt_supplier_label;

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
    public CompanyList selectedCompany = null;
    private ArrayList<String> typeList = new ArrayList<>();
    private ArrayAdapter<String> adpater;
    private String mobile = "";
    private String State = "";
    private String City = "";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Boolean gps_enabled;
    private Countries[] countries;
    private String CountryId = "1";
    private BuyersList selectedConnectSuppier;

    public Fragment_Register_New() {
    }

    public void onStop() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onResume() {
        // getStateFromLocation();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OtpReader.bind(Fragment_Register_New.this, Constants.SENDER_NUM);

        Intent intent = new Intent(getActivity(), CallbackLocationFetchingActivity.class);
        startActivityForResult(intent, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_new_2, container, false);
        ButterKnife.bind(this, view);
        companies = new ArrayList<>();
        donotcreate.setVisibility(View.GONE);
        check_approve.setVisibility(View.GONE);
        check_discoverable.setVisibility(View.GONE);
        companyListadapter = new AutoCompleteCompanyAdapter(getActivity(), R.layout.spinneritem_new, companies);
        companyautocomp.setAdapter(companyListadapter);
        companyautocomp.setThreshold(1);

        if (Application_Singleton.RegisterName != null) {
            input_name.setText(Application_Singleton.RegisterName);
        }

        if (UserInfo.getInstance(getContext()).getGroupstatus().equals("2")) {

            newcompanycheck.setVisibility(View.GONE);
            typeList.add("Buyer");
        } else {
            typeList.add("Buyer");
            typeList.add("Supplier");
            typeList.add("None");
        }

        if (UserInfo.getInstance(getContext()).getBroker() != null && UserInfo.getInstance(getContext()).getBroker()) {
            linear_broker_user_container.setVisibility(View.VISIBLE);
            newcompanycheck.setVisibility(View.GONE);
            btn_connect_supplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                    .putExtra("type", "supplier")
                                    .putExtra("toolbarTitle", "Select Supplier"),
                            Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                }
            });
        } else {
            Log.i("TAG", "onCreateView: False==>" + UserInfo.getInstance(getContext()).getBroker());
            linear_broker_user_container.setVisibility(View.GONE);
            newcompanycheck.setVisibility(View.VISIBLE);
        }


        getCountries(getActivity());
        adpater = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, typeList);
        inviteas.setAdapter(adpater);


        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, new Fragment_TermsConditions()).addToBackStack(null).commit();
                Application_Singleton.CONTAINER_TITLE = "Terms & Condition";
                Application_Singleton.CONTAINERFRAG = new Fragment_TermsConditions();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.getIntExtra("toolbarCategory", OpenContainer.TNC);
                startActivity(intent);
            }
        });
        inviteas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    group_type.setVisibility(View.GONE);
                } else {
                    group_type.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        group_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (UserInfo.getInstance(getContext()).getBroker() != null && UserInfo.getInstance(getContext()).getBroker()) {
                    if (inviteas.getSelectedItem().equals("Buyer") && ((GroupIDs) group_type.getSelectedItem()).getName().equals("Broker")) {
                        linear_broker_user_container.setVisibility(View.VISIBLE);
                        btn_connect_supplier.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                                .putExtra("type", "supplier")
                                                .putExtra("toolbarTitle", "Select Supplier"),
                                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                            }
                        });
                    } else {
                        linear_broker_user_container.setVisibility(View.GONE);
                    }

                } else {
                    linear_broker_user_container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //added by Abu

        input_password.setText("123456");
        input_confpassword.setVisibility(View.GONE);

        companyautocomp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!companyautocomp.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.
                    suggestions.clear();
                    if (companies != null) { // Check if the Original List and Constraint aren't null.
                        for (int i = 0; i < companies.size(); i++) {

                            if (companies.get(i).getName().toLowerCase().startsWith(String.valueOf(s).toLowerCase())) { // Compare item in original list if it contains constraints.
                                suggestions.add(companies.get(i)); // If TRUE add item in Suggestions.
                            }
                        }
                    }
                    companyListadapter = new AutoCompleteCompanyAdapter(getActivity(), R.layout.spinneritem, suggestions);
                    companyautocomp.setAdapter(companyListadapter);
                    companyautocomp.setThreshold(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        companyautocomp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                selectedCompany = (CompanyList) parent.getItemAtPosition(position);
            }
        });
        userInfo = UserInfo.getInstance(getActivity());

        input_password.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input_password.setTransformationMethod(new PasswordTransformationMethod());
        // input_confpassword.setTransformationMethod(new PasswordTransformationMethod());
        // input_confpassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        getstates();
        getgrouptype();

        check_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                termscheck = isChecked;
            }
        });
        check_approve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        });
        newcompanycheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNewCompany = false;
                    if (checkIfLocationSelected()) {
                        donotcreate.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Please select State and City ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectedCompany = null;
                    isNewCompany = true;
                    donotcreate.setVisibility(View.GONE);
                }
            }
        });


        //Added by abu
        input_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && input_mobile.getText().toString().length() == 10) {
                    NumberExist();
                }
            }
        });
        return view;
    }

    private void getgrouptype() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Type listOfProductObj = new TypeToken<ArrayList<GroupIDs>>() {
                }.getType();
                ArrayList<GroupIDs> buyergroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                SpinAdapter_suppliertypes spinAdapter_buyergroups = new SpinAdapter_suppliertypes(getActivity(), R.layout.spinneritem, buyergroups);
                group_type.setAdapter(spinAdapter_buyergroups);
                group_type.setSelection(1);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("sync response", error.getErrormessage());
            }
        });
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
                input_username.setText(input_mobile.getText().toString());
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
                input_mobile.setError("Phone Number Already exist");
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
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (allstates != null) {
                    try {
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem_new, R.id.spintext, allstates);
                        spinner_state.setAdapter(spinAdapter_state);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (allstates != null) {
                    if (State != null) {
                        for (int i = 0; i < allstates.length; i++) {
                            if (State.equals(allstates[i].getState_name())) {
                                spinner_state.setSelection(i);
                                break;
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

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();

                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (allcities != null) {
                                try {
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), android.R.layout.simple_spinner_item, allcities);
                                    spinner_city.setAdapter(spinAdapter_city);
                                    if (State != null && State != "") {
                                        if (City != null) {
                                            for (int i = 0; i < allcities.length; i++) {
                                                if (City.equals(allcities[i].getCity_name())) {
                                                    spinner_city.setSelection(i);
                                                    break;
                                                }

                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = allcities[position].getId();
                HashMap<String, String> params = new HashMap<>();
                params.put("city", cityId);
                params.put("state", stateId);
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "companylist", ""), params, null, false, new HttpManager.customCallBack() {
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
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.btn_register)
    public void onRegister() {
        String username = input_username.getText().toString().equals("") ? input_mobile.getText().toString() : input_username.getText().toString();
        input_username.setText(username);
        String companyname = input_name.getText().toString();
        String name = input_name.getText().toString();
        final String mobile = input_mobile.getText().toString();
        //final String mobile = input_mobile.getText().toString();
        String email = input_email.getText().toString();
        if (email.equals("")) {
            input_email.setText(input_mobile.getText().toString() + "@wishbook.io");
            email = input_email.getText().toString();
        }
        if (email.equals("@wishbook.io")) {
            input_email.setText("");
            input_email.setText(input_mobile.getText().toString() + "@wishbook.io");
        }
        String password = input_password.getText().toString();
        /*if (password.equals("")) {
            password = "123456";
        }*/
        String confPswd = input_password.getText().toString();
        //signUp(companyname, name, mobile, email, password, confPswd, username);
        String meetingID = null;
        if (Application_Singleton.MEETING_ID != null) {
            meetingID = Application_Singleton.MEETING_ID;
        }
        if (selectedCompany == null) {
            if (inviteas.getSelectedItem().equals("none")) {
                String invite = typeList.get(inviteas.getSelectedItemPosition());
                registerUser(companyname, username, mobile, email, password, confPswd, invite, meetingID);
            } else {
                if (inviteas.getSelectedItem() != null) {
                    String group_Type = ((GroupIDs) group_type.getSelectedItem()).getId();
                    String invite = typeList.get(inviteas.getSelectedItemPosition());
                    if (userInfo.getBroker() && selectedConnectSuppier != null) {
                        registerUserWithGroupType(companyname, username, mobile, email, password, confPswd, invite, group_Type, selectedConnectSuppier.getCompany_id(), meetingID);
                    } else {
                        registerUserWithGroupType(companyname, username, mobile, email, password, confPswd, invite, group_Type, null, meetingID);
                    }
                } else {

                }
            }
        } else {
            registerUser(companyname, username, mobile, email, password, confPswd, "none", meetingID);
        }
    }

    @OnClick(R.id.countrycodes)
    public void onCountryCodesClick() {
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


//changed by Abu

    private void registerUser(final String companyname, final String username, final String mobile, String email, final String password, final String confPswd, final String inviteas, final String meetingID) {

        if (checkValidations(companyname, username, mobile, password, confPswd)) {
            HashMap<String, String> params = new HashMap<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sharedPreferences.getString("gcmtoken", "");

            if (email.equals("")) {
                email = mobile + "@wishbook.io";
                input_email.setText(email);
            }
            params.put("username", username);
            params.put("phone_number", mobile);
            params.put("email", email);
            params.put("password1", password);
            params.put("password2", confPswd);
            params.put("connections_preapproved", "true");
            params.put("discovery_ok", "true");
            //registration id for new user is deliberately took empty
            params.put("registration_id", "");
            params.put("invite_as", inviteas);
            params.put("city", cityId);
            params.put("state", stateId);
            params.put("number_verified", "no");
            params.put("country", CountryId);
            params.put("company_name", companyname);
            params.put("send_user_detail", "yes");
            if (meetingID != null) {
                params.put("meeting", meetingID);
            }


            //changed by abu
            if (isNewCompany == false) {
                /*if (selectedCompany == null) {
                    Toast.makeText(getActivity(), "Please Select Valid Company", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                    params.put("usertype", "administrator");

                } else {
                    params.put("usertype", "salesperson");
                }
*/
                if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                    params.put("usertype", "administrator");

                } else {
                    params.put("usertype", "salesperson");
                }
                params.put("company", UserInfo.getInstance(getContext()).getCompany_id());
            } else {

            }
            final String finalEmail = email;
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.REGISTER_USER, params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Registered")
                            .content(username + " is Registered on wishbook. User need to login and verify the otp")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    // sendUserDetails(mobile,CountryId,password);
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            })
                            .show();

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

                    Log.d("ERRROR", error.toString());
                }
            });

        }
    }

   /* private void sendUserDetails(String mobile, String s, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("country", s);
        params.put("phone_number", mobile);
        params.put("password", password);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.NEW_REGISTER_USER, params,headers,false,new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(getActivity(),"Message is Sent",Toast.LENGTH_LONG).show();
                getActivity().finish();


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

                Log.d("ERRROR", error.toString());
            }
        });

    }*/

    private void registerUserWithGroupType(final String companyname, final String username, final String mobile, String email, final String password, final String confPswd, final String Inviteas, final String GroupType, final String connectSupplier, final String meetingID) {

        if (checkValidations(companyname, username, mobile, password, confPswd)) {
            HashMap<String, String> params = new HashMap<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sharedPreferences.getString("gcmtoken", "");

            if (email.equals("")) {
                email = mobile + "@wishbook.io";
                input_email.setText(email);
            }
            params.put("username", username);
            params.put("phone_number", mobile);
            params.put("email", email);
            params.put("password1", password);
            params.put("password2", confPswd);
            params.put("connections_preapproved", "true");
            params.put("discovery_ok", "true");

            //registration id for new user is deliberately took empty
            params.put("registration_id", "");
            params.put("city", cityId);
            params.put("state", stateId);
            params.put("number_verified", "no");
            params.put("group_type", GroupType);
            params.put("invite_as", Inviteas);
            params.put("country", CountryId);
            params.put("company_name", companyname);
            params.put("send_user_detail", "yes");

            if (userInfo.getBroker()) {
                if (connectSupplier != null) {
                    params.put("connect_supplier", connectSupplier);
                }
            }


            if (meetingID != null) {
                // when salesman create a new buyer from meeting,than set buyer as retailer and connect with that meeting
                params.put("meeting", meetingID);
            }

            //changed by abu
            if (isNewCompany == false) {
               /* if (selectedCompany == null) {
                    Toast.makeText(getActivity(), "Please Select Valid Company", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                    params.put("usertype", "administrator");

                } else {
                    params.put("usertype", "salesperson");

                }*/

                if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                    params.put("usertype", "administrator");

                } else {
                    params.put("usertype", "salesperson");
                }
                params.put("company", UserInfo.getInstance(getContext()).getCompany_id());
            } else {

            }
            final String finalEmail = email;
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.REGISTER_USER, params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Registered")
                            .content(username + " is Registered on wishbook. User need to login and verify the otp")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    getActivity().setResult(Application_Singleton.ADD_NEW_BUYER_RESPONSE_CODE);
                                    getActivity().finish();
                                }
                            })
                            .show();
                    // sendUserDetails(mobile,CountryId,password);

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });

        }
    }


    private boolean checkValidations(String companyname, String name, String mobile, String password, String confPswd) {


        if (companyname.equals("")) {
            input_name.requestFocus();
            input_name.setError("name cannot be empty !");
            return false;
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
            Log.i("TAG", "checkValidations: Password Empty");
            //  txtinput_password.setErrorEnabled(true);
            //  txtinput_password.setError("password cannot be empty !");
            input_password.requestFocus();
            input_password.setError("password cannot be empty !");
            return false;
        }

        if (password.length() < 6) {
            Log.i("TAG", "checkValidations: Password length <6 ");
            // txtinput_password.setErrorEnabled(true);
            // txtinput_password.setError("password too short !");
            input_password.requestFocus();
            input_password.setError("password too short !");
            return false;
        }

      /*  if (confPswd.equals("")) {
            input_confpassword.setError("confirm password cannot be empty !");
            return false;
        }*/

      /*  if (!password.equals(confPswd)) {
            input_confpassword.setError("password mismatch");
            return false;
        }
        */
        if (!termscheck) {
            Toast.makeText(getActivity(), "Should agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    public String getCompanyId() {

        return selectedCompany.getId();
    }

    @Override
    public void onDestroyView() {
        Application_Singleton.RegisterName = null;
        super.onDestroyView();
    }

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
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

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
        if (count < 4) {
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
                                        spinner_state.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
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

                            if(addresses.size() > 0) {

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
                            }
                            if (allstates != null) {
                                if (State != null) {
                                    for (int i = 0; i < allstates.length; i++) {
                                        if (State.equals(allstates[i].getState_name())) {
                                            spinner_state.setSelection(i);
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
        }

        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedConnectSuppier = (BuyersList) data.getSerializableExtra("buyer");
            flexbox_suppliers.removeAllViews();
            flexbox_suppliers.addView(txt_supplier_label);
            txt_supplier_label.setVisibility(View.VISIBLE);
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_suppliers, deleteableConfig);
            ArrayList<String> companies = new ArrayList<>();
            deleteableCloud.addChip(selectedConnectSuppier.getCompany_name());
            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int i, String s) {
                    txt_supplier_label.setVisibility(View.GONE);
                    btn_connect_supplier.setVisibility(View.VISIBLE);
                }
            });
            companies.add(selectedConnectSuppier.getCompany_id());
            /*if (getArguments().getString("buyerid") != null) {
                String buyerId = getArguments().getString("buyerid");
                addSuppliers(buyerId, companies);
            }*/
        }
    }

    public void addSuppliers(String buyerID, ArrayList<String> companies) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddSuppliers requestAddSuppliers = new RequestAddSuppliers();
        requestAddSuppliers.setSelling_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "add_suppliers", buyerID), new Gson().fromJson(new Gson().toJson(requestAddSuppliers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                txt_supplier_label.setVisibility(View.VISIBLE);
                btn_connect_supplier.setVisibility(View.GONE);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
