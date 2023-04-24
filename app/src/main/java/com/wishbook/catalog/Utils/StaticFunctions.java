package com.wishbook.catalog.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.exception.MethodNotAllowedException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wishbook.catalog.Activity_DisplayProduct;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.Contact;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.Fragment_ProductSelections;
import com.wishbook.catalog.home.cart.CodReconfirmDialogFragment;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.contacts.add.Fragment_AddSupplier;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.login.Activity_Login;
import com.wishbook.catalog.login.models.Response_User;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.sephiroth.android.library.tooltip.Tooltip;

import static android.content.Context.WINDOW_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vigneshkarnika on 07/03/16.
 */
public class StaticFunctions {


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "WISHBOOK";
    private static final boolean ISDEBUG = true;
    private final static String HEX = "0123456789ABCDEF";
    public static Tooltip.Gravity BOTTOM = Tooltip.Gravity.BOTTOM;
    public static Tooltip.Gravity LEFT = Tooltip.Gravity.LEFT;
    public static Tooltip.Gravity RIGHT = Tooltip.Gravity.RIGHT;
    public static Tooltip.Gravity TOP = Tooltip.Gravity.TOP;
    public static String SERVER_POST_FORMAT = "yyyy-MM-dd";
    public static String CLIENT_DISPLAY_FORMAT = "dd-MM-yyyy";
    public static String CLIENT_DISPLAY_FORMAT1 = "dd MMM yyyy";
    public static String CLIENT_DISPLAY_FORMAT2 = "dd MMM";
    public static String CLIENT_DISPLAY_FORMAT3 = "dd/MM/yyyy";
    public static String CLIENT_COUPON_DISPLAY_FORMAT = "dd-MMM-yyyy hh:mm:ss a";
    public static String SERVER_POST_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String COMMASEPRATED = ",";
    public static String COMMASEPRATEDSPACE = ", ";
    public static String COMMASEPRATEDNEWLINE = ",\n";
    public static String MATERIALDETAILSEPRATED = " | ";
    public static String SPACESEPRATED = " ";
    public static String SEMICOLORSEPRATED = "; ";
    public static String MONEYTAG = "MONEYTAG";
    public static String BRANCHIOTAG = "BRANCHIO";
    public static String WBCONTACT = "WBCONTACT";
    public static List<NameValues> selectedContacts = new ArrayList<>();
    public static List<NameValues> selectedBuyers = new ArrayList<>();
    private static int MAX_LENGTH = 4;

    public enum SHARETYPE {
        OTHER, FACEBOOK, WHATSAPP, WHATSAPP_BUSINESS, GALLERY, LINKSHARE, ONWISHBOOK,FBPAGE
    }


