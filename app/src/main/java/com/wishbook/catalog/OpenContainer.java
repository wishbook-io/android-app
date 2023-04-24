package com.wishbook.catalog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatCallbackStatus;
import com.freshchat.consumer.sdk.UnreadCountCallback;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.Send_EnquiryPost;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.CatalogUploadOption;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.SearchActivity;
import com.wishbook.catalog.home.cart.Fragment_CashPayment_2;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.cart.Fragment_Payment;
import com.wishbook.catalog.home.catalog.Fragment_WishList;
import com.wishbook.catalog.home.catalog.add.Fragment_AddProduct;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.contacts.Fragment_SuppliersEnquiry;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;
import com.wishbook.catalog.home.more.Activity_AboutUs_WebView;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateOrderNew;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreatePurchaseOrderVersion2;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;
import com.wishbook.catalog.home.orderNew.details.Fragment_CashPayment;
import com.wishbook.catalog.home.orders.add.Fragment_CreateOrder;
import com.wishbook.catalog.home.orders.add.Fragment_PurchaseBackOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Commonly used for open any fragment in this activity
 */
public class OpenContainer extends AppCompatActivity {

    public static final int CATALOGSHARE = 0;
    public static final int SELECTIONSHARE = 5;
    public static final int MYSELECTION = 6;
    public static final int HELP = 8;
    public static final int BROWSECATALOG = 14;
    public static final int BROWSE = 7;
    public static final int PRODUCTFAV = 1;
    public static final int SHAREDBYME = 2;
    public static final int CATALOGAPP = 4;
    public static final int SHAREDWITHME = 3;
    public static final int TNC = 9;
    public static final int NEW_BUYER_GROUP = 10;
    public static final int NOTIFICARION_CLEAR = 11;
    public static final int SCAN_BARCODE_SALESMAN = 12;
    public static final int ADD_CATALOG = 13;
    public Toolbar toolbar;
    private Intent bundle;
    private UserInfo userinfo;
    private Menu menu;

    //catalog type_dialog variables
    EditText public_price;
    RadioGroup radioGroup;

    TextView badgeTextView;
    FloatingActionButton support_chat_fab;
    private OnBackPressedListener onBackPressedListener;
    private Context context;


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();


