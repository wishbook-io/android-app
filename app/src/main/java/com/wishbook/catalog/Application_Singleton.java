package com.wishbook.catalog;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.Md5FileNameGeneratorExt;
import com.wishbook.catalog.commonmodels.CheckInmodel;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerFull;
import com.wishbook.catalog.commonmodels.responses.Response_Invoice;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;
import com.wishbook.catalog.commonmodels.responses.SharedByMe;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.rrc.RRCHandler;
import com.wishbook.catalog.rest.WishbookClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import co.in.moneysmart.common.MoneySmartInit;
import co.in.moneysmart.common.util.GoogleApiClientBuilderHelper;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

import static com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;

//import com.appsee.Appsee;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

/**
 * Created by Vigneshkarnika on 19/03/16.
 */
public class Application_Singleton extends MultiDexApplication implements GoogleApiClientBuilderHelper {
    public static final String EXTRAID = null;
    public static String CONTAINER_TITLE = "Details";
    public static String TOOLBARSTYLE = "BLUE";
    private static Application_Singleton ourInstance = new Application_Singleton();
    public static Gson gson = new GsonBuilder().serializeNulls().create();
    public static Object selectedOrder = null;
    public static Response_Invoice selectedInvoice = null;
    public static ArrayList<ProductObj> selectedCatalogProducts = new ArrayList<>();
    public static ProductObj selectedProduct = null;
    public static ArrayList<CheckInmodel> selectedBuyer = new ArrayList<>();
    public static Response_Suppliers selectedSupplier = null;
    public static Response_Buyer selectedbuyer = null;
    public static Response_Buyer navselectedBuyer = null;
    public static Response_BuyerFull navselectedBuyerfull = null;
    public static Response_meeting currentmeeting = null;
    public static Response_ShareStatus response_shareStatus = null;
    public static DisplayImageOptions options;
    public static CatalogMinified selectedshareCatalog = null;
    public static Response_catalogMini selectedbrowseCatalog = null;
    // public static Response_catalog_view selectedCatalogview=null;
    public static SharedByMe selectedshare = null;
    public static String Token = "";
    public static Fragment CONTAINERFRAG;
    public static Response_BuyerFull navselectedBuyerFull;
    public static Boolean errorLogoutpopshown = false;
    public static Boolean errorNoInternetShown = false;
    public static List<MyContacts> phoneContacts = null;
    public static String adapterFocusPosition = "adapterFocusPostion";
    public static Invoice purchaseInvoice = null;
    public static boolean isPublicTrusted = false;
    public static boolean logoutCalled = false;
    public static boolean unfortunateLogoutCalled = false;
    public static int unfortunateLogoutCalledCount = 0;
    public static CatalogMinified shareCatalogHolder = null;
    public static String RegisterName = null;
    public static String MEETING_ID = null;
    public static boolean isAskLocation = false;
    public static String isFromGallery = null;

    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    public static Response_catalog response_catalog;

    public static Response_catalog getResponse_catalog() {
        return response_catalog;
    }

    public static void setResponse_catalog(Response_catalog response_catalog) {
        Application_Singleton.response_catalog = response_catalog;
    }

    public static Application_Singleton getInstance() {
        return ourInstance;
    }

    public static int selectedInnerTabContacts;
    public static int selectedInnerTabOrders = 1;
    public static int selectedInnerSubTabOrders = 0;

    public static int selectedCatalogInnerSubTab = 2;
    public static Boolean isOnWishBookContactFetch = false;
    public static ArrayList<ConfigResponse> configResponse = null;
    public static int BUYER_SEARCH_REQUEST_CODE = 200;
    public static int BUYER_REMOVE_REQUEST_CODE = 300;
    public static int FABRIC_SEARCH_REQUEST_CODE = 2100;
    public static int WORK_SEARCH_REQUEST_CODE = 2200;
    public static int STYLE_SEARCH_REQUEST_CODE = 3200;
    public static int ADD_CATALOG_REQUEST_CODE = 3100;
    public static int ADD_NEW_BUYER_REQUEST_CODE = 600;
    public static int ADD_NEW_BUYER_RESPONSE_CODE = 601;
    public static int MULTIIMAGE_SELECT_REQUEST_CODE = 900;
    public static int MULTIIMAGE_SCREEN_REQUEST_CODE = 1500;
    public static int MULTIIMAGE_PRODUCT_REQUEST_CODE = 1700;
    public static int CAMERA_IMAGE_REQUEST_CODE = 1900;
    public static int CAMERA_IMAGE_RESPONSE_CODE = 1901;


