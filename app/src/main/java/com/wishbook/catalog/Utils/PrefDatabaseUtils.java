package com.wishbook.catalog.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.login.LoginManager;

public class PrefDatabaseUtils {

    private static final String TAG = PrefDatabaseUtils.class.getSimpleName();

    private static final String PREF_GCM_NOTIFICATION = "PREF_GCM_NOTIFICATION";
    private static final String PREF_WISHBOOK_CONTACTS = "PREF_WISHBOOK_CONTACTS";
    private static final String PREF_APPINTRO = "PREF_APPINTRO";
    private static final String PREF_STATISTICS = "PREF_STATISTICS";
    private static final String PREF_STORY_COMPLETION = "PREF_STORY_COMPLETION";
    private static final String PREF_STORY_COMPLETION_1 = "PREF_STORY_COMPLETION_1";
    private static final String PREF_KEEP_SELLING_STOCK = "PREF_KEEP_SELLING_STOCK";
    private static final String PREF_WISHLIST_PRODUCT = "PREF_WISHLIST_PRODUCT";
    private static final String PREF_VIEW_PRODUCT_COUNT = "PREF_VIEW_PRODUCT_COUNT";
    private static final String PREF_LOGIN = "PREF_LOGIN";
    private static final String PREF_RESELLER_WHATSAPP_GROUP = "PREF_RESELLER_WHATSAPP_GROUP";
    private static final String PREF_RETAILER_WHATSAPP_GROUP = "PREF_RETAILER_WHATSAPP_GROUP";
    private static final String PREF_FULL_SET_WHATSAPP_GROUP = "PREF_FULL_SET_WHATSAPP_GROUP";
    private static final String PREF_SINGLE_PC_WHATSAPP_GROUP = "PREF_SINGLE_PC_WHATSAPP_GROUP";
    private static final String PREF_FASHION_TREND_WHATSAPP_GROUP = "PREF_FASHION_TREND_WHATSAPP_GROUP";
    private static final String PREF_CONFIG = "PREF_CONFIG";
    private static final String PREF_CATEGORY = "PREF_CATEGORY";
    private static final String PREF_SEARCH_HISTORY = "PREF_SEARCH_HISTORY";
    private static final String PREF_COD_FREE_AMOUNT_LIMIT = "PREF_COD_FREE_AMOUNT_LIMIT";
    private static final String PREF_LAST_CART_FULL_COD_VERIFY = "PREF_LAST_CART_FULL_COD_VERIFY";
    private static final String PREF_FACEBOOK_LOGIN = "PREF_FACEBOOK_LOGIN";
    private static final String PREF_FACEBOOK_PAGE_ACCESS_TOKEN = "PREF_FACEBOOK_PAGE_ACCESS_TOKEN";
    private static final String PREF_WISHBOOK_SUPPORT_NUMBER_FROM_CONFIG = "PREF_WISHBOOK_SUPPORT_NUMBER_FROM_CONFIG";


