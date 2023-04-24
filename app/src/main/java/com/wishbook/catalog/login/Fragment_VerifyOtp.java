package com.wishbook.catalog.login;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonSyntaxException;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.gcm.GCMPreferences;
import com.wishbook.catalog.Utils.gcm.RegistrationIntentService;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.MySMSBroadcastReceiver;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.login.models.Response_Key;
import com.wishbook.catalog.login.models.Response_User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * OTP Verify Screen, contains two type method for verification
 * Login with OTP verify, Login with Password if user set
 */
public class Fragment_VerifyOtp extends GATrackedFragment implements OTPListener, Activity_Login.OnBackPressedListener, MySMSBroadcastReceiver.OTPReceiveListener {


    /**
     * Bind View
     */

    @BindView(R.id.appbar)
    Toolbar toolbar;

    @BindView(R.id.txt_verify_otp_header)
    TextView txt_verify_otp_header;

    @BindView(R.id.pinview)
    Pinview pinview;


    @BindView(R.id.input_password)
    EditText input_password;

    @BindView(R.id.link_forgot)
    TextView link_forgot;


    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;

    @BindView(R.id.txt_or)
    TextView txt_or;

    @BindView(R.id.card_password)
    CardView card_password;

    @BindView(R.id.link_resend_otp)
    TextView link_resend_otp;

    @BindView(R.id.txt_otp_timer)
    TextView txt_otp_timer;

    @BindView(R.id.link_whatsapp_otp)
    TextView link_whatsapp_otp;


    /**
     * Initialize Variable Start
     */
    private View view;
    String mobilNumber;
    private MaterialDialog materialDialogLoading;
    private String userName = "";
    private UserInfo userInfo;
    private boolean is_password_set;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String gcmtoken = "";
    String CountryId = "1";
    CountDownTimer countDownTimer;

    String whatsapp_notifications = "false";
    String whatsapp_promotions = "false";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 5001;

    private MySMSBroadcastReceiver smsReceiver;

