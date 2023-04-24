package com.wishbook.catalog.home;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicommons.people.contact.Contact;
import com.crashlytics.android.Crashlytics;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatCallbackStatus;
import com.freshchat.consumer.sdk.FreshchatConfig;
import com.freshchat.consumer.sdk.FreshchatUser;
import com.freshchat.consumer.sdk.UnreadCountCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BaseActivity;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.contactsutil.ContentProviderUtil;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.Advancedcompanyprofile;
import com.wishbook.catalog.commonmodels.AllContactsChatModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UploadContactsModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.Userprofile;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Profile;
import com.wishbook.catalog.commonmodels.responses.ResponseAppVersion;
import com.wishbook.catalog.commonmodels.responses.ResponseStatistics;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_Product_Tab;
import com.wishbook.catalog.home.contacts.Fragment_ContactsHolder;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;
import com.wishbook.catalog.home.inventory.Fragment_Inventory;
import com.wishbook.catalog.home.more.ActivityLanguage;
import com.wishbook.catalog.home.more.Activity_ShareApp;
import com.wishbook.catalog.home.more.Fragment_AboutUs;
import com.wishbook.catalog.home.more.Fragment_AboutUs_Policies;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.more.Fragment_Attendance;
import com.wishbook.catalog.home.more.Fragment_BuyerGroups;
import com.wishbook.catalog.home.more.Fragment_BuyersRejected;
import com.wishbook.catalog.home.more.Fragment_Categories;
import com.wishbook.catalog.home.more.Fragment_ChangePassword;
import com.wishbook.catalog.home.more.Fragment_ContactUs_Version2;
import com.wishbook.catalog.home.more.Fragment_Home;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.more.Fragment_SupplierRejected;
import com.wishbook.catalog.home.more.Fragment_Sync;
import com.wishbook.catalog.home.more.Fragment_WishbookBankDetail;
import com.wishbook.catalog.home.more.invoice.Fragment_Invoice;
import com.wishbook.catalog.home.more.visits.Fragment_Visits;
import com.wishbook.catalog.home.myBusiness.Fragment_MyBusiness;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.Fragment_Order_Holder_Version4;
import com.wishbook.catalog.login.Activity_Register_Version2;
import com.wishbook.catalog.login.Fragment_Register_New_Version2;
import com.wishbook.catalog.login.models.Response_User;
import com.wishbook.catalog.services.LocalService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.in.moneysmart.common.MoneySmartInit;
import co.in.moneysmart.common.util.CommonHelper;
import co.in.moneysmart.common.util.InitializationHelper;
import co.in.moneysmart.common.util.PermissionDialogInvocationChecker;
import co.in.moneysmart.common.util.PermissionsCheckHelper;
import co.in.moneysmart.commondb.provider.UserInfosContentProvider;
import co.in.moneysmart.perm.util.PermissionRunnable;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import rx.Subscriber;

import static com.wishbook.catalog.Application_Singleton.CONTAINERFRAG;
import static com.wishbook.catalog.Application_Singleton.CONTAINER_TITLE;
import static com.wishbook.catalog.Application_Singleton.SCREEN_HEIGHT;
import static com.wishbook.catalog.Application_Singleton.SCREEN_WIDTH;
import static com.wishbook.catalog.Application_Singleton.TOOLBARSTYLE;
import static com.wishbook.catalog.Application_Singleton.configResponse;
import static com.wishbook.catalog.Application_Singleton.contactPermissionDenied;
import static com.wishbook.catalog.Application_Singleton.gson;
import static com.wishbook.catalog.Application_Singleton.isAskLocation;
import static com.wishbook.catalog.Application_Singleton.selectedOrder;
import static com.wishbook.catalog.Application_Singleton.trackEvent;