    public static String getGCMNotificationData(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_GCM_NOTIFICATION, null);
        }
        return null;
    }

    public static void setGCMNotificationData(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_GCM_NOTIFICATION, data).apply();
        }

    }

    public static String getPrefWishlistProductData(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_WISHLIST_PRODUCT, null);
        }
        return null;

    }

    public static void setPrefWishlistProductData(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_WISHLIST_PRODUCT, data).apply();
        }

    }

    public static String getPrefViewProductCount(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_VIEW_PRODUCT_COUNT, null);
        }
        return null;
    }

    public static void setPrefViewProductCount(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_VIEW_PRODUCT_COUNT, data).apply();
        }
    }

    public static String getPrefResellerWhatsappGroup(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_RESELLER_WHATSAPP_GROUP, null);
        }
        return null;
    }

    public static void setPrefResellerWhatsappGroup(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_RESELLER_WHATSAPP_GROUP, data).apply();
        }
    }

    public static String getPrefRetailerWhatsappGroup(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_RETAILER_WHATSAPP_GROUP, null);
        }
        return null;
    }

    public static void setPrefRetailerWhatsappGroup(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_RETAILER_WHATSAPP_GROUP, data).apply();
        }
    }


    public static String getPrefSinglePcWhatsappGroup(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_SINGLE_PC_WHATSAPP_GROUP, null);
        }
        return null;
    }

    public static void setPrefSinglePcWhatsappGroup(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_SINGLE_PC_WHATSAPP_GROUP, data).apply();
        }
    }

    public static String getConfig(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_CONFIG, null);
        }
        return null;
    }

    public static void setConfig(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_CONFIG, data).apply();
        }
    }

    public static String getCategory(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_CATEGORY, null);
        }
        return null;
    }

    public static void setCategory(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_CATEGORY, data).apply();
        }
    }

    public static String getPrefFullSetWhatsappGroup(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_FULL_SET_WHATSAPP_GROUP, null);
        }
        return null;
    }

    public static void setPrefFullSetWhatsappGroup(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_FULL_SET_WHATSAPP_GROUP, data).apply();
        }
    }

    public static String getPrefFashionTrendWhatsappGroup(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_FASHION_TREND_WHATSAPP_GROUP, null);
        }
        return null;
    }

    public static void setPrefFashionTrendWhatsappGroup(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_FASHION_TREND_WHATSAPP_GROUP, data).apply();
        }
    }


    public static boolean getPrefAppintro(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getBoolean(PREF_APPINTRO, false);
        }
        return false;

    }

    public static void setPrefAppintro(Context context, boolean data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putBoolean(PREF_APPINTRO, data).apply();
        }
    }


    public static String getPrefWishbookContacts(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_WISHBOOK_CONTACTS, null);
        }
        return null;

    }

    public static void setPrefWishbookContacts(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_WISHBOOK_CONTACTS, data).apply();
        }
    }

    public static String getPrefStatistics(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_STATISTICS, null);
        }
        return null;
    }

    public static void setPrefStatistics(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_STATISTICS, data).apply();
        }
    }


    public static String getPrefStoryCompletion(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_STORY_COMPLETION, null);
        }
        return null;

    }

    public static void setPrefStoryCompletion(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_STORY_COMPLETION, data).apply();
        }
    }

    public static String getPrefKeepSellingStock(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_KEEP_SELLING_STOCK, null);
        }
        return null;

    }

    public static void setPrefKeepSellingStock(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_KEEP_SELLING_STOCK, data).apply();
        }
    }


    public static String getPrefStoryCompletion1(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_STORY_COMPLETION_1, null);
        }
        return null;
    }

    public static void setPrefStoryCompletion1(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_STORY_COMPLETION_1, data).apply();
        }
    }

    public static String getSearchHistory(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_SEARCH_HISTORY, null);
        }
        return null;
    }

    public static void setSearchHistory(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_SEARCH_HISTORY, data).apply();
        }
    }

    public static int getPrefCodFreeAmountLimit(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getInt(PREF_COD_FREE_AMOUNT_LIMIT, 6000);
        }
        return 6000;
    }

    public static void setPrefCodFreeAmountLimit(Context context, int data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putInt(PREF_COD_FREE_AMOUNT_LIMIT, data).apply();
        }
    }

    public static String getPrefLastCartFullCodVerify(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_LAST_CART_FULL_COD_VERIFY, null);
        }
        return null;
    }

    public static void setPrefLastCartFullCodVerify(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_LAST_CART_FULL_COD_VERIFY, data).apply();
        }
    }

    public static boolean getPrefFacebookLogin(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getBoolean(PREF_FACEBOOK_LOGIN, false);
        }
        return false;
    }

    public static void setPrefFacebookLogin(Context context, boolean data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putBoolean(PREF_FACEBOOK_LOGIN, data).apply();
        }
    }

    public static String getPrefFacebookPageAccessToken(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_FACEBOOK_PAGE_ACCESS_TOKEN, null);
        }
        return null;
    }

    public static void setPrefFacebookPageAccessToken(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_FACEBOOK_PAGE_ACCESS_TOKEN, data).apply();
        }
    }

    public static String getPrefWishbookSupportNumberFromConfig(Context context) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_WISHBOOK_SUPPORT_NUMBER_FROM_CONFIG, "07961343606");
        }
        return null;
    }

    public static void setPrefWishbookSupportNumberFromConfig(Context context, String data) {
        if (context != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_WISHBOOK_SUPPORT_NUMBER_FROM_CONFIG, data).apply();
        }
    }

    public static void clearPrefDatabase(Context context) {
        setGCMNotificationData(context, null);
        setPrefWishbookContacts(context, null);
        setPrefStoryCompletion(context, null);
        setPrefKeepSellingStock(context, null);
        setPrefWishlistProductData(context, null);
        setPrefViewProductCount(context, null);
        setCategory(context, null);
        setConfig(context, null);
        setSearchHistory(context, null);
        setPrefLastCartFullCodVerify(context, null);
        if (PrefDatabaseUtils.getPrefFacebookLogin(context)) {
            LoginManager.getInstance().logOut();
        }
        setPrefFacebookLogin(context, false);
        setPrefFacebookPageAccessToken(context, null);
    }
}
