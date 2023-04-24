package com.wishbook.catalog.Utils.analysis;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.Fragment_ClassName;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchContentSchema;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.CurrencyType;
import io.branch.referral.util.ProductCategory;

public class WishbookTracker {


    Context context;

    WishbookEvent wishbookEvent;
    private static boolean sendStaggingEvents;
    private static String TAG = WishbookTracker.class.getSimpleName();


    public WishbookTracker(Context context, WishbookEvent wishbookEvent) {
        this.context = context;
        this.wishbookEvent = wishbookEvent;


        this.sendStaggingEvents = false; //make it false after testing completed

        if(!BuildConfig.DEBUG || this.sendStaggingEvents) {
            finalizeEventDestinations();
        }

        //sendEvent();
    }

    private void finalizeEventDestinations() {
        try {
            if (context != null) {
                switch (this.wishbookEvent.getEvent_names()) {

                    // ========================= Login =======================//

                    case "Home_Screen":
                        sendCleverTapEvent();
                        break;
                    case "HOMETEST":
                        break;

                    case "USER PROPERTIES":
                        sendCleverUserData();
                        sendBranchUserData();
                        sendFirebaseUserEvent();
                        this.wishbookEvent.setEvent_track_ga(true);
                        break;

                    case "Onboarding_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        this.wishbookEvent.setEvent_track_ga(true);
                        break;
                    case "Login_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        this.wishbookEvent.setEvent_track_ga(true);
                        break;
                    case "Login_PhoneSubmitted":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        break;
                    case "Login_success":
                        sendFabricLoginEvent(true,wishbookEvent.getEvent_properties());
                        sendBranchRefferralLogin();
                        sendCleverTapEvent();
                        sendCleverPartialData();
                        sendFirebaseLoginEvent(this.wishbookEvent.getEvent_properties());
                        sendBranchEvent(null, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        this.wishbookEvent.setEvent_track_ga(true);
                        break;
                    case "Login_error":
                        sendCleverTapEvent();
                        sendFirebaseLoginEvent(this.wishbookEvent.getEvent_properties());
                        this.wishbookEvent.setEvent_track_ga(true);
                        sendFabricLoginEvent(false,wishbookEvent.getEvent_properties());
                        break;
                    case "Registration_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        this.wishbookEvent.setEvent_track_ga(true);
                        break;
                    case "Registration":
                        //sendBranchRefferralRegister();
                        sendCleverTapEvent();
                        sendBranchEvent(BRANCH_STANDARD_EVENT.COMPLETE_REGISTRATION, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseRegisterEvent(this.wishbookEvent.getEvent_properties());
                        sendFabricSignupEvent(this.wishbookEvent.getEvent_properties());
                        break;
                    case "Logout":
                        Application_Singleton.getInstance().cleverTapAPI.pushFcmRegistrationId(null, false);
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        this.wishbookEvent.setEvent_track_ga(true);
                        Branch.getInstance().logout();
                        Application_Singleton.getInstance().initializeCleverTap();
                        break;


                    //=====================    products ======================//
                    case "Products_Search_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        sendFabricCustomEvent();
                        break;
                    case "Products_SearchResults":
                        sendCleverTapEvent();
                        sendBranchEvent(BRANCH_STANDARD_EVENT.SEARCH, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFBSearchedEvent(wishbookEvent.getEvent_properties());
                        sendFirebaseSearchResultEvent(wishbookEvent.getEvent_properties());
                        sendFabricSearchEvent();
                        break;
                    case "Products_Filter_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        sendFabricCustomEvent();
                        break;
                    case "Products_FilterResults":
                        sendCleverTapEvent();
                        sendBranchEvent(BRANCH_STANDARD_EVENT.VIEW_ITEMS, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "CategoriesList_screen":
                        sendGAEvent();
                        break;
                    case "Product_View":
                        sendGACommerceProductViewEvent(wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        sendFBProductViewedEvent(wishbookEvent.getEvent_properties());
                        sendBranchEvent(BRANCH_STANDARD_EVENT.VIEW_ITEM, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseProductViewEvent(wishbookEvent.getEvent_properties());
                        sendFabricProductViewEvent(wishbookEvent.getEvent_properties());
                        break;
                    case "Product_Share":
                        sendCleverTapEvent();
                        sendBranchEvent(BRANCH_STANDARD_EVENT.SHARE, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        break;


                    //=================== Marketing ===========================//

                    case "Banner_Clicked":
                        sendCleverTapEvent();
                        sendFirebaseEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "Story_Open":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendGAEvent();
                        break;


                    case "StoryProductDetails_View":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        sendGAEvent();
                        break;

                    case "Brand_Follow":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        break;

                    case "Story_Back":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Notification_Received":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Notification_Clicked":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "CreditApplication_screen":
                        firebaseLogScreenEvent();
                        sendCleverTapEvent();
                        sendGAEvent();
                        break;

                    case "CreditApplication_Submitted":
                        sendCleverTapEvent();
                        sendBranchEvent(null, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Users_Invite":
                        sendBranchEvent(null, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(wishbookEvent.getEvent_properties());
                        break;


                    //========================== Seller Event =========================================

                    case "CatalogItem_Add_screen":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "CatalogItem_Add":
                        sendCleverTapEvent();
                        sendBranchEvent(null, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendFabricCustomEvent();
                        break;

                    case "Product_Add_screen":
                        firebaseLogScreenEvent();
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendFabricCustomEvent();
                        break;

                    case "Product_Add":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        sendBranchEvent(null, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Product_StateChange":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "SalesOrdersList_screen":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "SalesOrderDetails_screen":
                        sendCleverTapEvent();
                        break;

                    case "LeadsList_Screen":
                        firebaseLogScreenEvent();
                        sendCleverTapEvent();
                        break;

                    case "LeadDetails_Screen":
                        firebaseLogScreenEvent();
                        sendCleverTapEvent();
                        break;

                    case "Leads_Respond":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        break;


                    case "MyViewersList_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        break;

                    case "MyViewerDetails_page":
                        sendCleverTapEvent();
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "MyViewer_AddAsBuyer":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "MyFollowerList_screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        break;


                    // ========================Ecoomerce Event========================================//

                    case "Enquiry_Screen":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Enquiry_Placed":
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        break;

                    case "Product_AddToWishlist":
                        sendFirebaseAddToWishListEvent(this.wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        break;

                    case "Product_AddToCart":
                        sendBranchAddToCartEvent(wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        sendFBAddtoCartEvent(wishbookEvent.getEvent_properties());
                        sendFirebaseAddToCartEvent(wishbookEvent.getEvent_properties());
                        sendGACommerceAddToCartEvent(wishbookEvent.getEvent_properties());
                        sendFabricAddToCartEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "My_Cart_Screen":
                        sendCleverTapEvent();
                        firebaseLogScreenEvent();
                        break;

                    case "Checkout_Initiated":
                        sendCleverTapEvent();
                        sendFBInitiatedCheckoutEvent(wishbookEvent.getEvent_properties());
                        sendFirebaseCheckOutInitEvent(wishbookEvent.getEvent_properties());
                        sendGACommerceCheckoutInitEvent(wishbookEvent.getEvent_properties());
                        sendFabricStartCheckoutEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "CheckoutPayment_Initiated":
                        sendCleverTapEvent();
                        sendFirebaseCheckOutPaymentInitEvent(wishbookEvent.getEvent_properties());
                        sendGACommerceCheckoutPaymentInitEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "Checkout_Completed":
                        sendBranchRefferralOrder();
                        sendCleverTapCheckoutCompleted(wishbookEvent.getEvent_properties());
                        sendCleverTapEvent();
                        sendFBPurchasedEvent(wishbookEvent.getEvent_properties());
                        sendBranchCheckoutEvent(wishbookEvent.getEvent_properties());
                        sendFirebaseCheckOutCompleteEvent(wishbookEvent.getEvent_properties());
                        sendGACommercePurchaseEvent(wishbookEvent.getEvent_properties());
                        sendFabricPurchaseEvent(wishbookEvent.getEvent_properties());
                        break;


            //==================================Google Analytics Events Moved =============================//
                    case "Screen_View":
                        sendFirebaseScreenViewEvent(wishbookEvent.getEvent_properties());
                        break;

                    case "Track_Campaign":
                        sendFirebaseCampaginTrackEvent(wishbookEvent.getEvent_properties());
                        break;

                    default:
                        if(!this.wishbookEvent.isGa_legacy_event()) {
                            sendCleverTapEvent();
                            sendBranchEvent(BRANCH_STANDARD_EVENT.VIEW_ITEM, wishbookEvent.getEvent_names(), wishbookEvent.getEvent_properties());
                        }
                        sendFirebaseEvent(this.wishbookEvent.getEvent_properties());
                        this.wishbookEvent.setEvent_track_ga(true);
                        this.wishbookEvent.setEvent_track_branch(true);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            customLogException(e);
        }
    }


    public void sendCleverTapEvent() {
        Log.e(TAG, "sendCleverTapEvent: =====>" + wishbookEvent.getEvent_names());
        Application_Singleton.getInstance().cleverTapAPI.pushEvent(wishbookEvent.getEvent_names(), convertCleverTapHashmap());
    }


    public void sendCleverUserData() {
        UserInfo userInfo = UserInfo.getInstance(context);
        HashMap<String, String> prop = new HashMap<>();
        prop.put("Phone", userInfo.getMobile());
        prop.put("Identity", userInfo.getUserId());
        if (userInfo.isGuest()) {
            prop.put("user_type", "Guest");
        } else {
            prop.put("user_type", "Registered");
            prop.put("company", userInfo.getCompanyname());
            prop.put("company_id", userInfo.getCompany_id());
            prop.put("company_type", userInfo.getAllCompanyType());
            prop.put("city", userInfo.getCompanyCityName());
            prop.put("state", userInfo.getCompanyStateName());
            prop.put("Email", userInfo.getEmail());
            if (LocaleHelper.getLanguage(context) != null) {
                if (LocaleHelper.getLanguage(context).equals("en")) {
                    prop.put("language", "English");
                } else if (LocaleHelper.getLanguage(context).equals("hi")) {
                    prop.put("language", "Hindi");
                }
            } else {
                prop.put("language", "English");
            }
        }


        prop.put("Name", userInfo.getFirstName() + " " + userInfo.getLastName());
        prop.put("first_name", userInfo.getFirstName());
        prop.put("user_name", userInfo.getUserName());
        prop.put("MSG-email", "true");
        prop.put("MSG-push", "true");
        prop.put("MSG-sms", "true");

        if (userInfo.getUserStats() != null && !userInfo.getUserStats().isEmpty()) {
            HashMap<String, String> map = Application_Singleton.gson.fromJson(userInfo.getUserStats(), new TypeToken<HashMap<String, String>>() {
            }.getType());
            prop.putAll(map);
        }
        wishbookEvent.setEvent_properties(prop);
        Application_Singleton.getInstance().cleverTapAPI.onUserLogin(convertCleverTapHashmap());
    }

    public void sendCleverPartialData() {
        UserInfo userInfo = UserInfo.getInstance(context);
        HashMap<String, String> prop = new HashMap<>();
        prop.put("Phone", userInfo.getMobile());
        prop.put("Identity", userInfo.getUserId());
        prop.put("MSG-email", "true");
        prop.put("MSG-push", "true");
        prop.put("MSG-sms", "true");

        if (userInfo.getUserStats() != null && !userInfo.getUserStats().isEmpty()) {
            HashMap<String, String> map = Application_Singleton.gson.fromJson(userInfo.getUserStats(), new TypeToken<HashMap<String, String>>() {
            }.getType());
            prop.putAll(map);
        }
        wishbookEvent.setEvent_properties(prop);
        Application_Singleton.getInstance().cleverTapAPI.onUserLogin(convertCleverTapHashmap());
    }


    public void sendFirebaseUserEvent() {
        UserInfo userInfo = UserInfo.getInstance(context);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserId(userInfo.getUserId());
        mFirebaseAnalytics.setUserProperty("Phone", userInfo.getMobile());
        if (userInfo.isGuest()) {
            mFirebaseAnalytics.setUserProperty("user_type", "Guest");
        } else {
            mFirebaseAnalytics.setUserProperty("user_type", "Registered");
            mFirebaseAnalytics.setUserProperty("company", userInfo.getCompanyname());
            mFirebaseAnalytics.setUserProperty("company_id", userInfo.getCompany_id());
            mFirebaseAnalytics.setUserProperty("company_type", userInfo.getAllCompanyType());
            mFirebaseAnalytics.setUserProperty("city", userInfo.getCompanyCityName());
            mFirebaseAnalytics.setUserProperty("state", userInfo.getCompanyStateName());
            mFirebaseAnalytics.setUserProperty("Email", userInfo.getEmail());
            if (LocaleHelper.getLanguage(context) != null) {
                if (LocaleHelper.getLanguage(context).equals("en")) {
                    mFirebaseAnalytics.setUserProperty("language", "English");
                } else if (LocaleHelper.getLanguage(context).equals("hi")) {
                    mFirebaseAnalytics.setUserProperty("language", "Hindi");
                }
            } else {
                mFirebaseAnalytics.setUserProperty("language", "English");
            }
        }


        mFirebaseAnalytics.setUserProperty("Name", userInfo.getFirstName() + " " + userInfo.getLastName());
        mFirebaseAnalytics.setUserProperty("first_name", userInfo.getFirstName());
        mFirebaseAnalytics.setUserProperty("user_name", userInfo.getUserName());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }


    public void sendGAEvent() {
        // Branch code
    }

    public void sendGACommerceProductViewEvent(HashMap<String, String> hashMap) {
        /*try {
            Product product = new Product()
                    .setId(hashMap.get("product_id"))
                    .setName(hashMap.get("product_name"))
                    .setCategory(hashMap.get("category"))
                    .setBrand(hashMap.get("brand"));
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                    .addImpression(product, "Product Detail");
            Tracker t = AnalyticsTrackers.getInstance().getEcommerceTracker();
            t.setScreenName("Product Detail");
            t.send(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public void sendGACommerceAddToCartEvent(HashMap<String, String> hashMap) {
        /*try {
            Log.e(TAG, "sendGACommerceAddToCartEvent: " + wishbookEvent.getEvent_names());
            Product product = new Product()
                    .setId(hashMap.get("product_id"))
                    .setName(hashMap.get("product_name"))
                    .setCategory(hashMap.get("category"))
                    .setBrand(hashMap.get("brand"));
            ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD);
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                    .addProduct(product)
                    .setProductAction(productAction);

            Tracker t = AnalyticsTrackers.getInstance().getEcommerceTracker();
            t.setScreenName("Product Detail");
            t.send(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    public void sendGACommerceCheckoutInitEvent(HashMap<String, String> hashMap) {
      /*  try {
            String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] product_categories = hashMap.get("product_categories").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_total_amt = hashMap.get("catalog_total_amt").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            for (int i = 0; i < product_ids.length; i++) {
                Product product = new Product()
                        .setId(product_ids[i])
                        .setName(catalog_item_names[i])
                        .setCategory(product_categories[i])
                        .setPrice(Double.parseDouble(catalog_total_amt[i]));
                ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT)
                        .setTransactionId(hashMap.get("cart_id"))
                        .setCheckoutStep(1);
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);
                Tracker t = AnalyticsTrackers.getInstance().getEcommerceTracker();
                t.setScreenName("MyCart/List");
                t.send(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    public void sendGACommerceCheckoutPaymentInitEvent(HashMap<String, String> hashMap) {
       /* try {
            String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] product_categories = hashMap.get("product_categories").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_total_amt = hashMap.get("catalog_total_amt").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            for (int i = 0; i < product_ids.length; i++) {
                Product product = new Product()
                        .setId(product_ids[i])
                        .setName(catalog_item_names[i])
                        .setCategory(product_categories[i])
                        .setPrice(Double.parseDouble(catalog_total_amt[i]));
                ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT)
                        .setTransactionId(hashMap.get("cart_id"))
                        .setCheckoutOptions(hashMap.get("payment_method"))
                        .setCheckoutStep(2);
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);
                Tracker t = AnalyticsTrackers.getInstance().getEcommerceTracker();
                t.setScreenName("Cart/ShippingDetail");
                t.send(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    public void sendGACommercePurchaseEvent(HashMap<String, String> hashMap) {
     /*   try {
            String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] product_categories = hashMap.get("product_categories").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_total_amt = hashMap.get("catalog_total_amt").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            for (int i = 0; i < product_ids.length; i++) {
                Product product = new Product()
                        .setId(product_ids[i])
                        .setName(catalog_item_names[i])
                        .setCategory(product_categories[i])
                        .setPrice(Double.parseDouble(catalog_total_amt[i]));
                ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                        .setTransactionId(hashMap.get("cart_id"))
                        .setTransactionRevenue(Double.parseDouble(hashMap.get("cart_value")))
                        .setTransactionShipping(Double.parseDouble(hashMap.get("shipping_fee")));
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);
                Tracker t = AnalyticsTrackers.getInstance().getEcommerceTracker();
                t.setScreenName("Cart/ShippingDetail");
                t.send(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    //  Item Add Screen
    public static void sendScreenEvents(Context context, String category, String eventname,
                                        String source, HashMap<String, String> otherPara) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(category);
        wishbookEvent.setEvent_names(eventname);
        HashMap<String, String> prop = new HashMap<>();
        if (source != null && !source.isEmpty())
            prop.put("source", source);
        if (otherPara != null) {
            prop.putAll(otherPara);
        }
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(context, wishbookEvent);
    }

    public HashMap<String, Object> convertCleverTapHashmap() {
        HashMap<String, Object> prop = new HashMap<>();
        if (this.wishbookEvent.getEvent_properties() != null) {
            for (Map.Entry<String, String> entry : this.wishbookEvent.getEvent_properties().entrySet()) {
                if (entry.getValue() instanceof String) {
                    prop.put(entry.getKey(), (Object) entry.getValue());
                }
            }
        }
        return prop;
    }

    public void sendBranchUserData() {
        UserInfo userInfo = UserInfo.getInstance(context);
        if (userInfo.getUserId() != null) {
            Branch.getInstance().setIdentity(userInfo.getUserId());
        }
    }

    public void sendBranchEvent(BRANCH_STANDARD_EVENT eventType, String eventName, HashMap<String, String> props) {
        // Branch code

        Log.e(TAG, "sendBranchEvent: ===== eventname: " + eventName);
        if (!Branch.getInstance().isUserIdentified()) {
            sendBranchUserData();
        }

        BranchEvent branchEvent = new BranchEvent(eventName);
        if (eventType != null) {
            Log.e(TAG, "sendBranchEvent: =====>" + eventType.getName() + " eventname: " + eventName);
            branchEvent = new BranchEvent(eventType);
            branchEvent.setDescription(eventName);
        }


        if (props != null) {
            for (Map.Entry<String, String> entry : props.entrySet()) {
                if (entry.getValue() instanceof String) {
                    branchEvent.addCustomDataProperty(entry.getKey(), entry.getValue());
                }
            }
        }

        branchEvent.logEvent(context);

        new BranchEvent("open").logEvent(context);

    }


    public void sendBranchAddToCartEvent(HashMap<String, String> hashMap) {

        if (!Branch.getInstance().isUserIdentified()) {
            sendBranchUserData();
        }

        try {
            BranchUniversalObject buo = new BranchUniversalObject()
                    .setCanonicalIdentifier(hashMap.get("product_id"))
                    .setTitle(hashMap.get("product_name"))
                    .setContentMetadata(
                            new ContentMetadata()
                                    .addCustomMetadata("set_type", hashMap.get("set_type"))
                                    .addCustomMetadata("full_set_price", hashMap.get("full_set_price"))
                                    .addCustomMetadata("single_pc_price", hashMap.get("single_pc_price"))
                                    .addCustomMetadata("num_items", hashMap.get("num_items"))
                                    .addCustomMetadata("product_type", hashMap.get("product_type"))
                                    .setProductBrand(hashMap.get("brand"))
                                    .setProductCategory(ProductCategory.APPAREL_AND_ACCESSORIES)
                                    .setContentSchema(BranchContentSchema.COMMERCE_PRODUCT));

            new BranchEvent(BRANCH_STANDARD_EVENT.ADD_TO_CART)
                    .setCurrency(CurrencyType.INR)
                    .setDescription(hashMap.get("product_name"))
                    .setTransactionID(hashMap.get("product_id"))
                    .addCustomDataProperty("source", hashMap.get("source"))
                    .addCustomDataProperty("is_full_catalog_cart", hashMap.get("is_full_catalog_cart"))
                    .addContentItems(buo)
                    .logEvent(context);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBranchCheckoutEvent(HashMap<String, String> hashMap) {

        if (!Branch.getInstance().isUserIdentified()) {
            sendBranchUserData();
        }

        try {
            ArrayList<BranchUniversalObject> buo = new ArrayList<>();
            String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] product_types = hashMap.get("product_types").toString().split(StaticFunctions.COMMASEPRATEDSPACE);

            for (int i = 0; i < product_ids.length; i++) {
                BranchUniversalObject temp = new BranchUniversalObject()
                        .setCanonicalIdentifier(product_ids[i])
                        .setTitle(catalog_item_names[i])
                        .setContentMetadata(new ContentMetadata()
                                .addCustomMetadata("product_type", product_types[i]));
                buo.add(temp);
            }
            new BranchEvent(BRANCH_STANDARD_EVENT.PURCHASE)
                    .setCurrency(CurrencyType.INR)
                    .setTransactionID(hashMap.get("cart_id"))
                    .setRevenue(Double.parseDouble(hashMap.get("cart_value")))
                    .setShipping(Double.parseDouble(hashMap.get("shipping_fee")))
                    .setCoupon(hashMap.get("wb_money_used"))
                    .addCustomDataProperty("discount", hashMap.get("seller_discount"))
                    .addCustomDataProperty("status", hashMap.get("status"))
                    .addCustomDataProperty("payment_method", hashMap.get("payment_method"))
                    .addContentItems(buo)
                    .logEvent(context);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBranchRefferralRegister() {
        if (UserInfo.getInstance(context).isBranchReferral()) {
            Log.e(TAG, "sendBranchRefferralRegister: Fire Event===> referral_register");
            Branch.getInstance().userCompletedAction("referral_register");
        }

    }

    public void sendBranchRefferralLogin() {
        if (UserInfo.getInstance(context).getUserId() != null) {
            Branch.getInstance().setIdentity(UserInfo.getInstance(context).getUserId());
        }
        if (UserInfo.getInstance(context).isBranchReferral()) {
            Log.e(TAG, "sendBranchRefferral referral_login ===>");
            Branch.getInstance().userCompletedAction("referral_login");
        }

    }

    public void sendBranchRefferralOrder() {
        Log.e(TAG, "sendBranchRefferral referral_order ===>");
        if (UserInfo.getInstance(context).getUserId() != null) {
            Branch.getInstance().setIdentity(UserInfo.getInstance(context).getUserId());
        }
        if (UserInfo.getInstance(context).isBranchReferral()) {
            Branch.getInstance().userCompletedAction("referral_order");
        }

    }

    private void customLogException(Exception e) {
        try {
            StringBuffer custom_message = new StringBuffer();
            custom_message.append("User Id===>" + UserInfo.getInstance(context).getUserId() + "\n");
            custom_message.append("Wishbook Tracker Exception==>" + e.toString() + "\n");
            Crashlytics.logException(new Exception(custom_message.toString()));
        } catch (Exception e1) {
            e.printStackTrace();
        }
    }

    public void sendFBAddtoCartEvent(HashMap<String, String> hashMap) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, hashMap.get("product_id"));
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, Application_Singleton.gson.toJson(hashMap));

        if (hashMap.get("is_full_catalog_cart").equals("true")) {
            Double price = 0.0;
            if (hashMap.containsKey("full_set_price") && hashMap.get("full_set_price").contains("-")) {
                String[] priceRangeMultiple = hashMap.get("full_set_price").split("-");
                price = Double.valueOf(priceRangeMultiple[0]);
            } else {
                price = Double.valueOf(hashMap.get("full_set_price"));
            }
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                    price,
                    params);
        } else {
            Double price = 0.0;
            if (hashMap.containsKey("single_pc_price") && hashMap.get("single_pc_price").contains("-")) {
                String[] priceRangeMultiple = hashMap.get("single_pc_price").split("-");
                price = Double.valueOf(priceRangeMultiple[0]);
            } else {
                price = Double.valueOf(hashMap.get("single_pc_price"));
            }
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                    price,
                    params);
        }
    }


    public void sendFBInitiatedCheckoutEvent(HashMap<String, String> hashMap) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        try {
            if (hashMap != null) {
                ArrayList<BranchUniversalObject> buo = new ArrayList<>();
                String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] product_types = hashMap.get("product_types").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                Bundle params = new Bundle();
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, Application_Singleton.gson.toJson(product_ids));
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Application_Singleton.gson.toJson(hashMap));
                params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, product_ids.length);
                params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, Double.parseDouble(hashMap.get("cart_value")), params);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendFBPurchasedEvent(HashMap<String, String> hashMap) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        try {
            if (hashMap != null) {
                ArrayList<BranchUniversalObject> buo = new ArrayList<>();
                String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                Bundle params = new Bundle();
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, Application_Singleton.gson.toJson(product_ids));
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Application_Singleton.gson.toJson(hashMap));
                params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, product_ids.length);
                params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                logger.logPurchase(BigDecimal.valueOf(Double.parseDouble(hashMap.get("cart_value"))), Currency.getInstance("INR"), params);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendFBProductViewedEvent(HashMap<String, String> hashMap) {
        try {
            if (hashMap != null) {
                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                Bundle params = new Bundle();
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Application_Singleton.gson.toJson(hashMap));
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, hashMap.get("product_id"));
                params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, Double.parseDouble(hashMap.get("lowest_price")), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendFBSearchedEvent(HashMap<String, String> hashMap) {
        try {
            if (hashMap != null) {
                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                Bundle params = new Bundle();
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Application_Singleton.gson.toJson(hashMap));
                params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, hashMap.get("keyword"));
                logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendFirebaseAddToCartEvent(HashMap<String, String> prop) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, prop.get("product_id"));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, prop.get("product_name"));
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, prop.get("category"));
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
            bundle.putLong(FirebaseAnalytics.Param.QUANTITY, 1);
            Double price = 0.0;
            if (prop.get("is_full_catalog_cart").equals("true")) {

                if (prop.containsKey("full_set_price") && prop.get("full_set_price").contains("-")) {
                    String[] priceRangeMultiple = prop.get("full_set_price").split("-");
                    price = Double.valueOf(priceRangeMultiple[0]);
                } else {
                    price = Double.valueOf(prop.get("full_set_price"));
                }
            } else {
                if (prop.containsKey("single_pc_price") && prop.get("single_pc_price").contains("-")) {
                    String[] priceRangeMultiple = prop.get("single_pc_price").split("-");
                    price = Double.valueOf(priceRangeMultiple[0]);
                } else {
                    price = Double.valueOf(prop.get("single_pc_price"));
                }
            }
            bundle.putDouble(FirebaseAnalytics.Param.PRICE, price);
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, price);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendFirebaseCheckOutInitEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, prop.get("cart_id"));
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(prop.get("cart_value")));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
    }

    public void sendFirebaseCheckOutPaymentInitEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, prop.get("cart_id"));
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(prop.get("cart_value")));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, bundle);
    }


    public void sendFirebaseCheckOutCompleteEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, prop.get("cart_id"));
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(prop.get("cart_value")));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
        bundle.putDouble(FirebaseAnalytics.Param.SHIPPING, Double.parseDouble(prop.get("shipping_fee")));
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
    }

    public void sendFirebaseRegisterEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, prop.get("information_source"));
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    public void sendFirebaseLoginEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, prop.get("method"));
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public void sendFirebaseSearchResultEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, prop.get("keyword"));
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }

    public void sendFirebaseProductViewEvent(HashMap<String, String> prop) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, prop.get("product_id"));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, prop.get("product_name"));
        bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND, prop.get("brand"));
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, prop.get("category"));
        if (prop.containsKey("lowest_price")) {
            bundle.putDouble(FirebaseAnalytics.Param.PRICE, Double.parseDouble(prop.get("lowest_price")));
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(prop.get("lowest_price")));
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
        }
        firebAddOtherAttributes(bundle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }


    public void sendFirebaseAddToWishListEvent(HashMap<String, String> prop) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, prop.get("product_id"));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, prop.get("product_name"));
            bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND, prop.get("brand"));
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, prop.get("category"));
            if (prop.containsKey("lowest_price")) {
                bundle.putDouble(FirebaseAnalytics.Param.PRICE, Double.parseDouble(prop.get("lowest_price")));
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(prop.get("lowest_price")));
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
            }
            firebAddOtherAttributes(bundle);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFirebaseEvent(HashMap<String, String> prop) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            firebAddOtherAttributes(bundle);
            mFirebaseAnalytics.logEvent(this.wishbookEvent.getEvent_names(), bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendFirebaseScreenViewEvent(HashMap<String, String> prop) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            if(prop.containsKey("screen_name")){
                String screenName = prop.get("screen_name");
                if(context instanceof Activity) {
                    mFirebaseAnalytics.setCurrentScreen((Activity) context, Fragment_ClassName.ScreenName(screenName), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFirebaseCampaginTrackEvent(HashMap<String, String> prop) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            if(prop.containsKey("utm_source"))
                bundle.putString(FirebaseAnalytics.Param.SOURCE, prop.get("utm_source"));
            if(prop.containsKey("utm_medium"))
                bundle.putString(FirebaseAnalytics.Param.MEDIUM, prop.get("utm_medium"));
            if(prop.containsKey("utm_campaign"))
                bundle.putString(FirebaseAnalytics.Param.CAMPAIGN, prop.get("utm_campaign"));
            if(prop.containsKey("utm_content"))
                bundle.putString(FirebaseAnalytics.Param.CONTENT, prop.get("utm_content"));
            mFirebaseAnalytics.logEvent(this.wishbookEvent.getEvent_names(), bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void firebAddOtherAttributes(Bundle bundle) {
        try {
            for (Map.Entry<String, String> entry : this.wishbookEvent.getEvent_properties().entrySet()) {
                if (entry.getKey().equals("cart_value")) {
                    bundle.putDouble(entry.getKey(), Double.parseDouble(entry.getValue()));
                } else {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void firebaseLogScreenEvent() {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            mFirebaseAnalytics.setCurrentScreen((Activity) context, this.wishbookEvent.getEvent_names(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCleverTapCheckoutCompleted(HashMap<String, String> hashMap) {
        try {
            HashMap<String, Object> chargeDetails = new HashMap<>();
            chargeDetails.put("Amount", Double.parseDouble(hashMap.get("cart_value")));
            chargeDetails.put("Payment Mode", hashMap.get("payment_method"));
            chargeDetails.put("Currency", "INR");
            chargeDetails.put("Charged ID", Integer.parseInt(hashMap.get("cart_id")));

            try {
                ArrayList<HashMap<String, Object>> items = new ArrayList<>();
                String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] product_categories = hashMap.get("product_categories").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] catalog_total_amt = hashMap.get("catalog_total_amt").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                String[] product_types = hashMap.get("product_types").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
                for (int i = 0; i < product_ids.length; i++) {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("product_id", product_ids[i]);
                    item.put("product_category", product_categories[i]);
                    item.put("catalog_item_name", catalog_item_names[i]);
                    item.put("catalog_total_amt", catalog_total_amt[i]);
                    item.put("product_types", product_types[i]);
                    items.add(item);

                }
                Application_Singleton.getInstance().cleverTapAPI.pushChargedEvent(chargeDetails, items);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendFabricSignupEvent(HashMap<String, String> prop) {
        SignUpEvent customEvent = new SignUpEvent();
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            if (entry.getValue() instanceof String) {
                customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
            }
        }
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod(prop.get("information_source"))
                .putSuccess(true));
    }

    public void sendFabricLoginEvent(boolean isLoginSucess, HashMap<String, String> prop) {
        Log.e(TAG, "sendFabricLoginEvent: " + prop.get("method"));
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(prop.get("method").toString())
                .putSuccess(isLoginSucess));
    }

    public void sendFabricSearchEvent() {
        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(wishbookEvent.getEvent_properties().get("keyword")));
    }

    public void sendFabricProductViewEvent(HashMap<String, String> hashMap) {
        ContentViewEvent customEvent = new ContentViewEvent();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
            }
        }
        Answers.getInstance().logContentView(customEvent
                .putContentName(hashMap.get("product_name"))
                .putContentType(hashMap.get("category"))
                .putContentId(hashMap.get("product_id")));
    }

    public void sendFabricAddToCartEvent(HashMap<String, String> hashMap) {
        AddToCartEvent customEvent = new AddToCartEvent();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
            }
        }
        Double price = 0.0;
        if (hashMap.get("is_full_catalog_cart").equals("true")) {
            if (hashMap.containsKey("full_set_price") && hashMap.get("full_set_price").contains("-")) {
                String[] priceRangeMultiple = hashMap.get("full_set_price").split("-");
                price = Double.valueOf(priceRangeMultiple[0]);
            } else {
                price = Double.valueOf(hashMap.get("full_set_price"));
            }
        } else {
            if (hashMap.containsKey("single_pc_price") && hashMap.get("single_pc_price").contains("-")) {
                String[] priceRangeMultiple = hashMap.get("single_pc_price").split("-");
                price = Double.valueOf(priceRangeMultiple[0]);
            } else {
                price = Double.valueOf(hashMap.get("single_pc_price"));
            }
        }
        Answers.getInstance().logAddToCart(customEvent
                .putItemPrice(BigDecimal.valueOf(price))
                .putCurrency(Currency.getInstance("INR"))
                .putItemName(hashMap.get("product_name"))
                .putItemType(hashMap.get("category"))
                .putItemId(hashMap.get("product_id")));

    }

    public void sendFabricStartCheckoutEvent(HashMap<String, String> hashMap) {
        try {
            StartCheckoutEvent customEvent = new StartCheckoutEvent();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getValue() instanceof String) {
                    customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
                }
            }
            Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                    .putTotalPrice(BigDecimal.valueOf(Double.parseDouble(hashMap.get("cart_value"))))
                    .putCurrency(Currency.getInstance("INR")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFabricPurchaseEvent(HashMap<String, String> hashMap) {
        try {
            String[] product_ids = hashMap.get("product_ids").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_item_names = hashMap.get("catalog_item_names").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] product_categories = hashMap.get("product_categories").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            String[] catalog_total_amt = hashMap.get("catalog_total_amt").toString().split(StaticFunctions.COMMASEPRATEDSPACE);
            for (int i = 0; i < product_ids.length; i++) {
                PurchaseEvent customEvent = new PurchaseEvent();
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
                    }
                }
                boolean status = true;
                if (hashMap.containsKey("status")) {
                    if (hashMap.get("status").equals("Success")) {
                        status = true;
                    }
                }
                Answers.getInstance().logPurchase(customEvent
                        .putItemPrice(BigDecimal.valueOf(Double.parseDouble(catalog_total_amt[i])))
                        .putCurrency(Currency.getInstance("INR"))
                        .putItemName(catalog_item_names[i])
                        .putItemType(product_categories[i])
                        .putItemId(product_ids[i])
                        .putSuccess(status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendFabricCustomEvent() {
        CustomEvent customEvent = new CustomEvent(wishbookEvent.getEvent_names());
        for (Map.Entry<String, String> entry : this.wishbookEvent.getEvent_properties().entrySet()) {
            if (entry.getValue() instanceof String) {
                customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
            }
        }
        Answers.getInstance().logCustom(customEvent);
    }


}