    public static SharedPreferences getAppSharedPreferences(Context applicationContext) {
        return applicationContext.getApplicationContext().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getTutorialAppSharedPreferences(Context applicationContext) {
        return applicationContext.getApplicationContext().getSharedPreferences("wishbookprefstutorial", Context.MODE_PRIVATE);
    }

    public static void switchActivityDelayed(final Activity context, final Class actClass) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(new Intent(context, actClass));
                context.finish();
                context.overridePendingTransition(R.anim.animation_enter,
                        R.anim.animation_leave);
            }
        }, 2000);
    }

    public static String formatErrorTitle(String error) {
        return WordUtils.capitalize(error.replace("_", " "));
    }

    public static void initializeAppsee() {

        //old acccount
       /* if (BuildConfig.DEBUG) {
            Appsee.start("bde6f90a30184ac684711cef37970d56");
            if (UserInfo.getInstance(getApplicationContext()) != null && UserInfo.getInstance(getApplicationContext()).getUserId() != null) {
                Appsee.setUserId(UserInfo.getInstance(getApplicationContext()).getUserId());
            }
        } else {
            Appsee.start("9be625677b934185848c52da24ba54b8");
            if (UserInfo.getInstance(getApplicationContext()) != null && UserInfo.getInstance(getApplicationContext()).getUserId() != null) {
                Appsee.setUserId(UserInfo.getInstance(getApplicationContext()).getUserId());
            }
        }*/


    }

    public static void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getApplicationContext().getResources().updateConfiguration(configuration, metrics);
    }

    public static String getCountryCodes(Activity activity) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("countrycodes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static void switchActivityDelayedShort(final Activity context, final Class actClass) {
        context.startActivity(new Intent(context, actClass));
        context.finish();
        context.overridePendingTransition(R.anim.animation_enter,
                R.anim.animation_leave);
    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static boolean isOnline(Context context) {
     /*   ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();*/
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void switchActivity(final Activity context, final Class actClass) {
        context.startActivity(new Intent(context, actClass));
        context.overridePendingTransition(0,
                0);

    }


    public static void ShowRegisterDialog(final Context context, final String from) {
        if (UserInfo.getInstance(context).isGuest() && !UserInfo.getInstance(context).isUser_approval_status()) {
            // show pending permission dialog

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            View viewInflated = LayoutInflater.from(context).inflate(R.layout.view_pending_permission, null, false);
            alert.setView(viewInflated);
            alert.setCancelable(true);
            final AlertDialog dialog = alert.create();
            viewInflated.findViewById(R.id.linear_main_pending).setVisibility(View.VISIBLE);
            dialog.show();
            TextView textView = viewInflated.findViewById(R.id.txt_pending);
            textView.setText(String.format(context.getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(context).getCompanyname()));
            viewInflated.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
            viewInflated.findViewById(R.id.txt_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            viewInflated.findViewById(R.id.txt_reset_company).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();



                /*    Intent registerIntent = new Intent(context, Activity_Register.class);
                    registerIntent.putExtra("fromResetCompany", true);
                    context.startActivity(registerIntent);*/

                    Intent registerIntent = new Intent(context, Activity_Login.class);
                    registerIntent.putExtra("isGuestUserLogin", true);
                    context.startActivity(registerIntent);

                }
            });

        } else {
            // show Register Dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_register, null, false);
            alert.setView(viewInflated);
            alert.setCancelable(false);
            final AlertDialog dialog = alert.create();
            viewInflated.findViewById(R.id.linear_dialog_register_root).setVisibility(View.VISIBLE);
            dialog.show();
           /* if(UserInfo.getInstance(context).getGuestUserSession() > Application_Singleton.MaxGuestUserSession) {
                viewInflated.findViewById(R.id.txt_later).setVisibility(View.GONE);
            }*/
            viewInflated.findViewById(R.id.txt_later).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            viewInflated.findViewById(R.id.txt_register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


               /*     Intent registerIntent = new Intent(context, Activity_Register.class);
                    registerIntent.putExtra("from", from);
                    context.startActivity(registerIntent);*/

                    Intent registerIntent = new Intent(context, Activity_Login.class);
                    registerIntent.putExtra("isGuestUserLogin", true);
                    context.startActivity(registerIntent);

                }
            });
        }

    }

    public static void registerClickListener(final Context context, View view, final String from) {
        view.findViewById(R.id.txt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent registerIntent = new Intent(context, Activity_Register.class);
                registerIntent.putExtra("from", from);
                context.startActivity(registerIntent);*/

                Intent registerIntent = new Intent(context, Activity_Login.class);
                registerIntent.putExtra("isGuestUserLogin", true);
                context.startActivity(registerIntent);

            }
        });
    }

    public static void updateStatics(final Context context) {
        Intent updateStatistics = new Intent("com.wishbook.catalog.update-statistic");
        updateStatistics.putExtra("action", "updateStatistics");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateStatistics);
    }

    public static void resetClickListener(final Context context, View view) {
      /*  view.findViewById(R.id.txt_reset_company).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(context, Activity_Register.class);
                registerIntent.putExtra("fromResetCompany", true);
                context.startActivity(registerIntent);

            }
        });*/

        Intent registerIntent = new Intent(context, Activity_Login.class);
        registerIntent.putExtra("isGuestUserLogin", true);
        context.startActivity(registerIntent);
    }

    public static boolean isOnlyReseller(Context context) {
        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null) {
            try {
                CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getCompanyGroupFlag(), CompanyGroupFlag.class);
                if (companyGroupFlag.getOnline_retailer_reseller()
                        && !companyGroupFlag.getManufacturer()
                        && !companyGroupFlag.getWholesaler_distributor()
                        && !companyGroupFlag.getRetailer()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean isOnlyRetailer(Context context) {
        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null) {
            try {
                CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getCompanyGroupFlag(), CompanyGroupFlag.class);
                if (companyGroupFlag.getRetailer()
                        && !companyGroupFlag.getManufacturer()
                        && !companyGroupFlag.getWholesaler_distributor()
                        && !companyGroupFlag.getOnline_retailer_reseller()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean isAllGroupFlase(Context context) {
        if (UserInfo.getInstance(context).getCompanyGroupFlag() != null) {
            try {
                CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getCompanyGroupFlag(), CompanyGroupFlag.class);
                if (!companyGroupFlag.getOnline_retailer_reseller()
                        && !companyGroupFlag.getManufacturer()
                        && !companyGroupFlag.getWholesaler_distributor()
                        && !companyGroupFlag.getRetailer()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static void showBankDetailsDialog(int type, final Context context) {
        boolean isBankDetailAvailable = false, isPanAvailable = false, isGstAvailable = false, isPaytmAvilable = false;
        boolean isShowDialog = false;
        if (UserInfo.getInstance(getApplicationContext()).getBankDetails() != null) {
            isBankDetailAvailable = true;
        }
        if (UserInfo.getInstance(getApplicationContext()).getKyc() != null) {
            PostKycGst postKycGst = Application_Singleton.gson.fromJson(UserInfo.getInstance(getApplicationContext()).getKyc(), PostKycGst.class);
            if (postKycGst != null && postKycGst.getGstin() != null) {
                isGstAvailable = true;
            }
            if (UserInfo.getInstance(getApplicationContext()).getCompanyType().equals("buyer")) {
                if (postKycGst != null && postKycGst.getPan() != null) {
                    isPanAvailable = true;
                }
            } else {
                isPanAvailable = true;
            }

        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.WISHBOOK_PREFS, getApplicationContext().MODE_PRIVATE);
        if (!pref.getString("paytm_phone_number", "").isEmpty()) {
            isPaytmAvilable = true;
        }
        String msg = getApplicationContext().getResources().getString(R.string.show_bank_details_all_empty);
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
            new MaterialDialog.Builder(context)
                    .content(msg)
                    .positiveText("Add Details")
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("type", "tab");
                            params.put("page", "gst");
                            new DeepLinkFunction(params, context);

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
        }
    }


    public static void addWishList(Context context, String product_id) {
        ArrayList<String> wishlistProduct;
        if (PrefDatabaseUtils.getPrefWishlistProductData(context) != null) {
            wishlistProduct = new Gson().fromJson(PrefDatabaseUtils.getPrefWishlistProductData(context), new TypeToken<ArrayList<String>>() {
            }.getType());
        } else {
            wishlistProduct = new ArrayList<>();
        }

        wishlistProduct.add(0, product_id);
        PrefDatabaseUtils.setPrefWishlistProductData(context, new Gson().toJson(wishlistProduct));
    }

    public static void removeWishList(Context context, String product_id) {
        ArrayList<String> wishlistProduct;
        if (PrefDatabaseUtils.getPrefWishlistProductData(context) != null) {
            wishlistProduct = new Gson().fromJson(PrefDatabaseUtils.getPrefWishlistProductData(context), new TypeToken<ArrayList<String>>() {
            }.getType());
        } else {
            wishlistProduct = new ArrayList<>();
        }
        wishlistProduct.remove(product_id);
        PrefDatabaseUtils.setPrefWishlistProductData(context, new Gson().toJson(wishlistProduct));
    }

    public static void addProductViewCount(Context context, String product_id) {
        HashMap<String, Integer> responseData;
        if (PrefDatabaseUtils.getPrefViewProductCount(context) != null) {
            responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefViewProductCount(context), new TypeToken<HashMap<String, Integer>>() {
            }.getType());
            if (responseData.containsKey(product_id)) {
                int temp_view = responseData.get(product_id);
                responseData.put(product_id, temp_view + 1);
            } else {
                responseData.put(product_id, 1);
            }
        } else {
            responseData = new HashMap<>();
            responseData.put(product_id, 1);
        }
        PrefDatabaseUtils.setPrefViewProductCount(context, new Gson().toJson(responseData));
    }

    public static int getProductViewCount(Context context, String product_id) {
        HashMap<String, Integer> responseData;
        if (PrefDatabaseUtils.getPrefViewProductCount(context) != null) {
            responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefViewProductCount(context), new TypeToken<HashMap<String, Integer>>() {
            }.getType());
            if (responseData.containsKey(product_id)) {
                int temp_view = responseData.get(product_id);
                return temp_view;
            }
        }
        return 1;
    }

    public static boolean checkProductInWishList(Context context, String product_id) {
        ArrayList<String> wishlistProduct;
        if (PrefDatabaseUtils.getPrefWishlistProductData(context) != null) {
            wishlistProduct = new Gson().fromJson(PrefDatabaseUtils.getPrefWishlistProductData(context), new TypeToken<ArrayList<String>>() {
            }.getType());
            return wishlistProduct.contains(product_id);
        }
        return false;
    }


    public final static boolean isLengthEav(String key) {
        if (key != null) {
            String key_lower = key.toLowerCase(Locale.ENGLISH);
            return key_lower.contains("length") || key_lower.contains("width")
                    || key_lower.equalsIgnoreCase("top") || key_lower.equalsIgnoreCase("bottom") ||
                    key_lower.equalsIgnoreCase("dupatta");
        } else
            return false;
    }

    public static String getAuthString(String toEncode) {
        String authorizationString = "Token " + toEncode;
        StaticFunctions.logger(toEncode);
        return authorizationString;
    }

    public static MaterialDialog SuccessDialog(Context context, String Title, String content) {

        return new MaterialDialog.Builder(context)
                .title(Title)
                .content(content)
                .build();
    }

    public static MaterialDialog showProgress(Context context) {

        return new MaterialDialog.Builder(context)
                .title("Loading")
                .content("Please wait..")
                .progress(true, 0).build();
    }

    public static MaterialDialog showProgressDialog(Context context, String message, String title, boolean isCancelable) {

        return new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .progress(true, 0)
                .cancelable(isCancelable).build();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showNetworkAlert(Context activity) {
       /* final AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);
        alert.setTitle("Network unavailable");
        alert.setMessage("Please check your Internet connection!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();*/
    }


    public static int randInt(int min, int max) {

        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }

    public static void showOfflineSnackBar(Activity context) {
        if (context != null) {
            View view = context.findViewById(R.id.linear_offline);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void hideOfflineSnackBar(Activity context) {
        if (context != null) {
            View view = context.findViewById(R.id.linear_offline);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    public static void setUpselectedProdCounter(final AppCompatActivity context, Toolbar toolbar) {
        toolbar.getMenu().clear();
        MenuItem menuItem = toolbar.getMenu().add(randInt(0, 999), randInt(0, 999), 0, "selection");
        menuItem.setActionView(R.layout.view_notification_badge);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        LinearLayout viewBadge = (LinearLayout) MenuItemCompat.getActionView(menuItem);
        viewBadge.setVisibility(View.VISIBLE);
        TextView viewTextBadge = (TextView) viewBadge.findViewById(R.id.toolbar_badge_count);
        TextView toolbar_badge_text = (TextView) viewBadge.findViewById(R.id.toolbar_badge_text);

        Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
        }.getType();

        String previousProds = null;
        if (Activity_Home.pref != null) {
            previousProds = Activity_Home.pref.getString("selectedProds", null);
        }


        if (previousProds != null) {
            ArrayList<ProductObj> preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
            viewTextBadge.setText("" + preseletedprods.size());
            if (preseletedprods.size() == 0) {
                viewTextBadge.setVisibility(View.INVISIBLE);
                viewBadge.setVisibility(View.INVISIBLE);
                toolbar_badge_text.setVisibility(View.INVISIBLE);
            } else {
                viewTextBadge.setVisibility(View.VISIBLE);
            }
            viewBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application_Singleton.CONTAINER_TITLE = "Selected Products";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ProductSelections();
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(context, OpenContainer.class);

//                    context.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.content_main, new Fragment_ProductSelections()).addToBackStack("selection").commit();
                }
            });
        } else {
            viewTextBadge.setText("0");
            viewTextBadge.setVisibility(View.INVISIBLE);
            viewBadge.setVisibility(View.INVISIBLE);
            toolbar_badge_text.setVisibility(View.INVISIBLE);

            viewBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application_Singleton.CONTAINER_TITLE = "Selected Products";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ProductSelections();
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(context, OpenContainer.class);

//                    context.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.content_main, new Fragment_ProductSelections()).addToBackStack("selection").commit();
                }
            });
        }

    }


    public static void logger(String response) {
        if (StaticFunctions.ISDEBUG) {
            Log.v("wishbooks", "" + response);
        }
    }

    public static HashMap<String, String> getAuthHeader(Activity context) {
        HashMap<String, String> headers = new HashMap<>();
        if (Application_Singleton.Token.equals("")) {
            Application_Singleton.Token = UserInfo.getInstance(context).getKey();
        }
        headers.put("Authorization", getAuthString(Application_Singleton.Token));
        Log.v("authtoken", "Authorization" + getAuthString(Application_Singleton.Token));
        return headers;
    }

    public static boolean checkPlayServices(Activity context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("play", "This device is not supported.");
                context.finish();
            }
            return false;
        }
        return true;
    }


    public static void storeUserData(Response_User response_user, UserInfo userInfo, String password) {
        if (response_user.getCompanyUser() != null) {
            userInfo.setCompany_type(response_user.getCompanyUser().getCompany_type());


            userInfo.setBrandadded(response_user.getCompanyUser().getBrand_added_flag());
            userInfo.setCompany_id(response_user.getCompanyUser().getCompany());
            userInfo.setCompanyname(response_user.getCompanyUser().getCompanyname());


            userInfo.setDeputed_to(response_user.getCompanyUser().getDeputed_to());
            userInfo.setDeputed_to_name(response_user.getCompanyUser().getDeputed_to_name());

            userInfo.setTotalMyCatalogs(response_user.getCompanyUser().getTotal_my_catalogs());
            userInfo.setTotalBrandFollowers(response_user.getCompanyUser().getTotal_brand_followers());
            if (response_user.getCompanyUser().getAddress() != null) {
                userInfo.setUserCompanyAddress(response_user.getCompanyUser().getAddress());
            }
        }


        userInfo.setUserId(response_user.getId());
        userInfo.setUserName(response_user.getUsername());
        userInfo.setFirstName(response_user.getFirst_name());
        userInfo.setLastName(response_user.getLast_name());
        userInfo.setEmail(response_user.getEmail());
        if (password != null) {
            userInfo.setPassword(password);
        }


        if (response_user.getUserprofile() != null) {
            userInfo.setProfileimage(response_user.getUserprofile().getUser_image());
            userInfo.setMobile(response_user.getUserprofile().getPhone_number());
        }


        if (response_user.getGroups() != null) {
            if (response_user.getGroups().size() > 0) {
                userInfo.setGroupstatus(response_user.getGroups().get(0));
            }
        }


        userInfo.setIsLoggedIn(true);
    }


    public static boolean appInstalledOrNot(String uri, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            boolean app_installed = false;
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
            return app_installed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isWhatsappInstalled(Context context) {
        try {
            String uri = "com.whatsapp";
            PackageManager pm = context.getPackageManager();
            boolean app_installed = false;
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
            return app_installed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isWhatsappBusinessInstalled(Context context) {
        try {
            String uri = "com.whatsapp.w4b";
            PackageManager pm = context.getPackageManager();
            boolean app_installed = false;
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
            return app_installed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void genericShare(final AppCompatActivity activity, ArrayList<Uri> uris, String sharetext) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        //CharSequence[] cs = new String[]{sharetext};
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            activity.startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void loadImage(Context context, String imagepath, ImageView image, int placeholder) {
        //Picasso.with(context).load(imagepath).placeholder(placeholder).into(image);
        ImageLoader.getInstance().displayImage(imagepath, image, Application_Singleton.options, new SimpleImageLoadingListener() {
        });
    }

    public static void loadFresco(Context context, String imagepath, SimpleDraweeView image) {
        ImageUtils.loadImage(context, imagepath, image);
    }


    public static ArrayList<MyContacts> getContactsFromPhone(Context mContext) {
        ArrayList<MyContacts> contactList = new ArrayList<>();
        SharedPreferences pref = mContext.getSharedPreferences(Constants.WISHBOOK_PREFS, mContext.MODE_PRIVATE);
        String contacts = pref.getString("contact_list", "");
        if (!contacts.equals("")) {
            try {
                contactList =
                        new Gson().fromJson(contacts, new TypeToken<ArrayList<MyContacts>>() {
                        }.getType());
            } catch (Exception e) {

            }
        } else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
             /*   ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.READ_CONTACTS}, 1);*/
            } else {

                HashSet<String> nums = new HashSet<>();
                Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                try {
                    while (phones.moveToNext()) {

                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").trim();

                        // Log.d("Name", name + " Number " + phoneNumber);
                        //  Log.d("Name", name + " Number with trim " + phoneNumber.trim());

                        if (phoneNumber.length() >= 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                        MyContacts ct = new MyContacts(phoneNumber.trim(), null, name.toLowerCase().toString(), name.toLowerCase().toString(), "");
                        // Log.d("Name", name + " Number " + phoneNumber + "After trim");

                        String phoneNumber1 = phoneNumber.replaceAll("\\D+", "");

                        //   Log.d("Name", name + " Number " + phoneNumber + "After Replacing");
                        if (phoneNumber1.length() >= 10) {

                            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                            //      Log.d("Name", name + " Number " + phoneNumbertrim + "Adding After trim");
                            if (!nums.contains(phoneNumbertrim)) {
                                contactList.add(ct);
                            }
                            nums.add(phoneNumbertrim);
                        } else {
                            //  Log.d("Name", name + " Number " + phoneNumber + "Not added because less than 10 ");
                        }
                    }
                } catch (Exception e) {

                }
                phones.close();
                Collections.sort(contactList, new Comparator<MyContacts>() {

                    @Override
                    public int compare(MyContacts lhs, MyContacts rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });

                String items = new Gson().toJson(contactList);
                pref.edit().putString("contact_list", items).apply();
            }

        }


        return contactList;
    }

    public static ArrayList<MyContacts> reloadPhoneContacts(Context mContext) {
        Log.d("Phone fetching Method ", "Called");


        ArrayList<MyContacts> contactList = new ArrayList<>();
        ArrayList<Contact> contactListTobeStored = new ArrayList<>();
        SharedPreferences pref = mContext.getSharedPreferences(Constants.WISHBOOK_PREFS, mContext.MODE_PRIVATE);
        if (!Application_Singleton.contactPermissionDenied) {
            Log.d("Phone fetching Method  ", "Permission Denied");
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        } else {
            Log.d("fetching", "Contacts from phone");
            UserInfo.getInstance(mContext).setContactStatus("fetching");
            HashSet<String> nums = new HashSet<>();
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (phones != null) {
                try {
                    while (phones.moveToNext()) {
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").trim();

                        // Log.d("Name", name + " Number " + phoneNumber);
                        if (phoneNumber.length() >= 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                        MyContacts ct = new MyContacts(phoneNumber.trim(), null, name.toLowerCase().toString(), name.toLowerCase().toString(), "");
                        Contact ct1 = new Contact(name, phoneNumber, false, false);
                        //Log.d("Name", name + " Number " + phoneNumber + "After trim");
                        String phoneNumber1 = phoneNumber.replaceAll("\\D+", "");

                        // Log.d("Name", name + " Number " + phoneNumber + "After Replacing");
                        if (phoneNumber1.length() >= 10) {
                            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());

                            //   Log.d("Name", name + " Number " + phoneNumbertrim + "Adding After trim");

                            if (!nums.contains(phoneNumbertrim)) {
                                contactList.add(ct);
                                contactListTobeStored.add(ct1);
                            }
                            nums.add(phoneNumbertrim);
                        } else {
                            //    Log.d("Name", name + " Number " + phoneNumber + "Not added because less than 10 ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                phones.close();
            }
            if (contactList.size() > 0) {
                Collections.sort(contactListTobeStored, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact lhs, Contact rhs) {
                        return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                    }
                });
            }
            if (mContext != null) {
                // storePhoneContact(new Gson().toJson(contactListTobeStored));
                UserInfo.getInstance(mContext).setContacts(new Gson().toJson(contactListTobeStored));
                UserInfo.getInstance(mContext).setContactStatus("fetched");
                Log.i(TAG, "ContactsFetch: Contact fetched");
            }
            Collections.sort(contactList, new Comparator<MyContacts>() {

                @Override
                public int compare(MyContacts lhs, MyContacts rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });

           /* String items = new Gson().toJson(contactList);
            pref.edit().putString("contact_list", items).apply();*/
        }

        return contactList;
    }


    public static void catalogQR(final String catalog, final AppCompatActivity context, final String supplier) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HttpManager.getInstance(context).request(HttpManager.METHOD.HEAD, URLConstants.companyUrl(context, "catalogs", "") + catalog + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

                CatalogMinified selectedCatalog = new CatalogMinified(catalog, "catalog", false);
                Application_Singleton.selectedshareCatalog = selectedCatalog;
                Application_Singleton.CONTAINER_TITLE = "Catalog";
                Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                Intent intent = new Intent(context, OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                context.startActivity(intent);


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (supplier != null && context != null) {
                    getSeller(supplier, context);
                }
            }
        });
    }

    private static void getSeller(final String id, final AppCompatActivity context) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "sellers", "") + "?company=" + id, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

                final Response_Suppliers[] response_seller = new Gson().fromJson(response, Response_Suppliers[].class);
                if (response_seller.length > 0) {
                    Toast.makeText(context, response_seller[0].getSelling_company_name() + " has not shared this catalog with you on Wishbook", Toast.LENGTH_LONG).show();
                } else {
                    //   Activity_Home.tabs.getTabAt(2).select();
                    Fragment_AddSupplier addBuyerFragment = new Fragment_AddSupplier();
                    Bundle bundel = new Bundle();
                    bundel.putString("id", id);
                    addBuyerFragment.setArguments(bundel);
                    addBuyerFragment.show(context.getSupportFragmentManager()
                            , "addbuyer");
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    public static void hyperLinking(String choice, String id, Context context) {
        if (id != null && !id.equals("")) {
            Bundle bundle = new Bundle();
            switch (choice) {
                case "supplier":
                    bundle.putString("sellerid", id);
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = supplier;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "buyer":
                    Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                    Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    bundle.putString("buyerid", id);
                    buyerapproved.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = buyerapproved;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "brand":
                    bundle.putString("filtertype", "brand");
                    bundle.putString("filtervalue", id);
                    Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                    fragmentCatalogs.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Catalogs";
                    Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "product":
                    Intent intent = new Intent(context, Activity_DisplayProduct.class);
                    intent.putExtra("productid", id);
                    context.startActivity(intent);
                    break;
            }
        }

    }

    public static void hyperLinking1(String choice, String id, Context context, String toolbarTitle) {
        if (id != null && !id.equals("")) {
            Bundle bundle = new Bundle();
            switch (choice) {
                case "supplier":
                    bundle.putString("sellerid", id);
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = supplier;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "buyer":
                    Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                    Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    bundle.putString("buyerid", id);
                    buyerapproved.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = buyerapproved;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;

                case "brand":
                   /* bundle.putString("filtertype", "brand");
                    bundle.putString("filtervalue", id);
                    Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                    fragmentCatalogs.setArguments(bundle);
                    if (toolbarTitle != null && !toolbarTitle.isEmpty()) {
                        Application_Singleton.CONTAINER_TITLE = toolbarTitle;
                    } else {
                        Application_Singleton.CONTAINER_TITLE = "Catalogs";
                    }
                    Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);*/

                    HashMap<String, String> params = new HashMap<>();
                    params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                    params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                    params.put("brand", id);
                    params.put("page_title",toolbarTitle);
                    Application_Singleton.deep_link_filter = params;
                    Fragment_BrowseProduct fragment_browseProduct = new Fragment_BrowseProduct();
                    bundle = new Bundle();
                    bundle.putBoolean("isfrom_brand_filter", true);
                    fragment_browseProduct.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Products";
                    Application_Singleton.CONTAINERFRAG = fragment_browseProduct;
                    Intent intent2 = new Intent(context, OpenContainer.class);
                    context.startActivity(intent2);
                    break;
                case "product":
                    Intent intent = new Intent(context, Activity_DisplayProduct.class);
                    intent.putExtra("productid", id);
                    context.startActivity(intent);
                    break;
            }
        }

    }

    public static Tooltip.Builder tutorialToolTipBottom(View view, String text, String gravity) {
        Tooltip.Gravity grav;
        switch (gravity) {
            case "bottom":
                grav = Tooltip.Gravity.BOTTOM;
                break;
            case "right":
                grav = Tooltip.Gravity.RIGHT;
                break;
            case "left":
                grav = Tooltip.Gravity.LEFT;
                break;
            case "top":
                grav = Tooltip.Gravity.TOP;
                break;
            default:
                grav = Tooltip.Gravity.BOTTOM;
                break;
        }

        return new Tooltip.Builder()
                .anchor(view, grav)
                .closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_NO_CONSUME, 60000)
                .withStyleId(R.style.ToolTipLayoutCustomStyle)
                .text(text)
                .withArrow(true)
                .withOverlay(true)
                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT);
    }

    public static Tooltip.Builder tutorialToolTipBottomNoTouch(int id, View view, String text, String gravity) {
        Tooltip.Gravity grav;
        switch (gravity) {
            case "bottom":
                grav = Tooltip.Gravity.BOTTOM;
                break;
            case "right":
                grav = Tooltip.Gravity.RIGHT;
                break;
            case "left":
                grav = Tooltip.Gravity.LEFT;
                break;
            case "top":
                grav = Tooltip.Gravity.TOP;
                break;
            default:
                grav = Tooltip.Gravity.BOTTOM;
                break;
        }

        return new Tooltip.Builder(id)
                .anchor(view, grav)
                .closePolicy(Tooltip.ClosePolicy.TOUCH_NONE, 2000)
                .withStyleId(R.style.ToolTipLayoutCustomStyle)
                .text(text)
                .withArrow(true)
                .withOverlay(true)
                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT);
    }


    public static void checkAndShowTutorial(Context context, final String flag, View view, String string, String gravity) {
        if (view != null) {
            if (!Application_Singleton.tutorial_pref.getBoolean(flag, false) && view.getVisibility() == View.VISIBLE) {
                Tooltip.make(context,
                        StaticFunctions.tutorialToolTipBottom(view, string, gravity).withCallback(new Tooltip.Callback() {
                            @Override
                            public void onTooltipClose(Tooltip.TooltipView tooltipView, boolean b, boolean b1) {
                                Application_Singleton.tutorial_pref.edit().putBoolean(flag, true).apply();
                            }

                            @Override
                            public void onTooltipFailed(Tooltip.TooltipView tooltipView) {
                                Log.d("Failed", "Failed reason");
                            }

                            @Override
                            public void onTooltipShown(Tooltip.TooltipView tooltipView) {
                                Log.d("Shown", "Failed reason");
                            }

                            @Override
                            public void onTooltipHidden(Tooltip.TooltipView tooltipView) {
                                Log.d("Hidden", "Failed reason");
                            }
                        })
                ).show();

            }
        }
    }

    public static void checkAndShowTutorialNoTouch(Context context, final String flag, View view, String string, String gravity, int id) {
        if (view != null) {
            if (!Application_Singleton.tutorial_pref.getBoolean(flag, false) && view.getVisibility() == View.VISIBLE) {
                Tooltip.make(context,
                        StaticFunctions.tutorialToolTipBottomNoTouch(id, view, string, gravity).withCallback(new Tooltip.Callback() {
                            @Override
                            public void onTooltipClose(Tooltip.TooltipView tooltipView, boolean b, boolean b1) {
                                Application_Singleton.tutorial_pref.edit().putBoolean(flag, true).apply();
                            }

                            @Override
                            public void onTooltipFailed(Tooltip.TooltipView tooltipView) {
                                Log.d("Failed", "Failed reason");
                            }

                            @Override
                            public void onTooltipShown(Tooltip.TooltipView tooltipView) {
                                Log.d("Shown", "Failed reason");
                            }

                            @Override
                            public void onTooltipHidden(Tooltip.TooltipView tooltipView) {
                                Log.d("Hidden", "Failed reason");
                            }
                        })
                ).show();

            }
        }
    }


    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void showProgressbar(Activity activity) {
        activity.findViewById(R.id.relative_progress).setVisibility(View.VISIBLE);
    }

    public static void hideProgressbar(Activity activity) {
        activity.findViewById(R.id.relative_progress).setVisibility(View.GONE);
    }

    public static String ArrayListToString(ArrayList<String> list, String separator) {
        StringBuilder csvBuilder = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (String s : list) {
                csvBuilder.append(s);
                csvBuilder.append(separator);
            }
            String csv = csvBuilder.toString();
            return csv = csv.substring(0, csv.length() - separator.length());
        }
        return "";

    }

    public static ArrayList<String> stringToArray(String csv, String separator) {
        try {
            String[] elements = csv.split(separator);
            ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList(elements));
            return stringArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean compareTwoStringArrayList(ArrayList<String> orignalArray, ArrayList<String> newArray) {
        if (orignalArray == null || newArray == null)
            return false;
        if (orignalArray.isEmpty() || newArray.isEmpty())
            return false;
        if (orignalArray.size() == newArray.size()) {
            Collections.sort(orignalArray);
            Collections.sort(newArray);
            return orignalArray.equals(newArray);
        } else {
            return false;
        }
    }


    public static String JsonArrayToString(JsonArray list, String separator) {
        try {
            StringBuilder csvBuilder = new StringBuilder();

            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    csvBuilder.append(list.get(i).toString().replaceAll("\"", ""));
                    csvBuilder.append(separator);

                }
                String csv = csvBuilder.toString();
                return csv = csv.substring(0, csv.length() - separator.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }


    public static void showAppUpdateDialog(final Context context, boolean isForceUpdate) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_app_update, null, false);
            alert.setView(viewInflated);
            if (isForceUpdate) {
                alert.setCancelable(false);
                viewInflated.findViewById(R.id.txt_negative).setVisibility(View.GONE);
            } else {
                alert.setCancelable(true);
            }

            final AlertDialog dialog = alert.create();
            dialog.show();
            StaticFunctions.sendUpdateAppPopUpScreenAnalytics(context);
            viewInflated.findViewById(R.id.txt_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        sendUpdateNowClickAnalytics(context);
                        navigatePlayStore(context);
                        //dialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            });

            viewInflated.findViewById(R.id.txt_negative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        sendUpdateLaterClickAnalytics(context);
                        dialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public static void navigatePlayStore(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void initFreshChat(Context context) {
        UserInfo userInfo = UserInfo.getInstance(context);
        String restoreId = Freshchat.getInstance(getApplicationContext()).getUser().getRestoreId();
        SharedPreferences pref = getAppSharedPreferences(context);
        pref.edit().putString("freshchat_user_id", restoreId).commit();
        userInfo.setFreshchat_user_id(restoreId);
        try {
            Freshchat.getInstance(getApplicationContext()).identifyUser(userInfo.getUserId(), restoreId);
        } catch (MethodNotAllowedException e) {
            e.printStackTrace();
        }
    }


    public static void printHashmap(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e("TAG", "HASHMAP: " + key + "Value===>" + value);
            }
        }
    }

    public static void printHashmap1(Map<Integer, Integer> params) {
        if (params != null) {
            for (Map.Entry<Integer, Integer> entry : params.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                Log.e("TAG", "Screen1: " + key + "Value===>" + value);
            }
        }
    }

    public static void printHashmap(String tag, Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e(tag, "onQueryTextChange: " + key + "Value===>" + value);
            }
        }
    }

    public static Map<String, List<String>> convertMap(HashMap params) {
        Map<String, List<String>> paramstopost = new HashMap<>();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ArrayList<String> stringMap = new ArrayList<>();
                stringMap.add(pair.getValue().toString());
                paramstopost.put(pair.getKey().toString(), stringMap);
                it.remove();
            }
        }
        return paramstopost;
    }

    public static void clearAllService() {
        Application_Singleton.nomalService = null;
        Application_Singleton.longCacheService = null;
        Application_Singleton.pullToRefreshService = null;
        Application_Singleton.noCacheService = null;
    }


    public static void showResponseFailedDialog(ErrorString error) {
        try {
            if (Application_Singleton.canUseCurrentAcitivity() && error != null) {
                if(error.getErrormessage()!=null && !error.getErrormessage().isEmpty()) {
                    boolean isHtmlConverted = true;
                    try {
                        Html.fromHtml(error.getErrormessage());
                        isHtmlConverted = true;
                    } catch (Exception e){
                        isHtmlConverted = false;
                        e.printStackTrace();
                    }

                    if(isContainsPhoneNumber(error.getErrormessage())) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                .content(isHtmlConverted? Html.fromHtml(error.getErrormessage()): error.getErrormessage())
                                .positiveText("CALL")
                                .negativeText("CANCEL")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +  getMatchesPhone(error.getErrormessage())));
                                        Application_Singleton.getCurrentActivity().startActivity(intent);
                                    }
                                })
                                .show();
                    } else {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                .content(isHtmlConverted? Html.fromHtml(error.getErrormessage()): error.getErrormessage())
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isContainsPhoneNumber(String s) {
        Pattern p = Pattern.compile("[\\.)(]*([0-9+-.]{1,2})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})");
        Matcher m = p.matcher(s);
        return (m.find());
    }

    public static String getMatchesPhone(String s) {
        Pattern p = Pattern.compile("[\\.)(]*([0-9+-.]{1,2})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})");
        Matcher m = p.matcher(s);
        if(m.find()) {
            return m.group();
        }
        return "";
    }


    public static void sendUpdateAppPopUpScreenAnalytics(Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.UPDATE_EVENT_CATEGORY);
        wishbookEvent.setEvent_names("UpdateAppPopUp_screen");
        HashMap<String, String> prop = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        prop.put("date", sdf.format(new Date()));
        int versionCode = 1;
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionCode = versionCode / 10;
            prop.put("app_version", String.valueOf(versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(context, wishbookEvent);
    }

    public static String checkNotificationMapKey(Map<String, String> data, String key) {
        if (data != null && key != null) {
            if (data.containsKey(key) && data.get(key) != null) {
                return data.get(key);
            }
        }
        return "";
    }

    public static void sendUpdateNowClickAnalytics(Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("UpdateNow_Clicked");
        HashMap<String, String> prop = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        prop.put("date", sdf.format(new Date()));
        int versionCode = 1;
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionCode = versionCode / 10;
            prop.put("app_version", String.valueOf(versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(context, wishbookEvent);
    }

    public static void sendUpdateLaterClickAnalytics(Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("UpdateLater_Clicked");
        HashMap<String, String> prop = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        prop.put("date", sdf.format(new Date()));
        int versionCode = 1;
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionCode = versionCode / 10;
            prop.put("app_version", String.valueOf(versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(context, wishbookEvent);
    }

    public static boolean isNotiifactionEnabled(Context context) {
        return !NotificationManagerCompat.from(context).areNotificationsEnabled();
    }


    // ######################## Start Local Cart Data Manage  Region ###################################

    public static boolean checkCatalogIsInCart(Context context, String bundleCatalogID) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                }.getType();
                ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
                if (saveCartData != null && saveCartData.size() > 0) {
                    for (int i = 0; i < saveCartData.size(); i++) {
                        if (saveCartData.get(i).getId().equals(bundleCatalogID)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveCartData(Context context, CartCatalogModel cart_response) {
        try {
            if (context != null) {
                SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                ArrayList<SaveCartData> CartData = new ArrayList<>();

                for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                    SaveCartData saveCartData = new SaveCartData();
                    saveCartData.setId(cart_response.getCatalogs().get(i).getProduct_id());
                    if (cart_response.getCatalogs().get(i).getProducts() != null && cart_response.getCatalogs().get(i).getProducts().size() > 0 && cart_response.getCatalogs().get(i).getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                        // Logic for product type set
                    } else {
                        if (!cart_response.getCatalogs().get(i).isIs_full_catalog()) {
                            ArrayList<String> productId = new ArrayList<>();
                            for (int j = 0; j < cart_response.getCatalogs().get(i).getProducts().size(); j++) {
                                productId.add(cart_response.getCatalogs().get(i).getProducts().get(j).getProduct());
                            }
                            SaveCartData.Products products = new SaveCartData.Products();
                            products.setId(productId);
                            saveCartData.setProducts(products);
                        }
                    }

                    CartData.add(saveCartData);
                }
                editor.putString("cartdata", new Gson().toJson(CartData)).commit();
                saveProductsData(context, cart_response);
                Log.d("CARTDATA", "" + new Gson().toJson(CartData));
            } else {
                Log.d("CARTDATA", "Context is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveProductsData(Context context, CartCatalogModel cart_response) {
        try {
            if (context != null) {
                SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                ArrayList<String> productIDs = new ArrayList<>();
                for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                    // Add all product single pc added
                    if (!cart_response.getCatalogs().get(i).getIs_full_catalog()) {
                        for (int j = 0; j < cart_response.getCatalogs().get(i).getProducts().size(); j++) {
                            productIDs.add(cart_response.getCatalogs().get(i).getProducts().get(j).getProduct_id());
                        }
                    }
                }
                editor.putString("cartdataProducts", new Gson().toJson(productIDs)).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteCartData(Context context, String bundle_catalogID, boolean isFullCatalog) {
        if (context != null) {
            try {
                SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                    Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                    }.getType();
                    ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
                    if (saveCartData != null && saveCartData.size() > 0) {
                        for (int i = 0; i < saveCartData.size(); i++) {
                            if (saveCartData.get(i).getId().equals(bundle_catalogID)) {
                                if (isFullCatalog) {
                                    if (saveCartData.get(i).getProducts().getId().size() == 0) {
                                        saveCartData.remove(i);
                                        break;
                                    }
                                } else {
                                    saveCartData.remove(i);
                                    break;
                                }
                            }
                        }
                        preferences.edit().putString("cartdata", new Gson().toJson(saveCartData)).commit();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ###################### Start COD Confirmation Region ###########################################

    public static void getAllCODVerficationPending(final Context context) {
        if(PrefDatabaseUtils.getPrefLastCartFullCodVerify(context)!=null) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            String url = URLConstants.companyUrl(context, "purchaseorder", "") + "?processing_status=" + Constants.COD_VERIFICATION_PENDING+"&cart="+PrefDatabaseUtils.getPrefLastCartFullCodVerify(context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        Type orderType = new TypeToken<ArrayList<Response_buyingorder>>() {
                        }.getType();
                        ArrayList<Response_buyingorder> orders = Application_Singleton.gson.fromJson(response, orderType);
                        int next_count = 0;
                        //Application_Singleton.LAST_MYCART_ID = null;

                        if (context != null) {
                            showCODDialog1(context, orders, next_count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } else {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            String url = URLConstants.companyUrl(context, "purchaseorder", "") + "?processing_status=" + Constants.COD_VERIFICATION_PENDING;
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        Type orderType = new TypeToken<ArrayList<Response_buyingorder>>() {
                        }.getType();
                        ArrayList<Response_buyingorder> orders = Application_Singleton.gson.fromJson(response, orderType);
                        int next_count = 0;

                        if (context != null) {
                            showCODDialog(context, orders, next_count);
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

    }

    public static void showCODDialog(final Context context, final ArrayList<Response_buyingorder> orders, int count) {
        if (orders.size() > 0 && count < orders.size()) {
            Bundle bundle = new Bundle();
            bundle.putInt("current_count", count);
            bundle.putSerializable("data", orders.get(count));
            CodReconfirmDialogFragment codReconfirmDialogFragment = CodReconfirmDialogFragment.newInstance(bundle);
            codReconfirmDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "COD_RECONFIRM");
            codReconfirmDialogFragment.setDisMissListener(new CodReconfirmDialogFragment.DisMissListener() {
                @Override
                public void onCancelOrder(int count) {
                    count++;
                    showCODDialog(context, orders, count);
                }

                @Override
                public void onReconfirmOrder(int count) {

                }
            });
        }
    }

    public static void showCODDialog1(final Context context, final ArrayList<Response_buyingorder> orders, int count) {
        if (orders.size() > 0 && count < orders.size()) {
            Bundle bundle = new Bundle();
            bundle.putInt("current_count", count);
            bundle.putSerializable("data_array", orders);
            CodReconfirmDialogFragment codReconfirmDialogFragment = CodReconfirmDialogFragment.newInstance(bundle);
            codReconfirmDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "COD_RECONFIRM");
            codReconfirmDialogFragment.setDisMissListener(new CodReconfirmDialogFragment.DisMissListener() {
                @Override
                public void onCancelOrder(int count) {
                    count++;
                    showCODDialog(context, orders, count);
                }

                @Override
                public void onReconfirmOrder(int count) {

                }
            });
        }
    }

    public static boolean checkOrderDisableConfig(Context context) {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("ORDER_DISABLE_FEATURE_IN_APP")) {
                    try {
                        if(Integer.parseInt(data.get(i).getValue()) == 1) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return false;
    }

}
