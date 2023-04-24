package com.wishbook.catalog.login;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TrueSDK;
import com.truecaller.android.sdk.TrueSdkScope;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ToastUtil;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonmodels.AuthenticationModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.commonmodels.responses.Profile;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.models.Response_Key;
import com.wishbook.catalog.login.models.Response_User;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_Login_Version2 extends GATrackedFragment implements OTPListener, GoogleApiClient.OnConnectionFailedListener {


    /**
     * Bind View
     */

    @BindView(R.id.btn_submit)
    TextView btn_submit;

    @BindView(R.id.linear_truecaller_container)
    LinearLayout linear_truecaller_container;

    LinearLayout btn_login_with_truecaller;

    @BindView(R.id.input_mobile)
    EditText input_mobile;

    @BindView(R.id.countrycodes)
    TextView countrycodes;

    @BindView(R.id.txt_login_sms_permission)
    TextView txt_login_sms_permission;

    @BindView(R.id.check_whatsapp_promotion)
    CheckBox check_whatsapp_promotion;

    @BindView(R.id.check_whatsapp_mycatalog)
    CheckBox check_whatsapp_mycatalog;

    @BindView(R.id.txt_tc)
    TextView txt_tc;


    /**
     * Variable Initialize Start
     */
    private View view;
    private Countries[] countries;
    private String CountryId = "1";
    boolean isGuestUserLogin = false;


    public Fragment_Login_Version2() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_Login_Version2.this, Constants.SENDER_NUM);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getCountries(getActivity());
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_otp_login, ga_container, true);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        initView();
        initializeTrueCallerSDK();
        return view;
    }


    public void initView() {
        check_whatsapp_mycatalog.setChecked(true);
        check_whatsapp_promotion.setChecked(true);
        btn_login_with_truecaller = view.findViewById(R.id.btn_login_with_truecaller);
        txt_tc.setPaintFlags(txt_tc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.wishbook.io/tnc/"));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (Application_Singleton.isMoneySmartEnable) {
            txt_login_sms_permission.setVisibility(View.VISIBLE);
        } else {
            txt_login_sms_permission.setVisibility(View.GONE);
        }


        if (getArguments() != null && getArguments().getBoolean("isGuestUserLogin")) {
            isGuestUserLogin = true;
            input_mobile.setText(UserInfo.getInstance(getActivity()).getMobile());
            if (isNotEmpty(input_mobile.getText().toString()) && isValidMobile(input_mobile.getText().toString())) {
                postUserAuthenticate(input_mobile.getText().toString());
            }
        }


        KeyboardUtils.hideKeyboard(getActivity());
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotEmpty(input_mobile.getText().toString()) && isValidMobile(input_mobile.getText().toString())) {
                    postUserAuthenticate(input_mobile.getText().toString());
                } else {
                    if (!isNotEmpty(input_mobile.getText().toString())) {
                        input_mobile.setError("Please enter mobile number");
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

                }
            }
        });

        btn_login_with_truecaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TrueSDK.getInstance().getUserProfile(Fragment_Login_Version2.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void otpReceived(String messageText) {

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

    public void getCountries(final @NonNull Context context) {
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.COUNTRIES, null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    countries = Application_Singleton.gson.fromJson(response, Countries[].class);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "onActivityResult: Fragment Login  request code" + requestCode + "\n Result Code==>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        TrueSDK.getInstance().onActivityResultObtained(getActivity(), resultCode, data);
    }

    private void getOtpPasswordReset(String mobileNumber) {

        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", mobileNumber);
        params.put("country", CountryId);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.PASSWORD_RESET, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);

                Bundle bundle = new Bundle();

                bundle.putString("mobile_number", input_mobile.getText().toString());
                Fragment_VerifyOtp verifyOtp = new Fragment_VerifyOtp();
                verifyOtp.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, verifyOtp).addToBackStack(null).commit();
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


    private void postUserAuthenticate(String mobileNumber) {
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPreferences.getString("gcmtoken", "");
        params.put("phone_number", mobileNumber);
        params.put("country", CountryId);
        params.put("registration_id", token);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.AUTHENTICATION, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        sendPhoneSubmitted();
                        AuthenticationModel authResponse = Application_Singleton.gson.fromJson(response, AuthenticationModel.class);
                        StaticFunctions.logger(response);
                        Bundle bundle = new Bundle();
                        bundle.putString("mobile_number", input_mobile.getText().toString());
                        bundle.putBoolean("is_password_set", authResponse.getIs_password_set());
                        bundle.putString("countryID", CountryId);

                        bundle.putString("whatsapp_notifications", String.valueOf(check_whatsapp_mycatalog.isChecked()));
                        bundle.putString("whatsapp_promotions", String.valueOf(check_whatsapp_promotion.isChecked()));
                        UserInfo.getInstance(getActivity()).setPasswordset(authResponse.getIs_password_set());
                        Fragment_VerifyOtp verifyOtp = new Fragment_VerifyOtp();
                        verifyOtp.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, verifyOtp).addToBackStack(null).commit();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
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

    // ############################### Start True Caller Login Code #################################//

    private void initializeTrueCallerSDK() {

        ITrueCallback sdkCallback = new ITrueCallback() {
            @Override
            public void onSuccessProfileShared(@NonNull TrueProfile trueProfile) {
                if(isAdded() && !isDetached()) {
                    if (trueProfile != null && trueProfile.phoneNumber != null && !trueProfile.phoneNumber.isEmpty()) {
                        Log.e("TAG", "onSuccessProfileShared:===> " + trueProfile.toString());
                        String phone_number = trueProfile.phoneNumber;
                        if (trueProfile.countryCode != null && trueProfile.countryCode.equalsIgnoreCase("IN")) {
                            if (phone_number.contains("+91")) {
                                phone_number = phone_number.replace("+91", "");
                            }
                        }
                        StringBuilder trueCallerProfileName = new StringBuilder();
                        if(trueProfile.firstName!=null && !trueProfile.firstName.isEmpty()) {
                            trueCallerProfileName.append(trueProfile.firstName);
                        }
                        if(trueProfile.lastName!=null && !trueProfile.lastName.isEmpty()) {
                            trueCallerProfileName.append(" "+trueProfile.lastName);
                        }
                        postUserAuthenticateForTrueCaller(phone_number,trueCallerProfileName.toString());
                    }
                }
            }

            @Override
            public void onFailureProfileShared(@NonNull TrueError trueError) {
                Log.e("TAG", "onFailureProfileShared:===> " + trueError.getErrorType());
                switch (trueError.getErrorType()) {
                    case TrueError.ERROR_TYPE_INTERNAL:
                        // do something
                        break;
                    case TrueError.ERROR_TYPE_NETWORK:
                        ToastUtil.showShort("Network Failure");
                        break;
                    case TrueError.ERROR_TYPE_UNAUTHORIZED_USER:
                    case TrueError.ERROR_TYPE_INVALID_ACCOUNT_STATE:
                        ToastUtil.showShort("UNAUTHORIZED USER");
                        break;
                    case TrueError.ERROR_TYPE_TRUECALLER_CLOSED_UNEXPECTEDLY:
                        ToastUtil.showShort("Truecaller Internal Error");
                        break;
                }
            }

            @Override
            public void onVerificationRequired() {
                Log.e("TAG", "onVerificationRequired:===> ");
            }
        };
        TrueSdkScope trueScope = new TrueSdkScope.Builder(getActivity(), sdkCallback)
                .consentMode(TrueSdkScope.CONSENT_MODE_POPUP)
                .consentTitleOption(TrueSdkScope.SDK_CONSENT_TITLE_VERIFY)
                .footerType(TrueSdkScope.FOOTER_TYPE_SKIP)
                .build();

        TrueSDK.init(trueScope);

        if (TrueSDK.getInstance().isUsable()) {
            input_mobile.clearFocus();
            KeyboardUtils.hideKeyboard(getActivity());
            linear_truecaller_container.setVisibility(View.VISIBLE);
            btn_login_with_truecaller.setVisibility(View.VISIBLE);
            try {
                TrueSDK.getInstance().getUserProfile(Fragment_Login_Version2.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            linear_truecaller_container.setVisibility(View.GONE);
            btn_login_with_truecaller.setVisibility(View.GONE);
        }
    }

    private void postUserAuthenticateForTrueCaller(String mobileNumber,String trueCallerProfileName) {
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPreferences.getString("gcmtoken", "");
        if (token.toString().trim().isEmpty()) {
            generateTokenLoginWithTrueCaller(mobileNumber,trueCallerProfileName);
        } else {
            params.put("phone_number", mobileNumber);
            params.put("country", CountryId);
            params.put("registration_id", token);
            params.put("truecaller", "true");
            params.put("whatsapp_notifications", String.valueOf(check_whatsapp_mycatalog.isChecked()));
            params.put("whatsapp_promotions", String.valueOf(check_whatsapp_promotion.isChecked()));
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.AUTHENTICATION, params, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (isAdded() && !isDetached()) {
                            sendPhoneSubmitted();
                            UserInfo userInfo;
                            userInfo = UserInfo.getInstance(getActivity());
                            try {
                                Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                                if (response_key.getKey() != null) {
                                    userInfo.setKey(response_key.getKey());
                                    Application_Singleton.logoutCalled = false;
                                    Application_Singleton.isAskLocation = true;
                                }
                                getUserDetails(getActivity(), userInfo, "", mobileNumber,trueCallerProfileName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
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

    private void callSaveUserProfile(final String firstName) {
        if (firstName != null && !firstName.isEmpty()) {
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
                        try {
                            SharedPreferences preferencesUtils;
                            preferencesUtils = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
                            preferencesUtils.edit().putString("firstName", finalFirstname_split).apply();
                            preferencesUtils.edit().putString("lastName", finalLastname_split).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    navigateToHomePage();
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

        } else {
            navigateToHomePage();
        }

    }

    private void generateTokenLoginWithTrueCaller(String mobileNumber,String trueCallerProfileName) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Push Token not generated,Try Again",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult().getToken();
                        setGcmToken(token);
                        postUserAuthenticateForTrueCaller(mobileNumber,trueCallerProfileName);
                    }
                });
    }

    private void setGcmToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().putString("gcmtoken", token).apply();
    }

    private void getUserDetails(final Activity activity, final UserInfo userInfo, final String password, String mobilNumber,String trueCallerProfileName) {
        StaticFunctions.clearAllService();
        Application_Singleton.Token = userInfo.getKey();
        MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
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
                try {
                    hideProgress();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    Response_User response_user = Application_Singleton.gson.fromJson(response, Response_User.class);
                    storeData(response_user, userInfo, password);

                    WishbookEvent wishbookEvent = new WishbookEvent();
                    wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                    wishbookEvent.setEvent_names("Login_success");
                    HashMap<String, String> prop = new HashMap<>();
                    prop.put("phone", mobilNumber);
                    prop.put("method", "truecaller");
                    wishbookEvent.setEvent_properties(prop);
                    new WishbookTracker(getActivity(), wishbookEvent);

                    boolean isRequiredToAskName = false;
                    if(response_user.getFirst_name() == null || response_user.getFirst_name().isEmpty()){
                        isRequiredToAskName = true;
                    }

                    if(isRequiredToAskName && trueCallerProfileName!=null && !trueCallerProfileName.isEmpty()) {
                        callSaveUserProfile(trueCallerProfileName);
                    } else {
                        navigateToHomePage();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void storeData(Response_User response_user, UserInfo userInfo, String password) {
        StaticFunctions.storeUserData(response_user, userInfo, password);
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(getActivity(), Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(0,
                0);
    }

    // ############################### Send Analysis Data ###################################//

    private void sendPhoneSubmitted() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Login_PhoneSubmitted");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("phone", input_mobile.getText().toString());
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

}
