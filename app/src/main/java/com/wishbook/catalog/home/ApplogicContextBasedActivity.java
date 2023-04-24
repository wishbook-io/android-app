package com.wishbook.catalog.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.OpenContextBasedApplogicChat;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogEnquiry;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreatePurchaseOrderVersion2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ApplogicContextBasedActivity extends ConversationActivity {

    private Context mContext;
    private Toolbar toolbar;

    @BindView(R.id.catalog_img)
    SimpleDraweeView catalog_img;

    @BindView(R.id.txt_catalog_name)
    TextView txt_catalog_name;

    @BindView(R.id.txt_price_range)
    TextView txt_price_range;

    @BindView(R.id.txt_resolve)
    TextView txt_resolve;

    @BindView(R.id.btn_buy_now)
    AppCompatButton btn_buy_now;

    @BindView(R.id.linear_catalog_detail)
    LinearLayout linear_catalog_detail;

    ResponseCatalogEnquiry responseCatalogEnquiry;

    private String initTimeStatus;
    boolean isSupplierApproved, isTrustedSeller;
    CatalogMinified response_catalogs;
    MultipleSuppliers multipleSuppliers[];
    private static String TAG = ApplogicContextBasedActivity.class.getSimpleName();
    private ApplozicPermissions applozicPermission;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ApplogicContextBasedActivity.this;
        setContentView(R.layout.activity_applogic_new_ui);
        ButterKnife.bind(this);
        setupTopBar();
        layout = (LinearLayout) findViewById(R.id.footerAd);
        applozicPermission = new ApplozicPermissions(this, layout);
        createAnalyticsScreenEvent();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = null;
            String messageJson = intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT);
            if (!TextUtils.isEmpty(messageJson)) {
                message = (Message) GsonUtils.getObjectFromJson(messageJson, Message.class);
            }
            Log.e(TAG, "Got Messge: " + message);
            if (responseCatalogEnquiry != null) {
                sendResponseAnalyticsEvent();
                if (responseCatalogEnquiry.getStatus() != null && responseCatalogEnquiry.getStatus().equals(Constants.RESOLVED_ENQUIRY)) {
                    patchResolved(responseCatalogEnquiry.getId(), Constants.CREATED_ENQUIRY);

                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            findViewById(R.id.spinner_show).setVisibility(View.GONE);
            findViewById(R.id.contextFrameLayout).setVisibility(View.GONE);
            findViewById(R.id.my_toolbar).setClickable(false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    protected void setupTopBar() {
        if (getIntent().getSerializableExtra("catalog_detail") != null) {
            responseCatalogEnquiry = (ResponseCatalogEnquiry) getIntent().getSerializableExtra("catalog_detail");
            if (responseCatalogEnquiry.getThumbnail() != null && !responseCatalogEnquiry.getThumbnail().isEmpty()) {
                StaticFunctions.loadFresco(mContext, responseCatalogEnquiry.getThumbnail(), catalog_img);
            }

            if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.SUPPLIERTOBUYER)) {
                txt_resolve.setVisibility(View.VISIBLE);
            } else {
                txt_resolve.setVisibility(View.GONE);
            }
            if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.BUYERTOSUPPLIER)) {
                btn_buy_now.setVisibility(View.VISIBLE);
            } else {
                btn_buy_now.setVisibility(View.GONE);
            }

            initTimeStatus = responseCatalogEnquiry.getStatus();
            Toolbar my_toolbar = findViewById(R.id.my_toolbar);
            my_toolbar.setTitleTextColor(Color.WHITE);
            my_toolbar.setTitle(getIntent().getStringExtra(ConversationUIService.DISPLAY_NAME));
            setSupportActionBar(my_toolbar);
            if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.SUPPLIERTOBUYER)) {
                my_toolbar.setTitle("Buyer");
            } else {
                my_toolbar.setTitle("Supplier");
            }

            my_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (initTimeStatus.equals(responseCatalogEnquiry.getStatus())) {
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    } else {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                }
            });

            catalog_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCatalogDetailPage(responseCatalogEnquiry);
                }
            });

            linear_catalog_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobiComQuickConversationFragment conversationFragment = new MobiComQuickConversationFragment();
                    new MobiComKitBroadcastReceiver(ApplogicContextBasedActivity.this);
                    // Log.e(TAG, "onClick: " +conversationFragment.getRecyclerView().getChildCount());
                    openCatalogDetailPage(responseCatalogEnquiry);
                }
            });

            invalidateOptionsMenu();

            txt_resolve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (responseCatalogEnquiry != null) {
                        if (responseCatalogEnquiry.getStatus() != null && responseCatalogEnquiry.getStatus().equals(Constants.CREATED_ENQUIRY)) {
                            patchResolved(responseCatalogEnquiry.getId(), Constants.RESOLVED_ENQUIRY);
                        }
                    }

                }
            });
            if(StaticFunctions.checkCatalogIsInCart(mContext, responseCatalogEnquiry.getProduct())){
                btn_buy_now.setText("GO TO CART");
            }
            btn_buy_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_buy_now.getText().toString().contains("GO")) {
                        try {
                            SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            int cart_count = preferences.getInt("cartcount", 0);
                            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " Pc)";
                            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                            StaticFunctions.switchActivity(ApplogicContextBasedActivity.this, OpenContainer.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            CartHandler cartHandler = new CartHandler(((AppCompatActivity) ApplogicContextBasedActivity.this));
                            List<Integer> q = new ArrayList<>();
                            for (int i = 0; i < Integer.parseInt(responseCatalogEnquiry.getTotal_products()); i++) {
                                q.add(1);
                            }
                            int a[] = new int[q.size()];
                            for (int i = 0; i < a.length; i++) {
                                a[i] = q.get(i).intValue();
                            }

                            String id;
                            if (responseCatalogEnquiry.getProduct() != null)
                                id = responseCatalogEnquiry.getProduct();
                            else
                                id = responseCatalogEnquiry.getCatalog();

                            btn_buy_now.setText("GO TO CART");
                            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                                @Override
                                public void onSuccess(CartProductModel response) {
                                    btn_buy_now.setText("GO TO CART");
                                }

                                @Override
                                public void onFailure() {
                                    btn_buy_now.setText("ADD TO CART");
                                }
                            });
                            cartHandler.addCatalogToCart(id, "", null, "contextbased", null, btn_buy_now, "Nan");
                            Application_Singleton.trackEvent("Add to cart", "Click", "From Chat");
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                   /* if (responseCatalogEnquiry != null) {
                        getCatalogData(responseCatalogEnquiry.getCatalog(), responseCatalogEnquiry.getSelling_company(), responseCatalogEnquiry.getSelling_company_name());

                    }*/
                }
            });

            if (responseCatalogEnquiry.getStatus() != null && responseCatalogEnquiry.getStatus().equals(Constants.RESOLVED_ENQUIRY)) {
                txt_resolve.setText("Resolved");
                txt_resolve.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
            }


            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastService.INTENT_ACTIONS.MESSAGE_SYNC_ACK_FROM_SERVER.toString());
            intentFilter.addAction("sendMessage");
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    intentFilter);