    public static int SELECT_SUPPLIER_REQUEST_CODE = 700;
    public static int SELECT_SUPPLIER_REQUEST_CODE_RECENT = 710;
    public static int SELECT_SUPPLIER_RESPONSE_CODE = 701;
    public static int EXISTING_BUYER_SEARCH_REQUEST_CODE = 800;
    public static int BROKER_ADD_REQUEST_CODE = 900;
    public static int ADD_CATALOG_REQUEST = 1000;
    public static int STORY_VIEW_REQUEST_CODE = 4000;
    public static int SUPPLIER_SEARCH_REQUEST_CODE = 2300;
    public static int _REQUEST_CODE = 2300;
    public static boolean IS_COMPANY_RATING_GET = false;

    public static int New_ORDER_COUNT = 0;

    public static int selectedInnerTabCatalog = 0;
    public static HashMap<String, String> deep_link_filter = null;
    public static HashMap<String, String> deep_link_filter_non_catalog = null;
    public static int Non_CATALOG_POSITION = 0;


    //tabs and Subtabs
    public static int HOME = 0;
    public static int CATALOGS = 1;
    public static int USERS = 2;
    public static int ORDERS = 3;
    public static int SETTINGS = 4;
    public static int INVENTORY = 4;

    public static int CATALOG_LIMIT = 10;
    public static int CATALOG_INITIAL_OFFSET = 0;


    public static boolean contactPermissionDenied = false;



    public static SharedPreferences tutorial_pref;

    public static Boolean reloadPhoneContacts = true;

    public static int MaxGuestUserSession = 10;

    public static int StoryClickPostion = 0;

    public static boolean isChangeCreditScrore;

    public static boolean isAskedFirstTimeAfterApply;


    public static WishbookClient nomalService = null;
    public static WishbookClient longCacheService = null;
    public static WishbookClient pullToRefreshService = null;
    public static WishbookClient noCacheService = null;

    public static OkHttpClient baseClient = null;


    private static Activity currentForegroundActivity;
    private static ArrayList<Activity> currentActivities;

    public static CleverTapAPI cleverTapAPI;

    private static boolean isInBackground = false;
    public static Set<RRCHandler.RRCHandlerListner> rrcHandlerMultipleListner;
    public static boolean isMoneySmartEnable = false;

    public static String SMSTAG = "WBSMS";


    public Application_Singleton() {
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        Log.d("APP", "onCreate: Start");

        // to solve tls v1.2 issue in android 4.4.4
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                ProviderInstaller.installIfNeeded(this);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }


        currentActivities = new ArrayList<>();
        initializeCleverTap();
        registerActivityLifecycleCallbacks(new WishbookLifecycleCallback());
        if (BuildConfig.DEBUG) {
            Crashlytics crashlytics = new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build();

            //Fabric.with(this, crashlytics);
            Fabric.with(this, new Crashlytics());
        } else {
            Fabric.with(this, new Crashlytics());
        }
        Log.d("APP", "onCreate: crashlytics end");


        if(Application_Singleton.isMoneySmartEnable) {
            try {
                //if(startMoneySmart) {

                MoneySmartInit.googleApiClientBuilderHelper = this;
                MoneySmartInit.sApplication = this;
                MoneySmartInit.sContext = getApplicationContext();
                if (UserInfo.getInstance(this) != null && UserInfo.getInstance(this).getUserId() != null && !UserInfo.getInstance(this).getUserId().isEmpty() && UserInfo.getInstance(this).isCreditRatingApply()) {
                    checkSMSPermissionInit(getApplicationContext());

                } else {
                    //User not logged in
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // }


        UserInfo.getInstance(this).setContactStatus("init");
        UserInfo.getInstance(this).setWishBookContactStatus("init");





        ourInstance = this;




        Log.d("APP", "onCreate: Realm end");

        gson = new Gson();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.uploadempty)
                .showImageOnFail(R.drawable.uploadempty)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.uploadempty)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        initImageLoader(getApplicationContext());

        Log.d("APP", "onCreate: initImageLoader end");
       /* AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP).enableAdvertisingIdCollection(true);
        AnalyticsTrackers.getInstance().getEcommerceTracker();*/
        Log.d("APP", "onCreate: AnalyticsTrackers end");

        tutorial_pref = StaticFunctions.getTutorialAppSharedPreferences(getApplicationContext());

        initializeFresco();
        initializeBranchIO();
        FirebaseApp.initializeApp(this);

        Log.d("APP", "onCreate: Fresco end");

        try {
            // According to new facebook lib it will be auto initialize
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppEventsLogger.activateApp(this);


        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        }

        Log.d("APP", "onCreate:  end");

    }