    /**
     * Default Constructor
     */
    public Fragment_VerifyOtp() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Application_Singleton.isMoneySmartEnable) {
            String[] permissions = {
                    "android.permission.READ_SMS",
                    "android.permission.RECEIVE_SMS"
            };
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 900);
            } else {
                OtpReader.bind(Fragment_VerifyOtp.this, Constants.SENDER_NUM);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_verify_otp, ga_container, true);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            int hour = Integer.parseInt(sdf.format(new Date()));

            SimpleDateFormat day_of_week = new SimpleDateFormat("E");
            String day = day_of_week.format(new Date());

            Log.d("VerifyOTP", "Day of week " + day);


            if (hour >= 9 && hour < 20 && !day.toString().toLowerCase().equals("sun")) {
                link_whatsapp_otp.setVisibility(View.VISIBLE);
            } else {
                link_whatsapp_otp.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getActivity() instanceof Activity_Login)
            ((Activity_Login) getActivity()).setOnBackPressedListener(this);
        setUpToolbar(getActivity(), toolbar);
        KeyboardUtils.hideKeyboard(getActivity());
        userInfo = UserInfo.getInstance(getActivity());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        gcmtoken = sharedPreferences.getString("gcmtoken", "");
        initView();
        startSMSListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initView() {
        if (getArguments() != null) {
            mobilNumber = getArguments().getString("mobile_number");
            is_password_set = getArguments().getBoolean("is_password_set");
            CountryId = getArguments().getString("countryID");


            whatsapp_promotions = getArguments().getString("whatsapp_promotions");
            whatsapp_notifications = getArguments().getString("whatsapp_notifications");
        }
        if (is_password_set) {
            card_password.setVisibility(View.VISIBLE);
            txt_or.setVisibility(View.VISIBLE);
        } else {
            card_password.setVisibility(View.GONE);
            txt_or.setVisibility(View.GONE);
        }

        txt_verify_otp_header.setText(Html.fromHtml(String.format(getResources().getString(R.string.otp_pin_headertext), mobilNumber)));
        link_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_ForgotPassword fragment_forgotPassword = new Fragment_ForgotPassword();
                Bundle bundle = new Bundle();
                bundle.putString("type", "forgot");
                fragment_forgotPassword.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_forgotPassword).addToBackStack(null).commit();
            }
        });
        startCountDown();

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {

                if (pinview.getValue() != null) {
                    if (pinview.getValue().length() == 6) {
                        loginWithOtp();
                    }
                }

            }
        });
        link_whatsapp_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getActivity().getPackageManager();
                boolean isInstalled = StaticFunctions.isPackageInstalled("com.whatsapp", pm);
                if (isInstalled) {

                    checkWhatsappContactSaved();


                } else {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }


            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "onClick: " + pinview.getValue());
                if (pinview.getValue() != null && !pinview.getValue().isEmpty()) {
                    // Login With Otp
                    loginWithOtp();
                } else {
                    if (!input_password.getText().toString().isEmpty()) {

                        // Login with Password

                        loginWithPassword();
                       /* final HashMap<String, String> params = new HashMap<>();
                        params.put("phone_number", mobilNumber);
                        params.put("country", "1");
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
                                paramssub.put("password", input_password.getText().toString());
                                userName = jsonObject.get("username").getAsString();
                                loginUser(paramssub, getActivity(), "fromLogin", userInfo, input_password.getText().toString());
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                hideProgress();
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
                        });*/
                    } else {
                        Toast.makeText(getActivity(), "Enter OTP/Password to proceed", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }


    /**
     * Call LOGIN API URLConstants.AUTHENTICATION
     * After successfully verify otp user can move out to App Home Page
     * @param params
     * @param activity
     * @param status
     * @param userInfo
     * @param password
     */
    public void loginUser(HashMap<String, String> params, final FragmentActivity activity, final String status, final UserInfo userInfo, final String password) {
        HttpManager.getInstance(activity).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.AUTHENTICATION, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Response_Key response_key = Application_Singleton.gson.fromJson(response, Response_Key.class);
                    if (status.equals("fromReg")) {
                        materialDialogLoading = HttpManager.showProgress(activity, false);
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
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                try {
                    WishbookEvent wishbookEvent = new WishbookEvent();
                    wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                    wishbookEvent.setEvent_names("Login_error");
                    HashMap<String, String> prop = new HashMap<>();
                    if (mobilNumber != null) {
                        prop.put("phone", mobilNumber);
                        prop.put("otp", pinview.getValue());
                        if (pinview.getValue() != null && !pinview.getValue().isEmpty())
                            prop.put("method", "otp");
                        else
                            prop.put("method", "password");
                        wishbookEvent.setEvent_properties(prop);
                        new WishbookTracker(getActivity(), wishbookEvent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                StaticFunctions.logger(error.getErrormessage());
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void loginWithOtp() {
        try {
            UserInfo userInfo;
            userInfo = UserInfo.getInstance(getActivity());
            final HashMap<String, String> param = new HashMap<>();
            param.put("phone_number", mobilNumber);
            param.put("country", CountryId);
            param.put("otp", pinview.getValue());
            param.put("whatsapp_notifications", whatsapp_notifications);
            param.put("whatsapp_promotions", whatsapp_promotions);
            loginUser(param, getActivity(), "fromReg", userInfo, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginWithPassword() {
        try {
            UserInfo userInfo;
            userInfo = UserInfo.getInstance(getActivity());
            final HashMap<String, String> param = new HashMap<>();
            param.put("phone_number", mobilNumber);
            param.put("country", CountryId);
            param.put("password", input_password.getText().toString());
            param.put("whatsapp_notifications", whatsapp_notifications);
            param.put("whatsapp_promotions", whatsapp_promotions);
            loginUser(param, getActivity(), "fromReg", userInfo, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                pinview.setValue(match);
                txt_otp_timer.setVisibility(View.GONE);
            }
        }
    }



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
                try {
                    hideProgress();
                    if (materialDialogLoading != null && materialDialogLoading.isShowing()) {
                        materialDialogLoading.dismiss();
                    }

                    Response_User response_user = Application_Singleton.gson.fromJson(response, Response_User.class);

                    storeData(response_user, userInfo, password);

                    WishbookEvent wishbookEvent = new WishbookEvent();
                    wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                    wishbookEvent.setEvent_names("Login_success");
                    HashMap<String, String> prop = new HashMap<>();
                    prop.put("phone", mobilNumber);
                    prop.put("otp", pinview.getValue());
                    if (pinview.getValue() != null && !pinview.getValue().isEmpty())
                        prop.put("method", "otp");
                    else
                        prop.put("method", "password");
                    wishbookEvent.setEvent_properties(prop);
                    new WishbookTracker(getActivity(), wishbookEvent);



                    /*if (pref.getBoolean("isFirstTimeLogin", true)) {
                        Intent languageIntent = new Intent(activity, ActivityLanguage.class);
                        languageIntent.putExtra("backDisable", true);
                        activity.startActivity(languageIntent);
                        pref = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit().putBoolean("isFirstTimeLogin", false);
                        edit.commit();
                    } else {

                    }*/
                    Intent intent = new Intent(activity, Activity_Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    activity.finish();
                    getActivity().overridePendingTransition(0,
                            0);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    try {
                        StringBuffer custom_message = new StringBuffer();
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsString = sw.toString();
                        custom_message.append("User Verify OTP Exception===>"+exceptionAsString + "\n");
                        Crashlytics.logException(new Exception(custom_message.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    try {
                        StringBuffer custom_message = new StringBuffer();
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsString = sw.toString();
                        custom_message.append("User Verify OTP Exception===>"+exceptionAsString + "\n");
                        Crashlytics.logException(new Exception(custom_message.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (materialDialogLoading != null && materialDialogLoading.isShowing()) {
                    materialDialogLoading.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void storeData(Response_User response_user, UserInfo userInfo, String password) {
        StaticFunctions.storeUserData(response_user, userInfo, password);
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


    @Override
    public void doBack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
            if (getActivity() instanceof Activity_Login)
                ((Activity_Login) getActivity()).setOnBackPressedListener(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        try {
            if (smsReceiver != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setUpToolbar(final Context context, Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.color_primary));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.intro_text_color), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();

            }
        });
    }

    private void startCountDown() {
        link_resend_otp.setEnabled(false);
        link_resend_otp.setAlpha(0.2f);

        link_whatsapp_otp.setEnabled(false);
        link_whatsapp_otp.setAlpha(0.2f);
        txt_otp_timer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txt_otp_timer.setText("00:" + String.format("%02d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                txt_otp_timer.setVisibility(View.GONE);
                link_resend_otp.setEnabled(true);
                link_resend_otp.setAlpha(1);
                link_whatsapp_otp.setEnabled(true);
                link_whatsapp_otp.setAlpha(1);

                link_resend_otp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resendOTP();
                        startSMSListener();
                    }
                });
            }
        }.start();
    }

    private void resendOTP() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", mobilNumber);
        params.put("country", CountryId);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.AUTHENTICATION, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                startCountDown();
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

    private void checkWhatsappContactSaved() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                boolean isAvailable, isAvailable1, isAvailable2, isAvailable3, isAvailable4, isAvailable5, isAvailable6;
                isAvailable = contactExists(getActivity(), getResources().getString(R.string.whatspp_otp1));
                if (!isAvailable) {
                    addWishbookContacts();
                } else {
                    sendOTP();
                }
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean contactExists(Activity _activity, String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = _activity.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
            return false;
        } else {
            return false;
        }
    }

    private void addWishbookContacts() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        } else {
            insertContacts(Application_Singleton.getResourceString(R.string.wishbook_contacts_display_name4),
                    Application_Singleton.getResourceString(R.string.whatspp_otp1));
        }
    }

    protected void insertContacts(String DisplayName, String MobileNumber) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                DisplayName).build());
            }

            //------------------------------------------------------ Mobile Number
            if (MobileNumber != null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());


            }


            // Asking the Contact provider to create a new contact
            try {
                getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendOTP();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void sendOTP() {
        String smsNumber = getResources().getString(R.string.whatspp_otp1); // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Send this to get OTP");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                    OtpReader.bind(Fragment_VerifyOtp.this, Constants.SENDER_NUM);
                } else {

                }
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISSION TRUE
                    checkWhatsappContactSaved();
                } else {
                    Toast.makeText(getActivity(), "Contact Permission is denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void startSMSListener() {
        try {
            Log.d(Application_Singleton.SMSTAG, "startSMSListener: CALLED ");
            smsReceiver = new MySMSBroadcastReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            getActivity().registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(getActivity());

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        Log.d(Application_Singleton.SMSTAG, "onOTPReceived: " + otp);
        if(otp!=null && !otp.isEmpty()) {
            //showToast("OTP Received: " + otp);
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(otp);
            while (matcher.find()) {
                String match = matcher.group();
                pinview.setValue(match);
                txt_otp_timer.setVisibility(View.GONE);
            }
        }
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(smsReceiver);
        }
    }

    @Override
    public void onOTPTimeOut() {
        Log.d(Application_Singleton.SMSTAG, "onOTPTimeOut: ");
        //showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        Log.d(Application_Singleton.SMSTAG, "onOTPReceivedError: " + error);
        //showToast(error);
    }


    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
