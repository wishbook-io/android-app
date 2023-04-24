package com.wishbook.catalog.login;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonadapters.AutoCompleteCompanyAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_suppliertypes;
import com.wishbook.catalog.commonmodels.RequestNewUser;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.login.models.CompanyList;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import org.json.JSONException;
import org.json.JSONObject;

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

public class Fragment_Register_New_Version2 extends GATrackedFragment implements CompanyTypeBottomSheetDialogFragment.CompanyTypeSelectListener {
    private View view;
    private EditText inputotp;
    private boolean termscheck = false;


    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.input_mobile)
    EditText input_mobile;

    @BindView(R.id.countrycodes)
    TextView countrycodes;
    @BindView(R.id.input_name)
    EditText input_name;
    @BindView(R.id.check_terms)
    AppCompatCheckBox check_terms;


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

    @BindView(R.id.companyautocomp)
    CustomAutoCompleteTextView companyautocomp;
    @BindView(R.id.tnc)
    TextView tnc;

    @BindView(R.id.user_type_container)
    TextInputLayout user_type_container;

    @BindView(R.id.input_company_type)
    EditText input_company_type;

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

    JSONObject companyJsonobject;


    public Fragment_Register_New_Version2() {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), CallbackLocationFetchingActivity.class);
        startActivityForResult(intent, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_new_version2, container, false);
        ButterKnife.bind(this, view);
        companies = new ArrayList<>();
        companyautocomp.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.progress_bar));

        if (Application_Singleton.RegisterName != null) {
            input_name.setText(Application_Singleton.RegisterName);
        }

        if (UserInfo.getInstance(getContext()).getGroupstatus().equals("2")) {
            user_type_container.setVisibility(View.VISIBLE);
            // newcompanycheck.setVisibility(View.GONE);
            typeList.add("Buyer");
        } else {
            user_type_container.setVisibility(View.GONE);
            typeList.add("Buyer");
            typeList.add("Supplier");
            typeList.add("None");
        }
        spinner_state.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getstates();
                return false;
            }

        });
        if (UserInfo.getInstance(getContext()).getBroker() != null && UserInfo.getInstance(getContext()).getBroker()) {
            linear_broker_user_container.setVisibility(View.VISIBLE);
            // newcompanycheck.setVisibility(View.GONE);
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
            // newcompanycheck.setVisibility(View.VISIBLE);
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


        companyautocomp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                selectedCompany = (CompanyList) parent.getItemAtPosition(position);


                input_company_type.setVisibility(View.GONE);
            }
        });
        userInfo = UserInfo.getInstance(getActivity());


        getstates();
        getgrouptype();

        check_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                termscheck = isChecked;
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
        input_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (input_mobile.getError() != null) {
                    if (!input_mobile.getError().toString().isEmpty()) {
                        input_mobile.setError(null);
                    }
                } else {
                    input_mobile.setError(null);
                }
            }
        });

        input_company_type.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_company_type.getError() != null) {
                    if (!input_company_type.getError().toString().isEmpty()) {
                        input_company_type.setError(null);
                    }
                } else {
                    input_company_type.setError(null);
                }
            }
        });

        input_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_name.getError() != null) {
                    if (!input_name.getError().toString().isEmpty()) {
                        input_name.setError(null);
                    }
                } else {
                    input_name.setError(null);
                }
            }
        });
        companyautocomp.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (companyautocomp.getError() != null) {
                    if (!companyautocomp.getError().toString().isEmpty()) {
                        companyautocomp.setError(null);
                    }
                } else {
                    companyautocomp.setError(null);
                }
            }
        });

        input_company_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    final CompanyTypeBottomSheetDialogFragment myBottomSheet;
                    if (companyJsonobject != null) {
                        myBottomSheet = CompanyTypeBottomSheetDialogFragment.newInstance(companyJsonobject.toString());
                    } else {
                        myBottomSheet = CompanyTypeBottomSheetDialogFragment.newInstance(null);
                    }
                    myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
                    myBottomSheet.setCompanyTypeSelectListener(Fragment_Register_New_Version2.this);

                }
                return false;
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
                if (allstates != null && allstates.length > 0) {
                    stateId = allstates[position].getId();
                    State = allstates[position].getState_name();
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
                } else {
                    getstates();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allcities != null && allcities.length > 0) {
                    cityId = allcities[position].getId();
                    City = allcities[position].getCity_name();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("city", cityId);
                    params.put("state", stateId);
                    if (UserInfo.getInstance(getContext()).getGroupstatus().equals("2")) {
                        // Suggestion Fil up only for salesman
                        companyListadapter = new AutoCompleteCompanyAdapter(getActivity(), R.layout.spinneritem, companies, params);
                        companyautocomp.setAdapter(companyListadapter);
                        companyautocomp.setThreshold(3);
                    } else {


                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private boolean checkValidations() {

        if (!isNotEmpty(input_name.getText().toString())) {
            input_name.requestFocus();
            input_name.setError("Name is required");
            return false;
        }

        if (!isNotEmpty(input_mobile.getText().toString())) {
            input_mobile.requestFocus();
            input_mobile.setError("Please enter mobile number");
            return false;
        }
        if (input_mobile.getText().toString().length() != 10) {
            input_mobile.requestFocus();
            input_mobile.setError("Invalid Mobile Number");
            return false;
        }

        if (!input_mobile.getText().toString().isEmpty()) {
            if (input_mobile.getText().toString().startsWith("6") ||
                    input_mobile.getText().toString().startsWith("7") ||
                    input_mobile.getText().toString().startsWith("8") ||
                    input_mobile.getText().toString().startsWith("9")) {
            } else {
                input_mobile.requestFocus();
                input_mobile.setError("Invalid Mobile Number");
                return false;
            }
        }

        String email = input_email.getText().toString();
        if (email.equals("")) {
            input_email.setText(input_mobile.getText().toString() + "@wishbook.io");
            email = input_email.getText().toString();
        }
        if (email.equals("@wishbook.io")) {
            input_email.setText("");
            input_email.setText(input_mobile.getText().toString() + "@wishbook.io");
        }
        if (!ValidationUtils.isValidEmail(input_email.getText().toString())) {
            input_email.setError("Invalid email");
            return false;
        }

        if (State != null) {
            if (State.equals("-") || State.equals("")) {
                Toast.makeText(getActivity(), "Please Select State", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (City != null) {
            if (City.equals("-") || City.equals("")) {
                Toast.makeText(getActivity(), "Please Select City", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        if (selectedCompany != null) {

        } else {
            if (!isNotEmpty(companyautocomp.getText().toString().trim())) {
                companyautocomp.requestFocus();
                companyautocomp.setError("Company name is required");
                return false;
            }

            if (!isNotEmpty(input_company_type.getText().toString())) {
                input_company_type.requestFocus();
                input_company_type.setError("Please Select company type");
                return false;
            } else {
                input_company_type.setError(null);
            }
        }


        if (group_type.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Please select group type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!termscheck) {
            Toast.makeText(getActivity(), "Should agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;

    }

    private boolean isNotEmpty(String editText) {
        return editText != null && editText.toString().trim().length() > 1;
    }

    private boolean isValidMobile(String editText) {
        if (editText.toString().trim().length() != 10)
            return false;
        else {
            if (!editText.isEmpty()) {
                if (editText.startsWith("6") ||
                        editText.startsWith("7") ||
                        editText.startsWith("8") ||
                        editText.startsWith("9")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

    @OnClick(R.id.btn_register)
    public void onRegister() {
        if (checkValidations()) {
            String meetingID = null;
            if (Application_Singleton.MEETING_ID != null) {
                meetingID = Application_Singleton.MEETING_ID;
            }
            String invite = typeList.get(inviteas.getSelectedItemPosition());
            String group_Type = ((GroupIDs) group_type.getSelectedItem()).getId();
            boolean isinviteAsGroup = false;
            String selectedConnectsupplierid = null;

            if (inviteas.getSelectedItem().equals("none")) {
                isinviteAsGroup = false;
            } else {
                isinviteAsGroup = true;
            }

            if (userInfo.getBroker() && selectedConnectSuppier != null) {
                selectedConnectsupplierid = selectedConnectSuppier.getCompany_id();
            } else {
                selectedConnectsupplierid = null;
            }

            if (selectedCompany == null) {
                // means company is new

                registerNewUser(input_mobile.getText().toString(), companyautocomp.getText().toString(),
                        companyJsonobject.toString(), input_name.getText().toString(), input_email.getText().toString(), invite, group_Type, selectedConnectsupplierid, meetingID, null, isinviteAsGroup);
            } else {
                // means company already registered
                registerNewUser(input_mobile.getText().toString(), companyautocomp.getText().toString(),
                        null, input_name.getText().toString(), input_email.getText().toString(), invite, group_Type, selectedConnectsupplierid, meetingID, selectedCompany.getId(), isinviteAsGroup);
            }
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
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });

        }
    }

    private void registerNewUser(final String mobileNumber, String companyname, String companyType, final String firstname, String email, final String Inviteas, final String GroupType, final String connectSupplier, final String meetingID, String companyId, boolean isInviteAsGroup) {
        // HashMap<String, String> params = new HashMap<>();

        RequestNewUser requestNewUser = new RequestNewUser();
        requestNewUser.setCountry(CountryId);
        requestNewUser.setPhone_number(mobileNumber);
        requestNewUser.setState(stateId);
        requestNewUser.setCity(cityId);
        requestNewUser.setEmail(email);
        /*params.put("country", CountryId);
        params.put("phone_number", mobileNumber);
        params.put("state", stateId);
        params.put("city", cityId);*/


        if (companyId != null) {
            // for old company
            requestNewUser.setCompany_id(companyId);
            //params.put("company", companyId);
            if (spinner_usertype.getSelectedItem().toString().equals("Administrator")) {
                requestNewUser.setUser_group_type("administrator");
                // params.put("user_group_type", "administrator");

            } else {
                requestNewUser.setUser_group_type("salesperson");
                // params.put("user_group_type", "salesperson");
            }
        } else {
            // for new company

            requestNewUser.setCompany_name(companyname);
            //params.put("company_name", companyname);
            try {
                if (companyJsonobject.getBoolean("manufacturer")) {
                    requestNewUser.setManufacturer(true);
                }
                if (companyJsonobject.getBoolean("wholesaler_distributor")) {
                    requestNewUser.setWholesaler_distributor(true);
                }
                if (companyJsonobject.getBoolean("retailer")) {
                    requestNewUser.setRetailer(true);
                }
                if (companyJsonobject.getBoolean("online_retailer_reseller")) {
                    requestNewUser.setOnline_retailer_reseller(true);
                }
                if (companyJsonobject.getBoolean("broker")) {
                    requestNewUser.setBroker(true);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String, String> companyHash = Application_Singleton.gson.fromJson(companyType, type);
            if(companyHash.containsKey(""))
            params.putAll(companyHash);*/
        }


        requestNewUser.setFirst_name(firstname);
        // params.put("first_name", firstname);
        if (email != null && email.equals("")) {
            email = mobileNumber + "@wishbook.io";
            input_email.setText(email);
        }

        requestNewUser.setEmail(email);
        //params.put("email", email);

        if (isInviteAsGroup) {
            requestNewUser.setInvite_group_type(GroupType);
            requestNewUser.setInvite_as(Inviteas);
           /* params.put("invite_group_type", GroupType);
            params.put("invite_as", Inviteas);*/
        }


        if (userInfo.getBroker()) {
            if (connectSupplier != null) {
                requestNewUser.setConnect_supplier(connectSupplier);
                //params.put("connect_supplier", connectSupplier);
            }
        }

        if (meetingID != null) {
            // when salesman create a new buyer from meeting,than set buyer as retailer and connect with that meeting
            requestNewUser.setMeeting(meetingID);
            //params.put("meeting", meetingID);
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.AUTHENTICATION, (Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestNewUser), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                new MaterialDialog.Builder(getActivity())
                        .title("Success!")
                        .content(firstname + " is successfully registered on Wishbook. User needs to login and verify the otp.")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                // sendUserDetails(mobile,CountryId,password);
                                dialog.dismiss();
                                getActivity().setResult(Application_Singleton.ADD_NEW_BUYER_RESPONSE_CODE);
                                getActivity().finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

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

                            if (addresses.size() > 0) {

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
        }
    }


    @Override
    public void onCheck(String check) {
        StringBuffer s = new StringBuffer();
        try {
            companyJsonobject = new JSONObject(check);
            if (companyJsonobject.getBoolean("manufacturer")) {
                s.append("Manufacturer, ");
            }
            if (companyJsonobject.getBoolean("wholesaler_distributor")) {
                s.append("Wholesaler/Distributor, ");
            }
            if (companyJsonobject.getBoolean("retailer")) {
                s.append("Retailer, ");
            }
            if (companyJsonobject.getBoolean("online_retailer_reseller")) {
                s.append("Online Reatailer/Reseller, ");
            }
            if (companyJsonobject.getBoolean("broker")) {
                s.append("Broker, ");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // remove comma
            String finalType = s.substring(0, s.length() - 2);
            input_company_type.setText(finalType);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }
}
