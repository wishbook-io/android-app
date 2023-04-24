package com.wishbook.catalog.login;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonadapters.AutoCompleteCompanyAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.RequestNewUser;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.CompanyTypePatch;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.models.CompanyList;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;
import com.wishbook.catalog.login.models.Response_User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, CompanyTypeBottomSheetDialogFragment.CompanyTypeSelectListener {


    /**
     * Bind View Start
     */


    @BindView(R.id.appbar)
    Toolbar toolbar;

    @BindView(R.id.btn_sign_in)
    SignInButton google_sign_in;

    @BindView(R.id.login_facebookLoginButton)
    LoginButton fbLoginButton;

    @BindView(R.id.companyautocomp)
    CustomAutoCompleteTextView companyautocomp;

    @BindView(R.id.input_name)
    EditText input_name;


    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    @BindView(R.id.spinner_city)
    Spinner spinner_city;

    @BindView(R.id.linear_city)
    LinearLayout linear_city;

    @BindView(R.id.linear_state)
    LinearLayout linear_state;

    @BindView(R.id.check_terms)
    AppCompatCheckBox check_terms;

    @BindView(R.id.input_email)
    EditText input_email;

    @BindView(R.id.input_company_type)
    EditText input_company_type;

    @BindView(R.id.txt_scan_dialog)
    TextView txt_scan_dialog;

    @BindView(R.id.txt_input_companytype)
    TextInputLayout txt_input_companytype;

    @BindView(R.id.tnc)
    TextView tnc;

    @BindView(R.id.txt_header_text)
    TextView txt_header_text;

    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;


    /**
     * Variable Intialize Start
     */
    private Context mContext;
    private static final int GOOGLE_SIGN_IN = 7000;
    private CallbackManager callbackManager;
    //private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private MaterialDialog materialDialogLoading;

    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private Countries[] countries;
    private String stateId = "";
    private String cityId = "";
    private String CountryId = "1";
    private String State = "";
    private String City = "";
    ClickableSpan clickableSpan;
    private UserInfo userInfo;
    private boolean termscheck = false;
    JSONObject facebookJsonObject;
    JSONObject companyJsonobject;
    private SharedPreferences pref;
    private AutoCompleteCompanyAdapter companyListadapter;
    private static String TAG = "Ac_Register";
    private String CompanyGroupID;
    private boolean isOldCompany = false;
    public CompanyList selectedCompany;
    private ArrayList<CompanyList> companies;

    CompanyProfile companyProfile;
    private boolean isRequiredResetGuestUser = false;
    private final static int LOCATION_PERMISSION_REQUEST = 10000;

    private String from ="";

    boolean isGoogleSigninDone;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.fragment_signup_version_3);
        mContext = Activity_Register.this;




        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Registration_screen");
        HashMap<String, String> prop = new HashMap<>();
        if(getIntent().getStringExtra("from")!=null) {
            from = getIntent().getStringExtra("from");
        }
        prop.put("source", from);
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);



        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(Activity_Register.class.getSimpleName(),mContext);
        ButterKnife.bind(this);
        setUpToolbar(mContext, toolbar);


        companyJsonobject = new JSONObject();
        initView();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(mContext, CallbackLocationFetchingActivity.class);
            startActivityForResult(intent, 1);
        } else {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            ActivityCompat.requestPermissions((Activity) mContext, permissions, LOCATION_PERMISSION_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mContext, CallbackLocationFetchingActivity.class);
                    startActivityForResult(intent, 1);
                }


            }
        }
    }

    private void initFacebook() {
        try{
            // According to new facebook lib it will be auto initialize
            FacebookSdk.sdkInitialize(mContext);
        } catch (Exception e){
            e.printStackTrace();
        }
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                if (object != null) {
                                    try {
                                        Log.v("jsonObject", object.toString());
                                        if (object.has("email")) {
                                            fillData(object.toString());
                                        } else {
                                            Log.v("TAG", "Not able to find your email id.");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,first_name,last_name,about,address");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v("TAG", " on cancel login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("TAG", " on error login");
            }
        });


        }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result!=null) {
            isGoogleSigninDone = true;
            Log.d("TAG", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.e("TAG", "display name: " + acct.getDisplayName());

                String personPhotoUrl = null;
                String personName = null;
                String email = null;
                if (acct.getDisplayName() != null) {
                    personName = acct.getDisplayName();
                }
                if (acct.getPhotoUrl() != null) {
                    personPhotoUrl = acct.getPhotoUrl().toString();
                }

                if (acct.getEmail() != null) {
                    email = acct.getEmail();
                }
                Log.e("TAG", "Name: " + personName + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                if (email != null) {
                    Bundle bundle = new Bundle();
                    try {
                        JSONObject object = new JSONObject();
                        object.put("name", acct.getDisplayName());
                        object.put("email", acct.getEmail());
                        fillData(object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "onActivityResult: Login Request Code" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Double latitude = bundle.getDouble("latitude");
                Double longitude = bundle.getDouble("longitude");
                if (latitude != null && longitude != null) {
                    Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
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


        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


        if (requestCode == ReferralScanActivity.REQUEST_REGISTER_REFERRAL &&
                resultCode == ReferralScanActivity.RESPONSE_REGISTER_REFERRAL) {
            if (data.getStringExtra("content") != null) {
                if (UserInfo.getInstance(mContext).getCompany_id() != null && UserInfo.getInstance(mContext).getCompany_id().equals("1")) {
                    registerNewUser1(companyautocomp.getText().toString(), input_name.getText().toString(), null, null);
                } else {
                    patchCompany(companyJsonobject, data.getStringExtra("content"));
                }
            } else {

                if (UserInfo.getInstance(mContext).getCompany_id() != null && UserInfo.getInstance(mContext).getCompany_id().equals("1")) {
                    registerNewUser1(companyautocomp.getText().toString(), input_name.getText().toString(), null, null);
                } else {
                    patchCompany(companyJsonobject, data.getStringExtra("content"));
                }
            }

        }


    }


    public void initView() {
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (getIntent().getBooleanExtra("fromGuestSession", false)) {
            txt_header_text.setText(String.format(getResources().getString(R.string.after_20_session), Application_Singleton.MaxGuestUserSession));
        }
        companies = new ArrayList<>();
       // initFacebook();
        getCountries(mContext);
        getstates();
        getCompanyData();

       /* mGoogleSignInClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        google_sign_in.setSize(SignInButton.SIZE_STANDARD);
        google_sign_in.setColorScheme(SignInButton.COLOR_LIGHT);
        google_sign_in.setScopes(gso.getScopeArray());
        setGooglePlusButtonText(google_sign_in, "Register with Google");

        userInfo = UserInfo.getInstance(mContext);

        companyautocomp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                if (position != 0) {
                    selectedCompany = (CompanyList) parent.getItemAtPosition(position);
                    KeyboardUtils.hideKeyboard((Activity) mContext);
                    showCompanyDialog(mContext, selectedCompany.getName());
                }

            }
        });


        companyautocomp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //SPANNABLE STRING
        SpannableString ss = new SpannableString(getString(R.string.register_scan_label));
        String s = getString(R.string.register_scan_label);
        int start_link = s.indexOf("enter");
        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (!checkValidations()) {
                    new MaterialDialog.Builder(mContext)
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
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, 500);
                    } else {
                        Intent intent = new Intent(mContext, ReferralScanActivity.class);
                        startActivityForResult(intent, ReferralScanActivity.REQUEST_REGISTER_REFERRAL);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.color_primary));
            }
        };
        ss.setSpan(clickableSpan, start_link, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_scan_dialog.setText(ss);
        txt_scan_dialog.setMovementMethod(LinkMovementMethod.getInstance());
        txt_scan_dialog.setClickable(true);


        check_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                termscheck = isChecked;
            }
        });

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE = "Terms & Condition";
                Application_Singleton.CONTAINERFRAG = new Fragment_TermsConditions();
                Intent intent = new Intent(mContext, OpenContainer.class);
                intent.getIntExtra("toolbarCategory", OpenContainer.TNC);
                startActivity(intent);
            }
        });
        input_company_type.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(input_company_type.getError()!=null){
                    if(!input_company_type.getError().toString().isEmpty()) {
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
                if(input_name.getError()!=null){
                    if(!input_name.getError().toString().isEmpty()) {
                        input_name.setError(null);
                    }
                } else {
                    input_name.setError(null);
                }
                if(s.length()>50){
                    input_name.setError(getResources().getString(R.string.validate_name));
                }
            }
        });

        companyautocomp.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(companyautocomp.getError()!=null){
                    if(!companyautocomp.getError().toString().isEmpty()) {
                        companyautocomp.setError(null);
                    }
                } else {
                    companyautocomp.setError(null);
                }
                if(s.length()>50){
                    input_name.setError(getResources().getString(R.string.validate_name));
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
                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                    myBottomSheet.setCompanyTypeSelectListener(Activity_Register.this);

                }
                return false;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidations()) {
                    if (selectedCompany == null) {
                        if (isRequiredResetGuestUser) {

                            registerNewUser1(companyautocomp.getText().toString(), input_name.getText().toString(), null, null);
                        } else {
                            if (UserInfo.getInstance(mContext).getCompany_id() != null && UserInfo.getInstance(mContext).getCompany_id().equals("1")) {
                                registerNewUser1(companyautocomp.getText().toString(), input_name.getText().toString(), null, null);
                            } else {
                                patchCompany(companyJsonobject, null);
                            }
                        }


                    } else {
                        Log.i(TAG, "onClick: Call Api");
                        if (companyProfile != null) {
                            if (!companyProfile.getCompany_type_filled() && companyProfile.is_profile_set()) {
                                patchCompany(companyJsonobject, null); // already profile set case
                            } else {
                                callChangeCompany(stateId, cityId, selectedCompany.getId(), input_name.getText().toString());
                            }
                        } else {
                            callChangeCompany(stateId, cityId, selectedCompany.getId(), input_name.getText().toString());
                        }
                    }
                }
            }
        });

        if (getIntent().getBooleanExtra("fromResetCompany", false)) {
            isRequiredResetGuestUser = true;
        }

    }

    public void setUpToolbar(final Context context, Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Complete Registration");
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.color_primary));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.color_primary), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    public void fillData(String json) {
        try {
            String userProfileString = json;
            facebookJsonObject = new JSONObject(userProfileString);


            if (facebookJsonObject.has("name")) {
                String name = facebookJsonObject.getString("name");
                Log.e(TAG, "fillData: Name"+name );
                input_name.setText(facebookJsonObject.getString("name"));
                   /* String[] parts = name.split("\\s+");
                    String firstname = "";
                    String lastname = "";
                    for (int i = 0; i < parts.length; i++) {
                        if (i == 0) {
                            firstname = parts[i];
                        } else {
                            lastname += parts[i];
                        }
                    }
                    input_first_name.setText(firstname);
                    input_last_name.setText(lastname);*/
            }


            if (facebookJsonObject.has("email")) {
                input_email.setText(facebookJsonObject.getString("email"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidations() {


        if (!isNotEmpty(companyautocomp.getText().toString().trim())) {
            companyautocomp.requestFocus();
            companyautocomp.setError("Company name is required");
            return false;
        }
        if(companyautocomp.getText().toString().length()>50){
            companyautocomp.requestFocus();
            companyautocomp.setError(getResources().getString(R.string.validate_name));
            return false;
        }


        if (!isOldCompany) {
            if (!isNotEmpty(input_company_type.getText().toString())) {
                input_company_type.requestFocus();
                input_company_type.setError("Please Select company type");
                return false;
            }
        } else {
            // company type not filled case

            if (txt_input_companytype.getVisibility() == View.VISIBLE) {
                if (!isNotEmpty(input_company_type.getText().toString())) {
                    input_company_type.requestFocus();
                    input_company_type.setError("Please Select company type");
                    return false;
                }
                if (companyJsonobject == null) {
                    input_company_type.requestFocus();
                    input_company_type.setError("Please Select company type");
                    return false;
                }
            }
        }

        if (State != null) {
            if (State.equals("-") || State.equals("")) {
                Toast.makeText(mContext, "Please Select State", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (City != null) {
            if (City.equals("-") || City.equals("")) {
                Toast.makeText(mContext, "Please Select City", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!isNotEmpty(input_name.getText().toString())) {
            input_name.requestFocus();
            input_name.setError("Name is required");
            return false;
        }

        if(!input_email.getText().toString().isEmpty()){
            if (!ValidationUtils.isValidEmail(input_email.getText().toString())) {
                input_email.setError("Invalid email");
                return false;
            }
        }



        if(input_name.getText().toString().length()>50){
            input_name.requestFocus();
            input_name.setError(getResources().getString(R.string.validate_name));
            return false;
        }


        if (!termscheck) {
            Toast.makeText(mContext, "Should agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;

    }

    private boolean isNotEmpty(String editText) {
        return editText != null && editText.toString().trim().length() > 1;
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

    private void getstates() {
        Log.i(TAG, "getstates: Called");
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GET, URLConstants.companyUrl(mContext, "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.i(TAG, "getstates: Called Set Adapter");
                allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (mContext != null) {
                    if (allstates != null) {
                        linear_state.setVisibility(View.VISIBLE);
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State((Activity) mContext, R.layout.spinneritem, R.id.spintext, allstates);
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
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    State = allstates[position].getState_name();
                    Log.i(TAG, "getCity: Called");
                    HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GET, URLConstants.companyUrl(mContext, "city", stateId), null, null, true, new HttpManager.customCallBack() {
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
                                if (mContext != null) {
                                    linear_city.setVisibility(View.VISIBLE);
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City((Activity) mContext, R.layout.spinneritem, allcities);
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
                try {
                    cityId = allcities[position].getId();
                    City = allcities[position].getCity_name();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("city", cityId);
                    params.put("state", stateId);
                    companyListadapter = new AutoCompleteCompanyAdapter(mContext, R.layout.spinneritem, companies, params);
                    companyautocomp.setAdapter(companyListadapter);
                    companyautocomp.setThreshold(3);
                    companyautocomp.setLoadingIndicator(
                            (android.widget.ProgressBar) findViewById(R.id.progress_bar));
                } catch (Exception e) {
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @OnClick(R.id.btn_sign_in)
    public void googleSignin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void checkEmailExist() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", input_email.getText().toString());
        params.put("field", "is_exist");
        showProgress();

        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.USERS, params, null, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (error != null && error.getErrormessage() != null && mContext != null) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
                input_email.setError("Email-Id Already exist");
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Search all the views inside SignInButton for TextView
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            // if the view is instance of TextView then change the text SignInButton
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


    public void showProgress() {
        /*if(relativeProgress!=null){
            relativeProgress.setVisibility(View.VISIBLE);
        }*/
    }

    public void hideProgress() {
       /* if(relativeProgress !=null) {
            relativeProgress.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onCheck(String check) {
        Log.i(TAG, "onCheck: Called" + check);
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

    private void getCompanyData() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //parsing response data
                companyProfile = new Gson().fromJson(response, CompanyProfile.class);
                if (companyProfile != null) {
                    if (companyProfile.getCompany_group_flag() != null)
                        CompanyGroupID = companyProfile.getCompany_group_flag().getId();
                    if (companyProfile.getCompany_type_filled()) {
                        if (isRequiredResetGuestUser) {
                            if (UserInfo.getInstance(mContext).getFirstName() != null) {
                                input_name.setText(UserInfo.getInstance(mContext).getFirstName().toString());
                            }
                            if (companyProfile.getState_name() != null && !companyProfile.getState_name().isEmpty()) {
                                State = companyProfile.getState_name();
                                if (companyProfile.getCity_name() != null && !companyProfile.getCity_name().isEmpty()) {
                                    City = companyProfile.getCity_name();
                                }
                                getstates();
                            }
                        }
                    }
                    if (!companyProfile.getCompany_type_filled() && companyProfile.is_profile_set()) {
                        if (companyProfile.is_profile_set()) {
                            isOldCompany = true;
                            companyautocomp.setEnabled(false);
                            companyautocomp.setText(companyProfile.getName());
                            txt_scan_dialog.setVisibility(View.GONE);
                            CompanyList companyList = new CompanyList(companyProfile.getId(), companyProfile.getName());
                            selectedCompany = companyList;
                            if (companyProfile.getState_name() != null && !companyProfile.getState_name().isEmpty()) {
                                State = companyProfile.getState_name();
                                if (companyProfile.getCity_name() != null && !companyProfile.getCity_name().isEmpty()) {
                                    City = companyProfile.getCity_name();
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
                            }


                        }
                    }

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Toast.makeText(mContext, "Request Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void patchCompany(final JSONObject jsonObject, final String referral) {
        try {
            if (CompanyGroupID != null) {
                CompanyTypePatch patchcompanytype = new CompanyTypePatch(jsonObject.getBoolean("manufacturer"), jsonObject.getBoolean("wholesaler_distributor"), jsonObject.getBoolean("retailer"), jsonObject.getBoolean("online_retailer_reseller"), jsonObject.getBoolean("broker"));
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
                HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "companytype", CompanyGroupID), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchcompanytype), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            if (jsonObject.getBoolean("manufacturer") && !jsonObject.getBoolean("online_retailer_reseller") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker")) {
                                UserInfo.getInstance(mContext).setCompany_type("manufacturer");
                                UserInfo.getInstance(mContext).setCompanyType("seller");
                            } else if (!jsonObject.getBoolean("manufacturer") && !jsonObject.getBoolean("online_retailer_reseller") && jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker") ||
                                    !jsonObject.getBoolean("manufacturer") && jsonObject.getBoolean("online_retailer_reseller") && !jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker") ||
                                    !jsonObject.getBoolean("manufacturer") && jsonObject.getBoolean("online_retailer_reseller") && jsonObject.getBoolean("retailer") && !jsonObject.getBoolean("wholesaler_distributor") && !jsonObject.getBoolean("broker")) {
                                UserInfo.getInstance(mContext).setCompanyType("buyer");
                            } else {
                                UserInfo.getInstance(mContext).setCompany_type("nonmanufacturer");
                                UserInfo.getInstance(mContext).setCompanyType("all");
                            }
                        } catch (Exception e) {

                        }
                        UserInfo.getInstance(mContext).setGuest(false);
                        saveProfile(stateId, cityId, companyautocomp.getText().toString(), input_name.getText().toString(), referral);

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void registerNewUser1(final String companyname, final String firstname, String companyId, String referral) {
        if (isRequiredResetGuestUser) {
            resetGuestUser(companyname, firstname, companyId, referral);
        } else {
            registerNewUser(companyname, firstname, companyId, referral);
        }

    }

    private void registerNewUser(final String companyname, final String firstname, String companyId, String referral) {
        RequestNewUser requestNewUser = new RequestNewUser();
        requestNewUser.setCountry(CountryId);
        requestNewUser.setIs_guest_user_registration(true);
        requestNewUser.setPhone_number(UserInfo.getInstance(this).getMobile());
        requestNewUser.setState(stateId);
        requestNewUser.setCity(cityId);

        if (referral != null && !referral.isEmpty()) {
            requestNewUser.setRefered_by(referral);
        }

        //Crash occurs here as companyId is not null, the attributes of companyJsonobject are not getting set2
        if (companyId != null) {
            // for old company
            requestNewUser.setCompany_id(companyId);
            requestNewUser.setUser_group_type("administrator");
        } else {
            // for new company
            requestNewUser.setCompany_name(companyname);
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
        }
        String[] splited = firstname.split(" ");
        if(splited.length == 1){
            requestNewUser.setFirst_name(splited[0]);
        } else if(splited.length == 2){
            requestNewUser.setFirst_name(splited[0]);
            requestNewUser.setLast_name(splited[1]);
        } else if(splited.length>2){
            requestNewUser.setFirst_name(splited[0]);
            ArrayList<String> temp = new ArrayList<>() ;
            String lastName="";
            for (int i=1;i<splited.length;i++){
                temp.add(splited[i]);
            }
            lastName = StaticFunctions.ArrayListToString(temp," ");
            requestNewUser.setLast_name(lastName);
        }



        if (!input_email.getText().toString().isEmpty()) {
            requestNewUser.setEmail(input_email.getText().toString());
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Register.this);
        HttpManager.getInstance(Activity_Register.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.AUTHENTICATION, (Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestNewUser), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (companyJsonobject.getBoolean("manufacturer")
                            && !companyJsonobject.getBoolean("online_retailer_reseller") &&
                            !companyJsonobject.getBoolean("wholesaler_distributor") && !companyJsonobject.getBoolean("wholesaler_distributor") &&
                            !companyJsonobject.getBoolean("broker")) {
                        UserInfo.getInstance(mContext).setCompany_type("manufacturer");
                        UserInfo.getInstance(mContext).setCompanyType("seller");
                    } else if (!companyJsonobject.getBoolean("manufacturer") && !companyJsonobject.getBoolean("online_retailer_reseller") && companyJsonobject.getBoolean("retailer") && !companyJsonobject.getBoolean("wholesaler_distributor") && !companyJsonobject.getBoolean("broker") ||
                            !companyJsonobject.getBoolean("manufacturer") && companyJsonobject.getBoolean("online_retailer_reseller") && !companyJsonobject.getBoolean("retailer") && !companyJsonobject.getBoolean("wholesaler_distributor") && !companyJsonobject.getBoolean("broker") ||
                            !companyJsonobject.getBoolean("manufacturer") && companyJsonobject.getBoolean("online_retailer_reseller") && companyJsonobject.getBoolean("retailer") && !companyJsonobject.getBoolean("wholesaler_distributor") && !companyJsonobject.getBoolean("broker")) {
                        UserInfo.getInstance(mContext).setCompanyType("buyer");
                    } else {
                        UserInfo.getInstance(mContext).setCompany_type("nonmanufacturer");
                        UserInfo.getInstance(mContext).setCompanyType("all");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UserInfo.getInstance(mContext).setGuest(false);

                pref.edit().putString("companyname", companyname).apply();
                try {
                    pref.edit().putInt("company_profile_city", Integer.parseInt(cityId)).apply();
                    pref.edit().putInt("company_profile_state", Integer.parseInt(stateId)).apply();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (Activity_Home.pref != null) {
                    Activity_Home.pref.edit().putString("is_profile_set", "true").apply();
                } else {
                    SharedPreferences pref1 = StaticFunctions.getAppSharedPreferences(Activity_Register.this);
                    pref1.edit().putString("is_profile_set", "true").apply();
                }

                getUserDetails(Activity_Register.this);

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void showCompanyDialog(final Context context, String companyname) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_ask_admin_permission, null, false);
        alert.setView(viewInflated);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();
        TextView text = viewInflated.findViewById(R.id.txt_companyname);
        text.setText(getResources().getString(R.string.ask_admin_dialog_text_1));
        viewInflated.findViewById(R.id.txt_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isOldCompany = true;
                companyautocomp.setEnabled(false);
                txt_input_companytype.setVisibility(View.GONE);
                txt_scan_dialog.setVisibility(View.GONE);

                // to Do for exsiting company
            }
        });
        viewInflated.findViewById(R.id.txt_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                selectedCompany = null;
            }
        });
    }

    private void saveProfile(final String state, final String city, final String companyname, String name, String referral) {
        JsonObject jsonObject;
        CompanyProfile profile = new CompanyProfile();
        profile.setName(companyname);
        profile.setState(state);
        profile.setCity(city);
        profile.setFirst_name(name);
        if (!input_email.getText().toString().isEmpty()) {
            profile.setEmail(input_email.getText().toString());
        }

        if (referral != null && !referral.isEmpty()) {
            profile.setRefered_by(referral);
        }


        jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                pref.edit().putString("companyname", companyname).apply();
                try {
                    pref.edit().putInt("company_profile_city", Integer.parseInt(city)).apply();
                    pref.edit().putInt("company_profile_state", Integer.parseInt(state)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Activity_Home.pref != null) {
                    Activity_Home.pref.edit().putString("is_profile_set", "true").apply();
                } else {
                    SharedPreferences pref1 = StaticFunctions.getAppSharedPreferences(Activity_Register.this);
                    pref1.edit().putString("is_profile_set", "true").apply();
                }

                Intent homeintent = new Intent(mContext, Activity_Home.class);
                homeintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeintent);

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                Toast.makeText(mContext, "Request Failed!", Toast.LENGTH_LONG).show();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    private void callChangeCompany(final String state, final String city, final String companyId, final String name) {
        HashMap<String, String> params = new HashMap<>();
        params.put("city", city);
        params.put("state", state);
        params.put("company", companyId);
        params.put("first_name", name);
        params.put("email", input_email.getText().toString());
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.CHANGECOMPANY, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                getUserDetails(Activity_Register.this);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }

    private void getUserDetails(final Activity activity) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).requestwithOnlyHeaders(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PROFILE, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                WishbookEvent wishbookEvent = new WishbookEvent();
                wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                wishbookEvent.setEvent_names("Registration");
                HashMap<String, String> prop = new HashMap<>();
                prop.put("name", input_name.getText().toString());
                prop.put("company_name", companyautocomp.getText().toString());
                prop.put("information_source", isGoogleSigninDone ? "Google" : "Email");
                prop.put("status", "done");
                wishbookEvent.setEvent_properties(prop);
                new WishbookTracker(mContext, wishbookEvent);

                Response_User response_user = Application_Singleton.gson.fromJson(response, Response_User.class);
                if (response_user != null) {
                    storeData(response_user, userInfo, null);
                }
                Intent homeintent = new Intent(mContext, Activity_Home.class);
                homeintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeintent);
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void storeData(Response_User response_user, UserInfo userInfo, String password) {
        StaticFunctions.storeUserData(response_user, userInfo, password);
    }

    private void resetGuestUser(final String companyname, final String firstname, final String companyId, final String referral) {
        String url = URLConstants.RESET_GUEST_USER;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        HttpManager.getInstance((Activity) mContext).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                registerNewUser(companyname, firstname, companyId, referral);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(mContext)
                .content(getResources().getString(R.string.register_incomplete_dialog_text))
                .negativeText(getResources().getString(R.string.positve_text))
                .negativeColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        if (mGoogleSignInClient != null) {
                            Log.i(TAG, "Google Api Client Revoke");
                            mGoogleSignInClient.revokeAccess();
                        } else {
                            Log.i(TAG, "Google Api Client Not Called");
                        }
                        finish();
                    }
                })
                .positiveText(getResources().getString(R.string.negative_text))
                .positiveColor(ContextCompat.getColor(mContext, R.color.color_primary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        checkValidations();
                        return;
                    }
                })
                .show();
    }
}