        Freshchat.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on
                badgeTextView.setText("" + unreadCount);
            }
        });

        toolbar.getMenu().clear();
        int toolbarCategory = bundle.getIntExtra("toolbarCategory", -1);
        String pushUser = bundle.getStringExtra("pushId");
        Log.i("TAG", "Toolbar Category====>" + toolbarCategory);
        switch (toolbarCategory) {
            case NOTIFICARION_CLEAR:
                toolbar.inflateMenu(R.menu.menu_notification_clear);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       /* if (item.getItemId() == R.id.action_notification_clear) {
                            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Realm.getDefaultInstance().delete(NotificationModel.class);

                                }
                            });
                            LocalService.notificationCounter.onNext(0);   }*/
                        return false;
                    }
                });
                break;
            case NEW_BUYER_GROUP:
              /*  toolbar.inflateMenu(R.menu.menu_new_group);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_new_group) {
                            Application_Singleton.CONTAINER_TITLE = "Add Buyer Group";
                            Application_Singleton.CONTAINERFRAG = new Fragment_AddBuyerGroup_Version2();
                            Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                            startActivityForResult(intent, 2);
                            *//*Fragment_AddBuyerGroup addBuyergroupFragment =new Fragment_AddBuyerGroup();
                            addBuyergroupFragment.show(getSupportFragmentManager(),"add");*//*
                        }
                        return false;
                    }
                });*/
                break;
            case TNC:
                toolbar.setVisibility(View.GONE);
                break;
            case HELP:
                toolbar.inflateMenu(R.menu.menu_help);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_user_manual) {
                            Intent intent = new Intent(OpenContainer.this, Activity_AboutUs_WebView.class);
                            intent.putExtra("show", "user_manual");
                            startActivity(intent);
                        }
                        return false;
                    }
                });
                break;
            case BROWSE:
                try {
                    toolbar.inflateMenu(R.menu.menu_catalog_holder_version2);
                    toolbar.getMenu().findItem(R.id.action_cart).setVisible(true);
                    menu = toolbar.getMenu();
                    cartToolbarUpdate();
                    wishListToolbarUpdate();
                    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_search:
                                    Intent intent = new Intent(OpenContainer.this, SearchActivity.class);
                                    startActivity(intent);
                                    break;

                            }
                            return false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case BROWSECATALOG:
                Log.i("TAG", "onResume: Browse Catlog Toolbar");
                toolbar.inflateMenu(R.menu.menu_browsecatalog);
                toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
                menu = toolbar.getMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(0); // for display brand follow
                }
                if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getIs_supplier_approved()) {
                    toolbar.getMenu().getItem(0).setVisible(false);
                    // toolbar.getMenu().getItem(2).setVisible(true);

                    //change for display bottom button
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(1).setVisible(false);
                } else {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(1).setVisible(false);
                    toolbar.getMenu().getItem(0).setVisible(false);
                    //setting tutorial

                    // disable enqiry menu
                    //StaticFunctions.checkAndShowTutorial(OpenContainer.this,"enquiry_tut_shown",toolbar.getChildAt(2),getResources().getString(R.string.enquiry_tutorial_text),"bottom");
                }

               /* if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(3).setVisible(false);
                } else {
                    toolbar.getMenu().getItem(3).setVisible(false);
                }*/
                SharedPreferences preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }

                wishListToolbarUpdate();


                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        /*if(item.getItemId()==R.id.menu_inquiry){
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("text/plain");
                            String[] TO = {"bd@wishbook.io"};
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Catalog Inquiry for "+UserInfo.getInstance(OpenContainer.this).getCompanyname());
                            emailIntent.putExtra(Intent.EXTRA_TEXT,"Dear Team,\n\n" + UserInfo.getInstance(OpenContainer.this).getCompanyname() +" has inquired about below catalog\n\n"
                                    +"Brand : "+Application_Singleton.selectedshareCatalog.getBrand_name()+"\n\nCatalog Name : "+Application_Singleton.selectedshareCatalog.getTitle()+"\n\nThank you. ");
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        }*/
                        if (item.getItemId() == R.id.menu_inquiry) {


                            String text = "आप " + Application_Singleton.selectedshareCatalog.getTitle() + " के बारे में Enquiry भेजना चाहते हैं?";
                            new MaterialDialog.Builder(OpenContainer.this).title("Enquiry").content(text).positiveText("Yes")
                                    .negativeText("No")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(OpenContainer.this);
                                            Send_EnquiryPost enquiry = new Send_EnquiryPost();
                                            enquiry.setBuyer_type("Enquiry");

                                            String details = "{ \"catalog\" : " + Application_Singleton.selectedshareCatalog.getId() + "}";
                                            enquiry.setDetails(details);
                                            Gson gson1 = new Gson();
                                            HttpManager.getInstance(OpenContainer.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this, "sellers", ""), (gson1.fromJson(gson1.toJson(enquiry), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                                                @Override
                                                public void onCacheResponse(String response) {

                                                }

                                                @Override
                                                public void onServerResponse(String response, boolean dataUpdated) {
                                                    try {
                                                        final String status, company_phone_number, company, company_chat_admin_user, company_name;
                                                        JSONObject response_enquiry = null;
                                                        try {
                                                            response_enquiry = new JSONObject(response);
                                                            status = response_enquiry.getString("success");

                                                            if (status.equals("You are already connected to this supplier")) {
                                                                company_phone_number = response_enquiry.getString("company_phone_number");
                                                                company = response_enquiry.getString("company");
                                                                company_chat_admin_user = response_enquiry.getString("company_chat_admin_user");
                                                                company_name = response_enquiry.getString("company_name");


                                                                final MaterialDialog dialog = new MaterialDialog.Builder(OpenContainer.this).customView(R.layout.already_supplier_enquiry_layout, true).build();
                                                                AppCompatButton cancel_Button = (AppCompatButton) dialog.getCustomView().findViewById(R.id.cancel_button);
                                                                AppCompatButton chat_Button = (AppCompatButton) dialog.getCustomView().findViewById(R.id.chat_button);
                                                                AppCompatButton notify_Button = (AppCompatButton) dialog.getCustomView().findViewById(R.id.notify_supplier);

                                                                AppCompatButton call_button = (AppCompatButton) dialog.getCustomView().findViewById(R.id.call_button);
                                                                TextView title = (TextView) dialog.getCustomView().findViewById(R.id.title);
                                                                dialog.show();
                                                                call_button.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + company_phone_number));
                                                                        startActivity(intent);
                                                                    }
                                                                });
                                                                cancel_Button.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                chat_Button.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        Intent intent = new Intent(OpenContainer.this, ConversationActivity.class);
                                                                        intent.putExtra(ConversationUIService.USER_ID, company_chat_admin_user);
                                                                        intent.putExtra(ConversationUIService.DISPLAY_NAME, company_name); //put it for displaying the title.
                                                                        intent.putExtra(ConversationUIService.DEFAULT_TEXT, company_name + " has requested you to share a catalog " + Application_Singleton.selectedshareCatalog.getTitle()); //put it for displaying the title.
                                                                        startActivity(intent);
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                notify_Button.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        String url = URLConstants.NOTIFY;
                                                                        HashMap<String, String> params = new HashMap<>();
                                                                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(OpenContainer.this);
                                                                        params.put("company", company);
                                                                        params.put("message", company_name + " has requested you to share a catalog " + Application_Singleton.selectedshareCatalog.getTitle());
                                                                        HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
                                                                            @Override
                                                                            public void onCacheResponse(String response) {

                                                                            }

                                                                            @Override
                                                                            public void onServerResponse(String response, boolean dataUpdated) {
                                                                                try {
                                                                                    Toast.makeText(OpenContainer.this, "Supplier notified successfully", Toast.LENGTH_LONG).show();
                                                                                    dialog.dismiss();
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
                                                                });


                                                                String titleText = "You are already connected to this supplier " + company_name + " having catalog " + Application_Singleton.selectedshareCatalog.getTitle();
                                                                title.setText(titleText);


                                                            } else {
                                                                Fragment_SuppliersEnquiry suppliersEnquiry = new Fragment_SuppliersEnquiry();
                                                                Application_Singleton.CONTAINER_TITLE = "Supplier Enquiry";
                                                                Application_Singleton.CONTAINERFRAG = suppliersEnquiry;
                                                                StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                                                                StaticFunctions.showToast(OpenContainer.this, "Your enquiry has been sent");
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onResponseFailed(ErrorString error) {
                                                    if (error != null && error.getErrormessage() != null) {
                                                        StaticFunctions.showToast(OpenContainer.this, error.getErrormessage());
                                                        if (error.getErrormessage().equals("You have already requested this supplier for approval")) {
                                                            StaticFunctions.showToast(OpenContainer.this, "You have already requested this supplier for approval");
                                                        }
                                                    }

                                                }
                                            });
                                        }
                                    }).onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();

                        }
                        if (item.getItemId() == R.id.menu_supplier_chat) {
                            Intent intentToChat = new Intent(OpenContainer.this, ConversationActivity.class);
                            intentToChat.putExtra(ConversationUIService.USER_ID, Application_Singleton.selectedshareCatalog.getSupplier_chat_user());
                            intentToChat.putExtra(ConversationUIService.DISPLAY_NAME, Application_Singleton.selectedshareCatalog.getSupplier()); //put it for displaying the title.
                            startActivity(intentToChat);
                        }
                        if (item.getItemId() == R.id.cart) {
                            SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            int cart_count = preferences.getInt("cartcount", 0);
                            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                            StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                        }

                        if (item.getItemId() == R.id.action_wishlist) {
                            Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                            Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                            StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                        }
                        if (item.getItemId() == R.id.menu_purchase_order) {
                            Log.i("TAG", "onMenuItemClick: Purchase Order 1");
                            Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                            //Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                            //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
                            Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();
                            Bundle bundle = new Bundle();
                            if (Application_Singleton.selectedshareCatalog != null) {
                                bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                bundle.putString("selling_company", Application_Singleton.selectedshareCatalog.getSupplier());
                                bundle.putBoolean("is_public", true);
                                bundle.putBoolean("is_supplier_approved", Application_Singleton.selectedshareCatalog.getIs_supplier_approved());
                                bundle.putString("full_catalog_order", Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only());

                            }
                            purchase.setArguments(bundle);

                            Application_Singleton.CONTAINERFRAG = purchase;
                            Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                });
                break;
            case MYSELECTION:
                toolbar.inflateMenu(R.menu.menu_sharecat);
                toolbar.getMenu().getItem(6).setVisible(false);
                userinfo = UserInfo.getInstance(OpenContainer.this);
                if (!userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(2).setVisible(false);

                }
                if (userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(1).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }
                if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }
                /*if(Application_Singleton.selectedshareCatalog.getIs_disable()!=null) {
                    if(!userinfo.getGroupstatus().equals("2")) {
                        if (Application_Singleton.selectedshareCatalog.getIs_disable().equals("true")) {
                            toolbar.getMenu().getItem(1).setVisible(false);
                            toolbar.getMenu().getItem(2).setVisible(false);
                            toolbar.getMenu().getItem(3).setVisible(false);
                            toolbar.getMenu().getItem(4).setVisible(true);
                        } else {
                            toolbar.getMenu().getItem(5).setVisible(true);
                        }
                    }
                    else{
                        if (Application_Singleton.selectedshareCatalog.getIs_disable().equals("true")) {
                            toolbar.getMenu().getItem(3).setVisible(false);
                        }
                        else{
                            toolbar.getMenu().getItem(3).setVisible(true);
                        }
                    }
                }*/
                preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }

                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_cart_purchase:
                                Log.i("TAG", "onMenuItemClick: Purchase Order 2");
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                                Fragment_PurchaseBackOrder purchase = new Fragment_PurchaseBackOrder();
                                Bundle bundle = new Bundle();
                                bundle.putString("ordertype", "selection");
                                bundle.putString("ordervalue", DataPasser.selectedID);
                                purchase.setArguments(bundle);
                                Application_Singleton.CONTAINERFRAG = purchase;
                                Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_cart_sales:
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                                Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                                //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("ordertype", "selections");
                                bundle1.putString("selectiontype", "my");
                                bundle1.putString("ordervalue", DataPasser.selectedID);
                                createOrderFrag.setArguments(bundle1);

                                Application_Singleton.CONTAINERFRAG = createOrderFrag;
                                Intent intent1 = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent1);

                                break;

                           /* case R.id.menu_disable_catalog:
                                HashMap<String, String> params=new HashMap<String, String>();
                                String type="catalog";
                                params.put(type,Application_Singleton.selectedshareCatalog.getId());
                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this,"catalogs_disable",Application_Singleton.selectedshareCatalog.getId()), null, headers, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        Log.v("CHECKED ENABLED", response);
                                        HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.RECIEVED_CAT_APP,null);
                                        //   HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs_expand_true","") + Application_Singleton.selectedshareCatalog.getId(),null);

                                        finish();

                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }

                                });
                                //finish();
                                break;
                            case R.id.menu_enable_catalog:
                                HashMap<String, String> param=new HashMap<String, String>();
                                String type1="catalog";
                                param.put(type1,Application_Singleton.selectedshareCatalog.getId());
                                HashMap<String, String> headers1 = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this,"catalogs_enable",Application_Singleton.selectedshareCatalog.getId()), null, headers1, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        Log.v("CHECKED DISABLED", response);
                                        toolbar.getMenu().getItem(1).setVisible(true);
                                        toolbar.getMenu().getItem(2).setVisible(true);
                                        toolbar.getMenu().getItem(3).setVisible(true);
                                        toolbar.getMenu().getItem(4).setVisible(false);
                                        toolbar.getMenu().getItem(5).setVisible(true);
                                        HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.RECIEVED_CAT_APP,null);
                                        // HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs_expand_true","") + Application_Singleton.selectedshareCatalog.getId(),null);

                                        finish();
                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {







                                    }
                                });

                                break;*/
                        }
                        return false;
                    }
                });
                break;
            case CATALOGSHARE:
                toolbar.inflateMenu(R.menu.menu_sharecat);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(0); // for display brand follow
                } // for display brand follow
                userinfo = UserInfo.getInstance(OpenContainer.this);
                preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }

                if (userinfo.getGroupstatus().equals("1")) {
                    toolbar.getMenu().getItem(7).setVisible(false);
                    toolbar.getMenu().getItem(1).setVisible(false);// disable create purchase order

                }


                if (!userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    //setting tutorial
                    try {
                        StaticFunctions.checkAndShowTutorial(OpenContainer.this, "share_tut_shown_product_level", toolbar.getChildAt(2).findViewById(R.id.menu_share), getResources().getString(R.string.share_tutorial_text), "bottom");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(1).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }
                if (Application_Singleton.selectedshareCatalog != null) {
                    if (!userinfo.getGroupstatus().equals("2")) {
                        if (Application_Singleton.selectedshareCatalog.getBuyer_disabled()) {
                            toolbar.getMenu().getItem(1).setVisible(false);
                            toolbar.getMenu().getItem(2).setVisible(false);
                            toolbar.getMenu().getItem(3).setVisible(false);
                            toolbar.getMenu().getItem(4).setVisible(true);
                        } else {
                            toolbar.getMenu().getItem(5).setVisible(false);
                        }
                    } else {
                        if (Application_Singleton.selectedshareCatalog.getBuyer_disabled()) {
                            toolbar.getMenu().getItem(3).setVisible(false);
                        } else {
                            toolbar.getMenu().getItem(3).setVisible(true);
                        }
                    }
                }

                if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }

                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.barcode:
                                String[] permissions = {
                                        "android.permission.WRITE_EXTERNAL_STORAGE"
                                };
                                if (ContextCompat.checkSelfPermission(OpenContainer.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(OpenContainer.this,
                                            permissions, 800);
                                } else {
                                    generateBarcode();
                                }

                                break;
                            case R.id.menu_cart_purchase:
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                                //Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                                //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
                                Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();
                                Bundle bundle = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }
                                purchase.setArguments(bundle);

                                Application_Singleton.CONTAINERFRAG = purchase;
                                Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_cart_sales:

                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                                Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                                Bundle bundle1 = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle1.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle1.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }

                                createOrderFrag.setArguments(bundle1);

                                Application_Singleton.CONTAINERFRAG = createOrderFrag;
                                Intent intent1 = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent1);

                                break;

                            case R.id.menu_disable_catalog:
                                HashMap<String, String> params = new HashMap<String, String>();
                                String type = "catalog";
                                params.put(type, Application_Singleton.selectedshareCatalog.getId());
                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this, "catalogs_disable", Application_Singleton.selectedshareCatalog.getId()), null, headers, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        try {
                                            Log.v("CHECKED ENABLED", response);
                                            //  HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.RECIEVED_CAT_APP,null);
                                            //   HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs_expand_true","") + Application_Singleton.selectedshareCatalog.getId(),null);
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }

                                });
                                //finish();
                                break;
                            case R.id.menu_enable_catalog:
                                HashMap<String, String> param = new HashMap<String, String>();
                                String type1 = "catalog";

                                if (Application_Singleton.selectedshareCatalog != null) {
                                    param.put(type1, Application_Singleton.selectedshareCatalog.getId());

                                }
                                HashMap<String, String> headers1 = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this, "catalogs_enable", Application_Singleton.selectedshareCatalog.getId()), null, headers1, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        try {
                                            Log.v("CHECKED DISABLED", response);
                                            toolbar.getMenu().getItem(1).setVisible(true);
                                            toolbar.getMenu().getItem(2).setVisible(true);
                                            toolbar.getMenu().getItem(3).setVisible(true);
                                            toolbar.getMenu().getItem(4).setVisible(false);
                                            toolbar.getMenu().getItem(5).setVisible(true);
                                            //  HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.RECIEVED_CAT_APP,null);
                                            // HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs_expand_true","") + Application_Singleton.selectedshareCatalog.getId(),null);
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {


                                    }
                                });

                                break;
                            case R.id.chat_with_supplier:


                                Intent intentToChat = new Intent(OpenContainer.this, ConversationActivity.class);
                                intentToChat.putExtra(ConversationUIService.USER_ID, Application_Singleton.selectedshareCatalog.getSupplier_chat_user());
                                intentToChat.putExtra(ConversationUIService.DISPLAY_NAME, Application_Singleton.selectedshareCatalog.getSupplier()); //put it for displaying the title.
                                startActivity(intentToChat);
                                break;
                        }
                        return false;
                    }
                });
                break;
            case PRODUCTFAV:
                toolbar.setBackground(getResources().getDrawable(R.drawable.background_toolbar_translucent));
                StaticFunctions.setUpselectedProdCounter(OpenContainer.this, toolbar);
                break;
            case SHAREDBYME:
                toolbar.inflateMenu(R.menu.menu_sharecat);
                userinfo = UserInfo.getInstance(OpenContainer.this);
                if (!userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(2).setVisible(true);

                }
                if (userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(3).setVisible(false);
                }

                if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }
                preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }

                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_cart_purchase:
                                Log.i("TAG", "onMenuItemClick: Purchase Order 4");
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                                //Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                                //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
                                Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();
                                Bundle bundle = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }
                                purchase.setArguments(bundle);

                                Application_Singleton.CONTAINERFRAG = purchase;
                                Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_cart_sales:
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                                //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                                Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                                Bundle bundle1 = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle1.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle1.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }

                                createOrderFrag.setArguments(bundle1);

                                Application_Singleton.CONTAINERFRAG = createOrderFrag;
                                Intent intent1 = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent1);
                                break;

                        }
                        return false;
                    }
                });
                break;
            case CATALOGAPP:
                toolbar.inflateMenu(R.menu.menu_sharecat);
                preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(0); // for display brand follow
                } // for display brand follow bar
                userinfo = UserInfo.getInstance(OpenContainer.this);
                if (bundle.getStringExtra("pushId") == null && !userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().findItem(R.id.menu_add).setVisible(true);
                    toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
                    if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getBuyer_disabled()) {
                        toolbar.getMenu().findItem(R.id.menu_add).setVisible(false);
                        toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
                    } else {
                    }
                } else {
                    if (!userinfo.getGroupstatus().equals("2")) {
                        toolbar.getMenu().findItem(R.id.menu_share).setVisible(true);
                        if (Application_Singleton.selectedshareCatalog != null) {
                            if (Application_Singleton.selectedshareCatalog.getBuyer_disabled()) {
                            } else {
                                toolbar.getMenu().findItem(R.id.menu_share).setVisible(true);
                            }
                        }
                    } else {
                        if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getBuyer_disabled()) {
                        } else {
                        }
                    }
                }

               /* if (UserInfo.getInstance(OpenContainer.this).getGroupstatus().equals("2") && !UserInfo.getInstance(OpenContainer.this).getBuyerSplitSalesperson()) {
                    toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
                } else {
                    toolbar.getMenu().findItem(R.id.menu_share).setVisible(true);
                }


                if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
                }*/

                if (Application_Singleton.selectedshareCatalog != null
                        && Application_Singleton.selectedshareCatalog.is_owner()) {
                    // hide for new ui and show in detail page
                    toolbar.getMenu().findItem(R.id.menu_add).setVisible(false);
                } else {
                    toolbar.getMenu().findItem(R.id.menu_add).setVisible(false);
                }

                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (UserInfo.getInstance(OpenContainer.this).isGuest()) {
                            StaticFunctions.ShowRegisterDialog(OpenContainer.this, "");
                            return false;
                        }
                        switch (item.getItemId()) {

                            case R.id.cart:
                                SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                                int cart_count = preferences.getInt("cartcount", 0);
                                Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                                StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                                break;
                            case R.id.barcode:
                                String[] permissions = {
                                        "android.permission.WRITE_EXTERNAL_STORAGE"
                                };
                                if (ContextCompat.checkSelfPermission(OpenContainer.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(OpenContainer.this,
                                            permissions, 800);
                                } else {
                                    generateBarcode();
                                }
                                break;
                            case R.id.menu_cart_purchase:
                                Log.i("TAG", "onMenuItemClick: Purchase Order 5");
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                                //Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                                //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
                                Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();
                                Bundle bundle = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }
                                purchase.setArguments(bundle);

                                Application_Singleton.CONTAINERFRAG = purchase;
                                Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent);

                                break;
                            case R.id.menu_cart_sales:
                                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                                //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                                Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                                Bundle bundle1 = new Bundle();
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    bundle1.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                    bundle1.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                }
                                createOrderFrag.setArguments(bundle1);

                                Application_Singleton.CONTAINERFRAG = createOrderFrag;
                                Intent intent1 = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent1);
                                break;

                            case R.id.menu_add:




                                break;
                            case R.id.menu_disable_catalog:
                                HashMap<String, String> params = new HashMap<String, String>();
                                String type = "catalog";
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    params.put(type, Application_Singleton.selectedshareCatalog.getId());
                                }
                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this, "catalogs_disable", Application_Singleton.selectedshareCatalog.getId()), null, headers, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        try {
                                            Log.v("CHECKED ENABLED", response);
                                            // HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs",""),null);
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }

                                });
                                finish();
                                break;
                            case R.id.menu_enable_catalog:
                                HashMap<String, String> param = new HashMap<String, String>();
                                String type1 = "catalog";
                                if (Application_Singleton.selectedshareCatalog != null) {
                                    param.put(type1, Application_Singleton.selectedshareCatalog.getId());
                                }
                                HashMap<String, String> headers1 = StaticFunctions.getAuthHeader(OpenContainer.this);
                                HttpManager.getInstance(OpenContainer.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(OpenContainer.this, "catalogs_enable", Application_Singleton.selectedshareCatalog.getId()), null, headers1, true, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {
                                        onServerResponse(response, false);
                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        try {
                                            Log.v("CHECKED DISABLED", response);
                                            toolbar.getMenu().getItem(1).setVisible(true);
                                            toolbar.getMenu().getItem(2).setVisible(true);
                                            toolbar.getMenu().getItem(3).setVisible(true);
                                            toolbar.getMenu().getItem(4).setVisible(false);
                                            toolbar.getMenu().getItem(5).setVisible(true);
                                            //HttpManager.getInstance(OpenContainer.this).removeCacheParams(URLConstants.companyUrl(getActivity(),"catalogs",""),null);
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }
                                });
                                break;
                            case R.id.change_type:

                                final String view_permission = Application_Singleton.selectedshareCatalog.getView_permission();
                                MaterialDialog dialog = new MaterialDialog.Builder(OpenContainer.this)
                                        .positiveText(R.string.ok).negativeText(R.string.no)
                                        .customView(R.layout.change_catalog_type_layout, true)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                switch (radioGroup.getCheckedRadioButtonId()) {
                                                    case R.id.private_type:
                                                        if (!view_permission.equals("private")) {

                                                        } else {
                                                            Toast.makeText(OpenContainer.this, "Catalog is already private", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                        break;
                                                    case R.id.public_type:
                                                        if (!view_permission.equals("public")) {
                                                            if (!public_price.getText().equals("")) {
                                                                //patch
                                                            } else {
                                                                Toast.makeText(OpenContainer.this, "Enter public price", Toast.LENGTH_LONG).show();
                                                                return;
                                                            }

                                                        } else {
                                                            Toast.makeText(OpenContainer.this, "Catalog is already public", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                        break;
                                                }
                                            }
                                        })
                                        .build();

                                public_price = (EditText) dialog.getCustomView().findViewById(R.id.add_public_price);
                                radioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.catalog_type_radio_group);

                                if (view_permission.equals("private")) {
                                    public_price.setVisibility(View.GONE);
                                } else {
                                    public_price.setVisibility(View.VISIBLE);
                                }


                                /*new MaterialDialog.Builder(OpenContainer.this).title(R.string.change_type_price)
                                        .positiveText(R.string.ok).negativeText(R.string.no)
                                        .customView(R.layout.change_catalog_type_layout,true)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                String view_permission = Application_Singleton.selectedshareCatalog.getView_permission();
                                                if(view_permission!=null) {
                                                    patchTypeOfCatalog(dialog, OpenContainer.this);
                                                }
                                            }
                                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).show();*/
                                break;


                        }
                        return false;
                    }
                });
                break;
            case SHAREDWITHME:
                toolbar.inflateMenu(R.menu.menu_sharecat);
                preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    ActionItemBadge actionItemBadge = new ActionItemBadge();
                }
                userinfo = UserInfo.getInstance(OpenContainer.this);
                if (!userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(2).setVisible(true);

                }
                if (userinfo.getGroupstatus().equals("2")) {
                    toolbar.getMenu().getItem(1).setVisible(false);
                }

                if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                }
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_cart_purchase:
                                //   Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                                //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
                                Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();
                                Bundle bundle = new Bundle();
                                bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                                bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
                                purchase.setArguments(bundle);
                                Application_Singleton.CONTAINER_TITLE = "Purchase Order";
                                Application_Singleton.CONTAINERFRAG = purchase;
                                Intent intent = new Intent(OpenContainer.this, OpenContainer.class);
                                startActivity(intent);
                                break;

                        }
                        return false;
                    }
                });
                break;

            case SCAN_BARCODE_SALESMAN:
                toolbar.inflateMenu(R.menu.menu_scan_barcode);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_scan_barcode) {
                            if (ContextCompat.checkSelfPermission(OpenContainer.this, android.Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(OpenContainer.this,
                                        new String[]{android.Manifest.permission.CAMERA}, 1);
                            } else {

                                Intent i = new Intent(OpenContainer.this, SimpleScannerActivity.class);
                                Application_Singleton.CONTAINERFRAG.startActivityForResult(i, 1);
                            }
                        }
                        return false;
                    }
                });
                break;
            case ADD_CATALOG:
                toolbar.inflateMenu(R.menu.menu_add_catalog);
                if (Activity_Home.pref != null) {
                    if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(false);
                    }
                } else if (UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("buyer")) {
                    toolbar.getMenu().getItem(1).setVisible(false);
                } else if (Application_Singleton.CONTAINER_TITLE.equals("My Catalog")) {
                    toolbar.getMenu().findItem(R.id.menu_add).setVisible(true);
                    toolbar.getMenu().findItem(R.id.menu_scan_qr).setVisible(false);
                } else {
                    //setting tutorial
                    // StaticFunctions.checkAndShowTutorial(OpenContainer.this,"add_catalog_tut_shown",toolbar.getChildAt(1).findViewById(R.id.menu_add),getResources().getString(R.string.add_catalog_tutorial_text),"bottom");
                }
                toolbar.getMenu().findItem(R.id.menu_scan_qr).setVisible(false);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menu_add:
                                if (UserInfo.getInstance(OpenContainer.this).isGuest()) {
                                    StaticFunctions.ShowRegisterDialog(OpenContainer.this, "");
                                    break;
                                }
                                /*Application_Singleton.CONTAINER_TITLE = "Add Catalog";
                                Bundle bundle = new Bundle();
                                bundle.putString("isFrom","openContainer");
                                Fragment_AddCatalog fragment_addCatalog = new Fragment_AddCatalog();
                                fragment_addCatalog.setArguments(bundle);
                                Application_Singleton.CONTAINERFRAG = fragment_addCatalog;
                                //StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                                OpenContainer.this.startActivityForResult(new Intent(OpenContainer.this,OpenContainer.class),Application_Singleton.ADD_CATALOG_REQUEST);*/
                                OpenContainer.this.startActivityForResult(new Intent(OpenContainer.this, Activity_AddCatalog.class), Application_Singleton.ADD_CATALOG_REQUEST_CODE);
                                break;
                            case R.id.menu_scan_qr:
                                if (UserInfo.getInstance(OpenContainer.this).isGuest()) {
                                    StaticFunctions.ShowRegisterDialog(OpenContainer.this, "");
                                    break;
                                }
                                if (ContextCompat.checkSelfPermission(OpenContainer.this, android.Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(OpenContainer.this,
                                            new String[]{Manifest.permission.CAMERA}, 1);
                                } else {
                                    Intent i = new Intent(OpenContainer.this, SimpleScannerActivity.class);
                                    startActivityForResult(i, 9000);
                                }
                                break;
                        }
                        return false;
                    }
                });
                break;
            default:
                // StaticFunctions.setUpselectedProdCounter(OpenContainer.this, toolbar);
                break;

        }

    }

    private void showTutorial() {
        // StaticFunctions.TutorialTargetToolbarItem(CatalogHolder.toolbar,CatalogHolder.toolbar.getMenu().getItem(1).getItemId(),"Add Catalog","Add new catalog from here"),
       /* TapTargetView.showFor(context,StaticFunctions.TutorialTargetToolbarItem(toolbar,toolbar.getMenu().getItem(0).getItemId(),title,description),new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);      // This call is optional
                Application_Singleton.tutorial_pref.edit().putBoolean("enquiry_tut_shown",true).apply();

            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                Application_Singleton.tutorial_pref.edit().putBoolean("enquiry_tut_shown",true).apply();

            }

            @Override
            public void onTargetCancel(TapTargetView view) {
                super.onTargetCancel(view);
                Application_Singleton.tutorial_pref.edit().putBoolean("enquiry_tut_shown",true).apply();

            }
        });*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.initializeAppsee();
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_container);
        bundle = getIntent();
        onBackPressedListener = null;
        context = OpenContainer.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Application_Singleton.TOOLBARSTYLE.equals("WHITE")) {
            toolbar.setTitle(Application_Singleton.CONTAINER_TITLE);
            toolbar.setBackgroundColor(getResources().getColor(R.color.white));
            toolbar.setTitleTextColor(getResources().getColor(R.color.color_primary));
            Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
            icon.setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);
            toolbar.setNavigationIcon(icon);
            Application_Singleton.TOOLBARSTYLE = "BLUE";
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
            toolbar.setTitle(Application_Singleton.CONTAINER_TITLE);
            toolbar.setTitleTextColor(Color.WHITE);
            Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            toolbar.setNavigationIcon(icon);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        badgeTextView = (TextView) findViewById(R.id.badge_textview);
        support_chat_fab = (FloatingActionButton) findViewById(R.id.support_chat_fab);


        if (bundle.getBooleanExtra("havingTabs", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0);
            }
        }


        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, Application_Singleton.CONTAINERFRAG).commit();
        } catch (Exception e) {

        }

        support_chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Freshchat.showConversations(getApplicationContext());
            }
        });
        if (support_chat_fab != null) {
            support_chat_fab.setVisibility(View.GONE);
        }
        /*
        if (Application_Singleton.CONTAINERFRAG instanceof Fragment_CreatePurchaseOrderVersion2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_CreateSaleOrder_Version2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_AddCatalog ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_SettingsNew ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_SupplierDetailsNew2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Register_New_Version2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_BuyerDetails ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Profile ||
                Application_Singleton.CONTAINERFRAG instanceof FragmentCreditRating ||
                Application_Singleton.CONTAINERFRAG instanceof FragmentCreditRatingScan ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_MyViewers ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_CatalogEnquiry ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_MyViewersDetail ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Buyer_Rating ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Credit_Reference_Suppliers ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Order_Holder_Version3 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_AddProduct ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_My_Viewers ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_MyCart ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Cart_Invoice ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_ChangePassword ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_CashPayment_2 ||
                Application_Singleton.CONTAINERFRAG instanceof FragmentAddBrandDiscountVersion2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Manage_Images ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_GST_2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_ShipTrack ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_ContactUs_Version2 ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_ResellerHolder ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_BannerWebView ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_InviteFriend ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_MyEarningHolder ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_MyEarningHolder ||
                Application_Singleton.CONTAINERFRAG instanceof BottomReferandEarn ||
                Application_Singleton.CONTAINERFRAG instanceof RewardTcBottomSheetFragment ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Payment ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_BrowseProduct ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_CatalogsGallery ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_SellerHub ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_ProductAllReview ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_SingleDetail ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Seller_Pending_Order_item ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_Replacement ||
                Application_Singleton.CONTAINERFRAG instanceof Fragment_AddBuyerGroup_Version2) {
            if (support_chat_fab != null) {
                support_chat_fab.setVisibility(View.GONE);
            }


        }
        */

    }

    private File savebitmap(String filename, Bitmap bitmap) {
        File mDirectory = createDirectory(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/qrcodes/");
        FileOutputStream outStream;

        File file = new File(mDirectory, filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(mDirectory, filename + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    private File createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Open Container request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9000 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringExtra("content") != null) {
                boolean isValid = URLUtil.isValidUrl(data.getStringExtra("content"));
                if (isValid) {
                    Uri intentUri = Uri.parse(data.getStringExtra("content"));
                    callDeepLinkIntent(intentUri);
                }

            }
        } else if (requestCode == Application_Singleton.ADD_CATALOG_REQUEST && resultCode == Activity.RESULT_OK) {
            showBankDetailsDialog(0);
        } else if (requestCode == CFPaymentService.REQ_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Log.d("TAG", "onActivityResult: CashFree Data is Not Null======>");
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if(Application_Singleton.CONTAINERFRAG instanceof Fragment_Payment) {
                        ((Fragment_Payment) Application_Singleton.CONTAINERFRAG).handleCashFreeResponse(bundle);
                    } else if(Application_Singleton.CONTAINERFRAG instanceof Fragment_CashPayment) {
                        ((Fragment_CashPayment) Application_Singleton.CONTAINERFRAG).handleCashFreeResponse(bundle);
                    } else if(Application_Singleton.CONTAINERFRAG instanceof Fragment_CashPayment_2) {
                        ((Fragment_CashPayment_2) Application_Singleton.CONTAINERFRAG).handleCashFreeResponse(bundle);
                    }
                } else {
                    Log.d("TAG", "onActivityResult: CashFree Bundle Data is Null======>");
                }
            } else {
                Log.d("TAG", "<=======onActivityResult: CashFree Data is null======>");
            }
        }
    }




    public void getCatalogOptionsFromServer(AppCompatActivity activity, final Bundle bundle2, String catalog_id, final String catalog_type) {
        final Fragment_AddProduct addProduct = new Fragment_AddProduct();
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
            HttpManager.getInstance(activity).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(activity, "catalogs_upload_option_with_id", catalog_id), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        Log.v("sync response", response);
                        CatalogUploadOption[] catalogUploadOption = Application_Singleton.gson.fromJson(response, CatalogUploadOption[].class);

                        if (catalogUploadOption.length > 0 && catalogUploadOption[0] != null) {
                            if (catalogUploadOption[0].getPrivate_single_price() != null) {
                                bundle2.putString("catalog_price", catalogUploadOption[0].getPrivate_single_price());
                            }
                            if (catalogUploadOption[0].getPublic_single_price() != null) {
                                bundle2.putString("public_price", catalogUploadOption[0].getPublic_single_price());
                                bundle2.putString("catalog_price", catalogUploadOption[0].getPublic_single_price());
                            }

                            bundle2.putBoolean("productsWithoutSKU", catalogUploadOption[0].getWithout_sku());
                            bundle2.putBoolean("productsWithoutPrice", catalogUploadOption[0].getWithout_price());

                            if (catalogUploadOption[0].getFabric() != null) {
                                bundle2.putString("fabric", catalogUploadOption[0].getFabric());
                            }

                            if (catalogUploadOption[0].getWork() != null) {
                                bundle2.putString("work", catalogUploadOption[0].getWork());
                            }
                        }

                        bundle2.putString("catalog_type", catalog_type);
                        bundle2.putString("from", Fragment_CatalogsGallery.class.getSimpleName());
                        addProduct.setArguments(bundle2);
                        Application_Singleton.CONTAINERFRAG = addProduct;
                        StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    addProduct.setArguments(bundle2);
                    Application_Singleton.CONTAINERFRAG = addProduct;
                    StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                }
            });
        } catch (Exception e) {
            addProduct.setArguments(bundle2);
            Application_Singleton.CONTAINERFRAG = addProduct;
            StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);

        }

    }

    public interface OnBackPressedListener {
        public void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (this.onBackPressedListener != null) {
            Log.i("TAG", "onBackPressed: Open Container");
            onBackPressedListener.doBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bundle.getIntExtra("toolbarCategory", -1) == 7) {
            getMenuInflater().inflate(R.menu.menu_browsecatalog, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("TAG", "onOptionsItemSelected: OpenContainer");
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.cart: {
                SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cart_count = preferences.getInt("cartcount", 0);
                Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                return true;
            }

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_wishlist:
                Log.d("TAG", "onMenuItemClick: WishList Clik");
                Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                startActivity(new Intent(OpenContainer.this, OpenContainer.class));
                return true;

            case R.id.action_cart: {
                try {
                    SharedPreferences preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cart_count = preferences.getInt("cartcount", 0);
                    Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(OpenContainer.this, OpenContainer.class);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void generateBarcode() {
        try {
            Writer writer = new QRCodeWriter();
            //  String finaldata = Uri.encode("b2b.wishbook.io/m/catalogs/1101", "utf-8");
            String finaldata = URLConstants.BARCODE_APP_URL + "/m/catalogs/" + Application_Singleton.selectedshareCatalog.getId() + "?supplier=" + UserInfo.getInstance(OpenContainer.this).getCompany_id();
            BitMatrix bm = null;
            try {
                bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, 150, 150);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            Bitmap ImageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 150; i++) {//width
                for (int j = 0; j < 150; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            String shareText = Application_Singleton.selectedshareCatalog.getTitle() + " by " + Application_Singleton.selectedshareCatalog.getBrand_name() + "\nPlease scan QR code to view catalog in Wishbook";
            File Barcode_image = savebitmap(Application_Singleton.selectedshareCatalog.getTitle().toLowerCase().trim(), ImageBitmap);
            //API 24 BUG Uri.fromFile- http://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(OpenContainer.this, getApplicationContext().getPackageName() + ".provider", Barcode_image);
            } else {
                uri = Uri.fromFile(Barcode_image);
            }

            ArrayList<Uri> uris = new ArrayList<Uri>();
            uris.add(uri);
            StaticFunctions.genericShare(OpenContainer.this, uris, shareText);
                            /*    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("image*//*");
                                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(sharingIntent, "Share Image"));*/

        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 800) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    generateBarcode();
                } else {
                    // Permission Denied
                    Toast.makeText(OpenContainer.this, "Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    public void callDeepLinkIntent(Uri intentUri) {
        String type = null;
        if (intentUri.getQueryParameter("type") != null) {
            type = intentUri.getQueryParameter("type");
        } else if (intentUri.getQueryParameter("t") != null) {
            type = intentUri.getQueryParameter("t");
        }
        String id = intentUri.getQueryParameter("id");
        if (type != null) {
            Intent intent = new Intent(OpenContainer.this, Activity_Home.class);
            if (type != null) {
                HashMap<String, String> hashMap = SplashScreen.getQueryString(intentUri);
                new DeepLinkFunction(hashMap, OpenContainer.this);
            }
        } else {
            String catalog = intentUri.getPath().replaceAll("\\D+", "");
            String supplier = intentUri.getQueryParameter("supplier");
            StaticFunctions.catalogQR(catalog, OpenContainer.this, supplier);
        }
    }

    public void updateBadge(int count) {
        Log.e("TAG", "updateBadge: =-====>");
        try {
            if (count == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(this, toolbar.getMenu().findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBankDetailsDialog(int type) {
        boolean isBankDetailAvailable = false, isPanAvailable = false, isGstAvailable = false, isPaytmAvilable = false;
        boolean isShowDialog = false;
        if (UserInfo.getInstance(context).getBankDetails() != null) {
            isBankDetailAvailable = true;
        }
        if (UserInfo.getInstance(context).getKyc() != null) {
            PostKycGst postKycGst = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getKyc(), PostKycGst.class);
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
            new MaterialDialog.Builder(context)
                    .content(msg)
                    .positiveText("Add Details")
                    .cancelable(true)
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
        } else {
            Log.e("TAG", "showBankDetailsDialog: All Value True ");
        }

    }

    public void cartToolbarUpdate() {
        if (!UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("seller")) {
            try {
                SharedPreferences preferences = OpenContainer.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(OpenContainer.this, menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(OpenContainer.this, menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            menu.findItem(R.id.action_cart).setVisible(false);
        }
    }

    private void wishListToolbarUpdate() {
        if (!UserInfo.getInstance(OpenContainer.this).getCompanyType().equals("seller")) {
            menu.findItem(R.id.action_wishlist).setVisible(true);
            int wishcount = UserInfo.getInstance(OpenContainer.this).getWishlistCount();
            if (wishcount == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(OpenContainer.this, menu.findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(OpenContainer.this, menu.findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
            }
        } else {
            menu.findItem(R.id.action_wishlist).setVisible(false);
        }
    }
}