//            my_toolbar.getMenu().clear();

            txt_catalog_name.setText(responseCatalogEnquiry.getCatalog_title());
            String price_range = responseCatalogEnquiry.getPrice_range();
            if (price_range != null) {
                if (price_range.contains("-")) {
                    String[] priceRangeMultiple = price_range.split("-");
                    txt_price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc." + " , " + responseCatalogEnquiry.getTotal_products() + " Designs");
                } else {
                    String priceRangeSingle = price_range;
                    txt_price_range.setText("\u20B9" + priceRangeSingle + "/Pc." + ", " + responseCatalogEnquiry.getTotal_products() + " Designs");
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mobicom_basic_menu_for_normal_message, menu);
        menu.findItem(R.id.applozicUserProfile).setVisible(false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: ");
        if (item.getItemId() == R.id.applozicUserProfile) {
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openCatalogDetailPage(ResponseCatalogEnquiry responseCatalogEnquiry) {
        String id;
        if (responseCatalogEnquiry.getProduct() != null)
            id = responseCatalogEnquiry.getProduct();
        else
            id = responseCatalogEnquiry.getCatalog();

        Bundle bundle = new Bundle();
        bundle.putString("from", "Enquiry Chat Page");
        bundle.putString("product_id", id);
        new NavigationUtils().navigateDetailPage(mContext,bundle);

    }

    private void patchResolved(String enquiryID, final String status) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ApplogicContextBasedActivity.this);
        String url = URLConstants.companyUrl(ApplogicContextBasedActivity.this, "catalog-enquiries", "") + enquiryID + '/';
        ResponseCatalogEnquiry patchEnquiry = new ResponseCatalogEnquiry();
        if (status.equals(Constants.RESOLVED_ENQUIRY)) {
            patchEnquiry.setStatus(Constants.RESOLVED_ENQUIRY);
        } else {
            patchEnquiry.setStatus(Constants.CREATED_ENQUIRY);
        }

        HttpManager.getInstance(ApplogicContextBasedActivity.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchEnquiry), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.SUPPLIERTOBUYER)) {
                        txt_resolve.setVisibility(View.VISIBLE);
                    } else {
                        txt_resolve.setVisibility(View.GONE);
                    }
                    if (status.equals(Constants.RESOLVED_ENQUIRY)) {
                        txt_resolve.setText("Resolved");
                        txt_resolve.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
                        responseCatalogEnquiry.setStatus(Constants.RESOLVED_ENQUIRY);
                    } else {
                        txt_resolve.setText("Resolve");
                        txt_resolve.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                        responseCatalogEnquiry.setStatus(Constants.CREATED_ENQUIRY);
                    }
                    StaticFunctions.updateStatics(getApplicationContext());
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
    public void onBackPressed() {
        if (initTimeStatus.equals(responseCatalogEnquiry.getStatus())) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else {
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onBackPressed();
    }


    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(findViewById(R.id.footerAd), resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createPublicPurchaseOrder(CatalogMinified response_catalog, String supplier, String supplierName, boolean isSupplierApproved, boolean isTrusted) {


        //Fragment_CreatePurchaseOrderNew createOrderFrag = new Fragment_CreatePurchaseOrderNew();
        Application_Singleton.selectedshareCatalog = new CatalogMinified();
        Fragment_CreatePurchaseOrderVersion2 createOrderFrag = new Fragment_CreatePurchaseOrderVersion2();
        Application_Singleton.selectedshareCatalog.setSupplier(supplier);
        Application_Singleton.selectedshareCatalog.setSupplier_name(supplierName);
        Application_Singleton.selectedshareCatalog.setIs_supplier_approved(isSupplierApproved);
        if (response_catalog.getEavdata() != null) {
            Application_Singleton.selectedshareCatalog.setEavdata(response_catalog.getEavdata());
        }
        Application_Singleton.selectedshareCatalog.setIs_trusted_seller((isTrusted));
        Bundle bundle = new Bundle();
        bundle.putString("ordertype", "catalog");
        bundle.putString("ordervalue", response_catalog.getId());
        bundle.putString("selling_company", supplier);
        bundle.putBoolean("is_public", true);
        bundle.putBoolean("is_supplier_approved", isSupplierApproved);
        bundle.putString("full_catalog_order", response_catalog.getFull_catalog_orders_only());
        createOrderFrag.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = ApplogicContextBasedActivity.this.getResources().getString(R.string.new_purchase_order);
        Application_Singleton.CONTAINERFRAG = createOrderFrag;
        Intent intent = new Intent(ApplogicContextBasedActivity.this, OpenContainer.class);
        ApplogicContextBasedActivity.this.startActivity(intent);
    }

    public void getCatalogData(final String id, final String supplier_id, final String supplier_name) {
        StaticFunctions.showProgressbar(ApplogicContextBasedActivity.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(this);
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "catalogs", "") + id + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ApplogicContextBasedActivity.this);

                    response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified.class);

                    if (!isFinishing()) {
                        if (response_catalogs != null) {
                            getisTrustedSeller(id, supplier_id, supplier_name);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ApplogicContextBasedActivity.this);
            }

        });

    }

    public void getisTrustedSeller(final String id, final String supplier_id, final String supplier_name) {
        StaticFunctions.showProgressbar(ApplogicContextBasedActivity.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(this);
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "catalogs", "") + id + "/all_suppliers/?company=" + supplier_id, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ApplogicContextBasedActivity.this);
                    multipleSuppliers = Application_Singleton.gson.fromJson(response, MultipleSuppliers[].class);
                    if (!isFinishing()) {
                        if (multipleSuppliers != null && multipleSuppliers.length > 0) {
                            isSupplierApproved = multipleSuppliers[0].getRelation_id() == null ? false : true;
                            isTrustedSeller = multipleSuppliers[0].getTrusted_seller();
                            createPublicPurchaseOrder(response_catalogs, supplier_id, supplier_name, isSupplierApproved, isTrustedSeller);
                        } else {
                            Toast.makeText(ApplogicContextBasedActivity.this, "Catalog is disabled by supplier", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ApplogicContextBasedActivity.this);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsUtils.REQUEST_STORAGE) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.storage_permission_granted);
                if (isAttachment) {
                    isAttachment = false;
                    processAttachment();
                }
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.storage_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_LOCATION) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.location_permission_granted);
                processingLocation();
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.location_permission_not_granted);
            }

        } else if (requestCode == PermissionsUtils.REQUEST_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_state_permission_granted);
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_state_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_call_permission_granted);
                processCall(contact, currentConversationId);
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_call_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_AUDIO_RECORD) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.record_audio_permission_granted);
                showAudioRecordingDialog();
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.record_audio_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_camera_permission_granted);
                if (isTakePhoto) {
                    processCameraAction();
                } else {
                    processVideoRecording();
                }
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_camera_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CONTACT) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.contact_permission_granted);
                processContact();
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.contact_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA_AUDIO) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.phone_camera_and_audio_permission_granted);
            } else {
                showSnackBar(com.applozic.mobicomkit.uiwidgets.R.string.audio_or_camera_permission_not_granted);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void createAnalyticsScreenEvent() {
        if(getIntent()!=null) {
            if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.SUPPLIERTOBUYER)) {
                HashMap<String,String> prop = new HashMap<>();
                prop.put("lead_id",responseCatalogEnquiry.getId());
                prop.put("lead_date",responseCatalogEnquiry.getCreated());
                WishbookTracker.sendScreenEvents(mContext,WishbookEvent.SELLER_EVENTS_CATEGORY,"LeadDetails_Screen","",prop);
            } else {
                HashMap<String,String> prop = new HashMap<>();
                prop.put("catalog_item_id",responseCatalogEnquiry.getProduct());
                WishbookTracker.sendScreenEvents(mContext,WishbookEvent.ECOMMERCE_EVENTS_CATEGORY,"Enquiry_Screen","",prop);
            }
        }
    }


    public void sendResponseAnalyticsEvent() {
        if (getIntent().getStringExtra("conversion_type").equals(OpenContextBasedApplogicChat.SUPPLIERTOBUYER)) {
            HashMap<String,String> prop = new HashMap<>();
            prop.put("lead_id",responseCatalogEnquiry.getId());
            SimpleDateFormat sdf = new SimpleDateFormat(StaticFunctions.SERVER_POST_FORMAT);
            prop.put("lead_date",  sdf.format(new Date()));
            WishbookTracker.sendScreenEvents(mContext,WishbookEvent.SELLER_EVENTS_CATEGORY,"Leads_Respond","",prop);
        }
    }





}
