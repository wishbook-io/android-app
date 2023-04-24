package com.wishbook.catalog.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.gcm.GCMPreferences;
import com.wishbook.catalog.Utils.gcm.RegistrationIntentService;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.home.more.ActivityLanguage;
import com.wishbook.catalog.login.models.Response_Key;
import com.wishbook.catalog.login.models.Response_OTP;
import com.wishbook.catalog.login.models.Response_User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Fragment_login extends GATrackedFragment implements OTPListener, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.input_password)
    EditText input_password;

    /*@BindView(R.id.appbar)
    Toolbar appbar;*/

    @BindView(R.id.link_forgot)
    TextView link_forgot;

    @BindView(R.id.login_with_otp)
    TextView login_with_otp;

    @BindView(R.id.input_mobile)
    EditText input_mobile;

    @BindView(R.id.countrycodes)
    TextView countrycodes;

    @BindView(R.id.btn_login)
    AppCompatButton loginbut;

    private Countries[] countries;
    private String CountryId = "1";

    @BindView(R.id.btn_register)
    AppCompatButton regisbut;

    @BindView(R.id.btn_sign_in)
    SignInButton google_sign_in;

    @BindView(R.id.login_facebookLoginButton)
    LoginButton fbLoginButton;

    @BindView(R.id.txtinput_password)
    TextInputLayout txtinput_password;

    @BindView(R.id.link_login_with_otp)
    TextView link_login_with_otp;

    private View view;
    public static String countrysel;
    public static String username = "";
    public static String password = "";
    private UserInfo userInfo;
    private EditText inputotp;
    private String userName = "";
    private boolean isReceiverRegistered = false;
    String gcmtoken = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public final String POPUP_OTP_TITLE = "Mobile Verification";
    public final String POPUP_OTP_TEXT = "Please enter the One Time Password (OTP) sent to your registered mobile number and authenticate the registration.";

    private static final int GOOGLE_SIGN_IN = 7000;
    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;

    private MaterialDialog materialDialogLoading;


    public Fragment_login() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_login.this, Constants.SENDER_NUM);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getCountries(getActivity());

        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_login_version_2, ga_container, true);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        initFacebook();


        userInfo = UserInfo.getInstance(getActivity());
        //setUpToolbar((AppCompatActivity) getActivity(), appbar);
        input_password.setTransformationMethod(new PasswordTransformationMethod());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        gcmtoken = sharedPreferences.getString("gcmtoken", "");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), Fragment_login.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        google_sign_in.setSize(SignInButton.SIZE_STANDARD);
        google_sign_in.setColorScheme(SignInButton.COLOR_LIGHT);
        google_sign_in.setScopes(gso.getScopeArray());
        setGooglePlusButtonText(google_sign_in,"Register with Google");

        return view;
    }

    private void initFacebook() {
        try{
            // According to new facebook lib it will be auto initialize
            FacebookSdk.sdkInitialize(getActivity());
        } catch (Exception e){
            e.printStackTrace();
        }
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        fbLoginButton.setFragment(this);

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
                                            Bundle bundle = new Bundle();
                                            Fragment_Register fragment_register = new Fragment_Register();
                                            bundle.putString("registerDetailFb", object.toString());
                                            fragment_register.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_register).addToBackStack(null).commit();
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

    @OnClick(R.id.link_forgot)
    public void forgotPassword() {
        Fragment_ForgotPassword fragment_forgotPassword = new Fragment_ForgotPassword();
        Bundle bundle = new Bundle();
        bundle.putString("type","forgot");
        fragment_forgotPassword.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_forgotPassword).addToBackStack(null).commit();
    }

    @OnClick(R.id.link_login_with_otp)
    public void linkWithOtp() {
        Fragment_ForgotPassword fragment_forgotPassword = new Fragment_ForgotPassword();
        Bundle bundle = new Bundle();
        bundle.putString("type","loginOtp");
        fragment_forgotPassword.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_forgotPassword).addToBackStack(null).commit();
    }

    @OnClick(R.id.btn_register)
    public void register() {
        username = input_mobile.getText().toString();
        password = input_password.getText().toString();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, new Fragment_Register()).addToBackStack(null).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
       /* OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("TAG", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //  showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
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


    @Override
    public void onResume() {
        super.onResume();
        txtinput_password.setPasswordVisibilityToggleEnabled(true);
    }

    @OnClick(R.id.btn_login)
    public void onLogin() {
        userName = input_mobile.getText().toString();
        final String passWord = input_password.getText().toString();
        final String mobileNum = input_mobile.getText().toString();
        if (emptyFieldCheck(userName, passWord)) {
            /*if (userName.equals("")) {
                if (mobileNum.length() < 10) {
                    input_mobile.setError(getResources().getString(R.string.invalid_mobile));
                    return;
                }*/
            //change by abu
            boolean digitsOnly = TextUtils.isDigitsOnly(userName);
            if (userName.length() == 10 && digitsOnly) {
                final HashMap<String, String> params = new HashMap<>();
                params.put("phone_number", mobileNum);
                params.put("country", CountryId);
                params.put("field", "get_userdetail");

                showProgress();
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.USERS, params, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        hideProgress();

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        hideProgress();
                        StaticFunctions.logger(response);
                        Application_Singleton.errorLogoutpopshown = false;
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        HashMap<String, String> paramssub = new HashMap<>();
                        paramssub.put("username", jsonObject.get("username").getAsString());
                        paramssub.put("password", passWord);
                        // input_username.setText(jsonObject.get("username").getAsString());
                        userName = jsonObject.get("username").getAsString();
                        loginUser(paramssub, getActivity(), "fromLogin", userInfo, input_password.getText().toString());
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
                        StaticFunctions.logger(error.getErrormessage());
                        StaticFunctions.showResponseFailedDialog(error);
                        input_mobile.setError(error.getErrormessage());

                    }
                });

            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", userName);
                params.put("password", passWord);
                loginUser(params, getActivity(), "fromLogin", userInfo, input_password.getText().toString());
            }
        }
    }


    public void loginUser(HashMap<String, String> params, final FragmentActivity activity, final String status, final UserInfo userInfo, final String password) {
        HttpManager.getInstance(activity).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGIN_URL, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                if(status.equals("fromReg")) {
                    materialDialogLoading = HttpManager.showProgress(activity,false);
                }
                if (response_key.getKey() != null) {
                    userInfo.setKey(response_key.getKey());
                    Application_Singleton.logoutCalled = false;
                    Application_Singleton.isAskLocation = true;
                    // getCompanyType(getActivity(),userInfo);
                    getUserDetails(activity, userInfo, password);
                    if (gcmtoken == "") {
                        intiniateReciever();
                        registerReceiver(activity);
                        if (StaticFunctions.checkPlayServices(activity)) {
                            Intent intent = new Intent(activity, RegistrationIntentService.class);
                            activity.startService(intent);

                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                StaticFunctions.showResponseFailedDialog(error);
                //change by abu
                if (error.getErrormessage().equals("Phone number is not verified.")) {
                    validateOTP(userName, password);
                }
                if (status.equals("fromReg")) {

                    activity.getSupportFragmentManager().popBackStack();

                }
            }
        });
    }
    /* public void loginUser(HashMap<String, String> params, final FragmentActivity activity, final String status, final UserInfo userInfo, final String password) {
        HttpManager.getInstance(activity).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.LOGIN_URL, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                if (response_key.getKey() != null) {
                    userInfo.setKey(response_key.getKey());
                    getUserDetails(activity,userInfo,password);
                    if(gcmtoken=="") {
                        intiniateReciever();
                        registerReceiver();
                        if (StaticFunctions.checkPlayServices(activity)) {
                            Intent intent = new Intent(activity, RegistrationIntentService.class);
                            activity.startService(intent);

                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
                new MaterialDialog.Builder(activity)
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
                //change by abu
                if(error.getErrormessage().equals("Phone number is not verified.")) {
                    validateOTP(userName);
                }
                if(status.equals("fromReg"))
                {

                    activity.getSupportFragmentManager().popBackStack();

                }
            }
        });
    }*/


    private void getUserDetails(final Activity activity, final UserInfo userInfo, final String password) {
        StaticFunctions.clearAllService();
        Application_Singleton.Token = userInfo.getKey();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        showProgress();
        HttpManager.getInstance(activity).requestwithOnlyHeaders(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PROFILE, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if(materialDialogLoading!=null && materialDialogLoading.isShowing()) {
                    materialDialogLoading.dismiss();
                }
                StaticFunctions.logger(response);
                Response_User response_user = Application_Singleton.gson.fromJson(response, Response_User.class);
                storeData(response_user, userInfo, password);
                Intent languageIntent = new Intent(activity, ActivityLanguage.class);
                languageIntent.putExtra("backDisable", true);
                activity.startActivity(languageIntent);
                //StaticFunctions.switchActivity(activity, Activity_Home.class);
                //StaticFunctions.switchActivity(activity, ActivityLanguage.class);
                activity.finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if(materialDialogLoading!=null && materialDialogLoading.isShowing()) {
                    materialDialogLoading.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);


            }
        });
    }

    /* private void getCompanyType(final Activity activity, final UserInfo userInfo) {
         Application_Singleton.Token = userInfo.getKey();
         HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
         HttpManager.getInstance(activity).requestwithOnlyHeaders(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getContext(),"company_type",""), headers, new HttpManager.customCallBack() {
             @Override
             public void onCacheResponse(String response) {
                 onServerResponse(response, false);
             }

             @Override
             public void onServerResponse(String response, boolean dataUpdated) {
                 CompanyTypePatch companyTypePatch =Application_Singleton.gson.fromJson(response, CompanyTypePatch.class);
                 if(companyTypePatch!=null) {
                     userInfo.setManufacturer(companyTypePatch.getManufacturer());
                     userInfo.setRetailer(companyTypePatch.getRetailer());
                     userInfo.setBroker(companyTypePatch.getBroker());
                     userInfo.setWholesaler_distributor(companyTypePatch.getWholesaler_distributor());
                     userInfo.setOnline_retailer_reseller(companyTypePatch.getOnline_retailer_reseller());
                 }
             }

             @Override
             public void onResponseFailed(ErrorString error) {
                 new MaterialDialog.Builder(activity)
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
    private void storeData(Response_User response_user, UserInfo userInfo, String password) {

        StaticFunctions.storeUserData(response_user, userInfo, password);
    }


    private boolean emptyFieldCheck(String userName, String passWord) {
        if (userName.equals("")) {
            if (input_mobile.getText().toString().equals("")) {
                input_mobile.setError(getResources().getString(R.string.field_empty));
                return false;
            }
        }
        if (passWord.equals("")) {
            input_password.setError(getResources().getString(R.string.passwordempty));
            return false;
        } else {
            if (passWord.length() < 6) {
                input_password.setError(getResources().getString(R.string.passwordshort));
                return false;
            }
        }
        return true;
    }

    public void setUpToolbar(final AppCompatActivity context, Toolbar toolbar) {
        toolbar.setTitle("Login");
       /* toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.inflateMenu(R.menu.menu_login);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                *//*if (item.getItemId() == R.id.action_register) {
                    context.getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, new Fragment_Register()).addToBackStack(null).commit();
                }*//*
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 132: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("Permissions")
                            .content("No Permissions Granted !")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            })
                            .show();


                }
                return;
            }
        }
    }

    private void validateOTP(final String mobile, final String password) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        alert.setTitle(POPUP_OTP_TITLE);
        alert.setMessage(POPUP_OTP_TEXT);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_input, (ViewGroup) getView(), false);
        inputotp = (EditText) viewInflated.findViewById(R.id.input_otp);
        alert.setView(viewInflated);
        alert.setPositiveButton("Submit", null);
        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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
                        params.put("name_or_number", userName);
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
                                                      params.put("name_or_number", userName);
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
                                                              param.put("username", userName);
                                                              param.put("password", password);
                                                              loginUser(param, getActivity(), "fromLogin", userInfo, password);
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

    private void intiniateReciever() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        };
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

    private void registerReceiver(FragmentActivity activity) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
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


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("TAG", "display name: " + acct.getDisplayName());

            String personPhotoUrl = null;
            String personName = null;
             String email = null;
            if(acct.getDisplayName()!=null) {
                personName = acct.getDisplayName();
            }
            if(acct.getPhotoUrl()!=null) {
                personPhotoUrl = acct.getPhotoUrl().toString();
            }

            if(acct.getEmail()!=null) {
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
                    Fragment_Register fragment_register = new Fragment_Register();
                    bundle.putString("registerDetailFb", object.toString());
                    fragment_register.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_register).addToBackStack(null).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "onActivityResult: Login Request Code" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @OnClick(R.id.btn_sign_in)
    public void googleSignin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
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



}