public class Activity_Home extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, PermissionDialogInvocationChecker {
    protected static final int REQUEST_CHECK_SETTINGS = 600;
    private static final int REQUESTPERMISSION = 456;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 5001;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4001;
    private static final int PASSWORD_SET = 1500;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static int EXIT_APP = 1800;
    public static AppCompatActivity context;
    public static SharedPreferences pref;
    public static BottomBar bottomBar;
    public static FloatingActionButton support_chat_fab;
    public static FloatingActionButton freshbot_fab;
    public static GoogleApiClient mGoogleApiClient;
    private static boolean mLocationRequestCancel = false;
    public long statisticsLastCall = 0;
    AppContactService appContactService;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.relative_progress)
    RelativeLayout relative_progress;
    Runnable chatRunnable;
    Handler addHandler = new Handler();
    String selectedLanguage;
    TextView badgeTextView;
    int drawer_gravity = Gravity.RIGHT;
    ActionBarDrawerToggle mDrawerToggle;
    Handler permissionHandler = new Handler();
    boolean setUpBottomBarCalled = false;
    private SharedPreferences gcmSharedPreferences;


    //
    private SharedPreferences linkPreferences;
    //private BottomBarTab homeBadge;
    private UserInfo userInfo;
    private List<MyContacts> contactList = new ArrayList<>();
    private List<MyContacts> wishbookcontactList = new ArrayList<>();
    private List<AllContactsChatModel> AllContacts = new ArrayList<>();
    private Response_Buyer[] response_buyer;
    private Response_Suppliers[] response_suppliers;
    private Subscriber<Integer> subscriber;
    private Menu menu;
    private LocationRequest mLocationRequest;
    private IntentFilter updateStateFilter;
    PackageInfo pInfo;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("action") != null) {
                action = intent.getStringExtra("action");
                if (action.equals("updateStatistics")) {
                    getStatistics(Activity_Home.this);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        branchInit();

        getConfig();
        sendPlatformInfo(UserInfo.getInstance(this).getUserId());
        PostFCMKey();
        // To Get Statistics
        getStatistics(context);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_main);
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("Home", "onCreate: start");
        StaticFunctions.initializeAppsee();
        FirebaseApp.initializeApp(this);
        registerUser();
        FreshchatConfig freshchatConfig = new FreshchatConfig(BuildConfig.FRESHCHAT_ID, BuildConfig.FRESHCHAT_KEY);
        freshchatConfig.setCameraCaptureEnabled(true);
        freshchatConfig.setGallerySelectionEnabled(true);
        try {
            Freshchat.getInstance(getApplicationContext()).init(freshchatConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StaticFunctions.initFreshChat(Activity_Home.this);
        ButterKnife.bind(this);

        if (Application_Singleton.isMoneySmartEnable) {
            try {
                // MoneySmart Start
                Log.e(StaticFunctions.MONEYTAG, "onCreate: Set Current Activity");
                MoneySmartInit.sCurrentActivity = this;
                // MoneySmart End
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        bottomBar = findViewById(R.id.bottomBar);

        relative_progress.setVisibility(View.VISIBLE);


        pref = StaticFunctions.getAppSharedPreferences(Activity_Home.this);
        Application_Singleton.Token = pref.getString("key", "");
        Log.d("Activity_Home", "onCreate: initializeAppsee end");


        if (UserInfo.getInstance(this) != null) {
            selectedLanguage = UserInfo.getInstance(Activity_Home.this).getLanguage();
            LocaleHelper.setSelectedLanguage(this, selectedLanguage);
            //set all user as guest if something goes wrong

        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Log.d("Activity_Home", "onCreate: ButterKnife end");
        context = this;
        buildGoogleApiClient();

        getCompanyProfile(context);
        if (!pref.getString("groupstatus", "0").equals("2")) {
            checkRequireUpdateApp(context);
        }


        //getWishlistCount(context);

        Log.d("Activity_Home", "onCreate: buildGoogleApiClient end");

        if (mGoogleApiClient != null) {
            Log.i("GPS", "Conncted Call");
            mGoogleApiClient.connect();
        }


        linkPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        badgeTextView = (TextView) findViewById(R.id.badge_textview);
        support_chat_fab = (FloatingActionButton) findViewById(R.id.support_chat_fab);
        freshbot_fab = findViewById(R.id.freshbot_fab);
        freshbot_fab.setVisibility(View.GONE);
        setCrashlyticsUserInfo();


        support_chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Freshchat.showConversations(getApplicationContext());

               /* Bundle bundle = new Bundle();
                bundle.putString("url", "https://seller.wishbook.io/fresh_chat_web.html");
                Fragment_FreshChatBoatView freshChatBoatView = new Fragment_FreshChatBoatView();
                freshChatBoatView.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Wishbook FreshChat Bot";
                Application_Singleton.CONTAINERFRAG = freshChatBoatView;
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);*/
            }
        });
        freshbot_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Bundle bundle = new Bundle();
                bundle.putString("url", "file:///android_asset/freshboat.html" + "?sessionId=" + UserInfo.getInstance(Activity_Home.this).getKey() + "&userId=" + UserInfo.getInstance(Activity_Home.this).getUserId() + "&name=" + UserInfo.getInstance(Activity_Home.this).getUserName());
                Fragment_FreshChatBoatView freshChatBoatView = new Fragment_FreshChatBoatView();
                freshChatBoatView.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Wishbook Support Bot";
                Application_Singleton.CONTAINERFRAG = freshChatBoatView;
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);*/
            }
        });
        //tabs.setOnTabSelectedListener(this);


        if (linkPreferences.getString("link_type", "") != "" && linkPreferences.getString("link_id", "") != "") {
            linkIntentPreferences(linkPreferences.getString("link_type", ""), linkPreferences.getString("link_id", ""));
        }
        gcmSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null && !UserInfo.getInstance(context).getCompanyGroupFlag().isEmpty()) {
            if (!StaticFunctions.isAllGroupFlase(context)) {
                checkPermissions();
            }
        }


        userInfo = UserInfo.getInstance(Activity_Home.this);


        Log.d("Activity_Home", "onCreate: PostFCMKey end");

        Log.d("Activity_Home", "setupDrawerContent");
        setupDrawerContent(navigationView);


        Log.d("Activity_Home", "onCreate: setupDrawerContent end");

        if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
            // wishbook Users
            bottomBar.setItems(R.xml.bottombar_tabs_group1);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            navigationView.setLayoutParams(params);
            drawer_gravity = Gravity.LEFT;
        } else {
            // sales person
            bottomBar.setItems(R.xml.bottombar_tabs_sales_person);
            drawer_gravity = Gravity.RIGHT;
        }


        if (UserInfo.getInstance(this) != null
                && UserInfo.getInstance(this).getCompanyType() != null
                && !UserInfo.getInstance(this).getCompanyType().isEmpty()) {

            setUpBottomBar();

        }


        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null && !UserInfo.getInstance(context).getCompanyGroupFlag().isEmpty()) {
            if (!StaticFunctions.isAllGroupFlase(context)) {
                if (UserInfo.getInstance(this).getGroupstatus().equals("1")) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "====================Ask Permission onStart: ===================");
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_CONTACTS}, 1);
                    } else {
                        contactPermissionDenied = true;
                        getAysnContacts(Activity_Home.this);
                    }
                }
            }
        }


        //gcm listener send notification handler
        try {
            String fragment = getIntent().getStringExtra("FRAGMENTS");
            if (getIntent().getStringExtra("type") != null) {
                fragment = getIntent().getStringExtra("type");
            }
            final String companyId = getIntent().getStringExtra("id");


            if (fragment != null) {
                if (getIntent().getStringExtra("title") != null) {
                    trackEvent("Notification", "Open", getIntent().getStringExtra("title"));
                }
                LocalService.notificationCounter.onNext(0);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", fragment);
                if (companyId != null && !companyId.isEmpty())
                    hashMap.put("id", companyId);
                hashMap.put("page", getIntent().getStringExtra("title"));

                String push_id = getIntent().getStringExtra("push_id");
                if (push_id != null && push_id.isEmpty()) {
                    hashMap.put("push_id", push_id);
                }

                String other_para = getIntent().getStringExtra("other_para");
                if (other_para != null && !other_para.isEmpty()) {
                    Type type = new TypeToken<HashMap<String, String>>() {
                    }.getType();
                    HashMap<String, String> otherParam = new Gson().fromJson(other_para, type);
                    if (otherParam.containsKey("deep_link")) {
                        String url = otherParam.get("deep_link");
                        try {
                            Uri intentUri = Uri.parse(url);
                            otherParam.putAll(SplashScreen.getQueryString(intentUri));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    hashMap.putAll(otherParam);
                }


                if (hashMap != null && hashMap.containsKey("type")) {
                    sendNotificationClickedAnalytics(hashMap.get("type"), getIntent().getStringExtra("other_para"), hashMap.get("title"), hashMap.get("message"));
                }
                new DeepLinkFunction(hashMap, context);
            } else {
               /* if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, new Fragment_Summary()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, new Fragment_MeetingReport()).commit();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("type") != null) {
                HashMap<String, String> hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("otherPara");
                if (hashMap != null && hashMap.containsKey("type")) {
                    if (hashMap.containsKey("utm_source") && !hashMap.get("utm_source").equals("whatsapp")) {
                        sendNotificationClickedAnalytics(hashMap.get("type"), Application_Singleton.gson.toJson(hashMap), hashMap.get("title"), hashMap.get("message"));
                    }

                }
                new DeepLinkFunction(hashMap, context);
            }
            if (getIntent().getExtras().getString("catalog") != null) {
                final String catalog = getIntent().getExtras().getString("catalog");

                //initially loading
                /*if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                    Log.d("Activity_Home", "Fragment_Summary from HomeActivity DeepLinkFunction catalog");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, new Fragment_Summary()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, new Fragment_MeetingReport()).commit();
                }*/

                StaticFunctions.catalogQR(catalog, Activity_Home.this, getIntent().getExtras().getString("supplier"));
            }
        }


        Log.d("Activity_Home", "onCreate: deeplink end");


        Log.d("Activity_Home", "onCreate: End");
        try {
            if (UserInfo.getInstance(context).getCompanyGroupFlag() != null && !UserInfo.getInstance(context).getCompanyGroupFlag().isEmpty()) {
                if (!StaticFunctions.isAllGroupFlase(context)) {
                    checkWishbookContactSaved();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Application_Singleton.isMoneySmartEnable) {
            try {
                // Moneysmart Start
                MoneySmartInit.sCurrentActivity = this;
                Log.e(StaticFunctions.MONEYTAG, "onResume: Set Current Activity start");
                // MoneySmart End
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        registerUpdateReceiver();

        Freshchat.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on
                badgeTextView.setText("" + unreadCount);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (Application_Singleton.isMoneySmartEnable) {
            try {
                // Money Smart Start
                MoneySmartInit.sCurrentActivity = null;
                Log.e(StaticFunctions.MONEYTAG, "onPause: Set Current Activity null");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Money Smart End
        }

        if (subscriber != null)
            subscriber.unsubscribe();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // super.onSaveInstanceState(outState, outPersistentState);
        //to resolve Can not perform this action after onSaveInstanceState
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterUpdateReceiver();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
            mLocationRequestCancel = false;
        }
    }


    /**
     * Branch.IO session init
     * also Check any deep_link parameter specify than User Navigate that screen after home screen set
     */
    protected void branchInit() {
        try {
            Branch branch = Branch.getInstance();

            branch.initSession(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error == null) {
                        if (referringParams.has("deep_link")) {
                            try {
                                if (referringParams.get("deep_link") != null) {
                                    Log.e(StaticFunctions.BRANCHIOTAG, "Activity Home ==>onInitFinished: " + referringParams.get("deep_link"));
                                    Uri intent_uri = Uri.parse(referringParams.getString("deep_link"));
                                    HashMap<String, String> map = SplashScreen.getQueryString(intent_uri);
                                    new DeepLinkFunction(map, Activity_Home.this);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        Log.i(StaticFunctions.BRANCHIOTAG, "Init Failed==>" + error.getMessage());
                    }
                }
            }, this.getIntent().getData(), this);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("Activity_Home", "onNewIntent: Called");
        branchInit();
        if (intent != null)
            openDeepLinkonResume(intent);

    }

    /**
     * Check Splash screen have a deep_link than navigate to screen
     * @param intent
     */
    public void openDeepLinkonResume(Intent intent) {
        try {
            String fragment = intent.getStringExtra("FRAGMENTS");
            if (intent.getStringExtra("type") != null) {
                fragment = intent.getStringExtra("type");
            }
            final String companyId = getIntent().getStringExtra("id");
            if (fragment != null) {
                if (intent.getStringExtra("title") != null) {
                    trackEvent("Notification", "Open", intent.getStringExtra("title"));
                }
                LocalService.notificationCounter.onNext(0);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", fragment);
                if (companyId != null && !companyId.isEmpty())
                    hashMap.put("id", companyId);
                hashMap.put("page", intent.getStringExtra("title"));

                String push_id = intent.getStringExtra("push_id");
                if (push_id != null && push_id.isEmpty()) {
                    hashMap.put("push_id", push_id);
                }

                String other_para = intent.getStringExtra("other_para");
                if (other_para != null && !other_para.isEmpty()) {
                    Type type = new TypeToken<HashMap<String, String>>() {
                    }.getType();
                    HashMap<String, String> otherParam = new Gson().fromJson(other_para, type);
                    if (otherParam.containsKey("deep_link")) {
                        String url = otherParam.get("deep_link");
                        try {
                            Uri intentUri = Uri.parse(url);
                            otherParam.putAll(SplashScreen.getQueryString(intentUri));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    hashMap.putAll(otherParam);
                }

                if (intent.getSerializableExtra("otherPara") != null) {
                    HashMap<String, String> deeplink_hash = (HashMap<String, String>) intent.getSerializableExtra("otherPara");
                    hashMap.putAll(deeplink_hash);
                }
                if (hashMap != null && hashMap.containsKey("type")) {
                    sendNotificationClickedAnalytics(hashMap.get("type"), intent.getStringExtra("other_para"), hashMap.get("title"), hashMap.get("message"));
                }
                new DeepLinkFunction(hashMap, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ########################## BottomBar Set/ Navigation Start ###########################################//

    private void setUpBottomBar() {

        if (!setUpBottomBarCalled) {

            relative_progress.setVisibility(View.GONE);
            bottomBar.setVisibility(View.VISIBLE);

            Log.d("Activity_Home", "setUpBottomBar");

            //new tab section
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {


                    int position = 0;
                    if (tabId == R.id.tab_home) {
                        mDrawerLayout.closeDrawer(drawer_gravity);
                        position = 0;
                    }
                    if (tabId == R.id.tab_catalogs) {
                        mDrawerLayout.closeDrawer(drawer_gravity);
                        position = 1;
                    }
                    if (tabId == R.id.tab_orders) {
                        mDrawerLayout.closeDrawer(drawer_gravity);


                        position = 2;
                    }
                    if (tabId == R.id.tab_profile) {
                        mDrawerLayout.closeDrawer(drawer_gravity);
                        position = 3;
                    }
                    if (tabId == R.id.tab_more) {
                        mDrawerLayout.openDrawer(drawer_gravity);
                        position = 4;
                    }

                    bottomTabSelection(tabId);

                }
            });

            bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                @Override
                public void onTabReSelected(@IdRes int tabId) {
                    if (tabId == R.id.tab_more)
                        if (mDrawerLayout.isDrawerOpen(drawer_gravity)) {
                            mDrawerLayout.closeDrawer(drawer_gravity);
                        } else {
                            mDrawerLayout.openDrawer(drawer_gravity);
                        }
                }
            });
            setUpBottomBarCalled = true;


            Log.d("Activity_Home", "onCreate: bottomBar end");
        }
    }

    public void bottomTabSelection(int tab_id) {
        try {
            switch (tab_id) {
                case R.id.tab_home:
                    trackEvent("BottomTab", "Click", "Home");
                    if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                        Log.d("Activity_Home", "Fragment_Summary from HomeActivity Tab");
                        int page_version = 1;
                        if (PrefDatabaseUtils.getConfig(this) != null) {
                            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(this), new TypeToken<ArrayList<ConfigResponse>>() {
                            }.getType());
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).getKey().equals("HOME_PAGE_TYPE_VERSION_IN_APP")) {
                                    try {
                                        page_version = Integer.parseInt(data.get(i).getValue());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                        if (page_version == 0) {
                            try {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, new Fragment_Summary()).commit();
                                support_chat_fab.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, new Fragment_Summary()).commitAllowingStateLoss();
                                support_chat_fab.setVisibility(View.VISIBLE);
                            }
                        } else if (page_version == 1) {
                            try {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, new Fragment_Home2()).commit();
                                support_chat_fab.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, new Fragment_Home2()).commitAllowingStateLoss();
                                support_chat_fab.setVisibility(View.VISIBLE);
                            }
                        }

                    } else {
                        try {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, new Fragment_MeetingReport()).commit();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, new Fragment_MeetingReport()).commitAllowingStateLoss();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        }

                    }
                    break;
                case R.id.tab_catalogs:
                    trackEvent("BottomTab", "Click", "Catalog");
                    if (UserInfo.getInstance(this).getGroupstatus().equals("2")) {
                        // For Sales Person
                        try {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, new CatalogHolder()).commit();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, new CatalogHolder()).commitAllowingStateLoss();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // For Normal User
                        Fragment_Product_Tab fragment_product_tab = new Fragment_Product_Tab();
                        Bundle bundle = new Bundle();
                        if (Application_Singleton.Non_CATALOG_POSITION != 0) {
                            bundle.putString("focus_position", String.valueOf(Application_Singleton.Non_CATALOG_POSITION));
                        }
                        fragment_product_tab.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, fragment_product_tab).commit();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, fragment_product_tab).commitAllowingStateLoss();
                            support_chat_fab.setVisibility(View.VISIBLE);
                        }
                    }

                    break;

                case R.id.tab_contacts:
                    trackEvent("BottomTab", "Click", "Contacts");
                    try {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_ContactsHolder()).commit();
                        support_chat_fab.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_ContactsHolder()).commitAllowingStateLoss();
                        support_chat_fab.setVisibility(View.VISIBLE);
                    }
                    break;

                case R.id.tab_orders:
               /* support_chat_fab.setVisibility(View.GONE);
                support_chat_fab.setVisibility(View.VISIBLE);
                support_chat_fab.clearAnimation();
                support_chat_fab.show();*/
                    trackEvent("BottomTab", "Click", "Orders");
                    Fragment_Order_Holder_Version4 fragment_order_holder_version4 = new Fragment_Order_Holder_Version4();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("showOnlyOrders", true);
                    bundle.putBoolean("showOnlyEnquiries", true);
                    bundle.putBoolean("showOnlyManifest", true);
                    bundle.putBoolean("showOnlySellerInvoice", true);
                    bundle.putBoolean("isFromHome", true);
                    fragment_order_holder_version4.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, fragment_order_holder_version4).commit();

                    break;
                case R.id.tab_profile:
                    trackEvent("BottomTab", "Click", "MyBusiness");
                    if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_Inventory()).commit();

                    } else {
                    /*getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, new Fragment_Settings()).commit();*/

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_MyBusiness()).commit();
                        support_chat_fab.setVisibility(View.GONE);
                    }
                    break;
                case R.id.tab_inventory:
                    trackEvent("BottomTab", "Click", "Inventory");
                    if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_Inventory()).commit();

                    }
                    break;
                case R.id.tab_more:
                    trackEvent("BottomTab", "Click", "MoreDrawer");
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    break;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i("GPS", "buildGoogleApiClient: ");
    }

    private void linkIntentPreferences(String type, String id) {
        try {
            linkPreferences.edit().remove("link_type").apply();
            linkPreferences.edit().remove("link_id").apply();

            if (type != null && type.equalsIgnoreCase("sales")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Response_sellingorder order = new Response_sellingorder(id);

                selectedOrder = order;

                //startActivity(new Intent(this, Activity_OrderDetails.class));
                startActivity(new Intent(this, Activity_OrderDetailsNew.class));
            }
            if (type != null && type.equalsIgnoreCase("purchase")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Response_buyingorder order = new Response_buyingorder(id);

                selectedOrder = order;

                //startActivity(new Intent(this, Activity_OrderDetails.class));
                startActivity(new Intent(this, Activity_OrderDetailsNew.class));
            }
            if (type != null && type.equalsIgnoreCase("catalog")) {
                if (id != null && !id.isEmpty()) {
                    Log.e("TAG", "linkIntentPreferences: id not null");
                    getCatalogsData(id);
                }

            }
            if (type != null && type.equalsIgnoreCase("selection")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);

            }
            if (type != null && type.equalsIgnoreCase("buyer")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                CONTAINER_TITLE = "Buyer Details";
                TOOLBARSTYLE = "WHITE";
                Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                Bundle bundle = new Bundle();
                bundle.putString("buyerid", id);
                buyerapproved.setArguments(bundle);
                CONTAINERFRAG = buyerapproved;
                StaticFunctions.switchActivity(this, OpenContainer.class);
            }
            if (type != null && type.equalsIgnoreCase("supplier")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Bundle bundle = new Bundle();
                bundle.putString("sellerid", id);
                CONTAINER_TITLE = "Supplier Details";
                Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                supplier.setArguments(bundle);
                CONTAINERFRAG = supplier;
                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                StaticFunctions.switchActivity(this, OpenContainer.class);
            }

        } catch (Exception e) {

        }
    }

    private void BuildContactData() {
        appContactService = new AppContactService(context);
    }


    private void registerUser() {

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                //After successful registration with Applozic server the callback will come here
                ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.


                //PushNotification Applogic
                registerPushNotify();

                //Add Contacts
                BuildContactData();

                //ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();//To enable the com.applozic Registered Users Contact Note:for disable that you can comment this line of code
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                //If any failure in registration the callback  will come here
            }
        };

        User user = new User();
        user.setUserId(UserInfo.getInstance(this).getUserName()); //userId it can be any unique user identifier
        user.setDisplayName(UserInfo.getInstance(this).getFirstName() + " " + UserInfo.getInstance(this).getLastName()); //displayName is the name of the user which will be shown in chat messages
        //optional
        user.setImageLink("");//optional,pass your image link
        new UserLoginTask(user, listener, this).execute((Void) null);


    }

    private void registerPushNotify() {

        PushNotificationTask pushNotificationTask = null;
        PushNotificationTask.TaskListener listener1 = new PushNotificationTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse) {

            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

            }

        };
        String token = gcmSharedPreferences.getString("gcmtoken", "");
        pushNotificationTask = new PushNotificationTask(token, listener1, this);
        pushNotificationTask.execute((Void) null);
    }


    // ####################### Drawer Menu Set/ Navigation logic Start ####################################//

    private void setupDrawerContent(final NavigationView navigationview) {

        Log.i("TAG", "setupDrawerContent: " + Activity_Home.pref.getString("groupstatus", "0"));
        if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
            navigationView.inflateMenu(R.menu.menu_version_3);
            Log.d("Password Set", "" + UserInfo.getInstance(this).isPasswordSet());
            if (!UserInfo.getInstance(this).isPasswordSet()) {
                Log.d("Password Set=-", "" + UserInfo.getInstance(this).isPasswordSet());
                MenuItem menuItem = navigationview.getMenu().findItem(R.id.settings);
                menuItem.getSubMenu().findItem(R.id.ab_changepass).setTitle("Set Password");
            }
        } else if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            navigationview.inflateMenu(R.menu.moremenusales1);
        } else {
            navigationview.inflateMenu(R.menu.menu_version_3);

        }
        setTextColorForSectionMenuItem(navigationView.getMenu().findItem(R.id.product));
        setTextColorForSectionMenuItem(navigationView.getMenu().findItem(R.id.users));
        setTextColorForSectionMenuItem(navigationView.getMenu().findItem(R.id.settings));


        navigationview.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
           /* case R.id.ab_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, new Fragment_HomeHolder()).addToBackStack(null).commit();
                mDrawerLayout.closeDrawers();
                break;*/
            case R.id.ab_catalogs:
                CONTAINER_TITLE = "Catalogs";
                CONTAINERFRAG = new Fragment_Catalogs();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.ab_help:

                CONTAINER_TITLE = "Help";
                CONTAINERFRAG = new Fragment_Home();
                Intent intent = new Intent(this, OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.HELP);
                startActivity(intent);

                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_contactUs:
                CONTAINER_TITLE = "Contact Us";
                CONTAINERFRAG = new Fragment_ContactUs_Version2();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_AboutUs:
                CONTAINER_TITLE = "About Us";
                CONTAINERFRAG = new Fragment_AboutUs();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_visit:
                CONTAINER_TITLE = "Visits";
                CONTAINERFRAG = new Fragment_Visits();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_register_user:
                CONTAINER_TITLE = "New Registration";
                TOOLBARSTYLE = "WHITE";
                CONTAINERFRAG = new Fragment_Register_New_Version2();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_rejected_buyers:
                CONTAINER_TITLE = "Buyers Rejected";
                CONTAINERFRAG = new Fragment_BuyersRejected();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_rejected_suppliers:
                CONTAINER_TITLE = "Suppliers Rejected";
                CONTAINERFRAG = new Fragment_SupplierRejected();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_buyer_groups:
                if (pref.getString("groupstatus", "0").equals("1")) {
                    menuItem.setVisible(true);
                    CONTAINER_TITLE = "Buyer Groups";
                    CONTAINERFRAG = new Fragment_BuyerGroups();
                    StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                    mDrawerLayout.closeDrawers();
                } else {

                }
                break;
            case R.id.ab_brands:
                CONTAINER_TITLE = "Brands";
                CONTAINERFRAG = new Fragment_Mybrands();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_mybrands:
                if (pref.getString("groupstatus", "0").equals("1") && UserInfo.getInstance(Activity_Home.this).getCompanyType().equals("buyer")) {
                    if (pref.getString("brandadded", "no").equals("no")) {
                        CONTAINER_TITLE = "My Brands";
                        CONTAINERFRAG = new Fragment_AddBrand();
                        StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                        mDrawerLayout.closeDrawers();
                    } else {
                        CONTAINER_TITLE = "My Brands";
                        CONTAINERFRAG = new Fragment_Mybrands();
                        StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                        mDrawerLayout.closeDrawers();
                    }

                } else {

                    CONTAINER_TITLE = "My Brands";
                    CONTAINERFRAG = new Fragment_Mybrands();
                    StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.ab_categories:
                CONTAINER_TITLE = "Categories";
                CONTAINERFRAG = new Fragment_Categories();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_sync:
                CONTAINER_TITLE = "Sync";
                CONTAINERFRAG = new Fragment_Sync();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_invoice:
                CONTAINER_TITLE = "Invoice";
                CONTAINERFRAG = new Fragment_Invoice();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_profile:
                if (UserInfo.getInstance(Activity_Home.this).isGuest()) {
                    mDrawerLayout.closeDrawers();
                    StaticFunctions.ShowRegisterDialog(Activity_Home.this, "Home Profile");
                } else {
                    CONTAINER_TITLE = "Profile";
                    CONTAINERFRAG = new Fragment_Profile();
                    StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                    mDrawerLayout.closeDrawers();
                }
                break;

            case R.id.ab_replacement: {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "replacement");
                hashMap.put("from", "Home Side menu");
                new DeepLinkFunction(hashMap, Activity_Home.this);
            }
            mDrawerLayout.closeDrawers();
            break;

            case R.id.ab_return: {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "return");
                hashMap.put("from", "Home Side menu");
                new DeepLinkFunction(hashMap, Activity_Home.this);
            }
            mDrawerLayout.closeDrawers();
            break;
            case R.id.ab_attendance:
                CONTAINER_TITLE = "Attendance";
                CONTAINERFRAG = new Fragment_Attendance();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.ab_changepass:
                if (UserInfo.getInstance(Activity_Home.this).isGuest()) {
                    mDrawerLayout.closeDrawers();
                    StaticFunctions.ShowRegisterDialog(Activity_Home.this, "Home Change Password");
                } else {
                    CONTAINER_TITLE = "Change Password";
                    CONTAINERFRAG = new Fragment_ChangePassword();
                    Intent changPasswordIntet = new Intent(Activity_Home.this, OpenContainer.class);
                    startActivityForResult(changPasswordIntet, PASSWORD_SET);
                    //StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);

                }
                mDrawerLayout.closeDrawers();
                break;

            case R.id.ab_aboutus_policies:
                CONTAINER_TITLE = "About us & Policies";
                CONTAINERFRAG = new Fragment_AboutUs_Policies();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.ab_wishbook_bank_detail:
                CONTAINER_TITLE = "Wishbook Bank Details";
                CONTAINERFRAG = new Fragment_WishbookBankDetail();
                StaticFunctions.switchActivity(Activity_Home.this, OpenContainer.class);
                mDrawerLayout.closeDrawers();
                break;


            case R.id.ab_logout:
                if (Application_Singleton.canUseCurrentAcitivity()) {
                    new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                            .title("Logout")
                            .content(getResources().getString(R.string.logout_popup))
                            .positiveText("Yes")
                            .negativeText("No")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    LogoutCommonUtils.logout(Activity_Home.this, false);
                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            WishbookEvent wishbookEvent = new WishbookEvent();
                            wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
                            wishbookEvent.setEvent_names("Logout");
                            HashMap<String, String> prop = new HashMap<>();
                            prop.put("logout", "false");
                            wishbookEvent.setEvent_properties(prop);
                            new WishbookTracker(Application_Singleton.getCurrentActivity(), wishbookEvent);
                            dialog.dismiss();
                        }
                    })
                            .show();
                }
                break;
            case R.id.ab_faq:
                Freshchat.showFAQs(Activity_Home.this);
                break;
            case R.id.ab_support_chat:
                Freshchat.showConversations(Activity_Home.this.getApplicationContext());
                break;
            case R.id.ab_change_language:
                Intent languageIntent = new Intent(Activity_Home.this, ActivityLanguage.class);
                startActivityForResult(languageIntent, 1);
                break;
            case R.id.ab_share_app:
                StaticFunctions.switchActivity(Activity_Home.this, Activity_ShareApp.class);
                break;
            case R.id.ab_refer_earn:
                if (UserInfo.getInstance(Activity_Home.this).isGuest()) {
                    mDrawerLayout.closeDrawers();
                    StaticFunctions.ShowRegisterDialog(Activity_Home.this, "Home Profile");
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", "refer_earn");
                    hashMap.put("from", "Home Side menu");
                    new DeepLinkFunction(hashMap, Activity_Home.this);
                }
                mDrawerLayout.closeDrawers();
                break;

            case R.id.ab_join_wa_group:
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "join_whatsapp_group");
                new DeepLinkFunction(hashMap, Activity_Home.this);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.ab_resell_earn:
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "resell_earn");
                new DeepLinkFunction(param, Activity_Home.this);
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
        return false;
    }

    private void setTextColorForSectionMenuItem(MenuItem menuItem) {
        //((TextView) menuItem).setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        if (menuItem != null) {
            if (menuItem.getTitle() != null) {
                SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
                spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_primary)), 0, spanString.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.3f), 0, spanString.length(), 0);
                menuItem.setTitle(spanString);
            }
        }

    }

    // Show Hide menu in Drawer
    public void setMenuAfterCompany() {
        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null) {
            CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getCompanyGroupFlag(), CompanyGroupFlag.class);
            if (companyGroupFlag != null && (companyGroupFlag.getRetailer() || companyGroupFlag.getOnline_retailer_reseller())) {
                if (navigationView.getMenu() != null) {
                    MenuItem item = navigationView.getMenu().findItem(R.id.ab_resell_earn);
                    if (item != null) {
                        item.setVisible(true);
                    }
                }
            }

            /**
             * Don't show join whatsapp group when contain this type wholeasale,manufacture,broker
             */
            if (companyGroupFlag != null && (companyGroupFlag.getWholesaler_distributor() || companyGroupFlag.getManufacturer() || companyGroupFlag.getBroker())) {
                if (navigationView.getMenu() != null) {
                    MenuItem item = navigationView.getMenu().findItem(R.id.ab_join_wa_group);
                    if (item != null) {
                        item.setVisible(false);
                    }
                }
            }

            boolean isRRCModuleEnable = false;
            if (PrefDatabaseUtils.getConfig(context) != null) {
                ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
                }.getType());
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getKey().equals("RRC_CREATE_ENABLE_IN_APP")) {
                        isRRCModuleEnable = true;
                        break;
                    }
                }
            }

            /**
             * Don't show replacement,return create menu for manufaturer
             */
            if (companyGroupFlag != null && companyGroupFlag.getManufacturer() &&
                    !companyGroupFlag.getWholesaler_distributor() &&
                    !companyGroupFlag.getOnline_retailer_reseller() &&
                    !companyGroupFlag.getRetailer()) {
                isRRCModuleEnable = false;
            }

            if (!isRRCModuleEnable) {
                if (navigationView.getMenu() != null) {
                    MenuItem item = navigationView.getMenu().findItem(R.id.ab_return);
                    if (item != null) {
                        item.setVisible(false);
                    }

                    MenuItem item1 = navigationView.getMenu().findItem(R.id.ab_replacement);
                    if (item1 != null) {
                        item1.setVisible(false);
                    }
                }
            }

            /**
             * Temporary remove Refer-Earn
             */


            if(StaticFunctions.checkOrderDisableConfig(this)) {
                if(navigationView.getMenu()!=null) {
                    MenuItem item = navigationView.getMenu().findItem(R.id.ab_refer_earn);
                    if (item != null) {
                        item.setVisible(false);
                    }
                }
            }


        }
    }


    // ####################### Drawer Menu Set/ Navigation logic End ####################################//


    private void checkPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_CONTACTS",
                "android.permission.WRITE_CONTACTS",
        };

        // sequence maintain for check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED |
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED |
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED |
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED |
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED |
                    checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUESTPERMISSION);
            }
        }
    }

    //  ########################## ASk Location Update / Handle Task ######################################## //

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                Log.i("GPS", "Get Location 1");
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            } else {
                Log.i("GPS", "Get Location 2");
                handleNewLocation(location);
            }
        } catch (Exception e) {
            // handle exception due some reason google api client not connected, or location give exception
            e.printStackTrace();
        }

    }

    private static void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        AddressResponse address = new AddressResponse();
        address.setLatitude(String.valueOf(currentLatitude));
        address.setLongitude(String.valueOf(currentLongitude));
        if (UserInfo.getInstance(context).getUserCompanyAddress() != null
                && !UserInfo.getInstance(context).getUserCompanyAddress().isEmpty()) {
            String addressid = UserInfo.getInstance(context).getUserCompanyAddress();
            HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCH, URLConstants.companyUrl((Activity) context, "address", "") + addressid + "/", gson.fromJson(gson.toJson(address), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }


    }

    public static void showBankDetailsDialog(int type) {
        boolean isBankDetailAvailable = false, isPanAvailable = false, isGstAvailable = false, isPaytmAvilable = false;
        boolean isShowDialog = false;
        if (UserInfo.getInstance(context).getBankDetails() != null) {
            isBankDetailAvailable = true;
        }
        if (UserInfo.getInstance(context).getKyc() != null) {
            PostKycGst postKycGst = gson.fromJson(UserInfo.getInstance(context).getKyc(), PostKycGst.class);
            if (postKycGst != null && postKycGst.getGstin() != null) {
                isGstAvailable = true;
            }
            if (UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                if (postKycGst != null && postKycGst.getPan() != null) {
                    isPanAvailable = true;
                }
            } else {
                isPanAvailable = true;
            }
        }

        SharedPreferences pref = context.getSharedPreferences(Constants.WISHBOOK_PREFS, context.MODE_PRIVATE);
        if (!pref.getString("paytm_phone_number", "").isEmpty()) {
            Log.e("TAG", "showBankDetailsDialog:  Paytm Available");
            isPaytmAvilable = true;
        }
        String msg = context.getResources().getString(R.string.show_bank_details_all_empty);
        if (!isBankDetailAvailable && !isPanAvailable && !isGstAvailable && !isPaytmAvilable) {
            msg = context.getResources().getString(R.string.show_bank_details_all_empty);
            isShowDialog = true;
        } else if ((isBankDetailAvailable || isPaytmAvilable) && !isGstAvailable && !isPanAvailable) {
            msg = context.getResources().getString(R.string.show_bank_details_gst_pan_empty);
            isShowDialog = true;
        } else if (!isBankDetailAvailable && !isPaytmAvilable && isGstAvailable && isPanAvailable) {
            msg = context.getResources().getString(R.string.show_bank_details_bank_empty);
            isShowDialog = true;
        }
        if (isShowDialog) {
            try {
                new MaterialDialog.Builder(context)
                        .content(msg)
                        .positiveText("Add Details")
                        .cancelable(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                try {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("type", "tab");
                                    params.put("page", "gst");
                                    new DeepLinkFunction(params, context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .build()
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.e("TAG", "showBankDetailsDialog: All Value True ");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("GPS", "onConnected: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (isAskLocation) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    settingsrequest(mGoogleApiClient, Activity_Home.this);
                    isAskLocation = false;
                }
            }, 5000);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GPS", "onConnectionSuspended: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("GPS", "onConnectionFailed: ");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                Log.i("GPS", "onConnectionFailed: 1 ");
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {

        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    public void settingsrequest(final GoogleApiClient googleApiCLient, final Activity context) {
        Log.i("GPs", "settingsrequest: 1");
        if (context != null) {
            if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                // for get location normal user not for salesperson
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true); //this is the key ingredient

                if (mGoogleApiClient != null) {
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult result) {
                            Log.i("GPS", "onResult: 1");
                            final Status status = result.getStatus();
                            final LocationSettingsStates state = result.getLocationSettingsStates();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    Log.i("GPS", "onResult: Dialog Sucess");
                                    getLocation();
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    if (!mLocationRequestCancel) {
                                        try {
                                            Log.i("GPS", "onResult: Dialog Start");
                                            status.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e("Applicationsett", e.toString());
                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Log.i("GPS", "onResult: Dialog uNAVAILABLE");
                                    break;
                            }
                        }
                    });
                }
            }

        }

    }


    private void SupplierContact(final List<AllContactsChatModel> allContacts) {
        Log.d("Fetching", "Supplier");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Home.this);
        HttpManager.getInstance(Activity_Home.this).request(HttpManager.METHOD.GET, URLConstants.companyUrl(Activity_Home.this, "sellers_approved_chat", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    Log.d("Fetched", "Supplier");
                    response_suppliers = gson.fromJson(response, Response_Suppliers[].class);
                    Log.d("Fetch  Supplier length", response_suppliers.length + "");
                    if (response_suppliers.length > 0) {
                        for (Response_Suppliers supplier : response_suppliers)
                            allContacts.add(new AllContactsChatModel(supplier.getSelling_company_chat_user(), supplier.getSelling_company_name()));
                    }
                    allContacts.add(0, new AllContactsChatModel("WISHBOOK", "Wishbook Support"));
                    final int maxCount = allContacts.size();
                    int loopCount = 0;
                    while (loopCount != maxCount) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (appContactService != null) {
                                        if (allContacts.get(0) != null && allContacts.get(0).getUser_name() != null) {
                                            Contact contact = new Contact();
                                            contact.setUserId(allContacts.get(0).getUser_chat_id());
                                            contact.setFullName(allContacts.get(0).getUser_name());
                                            appContactService.add(contact);
                                        }
                                        allContacts.remove(0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }, 500);
                        loopCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void addContactToChat(List<AllContactsChatModel> allContacts, int i) {
        Contact contact = new Contact();
        contact.setUserId(allContacts.get(i).getUser_chat_id());
        contact.setFullName(allContacts.get(i).getUser_name());
        appContactService.add(contact);
        Log.d("Adding", "Contact" + i);
    }


    // ################################## Crashlytics User Info Set #####################################//


    /**
     * Set Fabric(Crashlytics) user information
     */
    private void setCrashlyticsUserInfo() {
        if (UserInfo.getInstance(Activity_Home.this) != null) {
            if (UserInfo.getInstance(Activity_Home.this).getMobile() != null)
                Crashlytics.setUserIdentifier(UserInfo.getInstance(Activity_Home.this).getMobile());
            if (UserInfo.getInstance(Activity_Home.this).getEmail() != null)
                Crashlytics.setUserEmail(UserInfo.getInstance(Activity_Home.this).getEmail());
            if (UserInfo.getInstance(Activity_Home.this).getUserName() != null)
                Crashlytics.setUserName(UserInfo.getInstance(Activity_Home.this).getUserName());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (Application_Singleton.isMoneySmartEnable) {
            // Money Smart Start
            InitializationHelper.reinitiateInitializationAfterDelay(getApplicationContext());
            // Money Smart End
        }

        switch (requestCode) {
            case 1:
                getAysnContacts(Activity_Home.this);
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISSION TRUE
                    checkWishbookContactSaved();
                    //addWishbookContacts();
                } else {
                    // PERMISSION DENIED
                }
                break;
            case REQUESTPERMISSION:
                if (grantResults.length > 0) {
                    try {
                        // Fill with results
                        Map<String, Integer> perms = new HashMap<String, Integer>();
                        // Initial
                        perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                        perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        // Check for ACCESS_FINE_LOCATION
                        if (perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            checkWishbookContactSaved();
                            //addWishbookContacts();
                        }

                        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mGoogleApiClient.connect();
                            if (isAskLocation) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        settingsrequest(mGoogleApiClient, Activity_Home.this);
                                        isAskLocation = false;
                                    }
                                }, 2000);

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("GPS", "onActivityResult: OK ");
                        if (ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationRequestCancel = true;
                        Log.i("GPS", "onActivityResult: Canceled ");
                        break;
                }
                break;
            case PASSWORD_SET:
                // First time show set password after successfully change password show change password in drawaer
                if (resultCode == Activity.RESULT_OK) {
                    navigationView.getMenu().clear();
                    setupDrawerContent(navigationView);
                    break;
                }
                break;
            case 1000:
                if (resultCode == Activity.RESULT_OK) {
                    showBankDetailsDialog(0);
                }
                break;
        }

        if (requestCode == EXIT_APP && resultCode == 1801) {
            finish();
        }
    }


    /**
     * Call App Version API for check new Update available or not
     * if update available than update dialog display
     * if force-update available than show not cancelable dialog show
     * @param activity
     */
    private void checkRequireUpdateApp(final Activity activity) {
        int versionCode = 1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionCode = versionCode / 10;
            Log.i("TAG", "Version Code==>" + versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, URLConstants.APP_VERSION_URL + "?version_code=" + versionCode + "&platform=Android", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseAppVersion[] responseAppVersion = gson.fromJson(response, ResponseAppVersion[].class);
                    if (responseAppVersion.length > 0) {
                        if (responseAppVersion[0].isUpdate()) {
                            StaticFunctions.showAppUpdateDialog(activity, responseAppVersion[0].isForce_update());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    public void registerUpdateReceiver() {
        if (updateStateFilter == null) {
            updateStateFilter = new IntentFilter();
            updateStateFilter.addAction("com.wishbook.catalog.update-statistic");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, updateStateFilter);
        }
    }

    public void unregisterUpdateReceiver() {
        if (mMessageReceiver != null) {
            try {
                unregisterReceiver(mMessageReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // ############################# Server API Calling  Start ########################################### //

    protected void getConfig() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Home.this);
        HttpManager.getInstance(Activity_Home.this).request(HttpManager.METHOD.GET, URLConstants.CONFIG_URL, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ConfigResponse[] configResponses = gson.fromJson(response, ConfigResponse[].class);
                    if (configResponses != null) {
                        if (configResponses.length > 0) {
                            ArrayList<ConfigResponse> configResponses1 = new ArrayList<ConfigResponse>(Arrays.asList(configResponses));
                            PrefDatabaseUtils.setConfig(Activity_Home.this, Application_Singleton.gson.toJson(configResponses1));
                            configResponse = configResponses1;
                            assignWishbookSupportNumberFromConfig();
                            for (int i = 0; i < configResponses1.size(); i++) {
                                if (configResponses1.get(i).getKey().equalsIgnoreCase("RESELLER_WHATSAPP_GROUP")) {
                                    PrefDatabaseUtils.setPrefResellerWhatsappGroup(Activity_Home.this, configResponses1.get(i).getValue());
                                } else if (configResponses1.get(i).getKey().equalsIgnoreCase("RETAILER_WHATSAPP_GROUP")) {
                                    PrefDatabaseUtils.setPrefRetailerWhatsappGroup(Activity_Home.this, configResponses1.get(i).getValue());
                                } else if (configResponses1.get(i).getKey().equalsIgnoreCase("FASHION_TREND_WHATSAPP_GROUP")) {
                                    PrefDatabaseUtils.setPrefFashionTrendWhatsappGroup(Activity_Home.this, configResponses1.get(i).getValue());
                                } else if (configResponses1.get(i).getKey().equalsIgnoreCase("FULL_SET_WHATSAPP_GROUP")) {
                                    PrefDatabaseUtils.setPrefFullSetWhatsappGroup(Activity_Home.this, configResponses1.get(i).getValue());
                                } else if (configResponses1.get(i).getKey().equalsIgnoreCase("SINGLE_PC_WHATSAPP_GROUP")) {
                                    PrefDatabaseUtils.setPrefSinglePcWhatsappGroup(Activity_Home.this, configResponses1.get(i).getValue());
                                } else if (configResponses1.get(i).getKey().equalsIgnoreCase("COD_FREE_AMOUNT_LIMIT")) {
                                    try {
                                        PrefDatabaseUtils.setPrefCodFreeAmountLimit(Activity_Home.this, Integer.parseInt(configResponses1.get(i).getValue()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void assignWishbookSupportNumberFromConfig() {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("WISHBOOK_SUPPORT_NUMBER")) {
                    try {
                        if(data.get(i).getValue()!=null && !data.get(i).getValue().isEmpty())
                            PrefDatabaseUtils.setPrefWishbookSupportNumberFromConfig(this,data.get(i).getValue());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    break;
                }
            }
        }
    }

    public void sendPlatformInfo(String userid) {

        //get Screen Size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        SCREEN_HEIGHT = height;
        SCREEN_WIDTH = width;

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);

        // get Device Model
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;

        // get Version of App
        String versionName = null;
        String versionCode = null;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("platform", "Android");
        params.put("app_version", versionName);
        params.put("app_version_code", versionCode);
        params.put("device_model", model);
        params.put("brand", brand);
        params.put("operating_system", "Android");
        params.put("operating_system_version", String.valueOf(version));
        params.put("screen_width", String.valueOf(width));
        params.put("screen_height", String.valueOf(height));
        HttpManager.getInstance(context).methodPost(HttpManager.METHOD.POST, URLConstants.userUrl(context, "user_platform", userid), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // ToDo after Post Device Information
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getStatistics(final Context activity) {

        if (((System.currentTimeMillis() / 1000) - statisticsLastCall) > 5) {

            String url = null;
            url = URLConstants.companyUrl(activity, "statistics", "");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) activity);
            HttpManager.getInstance((Activity) activity).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        ResponseStatistics responseStatistics = gson.fromJson(response, ResponseStatistics.class);
                        PrefDatabaseUtils.setPrefStatistics(activity, new Gson().toJson(responseStatistics));
                        UserInfo.getInstance(activity).setWishlistCount(responseStatistics.getWishlist());
                        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                        preferences.edit().putString("cartId", String.valueOf(responseStatistics.getLatest_cart_id())).apply();
                        preferences.edit().putInt("cartcount", responseStatistics.getTotal_cart_items()).apply();
                        statisticsLastCall = System.currentTimeMillis() / 1000;
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }
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
                try {
                    Log.d("TAG", "onServerResponse Auth User: ");
                    Response_User response_user = gson.fromJson(response, Response_User.class);
                    if (response_user != null) {
                        if (response_user.getUserprofile().getUser_approval_status().equalsIgnoreCase("Approved")) {
                            UserInfo.getInstance(activity).setUser_approval_status(true);
                        } else {
                            UserInfo.getInstance(activity).setUser_approval_status(false);
                            UserInfo.getInstance(activity).setGuest(true);
                            UserInfo.getInstance(activity).setCompanyType("buyer");
                        }
                        UserInfo.getInstance(activity).setSuper_User(response_user.isIs_superuser());

                        setUpBottomBar();
                        sendHomeScreenVisited(context);
                        postNotificationFlag();
                        if (Application_Singleton.isMoneySmartEnable) {
                            try {
                                if (response_user.getUserprofile().getMoneysmart_data_status() != null
                                        && response_user.getUserprofile().getMoneysmart_data_status().equals(Constants.MONEYSMART_DATA_STATUS_CORRUPTED)) {
                                    Log.e(StaticFunctions.MONEYTAG, "Data Corrupted");
                                    int numberOfUserInfoRecordsDeleted = context.getContentResolver().delete(UserInfosContentProvider.uris[UserInfosContentProvider.CONTENT_URI_DELETE_INDEX], " _id > 0 ", null);
                                    Log.e(StaticFunctions.MONEYTAG, "NO.====>" + numberOfUserInfoRecordsDeleted);
                                    patchMoneySmartStatus(Constants.MONEYSMART_DATA_STATUS_RESOLVED);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getCompanyProfile(final Activity activity) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    CompanyProfile companyProfile = new Gson().fromJson(response, CompanyProfile.class);
                    if (companyProfile != null) {


                        //user company profile type check

                        HashMap<String, String> userProp = new HashMap();
                        // store company city
                        UserInfo.getInstance(Activity_Home.this).setCompanyCity(companyProfile.getCity());
                        UserInfo.getInstance(Activity_Home.this).setCompanyProfileSet(companyProfile.is_profile_set());
                        if (companyProfile.getCompany_group_flag() != null && companyProfile.getCompany_group_flag().getId() != null) {
                            UserInfo.getInstance(Activity_Home.this).setCompanyGroupFlag(Application_Singleton.gson.toJson(companyProfile.getCompany_group_flag()));
                        }


/*
                    if(!UserInfo.getInstance(Activity_Home.this).isUser_approval_status()){
                        UserInfo.getInstance(Activity_Home.this).setCompanyType("buyer");
                        UserInfo.getInstance(Activity_Home.this).setGuest(true);
                    }
                    else*/

                        if (!companyProfile.getCompany_type_filled()) {
                            // consider Guest
                            UserInfo.getInstance(Activity_Home.this).setCompanyType("buyer");
                            UserInfo.getInstance(Activity_Home.this).setGuest(true);

                       /*     int session = UserInfo.getInstance(Activity_Home.this).getGuestUserSession();
                            UserInfo.getInstance(Activity_Home.this).setGuestUserSession(session + 1);

                            if (session > MaxGuestUserSession) {
                                Intent registerIntent = new Intent(context, Activity_Register.class);
                                registerIntent.putExtra("fromGuestSession", true);
                                context.startActivity(registerIntent);
                                finish();
                                Log.d("TAG", "======================= Guest Session Over 20: ===================");
                            }*/
                            startActivityForResult(new Intent(context, Activity_Register_Version2.class).putExtra("company_group_id", companyProfile.getCompany_group_flag().getId()), EXIT_APP);

                        } else if (companyProfile.getCompany_group_flag().getManufacturer() && !companyProfile.getCompany_group_flag().getWholesaler_distributor() && !companyProfile.getCompany_group_flag().getOnline_retailer_reseller() && !companyProfile.getCompany_group_flag().getBroker() && !companyProfile.getCompany_group_flag().getRetailer()) {
                            UserInfo.getInstance(Activity_Home.this).setCompanyType("seller");
                            UserInfo.getInstance(Activity_Home.this).setGuest(false);
                        } else if (!companyProfile.getCompany_group_flag().getManufacturer() && !companyProfile.getCompany_group_flag().getWholesaler_distributor() && !companyProfile.getCompany_group_flag().getOnline_retailer_reseller() && !companyProfile.getCompany_group_flag().getBroker() && companyProfile.getCompany_group_flag().getRetailer() ||
                                !companyProfile.getCompany_group_flag().getManufacturer() && !companyProfile.getCompany_group_flag().getWholesaler_distributor() && companyProfile.getCompany_group_flag().getOnline_retailer_reseller() && !companyProfile.getCompany_group_flag().getBroker() && !companyProfile.getCompany_group_flag().getRetailer() ||
                                !companyProfile.getCompany_group_flag().getManufacturer() && !companyProfile.getCompany_group_flag().getWholesaler_distributor() && companyProfile.getCompany_group_flag().getOnline_retailer_reseller() && !companyProfile.getCompany_group_flag().getBroker() && companyProfile.getCompany_group_flag().getRetailer()) {
                            UserInfo.getInstance(Activity_Home.this).setCompanyType("buyer");
                            UserInfo.getInstance(Activity_Home.this).setGuest(false);
                        } else {
                            UserInfo.getInstance(Activity_Home.this).setCompanyType("all");
                            UserInfo.getInstance(Activity_Home.this).setGuest(false);
                        }

                        if (companyProfile.getCompany_group_flag().getOnline_retailer_reseller()) {
                            UserInfo.getInstance(Activity_Home.this).setOnline_retailer_reseller(true);
                        } else {
                            UserInfo.getInstance(Activity_Home.this).setOnline_retailer_reseller(false);
                        }

                        if (companyProfile.getCompany_group_flag().getBroker()) {
                            UserInfo.getInstance(Activity_Home.this).setBroker(false);
                        }

                        SharedPreferences pref = getSharedPreferences(Constants.WISHBOOK_PREFS, MODE_PRIVATE);
                        pref.edit().putString("paytm_phone_number", companyProfile.getPaytm_phone_number()).apply();

                        String allCompanyType = null;
                        if (companyProfile.getCompany_group_flag().getManufacturer()) {
                            allCompanyType = "Manufacturer";
                        }

                        if (companyProfile.getCompany_group_flag().getWholesaler_distributor()) {
                            if (allCompanyType != null) {
                                allCompanyType += ", Wholesaler Distributor";
                            } else {
                                allCompanyType = "Wholesaler Distributor";
                            }
                        }

                        if (companyProfile.getCompany_group_flag().getOnline_retailer_reseller()) {
                            if (allCompanyType != null) {
                                allCompanyType += ", Online-Retailer Reseller";
                            } else {
                                allCompanyType = "Online-Retailer Reseller";
                            }
                        }

                        if (companyProfile.getCompany_group_flag().getBroker()) {
                            if (allCompanyType != null) {
                                allCompanyType += ", Broker";
                            } else {
                                allCompanyType = "Broker";
                            }

                        }
                        if (companyProfile.getCompany_group_flag().getRetailer()) {
                            if (allCompanyType != null) {
                                allCompanyType += ", Retailer";
                            } else {
                                allCompanyType = "Retailer";
                            }

                        }
                        UserInfo.getInstance(Activity_Home.this).setAllCompanyType(allCompanyType);


                        if (companyProfile.getCompany_group_flag() != null) {
                            String company_type = "";
                            if (companyProfile.getCompany_group_flag().getManufacturer())
                                userProp.put("Manufacturer", "true");//company_type += "Manufacturer/";
                            if (companyProfile.getCompany_group_flag().getWholesaler_distributor())
                                userProp.put("WholesalerDistributor", "true");//company_type += "Wholesaler/Distributor/";
                            if (companyProfile.getCompany_group_flag().getOnline_retailer_reseller())
                                userProp.put("OnlineRetailerReseller", "true");//company_type += "OnlineRetailer/Reseller/";
                            if (companyProfile.getCompany_group_flag().getBroker())
                                userProp.put("Broker", "true");//company_type += "Broker/";
                            if (companyProfile.getCompany_group_flag().getRetailer())
                                userProp.put("Retailer", "true");//company_type += "Retailer";


                            userProp.put("Type", company_type);
                        }

                        getUserDetails(context);

                        UserInfo.getInstance(context).setCompanyCityName(companyProfile.getCity_name());
                        UserInfo.getInstance(context).setCompanyStateName(companyProfile.getState_name());

                        userProp.put("City", companyProfile.getCity_name());
                        userProp.put("State", companyProfile.getState_name());
                        UserInfo.getInstance(context).setCompanyCityName(companyProfile.getCity_name());
                        UserInfo.getInstance(context).setCompanyStateName(companyProfile.getState_name());

                        UserInfo.getInstance(context).setOrderDisabled(companyProfile.isOrder_disabled());


                        //Get the user object for the current installation
                        FreshchatUser hlUser = Freshchat.getInstance(getApplicationContext()).getUser();

                        hlUser.setFirstName(UserInfo.getInstance(Activity_Home.this).getFirstName());
                        hlUser.setLastName(UserInfo.getInstance(Activity_Home.this).getLastName());
                        hlUser.setEmail(UserInfo.getInstance(Activity_Home.this).getEmail());
                        hlUser.setPhone("+91", UserInfo.getInstance(Activity_Home.this).getMobile());

                        String token = gcmSharedPreferences.getString("gcmtoken", "");
                        if (token != null && token.isEmpty() && Freshchat.getInstance(getApplicationContext()) != null) {
                            Freshchat.getInstance(getApplicationContext()).setPushRegistrationToken(token);
                        }

                        if (hlUser != null && Freshchat.getInstance(getApplicationContext()) != null) {
                            Freshchat.getInstance(getApplicationContext()).setUser(hlUser);
                        }


                        if (userProp != null && userProp.isEmpty() && Freshchat.getInstance(getApplicationContext()) != null) {
                            Freshchat.getInstance(getApplicationContext()).setUserProperties(userProp);
                        }

                        if (companyProfile.getAdvancedcompanyprofile() != null) {
                            UserInfo.getInstance(getApplicationContext()).setResaleDefaultMargin(String.valueOf(companyProfile.getAdvancedcompanyprofile().getResale_default_margin()));
                        }

                        try {
                          /*   JSONObject json = new JSONObject();

                            json.put("Purchased", 1111);


                            Branch.getInstance().userCompletedAction("Purchase",json );



                            Branch.getInstance().userCompletedAction("VIEW_ITEM" );*/


                            if (UserInfo.getInstance(Activity_Home.this).getUserId() != null) {
                                Branch.getInstance().setIdentity(UserInfo.getInstance(Activity_Home.this).getUserId());
                            }

                            //check and fetch branch share link
                            if (!UserInfo.getInstance(Activity_Home.this).isGuest()) {


                                if (companyProfile.getBranch_ref_link() != null
                                        && !companyProfile.getBranch_ref_link().isEmpty()) {

                                    UserInfo.getInstance(activity).setBranchRefLink(companyProfile.getBranch_ref_link());

                                    Log.d("Acitvity_Home", "branch share link found");


                                } else {

                                    Log.d("Acitvity_Home", "branch share link not found");

                                    LinkProperties lp = new LinkProperties()
                                            .setChannel("All")
                                            .setCampaign("Referral Launch 75-100")
                                            .setFeature("Branch Link")
                                            .addTag("company_" + UserInfo.getInstance(activity).getCompany_id());

                                    BranchUniversalObject buo = new BranchUniversalObject()
                                            .setTitle("Earn From Home - Start Fashion Business")
                                            .setContentDescription("Sell Sarees, Kurtis, Dress Materials, to your friends, relatives & customers. Place your orders on Wishbook and we will have products delivered to your customer. COD Available,Install Now!")
                                            .setContentImageUrl("https://cdn.branch.io/branch-assets/1547116558429-og_image.jpeg");

                                    buo.generateShortUrl(Activity_Home.this, lp, new Branch.BranchLinkCreateListener() {
                                        @Override
                                        public void onLinkCreate(String url, BranchError error) {
                                            if (error == null) {
                                                Log.d("Activity_Home", "branch share link created: " + url);
                                                UserInfo.getInstance(activity).setBranchRefLink(url);
                                                patchCompanyBranchRefLink(url);
                                            }
                                        }
                                    });


                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void patchMoneySmartStatus(String status) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        Userprofile userprofile = new Userprofile();
        userprofile.setMoneysmart_data_status(status);
        Response_User response_user = new Response_User();
        response_user.setUserprofile(userprofile);
        HttpManager.getInstance(context).requestPatch(HttpManager.METHOD.PATCH, URLConstants.PROFILE, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(response_user), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.e(StaticFunctions.MONEYTAG, "Data Resolved now logout");
                LogoutCommonUtils.logout(context, false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {


            }
        });
    }

    private void patchCompanyBranchRefLink(String link) {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);

        JsonObject jsonObject;
        CompanyProfile companyProfile = new CompanyProfile();
        Advancedcompanyprofile advancedcompanyprofile = new Advancedcompanyprofile();
        advancedcompanyprofile.setBranch_ref_link(link);
        companyProfile.setAdvancedcompanyprofile(advancedcompanyprofile);

        jsonObject = new Gson().fromJson(new Gson().toJson(companyProfile), JsonObject.class);
        jsonObject.remove("order_disabled");
        jsonObject.remove("is_profile_set");

        HttpManager.getInstance(context).requestPatch(HttpManager.METHOD.PATCH, URLConstants.companyUrl(context, "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.d("Acitvity_Home", "patchCompanyBranchRefLink");
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());

            }
        });


    }

    public void postNotificationFlag() {
        String url = null;
        HashMap<String, String> params = new HashMap<>();
        Profile profile = new Profile();
        com.wishbook.catalog.commonmodels.responses.Userprofile userprofile = new com.wishbook.catalog.commonmodels.responses.Userprofile();
        userprofile.setNotification_disabled(String.valueOf(StaticFunctions.isNotiifactionEnabled(context)));
        profile.setUserprofile(userprofile);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HttpManager.getInstance(context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(context, "", ""), Application_Singleton.gson.fromJson(new Gson().toJson(profile), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    private void PostFCMKey() {
        String token = gcmSharedPreferences.getString("gcmtoken", "");
        if (!token.toString().trim().isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            Log.v("gcmtoken", token);
            if (!gcmSharedPreferences.getBoolean("gcmregisterd", false)) {
                params.put("registration_id", token);
                params.put("device_id", Settings.Secure.getString(Activity_Home.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID));
                params.put("cloud_message_type", "FCM");
                Application_Singleton.getInstance().cleverTapAPI.pushFcmRegistrationId(token, true);
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Home.this);
                HttpManager.getInstance(Activity_Home.this).request(HttpManager.METHOD.POSTJSON, URLConstants.GCM, params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            StaticFunctions.logger(response);
                            gcmSharedPreferences.edit().putBoolean("gcmregisterd", true).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                    }
                });
            }
        } else {
            generateToken();
        }


    }

    private void generateToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    setGcmToken(deviceToken);
                    setOtherPluginToken(deviceToken);
                    PostFCMKey();
                }
            });

        } catch (Exception e) {
            Log.e("FCM", "generateToken: Not Generated" );
            e.printStackTrace();
        }

    }

    private void setGcmToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("gcmtoken", token).apply();
    }

    private void setOtherPluginToken(String token) {
        Freshchat.getInstance(this).setPushRegistrationToken(token);
    }


    private void getCatalogsData(final String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(this);
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "catalogs_expand_true_id", id), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_catalog response_catalog = gson.fromJson(response, Response_catalog.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "DeepLink");
                    bundle.putString("product_id", response_catalog.getId());
                    new NavigationUtils().navigateDetailPage(Activity_Home.this, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }


    // ####################### Server API Calling End  ############################################# //


    // ####################### Fetch User Phone Contact/ Post To Server (OnWishbook Task ) #############################//

    private void getAysnContacts(Context activity_home) {
        if (ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            GetAllContacts gac = new GetAllContacts(activity_home);
            gac.execute();
        }
    }

    private class GetAllContacts extends AsyncTask<Void, Void, Void> {

        Context mContext;

        public GetAllContacts(Context activity_home) {
            this.mContext = activity_home;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
           /* if (Application_Singleton.phoneContacts == null) {
                Log.d("Fetching", "Contacts");
                rtt();
                Application_Singleton.phoneContacts = contactList;
            } else {
                contactList.addAll(Application_Singleton.phoneContacts);
            }*/
            if (UserInfo.getInstance(context).getContactStatus().equals("init")) {
                Log.d("ContactsFetch", "started");
                contactList.addAll(StaticFunctions.reloadPhoneContacts(mContext));
                Log.d("ContactsFetch", "ended");
            } else {

            }

/*
           if(Application_Singleton.reloadPhoneContacts) {
               contactList.addAll(StaticFunctions.reloadPhoneContacts(mContext));
               Application_Singleton.reloadPhoneContacts=false;
               Log.e("Contacts","Reloaded");
           }else{
               contactList.addAll(StaticFunctions.getContactsFromPhone(mContext));
               Log.e("Contacts ","Fetched From Local");
           }
            Log.d("BACKGROUND", "OnWishbook");*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Home.this);
            ArrayList<NameValues> cons = new ArrayList<>();
            UploadContactsModel uploadContactsModel = new UploadContactsModel(cons);

            for (MyContacts contact : contactList) {
                cons.add(new NameValues(contact.getName(), contact.getPhone()));
            }
            uploadContactsModel.setContacts(cons);
            Log.d("Fetching", "WishbookContacts");
            HttpManager.getInstance(Activity_Home.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(context, "contacts_onwishbook", ""), new Gson().fromJson(new Gson().toJson(uploadContactsModel), JsonObject.class), headers, true, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            Log.d("CacheResponce", "OnWishbook");
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                Log.d("Fetched", "WishbookContacts");
                                Log.v("sync response", response);
                                MyContacts[] myContacts = gson.fromJson(response, MyContacts[].class);
                                wishbookcontactList.clear();

                                for (MyContacts myContacts1 : myContacts) {
                                    wishbookcontactList.add(myContacts1);
                                    AllContacts.add(new AllContactsChatModel(myContacts1.getChat_user(), myContacts1.getName()));
                                    //code added for chat contacts
                                }
                                if (wishbookcontactList.size() > 1) {
                                    Log.e("HOME", "onServerResponse: " + wishbookcontactList.get(0).toString());
                                    UserInfo.getInstance(Activity_Home.this).setwishContacts(new Gson().toJson(wishbookcontactList));
                                }


                                ArrayList<MyContacts> localWishbookList = new ArrayList<>();

                                //fetching local wishbook contacts
                                localWishbookList = UserInfo.getInstance(Activity_Home.this).getwishSuggestioncontacts();

                                //checking if local wishbook contacts has something
                                if (localWishbookList.size() >= 1) {

                                    //than for each contact we will check against local contacts and add if not present
                                    for (int i = 0; i < myContacts.length; i++) {
                                        //flag to check if contact present in local list
                                        Boolean contactPresent = false;
                                        for (int j = 0; j < localWishbookList.size(); j++) {
                                            if (localWishbookList.get(j).getCompany_name().equals(myContacts[i].getCompany_name())) {
                                                //Add those contacts that are new
                                                localWishbookList.get(j).setConnected_as(myContacts[i].getConnected_as());
                                                localWishbookList.get(j).setCompany_image(myContacts[i].getCompany_image());
                                                localWishbookList.get(j).setCompany_name(myContacts[i].getCompany_name());
                                                localWishbookList.get(j).setName(myContacts[i].getName());
                                                localWishbookList.get(j).setPhone(myContacts[i].getPhone());
                                                contactPresent = true;
                                            }
                                        }
                                        if (!contactPresent) {
                                            //if not present rhan add
                                            MyContacts myContacts1 = myContacts[i];
                                            if (!myContacts1.getCompany_name().equals("Invited") && !myContacts1.getConnected_as().equals("supplier") && !myContacts1.getConnected_as().equals("buyer")) {
                                                myContacts[i].setIs_visible(true);
                                                localWishbookList.add(myContacts[i]);
                                            }
                                        }
                                    }
                                    UserInfo.getInstance(Activity_Home.this).setwishSuggestionContacts(new Gson().toJson(localWishbookList));
                                } else {
                                    //Add all contacts and save to preference
                                    for (MyContacts myContacts1 : myContacts) {
                                        myContacts1.setIs_visible(true);
                                        if (myContacts1.getPhone().length() >= 10) {
                                            if (!myContacts1.getCompany_name().equals("Invited") && !myContacts1.getConnected_as().equals("supplier") && !myContacts1.getConnected_as().equals("buyer")) {
                                                localWishbookList.add(myContacts1);
                                            }
                                        }
                                    }
                                    UserInfo.getInstance(Activity_Home.this).setwishSuggestionContacts(new Gson().toJson(localWishbookList));
                                }

                                //getting buyer and Supplier
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    }


            );


        }


    }


    // ######################## Wishbook Contact save to User Phone Start  ############################## //

    public boolean contactExists(Activity _activity, String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = _activity.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            if (cur != null) {
                try {
                    int queryResultCount = cur.getCount();
                    cur.moveToFirst();
                    for (int i = 0; i < queryResultCount; i++) {
                        deleteContact(cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                    }
                    if (cur.moveToFirst()) {
                        return true;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cur != null)
                        cur.close();
                }
            }
            return false;
        } else {
            return false;
        }
    }// contactExists

    private void checkWishbookContactSaved() {
        try {
            if (ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                new SaveContactToPhoneAsync().execute();
            } else {
                Log.i("TAG", "checkWishbookContactSaved: Not Granted");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class SaveContactToPhoneAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            addWishbookContacts();
            return null;
        }
    }

    private void addWishbookContacts() {
        try {
            if (ContextCompat.checkSelfPermission(Activity_Home.this, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "====================Ask Permission Add WishBook ===================");
                ActivityCompat.requestPermissions(Activity_Home.this,
                        new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
            } else {

                SharedPreferences preferencesUtils;
                preferencesUtils = this.getSharedPreferences(Constants.WISHBOOK_PREFS, this.MODE_PRIVATE);


                String DisplayName = getResources().getString(R.string.wishbook_contacts_display_name);
                String DisplayName2 = getResources().getString(R.string.wishbook_contacts_display_name3);
                String DisplayName3 = getResources().getString(R.string.wishbook_contacts_display_name4);
                String wishbook_catalog_contact_name = getResources().getString(R.string.wishbook_catalog_contact_name);
                String wishbook_order_contact_name = getResources().getString(R.string.wishbook_order_contact_name);
                String[] strings = new String[]{getResources().getString(R.string.wishbook_contacts_number1_1),
                        getResources().getString(R.string.wishbook_contacts_number1_3),
                        getResources().getString(R.string.wishbook_contacts_number1_7)};

                String[] support_1 = new String[]{PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(this)};


                if (preferencesUtils.getString(DisplayName + "_" + pInfo.versionCode, null) == null)
                    insertContacts(DisplayName, PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(this));

           /*     if (preferencesUtils.getString(DisplayName2 + "_" + pInfo.versionCode, null) == null)
                    insertContacts(DisplayName2, strings);

                if (preferencesUtils.getString(DisplayName3 + "_" + pInfo.versionCode, null) == null)
                    insertContacts(DisplayName3, support_1);

                if (preferencesUtils.getString(wishbook_catalog_contact_name + "_" + pInfo.versionCode, null) == null)
                    insertContacts(wishbook_catalog_contact_name, getResources().getString(R.string.wishbook_catalog_contact));

                if (preferencesUtils.getString(wishbook_order_contact_name + "_" + pInfo.versionCode, null) == null)
                    insertContacts(wishbook_order_contact_name, getResources().getString(R.string.wishbook_orders_contact));*/
                // migrateVersion28();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void insertContacts(String DisplayName, String mobileNumber) {
        contactExists(context, mobileNumber);

        try {
            Log.i("TAG", "insertContacts: Called");

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
            if (mobileNumber != null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());


            }


            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                SharedPreferences preferencesUtils;
                preferencesUtils = this.getSharedPreferences(Constants.WISHBOOK_PREFS, this.MODE_PRIVATE);
                preferencesUtils.edit().putString(DisplayName + "_" + pInfo.versionCode, "done").apply();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void insertContacts(String DisplayName, String[] MobileNumber) {
        try {
            Log.i("TAG", "insertContacts: Called");
            contactExists(context, MobileNumber[0]);

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
                for (int i = 0; i < MobileNumber.length; i++) {
                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber[i])
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
            }
            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                SharedPreferences preferencesUtils;
                preferencesUtils = this.getSharedPreferences(Constants.WISHBOOK_PREFS, this.MODE_PRIVATE);
                preferencesUtils.edit().putString(DisplayName + "_" + pInfo.versionCode, "done").apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
     * Delete contact by it's display name.
     * */
    public void deleteContact(String displayname) {
        String brand = Build.BRAND;
        if (brand != null && (brand.equalsIgnoreCase("Xiaomi") || brand.equalsIgnoreCase("Redmi"))) {
            return;
        }
        try {
            ArrayList<Long> rowcontactId = ContentProviderUtil.getInstance(this).getRawContactIdByName(displayname);
            if (rowcontactId != null && rowcontactId.size() > 0) {
                for (Long updateId :
                        rowcontactId) {
                    ArrayList<ContentProviderOperation> deleteContactOperations = ContentProviderUtil.getInstance(this).getDeleteRawContactOperations(
                            String.valueOf(updateId));
                    ContentProviderResult[] results = ContentProviderUtil.getInstance(this).applyBatch(this,
                            ContactsContract.AUTHORITY, deleteContactOperations);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //######################## Wishbook Contact save to User Phone End ############################## //


    // #########################  MoneySmart Start ############################ //

    @Override
    public void getSMSPermission(final int permission) {
        Log.e(StaticFunctions.MONEYTAG, "onCreate: get Sms Permission");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PermissionRunnable permissionRunnable = new
                        PermissionRunnable(PermissionsCheckHelper.PERMISSION_TYPE_SMS, permission, permissionHandler);
                permissionHandler.postDelayed(permissionRunnable,
                        PermissionsCheckHelper.DELAY_TO_CHECK_IF_ACTIVITY_EXISTS);
            }
        });
    }

    @Override
    public boolean shouldCheckPermission(int i) {
        return true;
    }

    @Override
    public int canShowDialog() {
        return 0;
    }

    @Override
    public void preparationBeforeShowingDialog() {

    }

    @Override
    public void preparationOnDismissingDialog() {

    }


    public void callMoneySmartApi() {
        Map<String, String> params = new HashMap<>();
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        if (MoneySmartInit.isPermissionAvailable(MoneySmartInit.FUNCTIONALITY_ACTIVE_LIBRARY_AVAILABLE_INDEX)) {
            Log.e(StaticFunctions.MONEYTAG, "callMoneySmartApi 0: URL End Point API ==>" + brand);
            if (brand.toLowerCase().equals("vivo") || brand.toLowerCase().equals("xiaomi") || brand.toLowerCase().equals("oppo")) {
                Log.e(StaticFunctions.MONEYTAG, "callMoneySmartApi 1: URL End Point API ==>" + brand);
                CommonHelper.callRESTAPI(params, "url_server_end_point", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(StaticFunctions.MONEYTAG, "Response = " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(StaticFunctions.MONEYTAG, "Error = " + error.getMessage());
                    }
                });
            }
        } else {
            Log.e(StaticFunctions.MONEYTAG, "callMoneySmartApi: Intialize False ");
        }

    }

    // ######################## Money Smart End   ##################################//


    //  ########################### Home Page /Notification Click Analysys Send ###########################

    public void sendHomeScreenVisited(final Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.HOME_EVENT);
        wishbookEvent.setEvent_names("Home_Screen");
        HashMap<String, String> param = new HashMap<>();
        param.put("visited", "true");
        wishbookEvent.setEvent_properties(param);
        new WishbookTracker(context, wishbookEvent);

    }

    public void sendNotificationClickedAnalytics(String type, String otherPara, String title, String message) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Notification_Clicked");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", "Statusbar_Notification");
        HashMap<String, String> hashmap1 = checkNotificationType(type, otherPara, title, message);
        if (hashmap1 != null) {
            if (checkNotificationType(type, otherPara, title, message).size() > 0) {
                prop.putAll(checkNotificationType(type, otherPara, title, message));
            } else {
                prop.put("type", "other");
            }
            wishbookEvent.setEvent_properties(prop);
            new WishbookTracker(context, wishbookEvent);
        }

    }

    public HashMap<String, String> checkNotificationType(String type, String otherPara, String title, String message) {
        HashMap<String, String> prop = new HashMap<>();
        try {
            if (otherPara != null) {
                Type type_token = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> otherParam = new Gson().fromJson(otherPara, type_token);
                if (otherParam != null && otherPara.isEmpty() && otherParam.containsKey("deep_link")) {
                    String url = otherParam.get("deep_link");
                    try {
                        Uri intentUri = Uri.parse(url);
                        HashMap<String, String> query_string = SplashScreen.getQueryString(intentUri);
                        if (query_string.containsKey("utm_source") && query_string.get("utm_source").equals("whatsapp")) {
                            return null;
                        }
                        if ((query_string.containsKey("catalog") || query_string.containsKey("product"))) {
                            if (query_string.containsKey("id")) {
                                prop.put("type", "Buyer Side");
                                prop.put("property", "Marketing - Catalog");
                            } else {
                                prop.put("type", "Buyer Side");
                                prop.put("property", "Marketing - Product List");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            if (type != null && type.equalsIgnoreCase("catalog") && message != null && message.contains("just added")) {
                prop.put("type", "Buyer Side");
                prop.put("property", "Followed brand has new catalog");
            }

            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Sales Order")) {
                if (message != null && message.contains("Received")) {
                    prop.put("type", "Seller Side");
                    prop.put("property", "New Sales order received");
                } else {
                    prop.put("type", "Seller Side");
                    prop.put("property", "Order status update");
                }
            }
            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Purchase Order")) {
                prop.put("type", "Buyer Side");
                prop.put("property", "Order status update");
            }

            if (type != null && type.equalsIgnoreCase("credit_reference_buyer_requested")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Invited for Feedback");
            }

            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Supplier Status")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Seller approval status");
            }

            if (type != null && type.equalsIgnoreCase("catalogwise-enquiry-supplier")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Leads received");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }


}