    @Override
    protected void attachBaseContext(Context base) {
        // if(Application_Singleton.isMoneySmartEnable) {
        try {
            Context context = base;
            MoneySmartInit.sContext = context;
            super.attachBaseContext(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }


    public static String getAPIURL() {

        String APP_URL = "https://app.wishbook.io";
        // String APP_URL ="https://app.wishbook.io:9001";

        //String APP_URL ="http://b2b.wishbook.io";
        if (BuildConfig.FLAVOR.equals("stagging")) {
            APP_URL = "http://b2b.wishbook.io";
        }


        //APP_URL = "https://web.wishbook.io";
        // APP_URL = "http://192.168.0.6:8000";
        //  APP_URL = "https://app.wishbook.io";
        // APP_URL = "https://web.wishbook.io";

        return APP_URL;

    }

    public static String getCheckoutApi() {

        //String CHECKOUT_URL = "https://web.wishbook.io";
        String CHECKOUT_URL = "https://app.wishbook.io";

        //String APP_URL ="http://b2b.wishbook.io";
        if (BuildConfig.FLAVOR.equals("stagging")) {
            CHECKOUT_URL = "http://b2b.wishbook.io";
        }

       // CHECKOUT_URL = "http://192.168.0.6:8000";

        return CHECKOUT_URL;

    }

    public static boolean isStagging() {
        if (BuildConfig.DEBUG) {
            return false; //done for CRIF
        }
        return false;
    }


   /* public static synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        Tracker t = analyticsTrackers.get(AnalyticsTrackers.Target.APP);
        t.enableAdvertisingIdCollection(true);
        return t;
    }*/


    public static void trackEvent(String categoryId, String actionId, String labelId) {


      /*  // Get tracker.
        Tracker t = getGoogleAnalyticsTracker();

// Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categoryId)
                .setAction(actionId)
                .setLabel(labelId)
                .build());*/

      /*  try {
            Map<String, Object> appsee_prop = new HashMap<>();
            appsee_prop.put("actionId", actionId);
            appsee_prop.put("labelId", labelId);
            Appsee.addEvent(categoryId, appsee_prop);
        } catch (Exception e){
            e.printStackTrace();
        }*/

        HashMap<String,String> prop = new HashMap<>();
        prop.put("action",actionId);
        prop.put("label",labelId);
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_names(categoryId);
        wishbookEvent.setEvent_properties(prop);
        wishbookEvent.setGa_legacy_event(true);
        new WishbookTracker(getInstance().getApplicationContext(),wishbookEvent);

    }

    public static void trackCampaign(String campaignData,Context context) {

        // Get tracker.
    /*    Tracker t = getGoogleAnalyticsTracker();
        t.setScreenName("Setcampaign");
        if (campaignData != null && !campaignData.isEmpty()) {
            t.send(new HitBuilders.ScreenViewBuilder()
                    .setCampaignParamsFromUrl(campaignData)
                    .build()
            );
        }*/

        try {
            if (campaignData != null && !campaignData.isEmpty()) {
                HashMap<String,String> prop = new HashMap<>();
                prop.put("screen_name","Setcampaign");
                prop.putAll(SplashScreen.getQueryString(Uri.parse(campaignData)));
                WishbookEvent wishbookEvent = new WishbookEvent();
                wishbookEvent.setEvent_names("Track_Campaign");
                wishbookEvent.setEvent_properties(prop);
                new WishbookTracker(context,wishbookEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trackScreenView(String screenName, Context context) {
        /*try {
            if(context!=null) {
                if (!Fragment_ClassName.ScreenName(screenName).equals("")) {
                    Tracker t = getGoogleAnalyticsTracker();
                    t.setScreenName(Fragment_ClassName.ScreenName(screenName));
                    //Set Username
                    t.set("&uid", UserInfo.getInstance(context).getUserName());
                    // Send a screen view.
                    t.send(new HitBuilders.AppViewBuilder().build());
                    if(context!=null) {
                        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
                        GoogleAnalytics.getInstance(this).dispatchLocalHits();
                    }

                }
            } else {
                Log.e("TAG", "trackScreenView: app is in background");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        HashMap<String,String> prop = new HashMap<>();
        prop.put("screen_name",screenName);
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_names("Screen_View");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(context,wishbookEvent);
    }

    public static void forceCrash() {

        throw new IndexOutOfBoundsException("This is test crash");

    }






    public static void initImageLoader(Context context) {
        if (context.getExternalCacheDir() != null) {

            Builder config = new Builder(context);
            config.threadPriority(Thread.MAX_PRIORITY);
            config.threadPoolSize(Thread.MAX_PRIORITY);
            config.denyCacheImageMultipleSizesInMemory();
            config.diskCache(new UnlimitedDiskCache(context.getExternalCacheDir(), null, new Md5FileNameGeneratorExt()));
            config.diskCacheSize(500 * 1024 * 1024); // 50 MiB
            config.tasksProcessingOrder(QueueProcessingType.LIFO);

            //  config.writeDebugLogs(); // Remove for release app

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config.build());
        }

    }

    /**
     * Fresco Initialize before used , Define Image Cache
     */
    private void initializeFresco() {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)//
                .setMaxCacheSize(20 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(5 * ByteConstants.MB)
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();

        Fresco.initialize(this, imagePipelineConfig);
    }

    /**
     * Application level Initialize Branch.IO
     */
    private void initializeBranchIO() {
        if (BuildConfig.DEBUG) {
            // Branch logging for debugging
            Branch.enableLogging();
        }

        // Branch object initialization
        Branch.getAutoInstance(this);

        // Branch.enableLogging();
        Branch.setPlayStoreReferrerCheckTimeout(5000);
        //branch.setIdentity(UUID.randomUUID().toString());


    }


    /**
     * Application level  Initialize clevertTap
     */
    public  void initializeCleverTap() {
        cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
        cleverTapAPI.createNotificationChannel(getApplicationContext(), "com.wishbook.catalog.android", getString(R.string.wishbook_channel), getString(R.string.wishbook_channel), NotificationManager.IMPORTANCE_MAX, true);
        if (BuildConfig.DEBUG) {
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
        } else {
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.OFF);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


    @Override
    public void assignGoogleAPIs(GoogleApiClient.Builder builder) {
        if (Application_Singleton.isMoneySmartEnable) {
            builder.addApi(LocationServices.API);
        }

    }

    public boolean checkSMSPermissionInit(Context context) {
        String[] permissions = {
                "android.permission.READ_SMS",
                "android.permission.RECEIVE_SMS"
        };
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }

    }


    public static boolean canUseCurrentAcitivity() {
        return currentActivities.size() > 0;
    }

    public static Activity getCurrentActivity() {
        for (int i = 0; i < currentActivities.size(); i++) {
            Log.d("LifeCycle", ": " + currentActivities.get(i).getClass().getSimpleName());
        }
        if (currentActivities.size() > 0)
            return currentActivities.get((currentActivities.size() - 1));
        else
            return null;
    }

    public static String getResourceString(int id) {
        return getCurrentActivity().getResources().getString(id);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    public void loadImage(Context context, String imagepath, SimpleDraweeView image) {
        try {
            if (imagepath.contains(".gif")) {
                DraweeController controller =
                        Fresco.newDraweeControllerBuilder()
                                .setUri(imagepath)
                                .setAutoPlayAnimations(true)
                                .build();
                image.setController(controller);
            } else {
                Uri uri = Uri.parse(imagepath);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(image.getController())
                        .build();
                image.setController(controller);
                image.setImageURI(uri);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class WishbookLifecycleCallback implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            currentForegroundActivity = activity;
            if (!currentActivities.contains(activity)) {
                currentActivities.add(activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            currentForegroundActivity = activity;
            if (!currentActivities.contains(activity)) {
                currentActivities.add(activity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentForegroundActivity = activity;
            if (!currentActivities.contains(activity)) {
                currentActivities.add(activity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            currentForegroundActivity = null;
            if (currentActivities.contains(activity)) {
                currentActivities.remove(activity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            currentForegroundActivity = null;
            if (currentActivities.contains(activity)) {
                currentActivities.remove(activity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            currentForegroundActivity = null;
            if (currentActivities.contains(activity)) {
                currentActivities.remove(activity);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            currentForegroundActivity = null;
            if (currentActivities.contains(activity)) {
                currentActivities.remove(activity);
            }
        }
    }
}
