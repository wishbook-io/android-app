package com.wishbook.catalog.home.orderNew.details;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_warehouse;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.InvoiceItemSet;
import com.wishbook.catalog.commonmodels.postpatchmodels.OrderInvoiceCreate;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchAccept;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelInvoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelOrder;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchDispatch;
import com.wishbook.catalog.commonmodels.responses.ResponseRating;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder_new;
import com.wishbook.catalog.commonmodels.responses.Response_warehouse;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.contacts.details.ActionLogApi;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.adapters.InvoiceAdapter;
import com.wishbook.catalog.home.orderNew.adapters.RRCAdapter;
import com.wishbook.catalog.home.orderNew.adapters.expandable_adapter_new;
import com.wishbook.catalog.home.orders.details.Fragment_Dispatch;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;
import com.wishbook.catalog.home.rrc.Fragment_RRC_OrderItemSelection;
import com.wishbook.catalog.home.rrc.RRCHandler;
import com.wishbook.catalog.reseller.Fragment_ResellerHolder;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_OrderDetailsNew extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,RRCHandler.RRCHandlerListner {


    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.txt_order_no)
    TextView orderNo;
    @BindView(R.id.order_supplier_name)
    TextView orderSellerName;
    @BindView(R.id.txt_order_date)
    TextView orderDate;
    @BindView(R.id.txt_order_status)
    TextView orderStatus;
    @BindView(R.id.txt_payment_status)
    TextView paymentStatus;
    @BindView(R.id.order_amount)
    TextView orderAmount;
    @BindView(R.id.order_catalog_container)
    LinearLayout orderCatalogContainer;
    @BindView(R.id.invoice_catalog_container)
    LinearLayout invoiceCatalogContainer;
    @BindView(R.id.linear_pay_button)
    LinearLayout linear_pay_button;
    @BindView(R.id.btn_pay)
    AppCompatButton btnPay;


    @BindView(R.id.card_cancel_order)
    CardView cardButtonLayout;
    @BindView(R.id.btn_cancel_order)
    AppCompatButton btnCancel;
    @BindView(R.id.btn_dispatch)
    AppCompatButton btnDispatch;
    @BindView(R.id.btn_accept)
    AppCompatButton btnAccept;
    @BindView(R.id.btn_close)
    AppCompatButton btnClose;
    @BindView(R.id.btn_transfer)
    AppCompatButton btnTransfer;


   /* @BindView(R.id.relative_buyer_number)
    RelativeLayout relativeBuyerNumber;
    @BindView(R.id.txt_buyer_number)
    TextView txtBuyerNumber;*/

    @BindView(R.id.btn_buyer_call)
    AppCompatButton btn_buyer_call;

    @BindView(R.id.btn_call_wb_support)
    AppCompatButton btn_call_wb_support;
    @BindView(R.id.txt_supplier_text)
    TextView txtSuppierText;


    @BindView(R.id.txt_shipping_provider)
    TextView txt_shipping_provider;
    @BindView(R.id.txt_shipping_address)
    TextView txt_shipping_address;
    @BindView(R.id.shipping_details_card)
    LinearLayout shipping_details_card;


    // Invoice
    @BindView(R.id.invoice_recycler_view)
    RecyclerView recyclerInvoice;
    @BindView(R.id.card_invoice)
    CardView cardInvoice;
    @BindView(R.id.invoice_order_no)
    TextView invoiceOrderNo;
    @BindView(R.id.invoice_seller_name)
    TextView invoiceSellerName;
    @BindView(R.id.invoice_order_date)
    TextView invoiceOrdeDate;
    @BindView(R.id.txt_payable_amt)
    TextView invoiceTotalAmt;
    @BindView(R.id.relative_seller_discount)
    RelativeLayout relativeSellerDiscount;
    @BindView(R.id.txt_seller_discount)
    TextView txtSellerDiscount;
    @BindView(R.id.relative_tax_class_1)
    RelativeLayout relativeTaxClass1;
    @BindView(R.id.tax_class_1_name)
    TextView taxClass1Name;
    @BindView(R.id.tax_class_1_value)
    TextView txtClass1Value;
    @BindView(R.id.relative_tax_class_2)
    RelativeLayout relativeTaxClass2;
    @BindView(R.id.tax_class_2_name)
    TextView taxClass2Name;
    @BindView(R.id.tax_class_2_value)
    TextView txtClass2Value;

    // Attachment
    @BindView(R.id.attach_button)
    AppCompatButton attach_button;
    @BindView(R.id.attach_img_1)
    SimpleDraweeView attachImageView1;
    @BindView(R.id.attach_img_2)
    SimpleDraweeView attachImageView2;
    @BindView(R.id.attach_img_3)
    SimpleDraweeView attImageView3;
    @BindView(R.id.linear_attachment_container)
    LinearLayout linearAttachmentContainer;
    @BindView(R.id.attachment_container)
    LinearLayout attachment_container;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //Brokerage Place Order Button
    @BindView(R.id.linear_place_order)
    LinearLayout linear_place_order;
    @BindView(R.id.btn_place_order)
    AppCompatButton btn_place_order;

    @BindView(R.id.linear_broker_chat)
    LinearLayout linear_broker_chat;

    @BindView(R.id.btn_chat_buyer)
    AppCompatButton btn_chat_buyer;

    @BindView(R.id.btn_chat_supplier)
    AppCompatButton btn_chat_supplier;

    @BindView(R.id.card_order_summary)
    CardView card_order_summary;

    @BindView(R.id.card_attachment)
    CardView card_attachment;


    /**
     * Rating Control Intialize
     */

    @BindView(R.id.card_rating)
    CardView card_rating;
    @BindView(R.id.txt_edit_rating)
    TextView txt_edit_rating;
    @BindView(R.id.liner_question_selling)
    LinearLayout liner_question_selling;
    @BindView(R.id.liner_question_purchase)
    LinearLayout liner_question_purchase;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.btn_rating_done)
    TextView btn_rating_done;
    @BindView(R.id.btn_rating_cancel)
    TextView btn_rating_cancel;

    @BindView(R.id.other_purchase_container)
    LinearLayout other_purchase_container;
    @BindView(R.id.edit_other_purchase_reason)
    EditText edit_other_purchase_reason;
    @BindView(R.id.input_other_purchase_reason)
    TextInputLayout input_other_purchase_reason;
    @BindView(R.id.check_purchase_one)
    CheckBox check_purchase_one;
    @BindView(R.id.check_purchase_two)
    CheckBox check_purchase_two;
    @BindView(R.id.check_purchase_three)
    CheckBox check_purchase_three;
    @BindView(R.id.check_purchase_four)
    CheckBox check_purchase_four;
    @BindView(R.id.relative_payment_status)
    RelativeLayout relative_payment_status;


    @BindView(R.id.other_container)
    LinearLayout other_container;
    @BindView(R.id.edit_other_reason)
    EditText edit_other_reason;
    @BindView(R.id.input_other_reason)
    TextInputLayout input_other_reason;
    @BindView(R.id.check_selling_one)
    CheckBox check_selling_one;
    @BindView(R.id.check_selling_two)
    CheckBox check_selling_two;
    @BindView(R.id.check_selling_three)
    CheckBox check_selling_three;
    @BindView(R.id.check_selling_four)
    CheckBox check_selling_four;
    @BindView(R.id.txt_question_title)
    TextView txt_question_title;

    @BindView(R.id.relative_broker_name)
    RelativeLayout relative_broker_name;
    @BindView(R.id.relative_payment_detail)
    RelativeLayout relative_payment_detail;

    @BindView(R.id.offer_discount)
    TextView offer_discount;
    @BindView(R.id.card_order_discount)
    CardView card_order_discount;

    @BindView(R.id.edit_additional_discount)
    TextView edit_additional_discount;

    @BindView(R.id.seller_discount_label)
    TextView seller_discount_label;

    @BindView(R.id.linear_additional_discount)
    LinearLayout linear_additional_discount;

    @BindView(R.id.txt_add_discount)
    TextView txt_add_discount;

    @BindView(R.id.relative_seller_add_discount)
    RelativeLayout relative_seller_add_discount;

    @BindView(R.id.order_additional_discount)
    TextView order_additional_discount;

    @BindView(R.id.order_total_discount_label)
    TextView order_total_discount_label;

    @BindView(R.id.seller_discount)
    TextView seller_discount;

    @BindView(R.id.order_broker_name)
    TextView order_broker_name;

    @BindView(R.id.linear_burer_credit_rating)
    LinearLayout linear_burer_credit_rating;

    @BindView(R.id.img_good_credit)
    ImageView img_good_credit;

    @BindView(R.id.txt_credit_see_details)
    TextView txt_credit_see_details;

    @BindView(R.id.txt_unrated)
    TextView txt_unrated;


    @BindView(R.id.relative_payment_date)
    RelativeLayout relative_payment_date;

    @BindView(R.id.txt_payment_date)
    TextView txt_payment_date;


    @BindView(R.id.txt_payment_detail)
    TextView txt_payment_detail;

    @BindView(R.id.relative_additional_note)
    RelativeLayout relative_additional_note;

    @BindView(R.id.txt_note)
    TextView txt_note;

    @BindView(R.id.seller_discount_label1)
    TextView seller_discount_label1;

    @BindView(R.id.seller_notes_order)
    TextView seller_notes_order;

    @BindView(R.id.buyer_notes_order)
    TextView buyer_notes_order;

    @BindView(R.id.relative_shipping_provider)
    RelativeLayout relative_shipping_provider;

    @BindView(R.id.btn_chat_wb_support)
    AppCompatButton btn_chat_wb_support;


    @BindView(R.id.txt_reseller_note)
    TextView txt_reseller_note;

    @BindView(R.id.btn_reseller_hub)
    TextView btn_reseller_hub;

    @BindView(R.id.linear_reseller_order)
    LinearLayout linear_reseller_order;

    @BindView(R.id.txt_cod_verification_note_reseller)
    TextView txt_cod_verification_note_reseller;

    @BindView(R.id.btn_call_wb_support_reseller)
    AppCompatButton btn_call_wb_support_reseller;

    @BindView(R.id.btn_chat_wb_support_reseller)
    AppCompatButton btn_chat_wb_support_reseller;

    @BindView(R.id.linear_reseller_chat_call)
    LinearLayout linear_reseller_chat_call;

    @BindView(R.id.linear_dispatch_pending)
    LinearLayout linear_dispatch_pending;

    @BindView(R.id.linear_dispatch_row_container)
    LinearLayout linear_dispatch_row_container;

    @BindView(R.id.relative_expected_dispatch_date)
    CardView relative_expected_dispatch_date;

    @BindView(R.id.txt_order_expected_date)
    TextView txt_order_expected_date;

    @BindView(R.id.rrc_detail_card)
    CardView rrc_detail_card;

    @BindView(R.id.rrc_recycler_view)
    RecyclerView rrc_recycler_view;

    @BindView(R.id.linear_cancellation_item)
    LinearLayout linear_cancellation_item;

    @BindView(R.id.recycler_view_cancellation_item)
    RecyclerView recycler_view_cancellation_item;

    @BindView(R.id.btn_create_rrc)
    AppCompatButton btn_create_rrc;

    @BindView(R.id.card_btn_rrc)
    CardView card_btn_rrc;

   /* @BindView(R.id.txt_request_replacement)
    TextView txt_request_replacement;

    @BindView(R.id.txt_request_return)
    TextView txt_request_return;*/


    private Bitmap bitmap;
    private Context mContext;
    BuyersList buyer = null;

    //for Transfer Dialog
    TextView edit_buyername;
    TextInputLayout txt_input_buyer;
    private boolean isBroker;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    String additional_discount;
    boolean data_flag = false;

    String from = "";

    private static final int REQUESTWRITEPERMISSION = 556;
    private String temp_download_invoice_url, temp_download_invoice_id;
    int cod_free_amount_limit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.initializeAppsee();
        mContext = Activity_OrderDetailsNew.this;
        KeyboardUtils.hideKeyboard(Activity_OrderDetailsNew.this);
        setContentView(R.layout.fragment_order_reciept);
        ButterKnife.bind(this);
        cod_free_amount_limit = PrefDatabaseUtils.getPrefCodFreeAmountLimit(mContext);
        /**
         * WB-4420 Remove Order Summary Section
         * Changed by Bhavik Gandhi
         */
        card_order_summary.setVisibility(View.GONE);


        initSwipeRefresh();
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("isAfterPayment", false)) {
                isAllowCache = false;
            }

            if (getIntent().getStringExtra("from") != null) {
                from = getIntent().getStringExtra("from");
            }
        }

        orderfetch();


    }

    private void orderfetch() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Receipt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        if (Application_Singleton.selectedOrder != null) {
            if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
                isBroker = ((Response_sellingorder) Application_Singleton.selectedOrder).isBrokerage();
                callSalesOrderDetail();
            } else if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
                isBroker = ((Response_sellingorder_new) Application_Singleton.selectedOrder).isBrokerage();
                callSalesOrderDetail();
            } else {
                isBroker = ((Response_buyingorder) Application_Singleton.selectedOrder).isBrokerage();
                callPurchaseOrderDetail();
            }
        }
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recyclerInvoice.setLayoutManager(layoutManager1);
        recyclerInvoice.setHasFixedSize(false);
        recyclerInvoice.setNestedScrollingEnabled(false);


        rrc_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        rrc_recycler_view.setHasFixedSize(false);
        rrc_recycler_view.setNestedScrollingEnabled(false);


        recycler_view_cancellation_item.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        recycler_view_cancellation_item.setHasFixedSize(false);
        recycler_view_cancellation_item.setNestedScrollingEnabled(false);
    }

    private void OrderFocusSection() {
        if (getIntent().getStringExtra("focus_on") != null) {
            String focus_view = getIntent().getStringExtra("focus_on");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (focus_view.equalsIgnoreCase("rating")) {
                        nested_scroll_view.smoothScrollTo(0, (int) mRecyclerView.getY());
                    } else if (focus_view.equalsIgnoreCase("rrc-item")) {
                        nested_scroll_view.smoothScrollTo(0, (int) rrc_recycler_view.getY());
                    } else if (focus_view.equalsIgnoreCase("cancelable-item")) {
                        nested_scroll_view.smoothScrollTo(0, (int) recycler_view_cancellation_item.getY());
                    }
                }
            }, 600);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /// ############################## Purchase Order Call/ Bind UI ###################################//

    private void callPurchaseOrderDetail() {
        final Response_buyingorder selectedOrder1 = (Response_buyingorder) Application_Singleton.selectedOrder;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        String url = "";
        if (isBroker) {
            url = "purchase_brokerage_orders_catalogwise";
        } else {
            url = "purchaseorders_catalogwise";
        }
        sendScreenAnalytics(selectedOrder1.getId(), "buyer");
        StaticFunctions.showProgressbar(Activity_OrderDetailsNew.this);
        HttpManager.getInstance(Activity_OrderDetailsNew.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, url, selectedOrder1.getId()), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                Application_Singleton.selectedOrder = selectedOrder;
                bindPurchaseOrderPage(selectedOrder);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
            }
        });


    }

    private void bindPurchaseOrderPage(Response_buyingorder selectedOrder) {
        card_attachment.setVisibility(View.GONE);
        btn_create_rrc.setVisibility(View.GONE);
        card_btn_rrc.setVisibility(View.GONE);
       // txt_request_replacement.setVisibility(View.GONE);
       // txt_request_return.setVisibility(View.GONE);


        if (selectedOrder.getOrder_number() != null && !selectedOrder.getOrder_number().isEmpty())
            orderNo.setText(selectedOrder.getOrder_number());
        else
            orderNo.setText(selectedOrder.getId());
        orderSellerName.setText(StringUtils.capitalize(selectedOrder.getSeller_company_name().toLowerCase().trim()));

        if (!isBroker) {
            if (selectedOrder.getBroker_company() != null && !selectedOrder.getBroker_company().isEmpty()) {
                relative_broker_name.setVisibility(View.VISIBLE);
                if (selectedOrder.getBroker_company_name() != null)
                    order_broker_name.setText(StringUtils.capitalize(selectedOrder.getBroker_company_name().toLowerCase().trim()));
            } else {
                relative_broker_name.setVisibility(View.GONE);
            }
        }

        if (selectedOrder.getNote() != null && !selectedOrder.getNote().isEmpty()) {
            relative_additional_note.setVisibility(View.VISIBLE);
            txt_note.setText(selectedOrder.getNote().toString());
        } else {
            relative_additional_note.setVisibility(View.GONE);
        }


        card_order_discount.setVisibility(View.GONE);
        if (selectedOrder.getBroker_company() != null && selectedOrder.getBroker_company().equals(UserInfo.getInstance(mContext).getCompany_id())) {
            ((Response_buyingorder) Application_Singleton.selectedOrder).setBrokerage(true);
            selectedOrder.setBrokerage(true);
        }


        if (selectedOrder.getPayment_details() != null && !selectedOrder.getPayment_details().isEmpty()) {
            if (selectedOrder.isReseller_order() && selectedOrder.getPayment_details().contains("COD")) {
                relative_payment_detail.setVisibility(View.VISIBLE);
                txt_payment_detail.setText("Resell Amount: " + "\u20B9" + selectedOrder.getDisplay_amount());
            } else {
                if(selectedOrder.getPayment_details().contains("Partially Paid")) {
                    String temp = selectedOrder.getPayment_details().replace("Partially Paid","COD");
                    txt_payment_detail.setText(temp.trim());
                }else {
                    txt_payment_detail.setText(selectedOrder.getPayment_details().trim());
                }
                relative_payment_detail.setVisibility(View.VISIBLE);

            }

        } else {
            relative_payment_detail.setVisibility(View.GONE);
        }
       /* if (selectedOrder.getPayment_date() != null && !selectedOrder.getPayment_date().isEmpty()) {
            relative_payment_date.setVisibility(View.VISIBLE);
            txt_payment_date.setText(DateUtils.getFormattedDate(selectedOrder.getPayment_date().toString().trim()));
        } else {
            relative_payment_date.setVisibility(View.GONE);
        }*/
        relative_payment_date.setVisibility(View.GONE);


        String temp_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, selectedOrder.getCreated_at());
        orderDate.setText(temp_date);
        orderAmount.setText("\u20B9" + selectedOrder.getTotal_rate());


        if (selectedOrder.getProcessing_status().toLowerCase().trim().equalsIgnoreCase("pending")) {
            orderStatus.setText(Constants.PENDING_ORDER_DISPLAY_TEXT);
        } else {
            orderStatus.setText(StringUtils.capitalize(selectedOrder.getProcessing_status().toLowerCase().trim()));
        }

        setColor(selectedOrder.getProcessing_status().toString(), orderStatus);
        //paymentStatus.setText(StringUtils.capitalize(selectedOrder.getPayment_status().toLowerCase().trim()));
        if(selectedOrder.getPayment_status().toLowerCase().contains("partially paid")){
            paymentStatus.setText("COD");
        } else {
            paymentStatus.setText(StringUtils.capitalize(selectedOrder.getPayment_status().toLowerCase().trim()));
        }
        setColor(selectedOrder.getPayment_status().toString(), paymentStatus);
        btn_buyer_call.setVisibility(View.GONE);
        btn_call_wb_support.setVisibility(View.VISIBLE);
        btn_chat_wb_support.setVisibility(View.VISIBLE);
        btn_call_wb_support_reseller.setVisibility(View.VISIBLE);
        btn_chat_wb_support_reseller.setVisibility(View.VISIBLE);

        if (selectedOrder.isReseller_order()) {
            linear_reseller_order.setVisibility(View.VISIBLE);
            txt_reseller_note.setVisibility(View.VISIBLE);
            if (selectedOrder.getProcessing_status().contains("COD Verification Pending")) {
                linear_reseller_chat_call.setVisibility(View.VISIBLE);
                txt_cod_verification_note_reseller.setVisibility(View.VISIBLE);
            } else {
                linear_reseller_chat_call.setVisibility(View.GONE);
                txt_cod_verification_note_reseller.setVisibility(View.GONE);
            }
        } else {
            linear_reseller_order.setVisibility(View.GONE);
            txt_reseller_note.setVisibility(View.GONE);
        }

        btn_reseller_hub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment_ResellerHolder fragment_resellerHolder = new Fragment_ResellerHolder();
                bundle.putString("from", Activity_OrderDetailsNew.class.getSimpleName());
                fragment_resellerHolder.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Reseller Hub";
                Application_Singleton.CONTAINERFRAG = fragment_resellerHolder;
                StaticFunctions.switchActivity((Activity) mContext, OpenContainer.class);
            }
        });

        if (selectedOrder.getOrder_expected_date() != null && !selectedOrder.getOrder_expected_date().equalsIgnoreCase("null")) {
            relative_expected_dispatch_date.setVisibility(View.VISIBLE);
            String t1 = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT1, selectedOrder.getOrder_expected_date());
            txt_order_expected_date.setText(t1);
        } else {
            relative_expected_dispatch_date.setVisibility(View.GONE);
        }

        btn_call_wb_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("callwbsupport", "call", "orderDetail");
                new ChatCallUtils(mContext, ChatCallUtils.CHAT_CALL_TYPE, null);
            }
        });

        btn_chat_wb_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("chatwbsupport", "chat", "orderDetail");
                String msg = "Buyer Asking Details:\n Buyer Ref No #" + selectedOrder.getCompany() + "\nSupplier Ref No #" + selectedOrder.getSeller_company() + "\nOrder Ref No #" + selectedOrder.getId();
                new ChatCallUtils(mContext, ChatCallUtils.WB_CHAT_TYPE, msg);
            }
        });

        btn_call_wb_support_reseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("callwbsupport", "call", "orderDetail");
                new ChatCallUtils(mContext, ChatCallUtils.CHAT_CALL_TYPE, null);
            }
        });

        btn_chat_wb_support_reseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("chatwbsupport", "chat", "orderDetail");
                String msg = "COD Verification Request:\n Buyer Ref No #" + selectedOrder.getCompany() + "\nSupplier Ref No #" + selectedOrder.getSeller_company() + "\nOrder Ref No #" + selectedOrder.getId();
                new ChatCallUtils(mContext, ChatCallUtils.WB_CHAT_TYPE, msg);
            }
        });


        try {
            if (selectedOrder.getOrder_type().equals(Constants.ORDER_TYPE_PREPAID) && selectedOrder.getProcessing_status().equals("Pending") && selectedOrder.getPayment_status().equals("Pending")) {
                buyer_notes_order.setVisibility(View.VISIBLE);
            }

            if (selectedOrder.getPayment_status().equalsIgnoreCase("Partially Paid")
                    && selectedOrder.getPayment_details().toLowerCase().contains("cod")
                    && selectedOrder.getOrder_type().equals(Constants.ORDER_TYPE_PREPAID)) {
                buyer_notes_order.setVisibility(View.VISIBLE);
                buyer_notes_order.setTextColor(getResources().getColor(R.color.red));
                if (selectedOrder.isReseller_order()) {
                    if (selectedOrder.getInvoice().size() > 0 && selectedOrder.getInvoice().get(0).getTotal_amount() != null && Double.parseDouble(selectedOrder.getInvoice().get(0).getTotal_amount()) > cod_free_amount_limit) {
                        buyer_notes_order.setText(getResources().getString(R.string.buyer_cod_note_resaler_order));
                    }

                    /**
                     * WB-4381 not show note for COD 0 Amount
                     */
                    if (selectedOrder.getInvoice() != null
                            && selectedOrder.getInvoice().size() > 0
                            && Double.parseDouble(selectedOrder.getInvoice().get(0).getPaid_amount()) < 1
                            && selectedOrder.getPayment_status().equalsIgnoreCase("Partially Paid")) {
                        buyer_notes_order.setVisibility(View.GONE);
                    }
                } else {
                    if (selectedOrder.getInvoice().size() > 0 && selectedOrder.getInvoice().get(0).getTotal_amount() != null && Double.parseDouble(selectedOrder.getInvoice().get(0).getTotal_amount()) > cod_free_amount_limit) {
                        buyer_notes_order.setVisibility(View.VISIBLE);
                        buyer_notes_order.setText(getResources().getString(R.string.buyer_cod_note_order));
                    } else {
                        buyer_notes_order.setVisibility(View.GONE);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (selectedOrder.getOrder_type().equals(Constants.ORDER_TYPE_CREDIT)) {
            buyer_notes_order.setTextColor(getResources().getColor(R.color.red));
            buyer_notes_order.setText(Application_Singleton.getResourceString(R.string.buyer_credit_note_order));
            buyer_notes_order.setVisibility(View.VISIBLE);
        }


        if (isBroker) {

                   /* if (selectedOrder.getBuyer_credit_rating() != null && !selectedOrder.getBuyer_credit_rating().isEmpty()) {
                        linear_burer_credit_rating.setVisibility(View.VISIBLE);
                        if (selectedOrder.getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_GOOD)) {
                            img_good_credit.setVisibility(View.VISIBLE);
                            txt_credit_see_details.setVisibility(View.VISIBLE);
                            txt_unrated.setVisibility(View.GONE);
                        } else if (selectedOrder.getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_UNRATED)) {
                            img_good_credit.setVisibility(View.GONE);
                            txt_credit_see_details.setVisibility(View.GONE);
                            txt_unrated.setVisibility(View.VISIBLE);
                        }

                        linear_burer_credit_rating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (img_good_credit.getVisibility() == View.VISIBLE) {
                                    openCreditBottom(mContext, selectedOrder.getCompany(), selectedOrder.getCompany_name());
                                }

                            }
                        });
                    } else {
                        linear_burer_credit_rating.setVisibility(View.GONE);
                    }*/
        } else {
            // linear_burer_credit_rating.setVisibility(View.GONE);
        }

        getBuyerDiscount(selectedOrder.getCurrent_discount(), selectedOrder.getSeller_company(), selectedOrder.getSeller_extra_discount_percentage(), true,
                selectedOrder.getOrder_type(), selectedOrder.getId(), selectedOrder.getTotal_rate(), selectedOrder.getProcessing_status(), selectedOrder.getPayment_status());
        if (selectedOrder.getProcessing_status().equals("Delivered")) {
            /**
             * Remove order rating because of new introduce the product rating
             * Changes done by Bhavik Gandhi May-1 (WB-4792)
             */
            //getOrderRating(selectedOrder.getId(), false);
        }
        ArrayList<Invoice> invoices = new ArrayList<>();

        if (!selectedOrder.getProcessing_status().equals("Draft")
                && !selectedOrder.getProcessing_status().equals("Closed")
                && !selectedOrder.getProcessing_status().equals("Cart")) {
            if (selectedOrder.getInvoice() != null && selectedOrder.getInvoice().size() > 0) {
                invoices = selectedOrder.getInvoice();
                InvoiceAdapter invoiceAdapter = new InvoiceAdapter(Activity_OrderDetailsNew.this,
                        invoices, selectedOrder.getId(),
                        selectedOrder.getOrder_number(),
                        selectedOrder.getOrder_type(),
                        selectedOrder.getSeller_company_name(),
                        selectedOrder.getCreated_at(),
                        true,
                        selectedOrder.getCatalogs(), isBroker, selectedOrder.getProcessing_status());
                recyclerInvoice.setVisibility(View.VISIBLE);
                recyclerInvoice.setAdapter(invoiceAdapter);
            } else {
                recyclerInvoice.setVisibility(View.GONE);
                linear_pay_button.setVisibility(View.VISIBLE);
                cardButtonLayout.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        } else {

        }

        if (selectedOrder.getPreffered_shipping_provider().equals("WB Provided") && selectedOrder.getShip_to() != null) {
            shipping_details_card.setVisibility(View.VISIBLE);
            txt_shipping_provider.setText("Wishbook provided");
            if (selectedOrder.getShip_to() != null) {
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }

                    }
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    shipping_details_card.setVisibility(View.GONE);
                }

            }
        } else if (!selectedOrder.getPreffered_shipping_provider().equals("WB Provided")) {
            if (selectedOrder.getBuyer_preferred_logistics() != null && !selectedOrder.getBuyer_preferred_logistics().isEmpty()) {
                relative_shipping_provider.setVisibility(View.VISIBLE);
                txt_shipping_provider.setText(selectedOrder.getBuyer_preferred_logistics());
            } else {
                relative_shipping_provider.setVisibility(View.GONE);
            }

            shipping_details_card.setVisibility(View.VISIBLE);
            if (selectedOrder.getShip_to() != null) {
                // if(selectedOrder.getShip_to() instanceof  Response_buyingorder.Ship_to){
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }
                    }
                    //ShippingAddressResponse ship = (ShippingAddressResponse) selectedOrder.getShip_to();
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    shipping_details_card.setVisibility(View.GONE);
                }

                // }

            }
        } else {
            Log.e("TAG", "onServerResponse: 3");
            shipping_details_card.setVisibility(View.GONE);
        }

               /* if (selectedOrder.getPayment_status().equals("Paid")
                        || selectedOrder.getPayment_status().equals("Partially Paid")
                        || selectedOrder.getProcessing_status().equals("Dispatched")
                        || selectedOrder.getProcessing_status().equals("Closed")
                        || selectedOrder.getProcessing_status().equals("Delivered")
                        || selectedOrder.getProcessing_status().equals("In Progress")) {
                    if (selectedOrder.getInvoice() != null && selectedOrder.getInvoice().size() > 0) {
                        invoices = selectedOrder.getInvoice();
                        InvoiceAdapter invoiceAdapter = new InvoiceAdapter(Activity_OrderDetailsNew.this, invoices, selectedOrder.getId(), selectedOrder.getOrder_number(), selectedOrder.getSeller_company_name(), selectedOrder.getDate(), true, selectedOrder.getCatalogs(), isBroker);
                        recyclerInvoice.setAdapter(invoiceAdapter);
                    } else {
                        linear_pay_button.setVisibility(View.VISIBLE);
                        cardButtonLayout.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (selectedOrder.getPreffered_shipping_provider().equals("WB Provided") && selectedOrder.getShip_to() != null) {
                        shipping_details_card.setVisibility(View.VISIBLE);
                        txt_shipping_provider.setText("Wishbook provided");
                        if (selectedOrder.getShip_to() != null) {
                            // if(selectedOrder.getShip_to() instanceof  Response_buyingorder.Ship_to){
                            try {

                                //selectedOrder.getShip_to() instanceof String


                                ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                                String pincode = ship.getPincode();
                                if(ship.getPincode()!=null){
                                    try{
                                        pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                                    } catch (Exception e){
                                        pincode = ship.getPincode();
                                    }

                                }
                                //ShippingAddressResponse ship = (ShippingAddressResponse) selectedOrder.getShip_to();
                                String address = ship.getStreet_address() + ", " +
                                        ship.getState().getState_name() + ", " +
                                        ship.getCity().getCity_name() + " - " +
                                        pincode;
                                txt_shipping_address.setText(address);
                            } catch (Exception e) {
                                shipping_details_card.setVisibility(View.GONE);
                            }

                            // }

                        }
                    } else if (!selectedOrder.getPreffered_shipping_provider().equals("WB Provided") && selectedOrder.getBuyer_preferred_logistics() != null) {
                        txt_shipping_provider.setText(selectedOrder.getBuyer_preferred_logistics());
                        shipping_details_card.setVisibility(View.VISIBLE);
                        if (selectedOrder.getShip_to() != null) {
                            // if(selectedOrder.getShip_to() instanceof  Response_buyingorder.Ship_to){
                            try {
                                ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                                String pincode = ship.getPincode();
                                if(ship.getPincode()!=null){
                                    try{
                                        pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                                    } catch (Exception e){
                                        pincode = ship.getPincode();
                                    }
                                }
                                //ShippingAddressResponse ship = (ShippingAddressResponse) selectedOrder.getShip_to();
                                String address = ship.getStreet_address() + ", " +
                                        ship.getState().getState_name() + ", " +
                                        ship.getCity().getCity_name() + " - " +
                                        pincode;
                                txt_shipping_address.setText(address);
                            } catch (Exception e) {
                                shipping_details_card.setVisibility(View.GONE);
                            }

                            // }

                        }
                    } else {
                        Log.e("TAG", "onServerResponse: 3");
                        shipping_details_card.setVisibility(View.GONE);
                    }
                }*/


        if (!isBroker) {
            if (selectedOrder.getPayment_status().equals("Paid")
                    || selectedOrder.getPayment_status().equals("Partially Paid")
                    || selectedOrder.getProcessing_status().equals("Dispatched")
                    || selectedOrder.getProcessing_status().equals("Closed")
                    || selectedOrder.getProcessing_status().equals("Delivered")
                    || selectedOrder.getProcessing_status().equals("In Progress")) {
                if (selectedOrder.getInvoice() != null && selectedOrder.getInvoice().size() > 0) {


                    btnAccept.setVisibility(View.GONE);
                    btnTransfer.setVisibility(View.GONE);
                    btnDispatch.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnPay.setVisibility(View.GONE);


                } else {

                    if (selectedOrder.getProcessing_status().equals("Cancelled")
                            || selectedOrder.getProcessing_status().equals("Transferred")
                            || selectedOrder.getPayment_status().equals("Paid")) {
                        linear_pay_button.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        cardButtonLayout.setVisibility(View.VISIBLE);
                    }
                }


            } else {
                if (selectedOrder.getProcessing_status().equals("Draft")
                        || selectedOrder.getProcessing_status().equals("Accepted")) {

                    if (selectedOrder.getPayment_status().equals("Paid")
                            || selectedOrder.getPayment_status().equals("Payment Confirmed")) {
                        linear_pay_button.setVisibility(View.GONE);
                        /**
                         * WB-2751
                         */
                        if (selectedOrder.getPayment_status().contains("Paid")) {
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            if (selectedOrder.getInvoice().size() > 0) {
                                btnCancel.setVisibility(View.GONE);
                            } else {
                                btnCancel.setVisibility(View.VISIBLE);
                            }

                        }

                        cardButtonLayout.setVisibility(View.VISIBLE);

                    } else {
                        linear_pay_button.setVisibility(View.VISIBLE);
                        /**
                         * WB-2751
                         */
                        if (selectedOrder.getPayment_status().contains("Paid")) {
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            if (selectedOrder.getInvoice().size() > 0) {
                                btnCancel.setVisibility(View.GONE);
                            } else {
                                btnCancel.setVisibility(View.VISIBLE);
                            }
                        }
                        cardButtonLayout.setVisibility(View.VISIBLE);
                    }
                }

                if (selectedOrder.getProcessing_status().equals("Pending")) {

                    linear_pay_button.setVisibility(View.GONE);
                    /**
                     * WB-2751
                     */
                    if (selectedOrder.getPayment_status().contains("Paid")) {
                        btnCancel.setVisibility(View.GONE);
                    } else {
                        if (selectedOrder.getInvoice() != null && selectedOrder.getInvoice().size() > 0) {
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            btnCancel.setVisibility(View.VISIBLE);
                        }
                    }
                    cardButtonLayout.setVisibility(View.VISIBLE);

                }

                if (selectedOrder.getProcessing_status().equals("In Progress")) {
                    cardButtonLayout.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    linear_pay_button.setVisibility(View.GONE);
                }

                if (selectedOrder.getProcessing_status().equals("Dispatched")
                        || selectedOrder.getProcessing_status().equals("Delivered")
                        ) {
                    cardButtonLayout.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    if (selectedOrder.getPayment_status().equals("Paid")) {
                        linear_pay_button.setVisibility(View.GONE);
                    } else {
                        linear_pay_button.setVisibility(View.VISIBLE);
                    }

                }


                if (selectedOrder.getProcessing_status().equals("Cancelled")
                        || selectedOrder.getProcessing_status().equals("Transferred")) {
                    linear_pay_button.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    cardButtonLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // for broker order
            if (selectedOrder.getProcessing_status().equals("Draft")) {
                linear_place_order.setVisibility(View.VISIBLE);
                btn_place_order.setVisibility(View.VISIBLE);
            } else {
                linear_broker_chat.setVisibility(View.VISIBLE);
                btn_chat_buyer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatWithBuyer(selectedOrder.getBuyer_chat_user(), selectedOrder.getBuyer_chat_user());
                    }
                });

                btn_chat_supplier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatWithSupplier(selectedOrder.getSeller_chat_user(), selectedOrder.getSeller_chat_user());
                    }
                });
            }
        }


        if (selectedOrder.getSales_image() != null) {
            attachImageView2.setVisibility(View.VISIBLE);
            attachImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedOrder.getSales_image() != null) {
                        showimage(selectedOrder.getSales_image());
                    }
                }
            });
            // StaticFunctions.loadImage(Activity_OrderDetailsNew.this, selectedOrder.getSales_image(), attachImageView2, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, selectedOrder.getSales_image(), attachImageView2);
        } else {
            attachImageView2.setVisibility(View.GONE);
        }

        if (selectedOrder.getPurchase_image() != null) {
            attachImageView1.setVisibility(View.VISIBLE);
            attachImageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedOrder.getPurchase_image() != null) {
                        showimage(selectedOrder.getPurchase_image());
                    }
                }
            });
            //StaticFunctions.loadImage(Activity_OrderDetailsNew.this, selectedOrder.getPurchase_image(), attachImageView1, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, selectedOrder.getPurchase_image(), attachImageView1);
        } else {
            attachImageView1.setVisibility(View.GONE);
        }

        addCatalogList(selectedOrder.getCatalogs(), orderCatalogContainer);

        pendingDispatchList(selectedOrder.getCatalogs(), linear_dispatch_row_container);

        //configuring Shipment In sales order


        addCatalogList(selectedOrder.getCatalogs(), invoiceCatalogContainer);


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Application_Singleton.trackEvent("BtnPay", "Click", "BtnPay");

                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
                OrderInvoiceCreate orderInvoiceCreate = new OrderInvoiceCreate();
                ArrayList<InvoiceItemSet> invoiceItemSets = new ArrayList<InvoiceItemSet>();
                for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
                    for (int j = 0; j < selectedOrder.getCatalogs().get(i).getProducts().size(); j++) {
                        invoiceItemSets.add(
                                new InvoiceItemSet(selectedOrder.getCatalogs().get(i).getProducts().get(j).getId(),
                                        Integer.parseInt(selectedOrder.getCatalogs().get(i).getProducts().get(j).getQuantity())));
                    }
                }
                orderInvoiceCreate.setInvoiceitem_set(invoiceItemSets);
                orderInvoiceCreate.setOrder(selectedOrder.getId());
                Gson gson1 = new Gson();
                HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, "create_invoice", ""), (gson1.fromJson(gson1.toJson(orderInvoiceCreate), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Invoice invoice = Application_Singleton.gson.fromJson(response, Invoice.class);
                            Application_Singleton.purchaseInvoice = invoice;
                            if (invoice != null) {
                                Intent intent = new Intent(mContext, Activity_PaymentOrder.class);
                                startActivityForResult(intent, 1000);
                                finish();
                            }
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder("purchaseorder", selectedOrder.getId());
            }
        });

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrderPending();
            }
        });
        attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                };
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, permissions, 900);
                } else {
                    openGallery();
                }
            }
        });

        List<CatalogListItem> catalogListItem = new ArrayList<>();
        for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
            catalogListItem.add(new CatalogListItem(selectedOrder.getCatalogs().get(i).getName().toString(), selectedOrder.getCatalogs().get(i).getBrand().toString(), selectedOrder.getCatalogs().get(i).getTotal_products().toString(), selectedOrder.getCatalogs().get(i).getProducts()));
        }

        expandable_adapter_new adapter = new expandable_adapter_new(Activity_OrderDetailsNew.this, catalogListItem,
                true, selectedOrder.getInvoice(),
                false, selectedOrder.getProcessing_status());
        mRecyclerView.setAdapter(adapter);
        adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "onListItemExpanded: ");
                        nested_scroll_view.smoothScrollBy(0, 500);
                    }
                }, 200);

            }

            @Override
            public void onListItemCollapsed(int position) {
            }
        });
        buttonCardVisiblityCheck();


        if (selectedOrder.getRrces() != null && selectedOrder.getRrces().size() > 0) {
            rrc_detail_card.setVisibility(View.VISIBLE);
            RRCAdapter rrcAdapter = new RRCAdapter(Activity_OrderDetailsNew.this, selectedOrder.getRrces());
            rrc_recycler_view.setAdapter(rrcAdapter);
        } else {
            rrc_detail_card.setVisibility(View.GONE);
        }

        bindCancellationRecyclerView(catalogListItem, selectedOrder);

        OrderFocusSection();


       /* if(selectedOrder.getProcessing_status().equalsIgnoreCase("Delivered")
                && selectedOrder.getDelivery_date()!=null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(StaticFunctions.SERVER_POST_FORMAT);
            try {
                Date today_date = new Date();
                today_date = simpleDateFormat.parse(simpleDateFormat.format(today_date));
                Date deliver_date = DateUtils.stringToDate(selectedOrder.getDelivery_date(),simpleDateFormat);
                int diffInDays = (int) ((today_date.getTime() - deliver_date.getTime()) / (1000 * 60 * 60 * 24));

                if(diffInDays>=0 && diffInDays < 5) {
                    callGetRRCOrdersList(selectedOrder.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }


    // ############################ Sales Order Call/Bind UI ######################################### //

    private void callSalesOrderDetail() {
        linear_pay_button.setVisibility(View.GONE);
        String id = "";
        if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
            final Response_sellingorder selectedOrder1 = (Response_sellingorder) Application_Singleton.selectedOrder;
            id = selectedOrder1.getId();
        }

        if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
            final Response_sellingorder_new selectedOrder1 = (Response_sellingorder_new) Application_Singleton.selectedOrder;
            id = selectedOrder1.getId();
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        String url = "";
        if (isBroker) {
            url = "purchase_brokerage_orders_catalogwise";
        } else {
            url = "salesorders_catalogwise";
        }
        sendScreenAnalytics(id, "seller");
        StaticFunctions.showProgressbar(Activity_OrderDetailsNew.this);
        HttpManager.getInstance(Activity_OrderDetailsNew.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, url, id), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Gson gson = new Gson();
                final Response_sellingorder_new selectedOrder = gson.fromJson(response, Response_sellingorder_new.class);
                Application_Singleton.selectedOrder = selectedOrder;
                bindSalesOrderPage(selectedOrder);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
            }
        });

    }

    public void bindSalesOrderPage(Response_sellingorder_new selectedOrder) {
        btn_create_rrc.setVisibility(View.GONE);
        card_btn_rrc.setVisibility(View.GONE);
       // txt_request_replacement.setVisibility(View.GONE);
       // txt_request_return.setVisibility(View.GONE);
        if (selectedOrder.getProcessing_status().equals("Dispatched")) {
            //getOrderRating(selectedOrder.getId(), true);
        }
        if (selectedOrder.getOrder_number() != null && !selectedOrder.getOrder_number().isEmpty())
            orderNo.setText(selectedOrder.getOrder_number());
        else
            orderNo.setText(selectedOrder.getId());
        if (selectedOrder.getBuying_company_name() != null) {
            txtSuppierText.setText("Buyer Name");
            orderSellerName.setText(StringUtils.capitalize(selectedOrder.getBuying_company_name().toLowerCase().trim()));
        }

        if (selectedOrder.getPayment_details() != null && !selectedOrder.getPayment_details().isEmpty()) {
            relative_payment_detail.setVisibility(View.VISIBLE);
            txt_payment_detail.setText(selectedOrder.getPayment_details());

            if (!isBroker) {
                if (selectedOrder.getBroker_company() != null && !selectedOrder.getBroker_company().isEmpty()) {
                    relative_broker_name.setVisibility(View.VISIBLE);
                    if (selectedOrder.getBroker_company_name() != null)
                        order_broker_name.setText(StringUtils.capitalize(selectedOrder.getBroker_company_name().toLowerCase().trim()));
                } else {
                    relative_broker_name.setVisibility(View.GONE);
                }
            }

            if (selectedOrder.getNote() != null && !selectedOrder.getNote().isEmpty()) {
                relative_additional_note.setVisibility(View.VISIBLE);
                txt_note.setText(selectedOrder.getNote().toString());
            } else {
                relative_additional_note.setVisibility(View.GONE);
            }
        } else {
            relative_payment_detail.setVisibility(View.GONE);
        }
        if (selectedOrder.getPayment_date() != null && !selectedOrder.getPayment_date().isEmpty()) {
            relative_payment_date.setVisibility(View.VISIBLE);
            txt_payment_date.setText(DateUtils.getFormattedDate(selectedOrder.getPayment_date().toString().trim()));
        } else {
            relative_payment_date.setVisibility(View.GONE);
        }
        paymentStatus.setText(StringUtils.capitalize(selectedOrder.getPayment_status().toLowerCase().trim()));
        String temp_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, selectedOrder.getCreated_at());
        orderDate.setText(temp_date);
        orderAmount.setText("\u20B9" + selectedOrder.getTotal_rate());
        if (selectedOrder.getProcessing_status().toLowerCase().trim().equalsIgnoreCase("pending")) {
            orderStatus.setText(Constants.PENDING_ORDER_DISPLAY_TEXT);
        } else {
            orderStatus.setText(StringUtils.capitalize(selectedOrder.getProcessing_status().toLowerCase().trim()));
        }


        setColor(selectedOrder.getProcessing_status(), orderStatus);
        setColor(selectedOrder.getPayment_status(), paymentStatus);

        // Hide Payment Details for Wishbook As Seller
        if (selectedOrder.getCompany() != null && selectedOrder.getCompany().equalsIgnoreCase("10")) {
            relative_payment_status.setVisibility(View.GONE);
            relative_payment_date.setVisibility(View.GONE);
            relative_payment_detail.setVisibility(View.GONE);
        } else {

        }
        btn_buyer_call.setVisibility(View.GONE);
        btn_call_wb_support.setVisibility(View.VISIBLE);
        btn_chat_wb_support.setVisibility(View.VISIBLE);
                /*if (selectedOrder.getBuyer_credit_rating() != null && !selectedOrder.getBuyer_credit_rating().isEmpty()) {
                    linear_burer_credit_rating.setVisibility(View.VISIBLE);
                    if (selectedOrder.getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_GOOD)) {
                        img_good_credit.setVisibility(View.VISIBLE);
                        txt_credit_see_details.setVisibility(View.VISIBLE);
                        txt_unrated.setVisibility(View.GONE);
                    } else if (selectedOrder.getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_UNRATED)) {
                        img_good_credit.setVisibility(View.GONE);
                        txt_credit_see_details.setVisibility(View.GONE);
                        txt_unrated.setVisibility(View.VISIBLE);
                    }


                    linear_burer_credit_rating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (img_good_credit.getVisibility() == View.VISIBLE) {
                                openCreditBottom(mContext, selectedOrder.getCompany(), selectedOrder.getCompany_name());
                            }
                        }
                    });
                } else {
                    linear_burer_credit_rating.setVisibility(View.GONE);
                }*/
        if (selectedOrder.getCompany_number() != null) {


                  /*  txtBuyerNumber.setText(selectedOrder.getCompany_number());
                    txtBuyerNumber.setPaintFlags(txtBuyerNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
            btn_buyer_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedOrder.getBuyer_table_id() != null) {
                        new ActionLogApi(Activity_OrderDetailsNew.this, ActionLogApi.RELATION_TYPE_BUYER, ActionLogApi.ACTION_TYPE_CALL, selectedOrder.getBuyer_table_id());
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + selectedOrder.getCompany_number()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            btn_call_wb_support.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Application_Singleton.trackEvent("callwbsupport", "call", "orderDetail");
                    new ChatCallUtils(mContext, ChatCallUtils.CHAT_CALL_TYPE, null);
                }
            });


            btn_chat_wb_support.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Application_Singleton.trackEvent("chatwbsupport", "chat", "orderDetail");
                    String msg = "Seller Asking Details: \nBuyer Ref No #" + selectedOrder.getCompany() + "\nSupplier Ref No #" + selectedOrder.getSeller_company() + "\nOrder Ref No #" + selectedOrder.getId();
                    new ChatCallUtils(mContext, ChatCallUtils.WB_CHAT_TYPE, msg);
                }
            });

        }


        if (selectedOrder.getSales_image() != null) {
            attachImageView2.setVisibility(View.VISIBLE);
            attachImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedOrder.getSales_image() != null) {
                        showimage(selectedOrder.getSales_image());
                    }
                }
            });
            // StaticFunctions.loadImage(Activity_OrderDetailsNew.this, selectedOrder.getSales_image(), attachImageView2, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, selectedOrder.getSales_image(), attachImageView2);
        } else {
            attachImageView2.setVisibility(View.GONE);
        }

        if (selectedOrder.getPurchase_image() != null) {
            attachImageView1.setVisibility(View.VISIBLE);
            attachImageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedOrder.getPurchase_image() != null) {
                        showimage(selectedOrder.getPurchase_image());
                    }
                }
            });
            //StaticFunctions.loadImage(Activity_OrderDetailsNew.this, selectedOrder.getPurchase_image(), attachImageView1, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, selectedOrder.getPurchase_image(), attachImageView1);
        } else {
            attachImageView1.setVisibility(View.GONE);
        }
        addCatalogList(selectedOrder.getCatalogs(), orderCatalogContainer);
        pendingDispatchList(selectedOrder.getCatalogs(), linear_dispatch_row_container);

        // show hide button
             /*   if (selectedOrder.getProcessing_status().equals("Pending")) {
                    cardButtonLayout.setVisibility(View.VISIBLE);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnDispatch.setVisibility(View.GONE);
                } else if (selectedOrder.getProcessing_status().equals("Accepted")) {
                    cardButtonLayout.setVisibility(View.VISIBLE);
                    btnDispatch.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnAccept.setVisibility(View.GONE);
                } else {
                    cardButtonLayout.setVisibility(View.GONE);
                    btnDispatch.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    cardButtonLayout.setVisibility(View.GONE);
                }*/

        if (!isBroker) {

            // Hide Additional Seller Discount (6-Nov-2018)

            card_order_discount.setVisibility(View.GONE);
                   /* if (!selectedOrder.getProcessing_status().equals("Cancelled") &&
                            !selectedOrder.getProcessing_status().equals("Transferred") &&
                            !selectedOrder.getProcessing_status().equals("In Progress") &&
                            !selectedOrder.getProcessing_status().equals("Closed") &&
                            !selectedOrder.getProcessing_status().equals("Dispatched") &&
                            !selectedOrder.getProcessing_status().equals("Delivered")
                            && !selectedOrder.getPayment_status().contains("Paid")) {
                        // Show Hide Additional Discount
                        card_order_discount.setVisibility(View.VISIBLE);


                    } else {
                        card_order_discount.setVisibility(View.GONE);
                        seller_notes_order.setVisibility(View.GONE);
                    }


                    ;*/

            getBuyerDiscount(selectedOrder.getCurrent_discount(), selectedOrder.getCompany(), selectedOrder.getSeller_extra_discount_percentage(),
                    false, selectedOrder.getOrder_type(), selectedOrder.getId(), selectedOrder.getTotal_rate(),
                    selectedOrder.getProcessing_status(), selectedOrder.getPayment_status());

            if (UserInfo.getInstance(Activity_OrderDetailsNew.this).getGroupstatus().equals("1")) {

                if (selectedOrder.getProcessing_status().equals("Pending")) {
                    cardButtonLayout.setVisibility(View.VISIBLE);
                    /**
                     *   https://wishbook.atlassian.net/browse/WB-2580
                     *   hide cancel button for sales order
                     */

                    btnCancel.setVisibility(View.GONE);
                }
                if (selectedOrder.getProcessing_status().equals("Accepted")) {
                    btnAccept.setVisibility(View.GONE);
                    if (selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() < 1) {
                        cardButtonLayout.setVisibility(View.VISIBLE);
                        /**
                         *   https://wishbook.atlassian.net/browse/WB-2580
                         *   hide cancel button for sales order
                         */
                        btnDispatch.setVisibility(View.GONE);

                    }
                } else {
                    btnDispatch.setVisibility(View.GONE);
                }
                if (selectedOrder.getCustomer_status().equals("Paid")) {
                    // btn_verify.setVisibility(View.VISIBLE);
                }
                if ((selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() < 1) && !(selectedOrder.getCustomer_status().equals("Cancelled") || selectedOrder.getProcessing_status().equals("Cancelled") || selectedOrder.getProcessing_status().equals("Delivered") || selectedOrder.getProcessing_status().equals("Dispatched"))) {
                    cardButtonLayout.setVisibility(View.VISIBLE);
                    /**
                     *   https://wishbook.atlassian.net/browse/WB-2580
                     *   hide cancel button for sales order
                     */

                    btnCancel.setVisibility(View.GONE);
                }
            }


            if (selectedOrder.getPayment_status().equals("Paid")
                    || selectedOrder.getPayment_status().equals("Partially Paid")
                    || selectedOrder.getProcessing_status().equals("Dispatched")
                    || selectedOrder.getProcessing_status().equals("Closed")
                    || selectedOrder.getProcessing_status().equals("Delivered")
                    || selectedOrder.getProcessing_status().equals("In Progress")) {
                if (selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() > 0) {


                    btnAccept.setVisibility(View.GONE);
                    btnTransfer.setVisibility(View.GONE);
                    btnDispatch.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnClose.setVisibility(View.GONE);
                    btnPay.setVisibility(View.GONE);


                    if (selectedOrder.getProcessing_status().equals("In Progress")) {
                        cardButtonLayout.setVisibility(View.VISIBLE);
                        /**
                         *   https://wishbook.atlassian.net/browse/WB-2580
                         *   hide cancel button for sales order
                         */
                        btnClose.setVisibility(View.GONE);
                    }


                }


            } else {

                if (selectedOrder.getProcessing_status().equals("In Progress")) {
                    btnCancel.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    btnDispatch.setVisibility(View.GONE);
                    // in_progress_note.setText("Note: You need to process this order from Web Application");

                    /**
                     *   https://wishbook.atlassian.net/browse/WB-2580
                     *   hide cancel button for sales order
                     */
                    btnClose.setVisibility(View.GONE);
                    cardButtonLayout.setVisibility(View.VISIBLE);
                } else {
                    btnClose.setVisibility(View.GONE);
                }

                if (selectedOrder.getProcessing_status().equals("Cancelled")
                        || selectedOrder.getProcessing_status().equals("Transferred")) {
                    btnCancel.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    btnDispatch.setVisibility(View.GONE);
                    cardButtonLayout.setVisibility(View.GONE);
                }

                if (!selectedOrder.getProcessing_status().equals("Delivered")
                        && !selectedOrder.getProcessing_status().equals("Dispatched")
                        && !selectedOrder.getProcessing_status().equals("Cancelled")
                        && !selectedOrder.getProcessing_status().equals("Transferred")
                        && !selectedOrder.getProcessing_status().equals("In progress")) {

                    btnTransfer.setVisibility(View.GONE);
                    cardButtonLayout.setVisibility(View.VISIBLE);
                    btnDispatch.setVisibility(View.GONE);
                    /**
                     *   https://wishbook.atlassian.net/browse/WB-2580
                     *   hide cancel button for sales order
                     */

                    btnCancel.setVisibility(View.GONE);

                    if (!selectedOrder.getProcessing_status().equals("Accepted")) {
                        cardButtonLayout.setVisibility(View.VISIBLE);
                        if (selectedOrder.getOrder_type().equals(Constants.ORDER_TYPE_PREPAID) && selectedOrder.getPayment_status().equals("Pending")) {
                            btnAccept.setVisibility(View.GONE);
                        } else {
                            btnAccept.setVisibility(View.GONE);
                        }
                        btnDispatch.setVisibility(View.GONE);

                    } else {

                        btnAccept.setVisibility(View.GONE);
                        btnTransfer.setVisibility(View.GONE);
                    }

                }


            }

        }


        ArrayList<Invoice> invoices = new ArrayList<>();


        if (!selectedOrder.getProcessing_status().equals("Draft")
                && !selectedOrder.getProcessing_status().equals("Cancelled")
                && !selectedOrder.getProcessing_status().equals("Closed")
                && !selectedOrder.getProcessing_status().equals("Cart")) {
            if (selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() > 0) {
                invoices = selectedOrder.getInvoices();
                InvoiceAdapter invoiceAdapter = new InvoiceAdapter(Activity_OrderDetailsNew.this,
                        invoices, selectedOrder.getId(),
                        selectedOrder.getOrder_number(), selectedOrder.getOrder_type(),
                        selectedOrder.getBuying_company_name(), selectedOrder.getCreated_at(),
                        false, selectedOrder.getCatalogs(), isBroker,
                        selectedOrder.getProcessing_status());
                recyclerInvoice.setAdapter(invoiceAdapter);
                //card_order_summary.setVisibility(View.GONE);
            }
        } else {

        }

        if (selectedOrder.getPreffered_shipping_provider().equals("WB Provided") && selectedOrder.getShip_to() != null) {
            shipping_details_card.setVisibility(View.VISIBLE);
            txt_shipping_provider.setText("Wishbook provided");
            if (selectedOrder.getShip_to() != null) {
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }
                    }
                    //ShippingAddressResponse ship = (ShippingAddressResponse) selectedOrder.getShip_to();
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    shipping_details_card.setVisibility(View.GONE);
                }

            }
        } else if (!selectedOrder.getPreffered_shipping_provider().equals("WB Provided")) {
            if (selectedOrder.getBuyer_preferred_logistics() != null && !selectedOrder.getBuyer_preferred_logistics().isEmpty()) {
                relative_shipping_provider.setVisibility(View.VISIBLE);
                txt_shipping_provider.setText(selectedOrder.getBuyer_preferred_logistics());
            } else {
                relative_shipping_provider.setVisibility(View.GONE);
            }
            shipping_details_card.setVisibility(View.VISIBLE);
            if (selectedOrder.getShip_to() != null) {
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectedOrder.getShip_to())), ShippingAddressResponse.class);
                    //ShippingAddressResponse ship = (ShippingAddressResponse) selectedOrder.getShip_to();
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }
                    }
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    shipping_details_card.setVisibility(View.GONE);
                }

            }
        } else {
            Log.e("TAG", "onServerResponse: 3");
            shipping_details_card.setVisibility(View.GONE);
        }



               /* if (selectedOrder.getInvoices() != null && !selectedOrder.getPayment_status().equals("Pending")) {
                    if (selectedOrder.getInvoices().size() > 0) {
                        cardInvoice.setVisibility(View.VISIBLE);
                        for (Invoice invoice : selectedOrder.getInvoices()) {
                            invoices.add(invoice);
                        }
                        invoiceOrderNo.setText(selectedOrder.getOrder_number());
                        invoiceSellerName.setText(StringUtils.capitalize(selectedOrder.getSeller_company_name().toLowerCase().trim()));
                        invoiceOrdeDate.setText(getformatedDate(selectedOrder.getDate()));
                        if (invoices != null && invoices.size() > 0) {
                            invoiceTotalAmt.setText("\u20B9" + " " + invoices.get(0).getTotal_amount());

                            //set Discount tax
                            if (invoices.get(0).getTax_class_1() != null) {
                                relativeTaxClass1.setVisibility(View.VISIBLE);
                                taxClass1Name.setText(invoices.get(0).getTax_class_1());
                                txtClass1Value.setText("+ " + "\u20B9" + invoices.get(0).getTotal_tax_value_1());
                            } else {
                                relativeTaxClass1.setVisibility(View.GONE);
                            }

                            if (invoices.get(0).getTax_class_2() != null) {
                                relativeTaxClass2.setVisibility(View.VISIBLE);
                                taxClass2Name.setText(invoices.get(0).getTax_class_2());
                                txtClass2Value.setText("+ " + "\u20B9" + invoices.get(0).getTotal_tax_value_2());
                            } else {
                                relativeTaxClass2.setVisibility(View.GONE);
                            }

                            if (invoices.get(0).getSeller_discount() != null) {
                                relativeSellerDiscount.setVisibility(View.VISIBLE);
                                txtSellerDiscount.setText("- " + "\u20B9" + invoices.get(0).getSeller_discount());
                            } else {
                                relativeSellerDiscount.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        cardInvoice.setVisibility(View.GONE);
                    }
                } else {
                    cardInvoice.setVisibility(View.GONE);
                }
*/
        attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
            }
        });

        List<CatalogListItem> catalogListItem = new ArrayList<>();
        for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
            catalogListItem.add(new CatalogListItem(selectedOrder.getCatalogs().get(i).getName().toString(), selectedOrder.getCatalogs().get(i).getBrand().toString(), selectedOrder.getCatalogs().get(i).getTotal_products().toString(), selectedOrder.getCatalogs().get(i).getProducts()));
        }

        expandable_adapter_new adapter = new expandable_adapter_new(Activity_OrderDetailsNew.this,
                catalogListItem, false,
                null, false, selectedOrder.getProcessing_status());
        adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "onListItemExpanded: ");
                        nested_scroll_view.smoothScrollBy(0, 500);
                    }
                }, 200);
            }

            @Override
            public void onListItemCollapsed(int position) {
            }
        });
        mRecyclerView.setAdapter(adapter);


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salesOrderAccept(selectedOrder);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                salesOrdercancel1("salesorder", selectedOrder.getId());
            }
        });

        btnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchOrder(selectedOrder.getId(), "order");
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close(selectedOrder.getId());
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransferDialog(selectedOrder.getId());
            }
        });

        buttonCardVisiblityCheck();
    }


    /**
     * Bind Cancellation Recyclerview only for Purchase Order
     *
     * @param catalogListItem
     * @param selectedOrder
     */
    public void bindCancellationRecyclerView(List<CatalogListItem> catalogListItem, Response_buyingorder selectedOrder) {
        ArrayList<CatalogListItem> cancellation_item = new ArrayList<>();
        for (int i = 0; i < catalogListItem.size(); i++) {
            if (catalogListItem.get(i).getFeedItemList() != null && catalogListItem.get(i).getFeedItemList().size() > 0 && catalogListItem.get(i).getFeedItemList().get(0).isShow_cancellation_option()) {
                cancellation_item.add(catalogListItem.get(i));
            }
        }

        if (cancellation_item != null && cancellation_item.size() > 0) {
            linear_cancellation_item.setVisibility(View.VISIBLE);
            expandable_adapter_new adapter1 = new expandable_adapter_new(Activity_OrderDetailsNew.this,
                    cancellation_item, true, selectedOrder.getInvoice(),
                    true, selectedOrder.getProcessing_status());
            adapter1.setChangeOrderItemsListener(new expandable_adapter_new.ChangeOrderItemsListener() {
                @Override
                public void onChange() {
                    // Refresh the page
                    onRefresh();
                }
            });
            recycler_view_cancellation_item.setAdapter(adapter1);
        } else {
            linear_cancellation_item.setVisibility(View.GONE);
        }
    }

    public void showimage(String imagepath) {
        Dialog mSplashDialog = new Dialog(Activity_OrderDetailsNew.this, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(Activity_OrderDetailsNew.this, imagepath, image, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, imagepath, image);

            //  Picasso.with(mContext).load(imagepath).into(image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    public void showConfirmOrderReviewDialog(String orderNumber) {
        new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
                .title(StaticFunctions.formatErrorTitle(getString(R.string.confirm_order_delivery_title)))
                .content(String.format(getString(R.string.confirm_delivery_content), orderNumber))
                .positiveText("YES")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        getwarehouse();
                        dialog.dismiss();
                    }
                })
                .negativeText("NO")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void getwarehouse() {
        StaticFunctions.showProgressbar(Activity_OrderDetailsNew.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        HttpManager.getInstance(Activity_OrderDetailsNew.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "warehouse", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Log.v("sync response", response);
                final SpinAdapter_warehouse spinAdapter_warehouse;
                Spinner spinner_warehouse = null;
                Response_warehouse[] response_warehous = Application_Singleton.gson.fromJson(response, Response_warehouse[].class);
                try {
                    MaterialDialog materialDialog = new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
                            .title(StaticFunctions.formatErrorTitle("Select Warehouse"))
                            .customView(R.layout.dialog_warehouse, true)
                            .positiveText("Done")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    try {

                                        Spinner spinner = (Spinner) dialog.getCustomView().findViewById(R.id.spinner_warehouse);
                                        setOrderDelivered(((Response_warehouse) spinner.getSelectedItem()).getId());
                                        dialog.dismiss();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .build();

                    spinner_warehouse = (Spinner) materialDialog.getCustomView().findViewById(R.id.spinner_warehouse);
                    ArrayList<Response_warehouse> response_warehouses = new ArrayList<Response_warehouse>(Arrays.asList(response_warehous));
                    spinAdapter_warehouse = new SpinAdapter_warehouse(Activity_OrderDetailsNew.this, R.layout.spinneritem, response_warehouses);
                    spinner_warehouse.setAdapter(spinAdapter_warehouse);
                    materialDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Log.v("sync response", error.getErrormessage());
            }
        });
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    // Permission Denied
                    Toast.makeText(mContext, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } else if (requestCode == 556) {
            // Invoice Download request
            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++)
                params.put(permissions[i], grantResults[i]);
            if (params.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                checkWritePermissionAndDownloadInvoice(temp_download_invoice_url, temp_download_invoice_id);
            } else {
                // Permission Denied
                Toast.makeText(mContext, "WRITE_EXTERNAL_STORAGE Permission is Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    private void addCatalogList(ArrayList<Response_sellingoder_catalog> catalogs, LinearLayout root) {
        Log.d("TAG", "addCatalogList: " + catalogs.size());
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < catalogs.size(); i++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            float totalPriceCatalog = 0;
            int totalQty = 0;

            for (int j = 0; j < catalogs.get(i).getProducts().size(); j++) {

                Response_Product product = catalogs.get(i).getProducts().get(j);
                totalQty += Integer.parseInt(product.getQuantity());
                totalPriceCatalog += Float.parseFloat(product.getRate()) * Integer.parseInt(product.getQuantity());

                if (catalogs.get(i).getProducts().get(0).getProduct_type() != null
                        && catalogs.get(i).getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                    totalQty = Integer.parseInt(catalogs.get(i).getProducts().get(0).getNo_of_pcs()) * Integer.parseInt(catalogs.get(i).getProducts().get(0).getQuantity());

                    totalPriceCatalog = Float.parseFloat(catalogs.get(i).getProducts().get(0).getRate())
                            * Float.parseFloat(catalogs.get(i).getProducts().get(0).getNo_of_pcs())
                            * Float.parseFloat(catalogs.get(i).getProducts().get(0).getQuantity());

                }

            }

            String cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";

           /* String cname = "";
            if (catalogs.get(i).getProducts().get(0).getProduct_type() != null
                    && catalogs.get(i).getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                int pc = Integer.parseInt(catalogs.get(i).getProducts().get(0).getNo_of_pcs()) * Integer.parseInt(catalogs.get(i).getProducts().get(0).getQuantity());
                cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + pc + "Pcs.)";

                totalPriceCatalog += Integer.parseInt(catalogs.get(i).getProducts().get(0).getRate())
                        * Integer.parseInt(catalogs.get(i).getProducts().get(0).getNo_of_pcs())
                        * Integer.parseInt(catalogs.get(i).getProducts().get(0).getQuantity());

            } else {
                cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";
            }*/

            catalogValue.setText("\u20B9" + String.valueOf(totalPriceCatalog));
            catalogName.setText(cname);
            root.addView(v);
        }
    }

    private void pendingDispatchList(ArrayList<Response_sellingoder_catalog> catalogs, LinearLayout root) {
        boolean isshowPendingDispatchQty = false;
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < catalogs.size(); i++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            int totalQty = 0;
            boolean isDifferentprice = false;
            ArrayList<String> all_product_rate = new ArrayList<>();

            for (int j = 0; j < catalogs.get(i).getProducts().size(); j++) {
                if (all_product_rate != null && !all_product_rate.contains(catalogs.get(i).getProducts().get(j).getRate()))
                    all_product_rate.add(catalogs.get(i).getProducts().get(j).getRate());
                Response_Product product = catalogs.get(i).getProducts().get(j);
                int pending_qty = 0;
                try {
                    pending_qty = product.getPending_quantity() + product.getInvoiced_qty() - product.getDispatched_qty() - product.getCanceled_qty();
                    if (pending_qty < 0) {
                        pending_qty = 0;
                    }
                } catch (Exception e) {
                    pending_qty = 0;
                }

                if (pending_qty > 0) {
                    totalQty += pending_qty;
                }
            }

            if (all_product_rate.size() > 1) {
                isDifferentprice = true;
            }

            if (totalQty > 0) {
                isshowPendingDispatchQty = true;
                String cname = "";
                if (!isDifferentprice) {
                    cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty / Integer.parseInt(catalogs.get(i).getTotal_products()) + "Pcs.)";
                } else {
                    cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";
                }

                catalogValue.setVisibility(View.GONE);
                catalogName.setText(cname);
                root.addView(v);
            }

        }

        if (isshowPendingDispatchQty) {
            linear_dispatch_pending.setVisibility(View.VISIBLE);
        } else {
            linear_dispatch_pending.setVisibility(View.GONE);
        }


    }


    private void addCatalogListWithPkgType(ArrayList<Response_sellingoder_catalog> catalogs, LinearLayout root) {
        Log.d("TAG", "addCatalogList: " + catalogs.size());
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < catalogs.size(); i++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            RelativeLayout relative_pkg_type = (RelativeLayout) v.findViewById(R.id.relative_pkg_type);
            relative_pkg_type.setVisibility(View.VISIBLE);
            TextView txt_pkg_type = (TextView) v.findViewById(R.id.txt_pkg_type);
            float totalPriceCatalog = 0;
            int totalQty = 0;
            for (int j = 0; j < catalogs.get(i).getProducts().size(); j++) {
                Response_Product product = catalogs.get(i).getProducts().get(j);
                totalQty += Integer.parseInt(product.getQuantity());
                totalPriceCatalog += Float.parseFloat(product.getRate()) * Integer.parseInt(product.getQuantity());
            }
            String cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";
            catalogValue.setText("\u20B9" + String.valueOf(totalPriceCatalog));
            catalogName.setText(cname);
            txt_pkg_type.setText(catalogs.get(0).getProducts().get(0).getPacking_type());
            root.addView(v);
        }
    }


    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }
    }




    /// ######################### Order Action API Call Start ######################################### //

    public void salesOrderAccept(Response_sellingorder_new selectedOrder) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        Gson gson = new Gson();
        PatchAccept patchAccept = new PatchAccept(selectedOrder.getId(), "Accepted");
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                orderStatus.setText("Accepted");
                btnAccept.setVisibility(View.GONE);
                isAllowCache = false;
                orderfetch();
                setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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

    public void salesOrdercancel1(final String type, final String id) {
        new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
                .title("Cancel")
                .content("Order cancel note")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
                        Gson gson = new Gson();
                        if (input.toString().equals("")) {
                            Toast.makeText(Activity_OrderDetailsNew.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Cancelled", "Cancelled", "" + input.toString());
                        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                orderStatus.setText("Cancelled");
                                btnCancel.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.GONE);
                                btnPay.setVisibility(View.GONE);
                                btnDispatch.setVisibility(View.GONE);
                                isAllowCache = false;
                                orderfetch();
                                setResult(Activity.RESULT_OK);
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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
                }).show();
    }

    public void salesOrderCancel(String orderid) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        Gson gson = new Gson();
        PatchAccept patchAccept = new PatchAccept(orderid, "Closed");
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "") + orderid + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                orderStatus.setText("Closed");
                btnCancel.setVisibility(View.GONE);
                btnAccept.setVisibility(View.GONE);
                btnDispatch.setVisibility(View.GONE);
                isAllowCache = false;
                orderfetch();
                setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void dispatchOrder(final String id, final String type) {
        final String url;
        if (type.equals("invoice")) {
            url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "invoice_dispatch", id);
        } else {
            url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "") + id + '/';
        }


        Fragment_Dispatch dispatchFragment = new Fragment_Dispatch();
        dispatchFragment.setListener(new Fragment_Dispatch.Listener() {
            @Override
            public void onDismiss(String date, String mode, String transporter, String tracking_number) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
                Gson gson = new Gson();
                // selectedOrder.getId(), "Dispatched", date, mode,tracking_number,transporter
                PatchDispatch patchDispatch = new PatchDispatch();
                patchDispatch.setId(id);
                patchDispatch.setProcessing_status("Dispatched");
                patchDispatch.setDispatch_date(date);
                if (!tracking_number.equals(""))
                    patchDispatch.setTracking_number(tracking_number);
                if (!transporter.equals(""))
                    patchDispatch.setTransporter_courier(transporter);
                if (!mode.equals(""))
                    patchDispatch.setMode(mode);
                Log.d("Dispatch Date", date);
                if (type.equals("invoice")) {
                    //POST
                    HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, url, gson.fromJson(gson.toJson(patchDispatch), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            orderStatus.setText("Dispatched");
                            btnDispatch.setVisibility(View.GONE);
                            isAllowCache = false;
                            orderfetch();
                            btnCancel.setVisibility(View.GONE);
                            setResult(Activity.RESULT_OK);
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });
                } else {
                    //Patch
                    HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, gson.fromJson(gson.toJson(patchDispatch), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            orderStatus.setText("Dispatched");
                            btnDispatch.setVisibility(View.GONE);
                            isAllowCache = false;
                            orderfetch();
                            btnCancel.setVisibility(View.GONE);
                            setResult(Activity.RESULT_OK);
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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
            }
        });
        dispatchFragment.show(getSupportFragmentManager(), "dispatch");
    }

    public void cancelOrder(final String type, final String id) {
        new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
                .title("Cancel")
                .content("Order cancel note")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText("OK")
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
                        Gson gson = new Gson();
                        if (input.toString().equals("")) {
                            Toast.makeText(Activity_OrderDetailsNew.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Cancelled", "Cancelled", "" + input.toString());
                        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {

                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                orderStatus.setText("Cancelled");
                                isAllowCache = false;
                                orderfetch();
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                    }
                }).show();
    }

    public void cancelInvoice(final String type, final String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        Gson gson = new Gson();
        PatchCancelInvoice patchCancelOrder = new PatchCancelInvoice("Cancelled");
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                isAllowCache = false;
                orderfetch();
                setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void close(String orderId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        Gson gson = new Gson();
        PatchAccept patchAccept = new PatchAccept(orderId, "Closed");
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "") + orderId + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                orderStatus.setText("Closed");
                isAllowCache = false;
                orderfetch();
                setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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

    public void setOrderDelivered(String warehouseId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        Gson gson = new Gson();
        PatchAccept patchAccept = new PatchAccept(((Response_buyingorder) Application_Singleton.selectedOrder).getId(), "Delivered");
        patchAccept.setWarehouse(warehouseId);
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "purchaseorder", "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(mContext, "Order created successfully", Toast.LENGTH_SHORT).show();
                final Response_buyingorder selectedOrder = Application_Singleton.gson.fromJson(response, Response_buyingorder.class);
                Application_Singleton.selectedOrder = selectedOrder;
                isAllowCache = false;
                orderfetch();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        nested_scroll_view.smoothScrollTo(0, ratingBar.getBottom());
                    }
                });

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void callMarkBuyerReqDelivered(String shipmentID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        HashMap<String,String> param = new HashMap<>();
        param.put("buyer_req_to_mark_delivered","true");
        JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(param), JsonObject.class);
        String url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "shipment", "") + shipmentID + "/";
        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                isAllowCache = false;
                orderfetch();
                setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Order Detail New request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        File imageFile = null;
        /**
         * For Attach ScreenShot
         */
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                String selectedImagePath = getAbsolutePath(data.getData());
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                try {
                    imageFile = new File(selectedImagePath);
                    String url = "";
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
                    HashMap<String, String> params = new HashMap<>();
                    String filename = null;
                    if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
                        url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "");
                        if (checkImage(Application_Singleton.selectedOrder, 1)) {
                            filename = "sales_image";
                        } else if (checkImage(Application_Singleton.selectedOrder, 2)) {
                            filename = "sales_image_2";
                        } else if (checkImage(Application_Singleton.selectedOrder, 3)) {
                            filename = "sales_image_3";
                        }
                        params.put("id", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId());
                        params.put("order_number", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getOrder_number());
                        params.put("company", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getCompany());
                        params.put("total_rate", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getTotal_rate());
                        params.put("date", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getDate());
                        params.put("time", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getTime());
                        params.put("processing_status", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getProcessing_status());
                        params.put("customer_status", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getCustomer_status());
                        params.put("user", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getUser());
                        params.put("company_name", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getCompany_name());
                        params.put("seller_company", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSeller_company());

                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getSeller_company_name() != null) {
                            params.put("seller_company_name", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSeller_company_name());
                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getNote() != null) {
                            params.put("note", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getNote());
                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getTotal_products() != null) {
                            params.put("total_products", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getTotal_products());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getTracking_details() != null) {
                            params.put("tracking_details", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getTracking_details());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getSupplier_cancel() != null) {
                            params.put("supplier_cancel", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSupplier_cancel());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getBuyer_cancel() != null) {
                            params.put("buyer_cancel", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getBuyer_cancel());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getPayment_details() != null) {
                            params.put("payment_details", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getPayment_details());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getPayment_date() != null) {
                            params.put("payment_date", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getPayment_date());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getDispatch_date() != null) {
                            params.put("dispatch_date", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getDispatch_date());

                        }
                        if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getSeller_company() != null) {
                            params.put("broker_company_name", ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSeller_company());

                        }

                        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithFile(HttpManager.METHOD.PUTFILEWITHPROGRESS, url + (Application_Singleton.selectedOrder instanceof Response_sellingorder_new ? ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId() : ((Response_buyingorder) Application_Singleton.selectedOrder).getId()) + "/", params, headers, filename, "image/jpg", imageFile, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Gson gson = new Gson();
                                Response_sellingorder_new selectedOrder = gson.fromJson(response, Response_sellingorder_new.class);
                                Application_Singleton.selectedOrder = selectedOrder;
                                Toast.makeText(Activity_OrderDetailsNew.this, "Attached Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("File", "Attached");
                                SimpleDraweeView imageView = new SimpleDraweeView(mContext);
                                imageView.setPadding(10, 10, 10, 10);
                                imageView.setLayoutParams(new ViewGroup.LayoutParams(StaticFunctions.dpToPx(mContext, 70), StaticFunctions.dpToPx(mContext, 90)));
                                attachment_container.addView(imageView);
                                //StaticFunctions.loadImage(Activity_OrderDetailsNew.this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), imageView, R.drawable.uploadempty);
                                StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), imageView);
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                            }
                        });
                    }
                    if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
                        url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "purchaseorder", "");
                        filename = "purchase_image";
                        params.put("id", ((Response_buyingorder) Application_Singleton.selectedOrder).getId());
                        params.put("order_number", ((Response_buyingorder) Application_Singleton.selectedOrder).getOrder_number());

                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getCompany() != null) {
                            params.put("company", ((Response_buyingorder) Application_Singleton.selectedOrder).getCompany());
                        }
                        params.put("total_rate", ((Response_buyingorder) Application_Singleton.selectedOrder).getTotal_rate());
                        params.put("date", ((Response_buyingorder) Application_Singleton.selectedOrder).getDate());
                        params.put("time", ((Response_buyingorder) Application_Singleton.selectedOrder).getTime());
                        params.put("processing_status", ((Response_buyingorder) Application_Singleton.selectedOrder).getProcessing_status());
                        params.put("customer_status", ((Response_buyingorder) Application_Singleton.selectedOrder).getCustomer_status());
                        params.put("user", ((Response_buyingorder) Application_Singleton.selectedOrder).getUser());

                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getCompany_name() != null) {
                            params.put("company_name", ((Response_buyingorder) Application_Singleton.selectedOrder).getCompany_name());
                        }
                        params.put("seller_company", ((Response_buyingorder) Application_Singleton.selectedOrder).getSeller_company());

                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getSeller_company_name() != null) {
                            params.put("seller_company_name", ((Response_buyingorder) Application_Singleton.selectedOrder).getSeller_company_name());
                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getNote() != null) {
                            params.put("note", ((Response_buyingorder) Application_Singleton.selectedOrder).getNote());
                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getTotal_products() != null) {
                            params.put("total_products", ((Response_buyingorder) Application_Singleton.selectedOrder).getTotal_products());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getTracking_details() != null) {
                            params.put("tracking_details", ((Response_buyingorder) Application_Singleton.selectedOrder).getTracking_details());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getSupplier_cancel() != null) {
                            params.put("supplier_cancel", ((Response_buyingorder) Application_Singleton.selectedOrder).getSupplier_cancel());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getBuyer_cancel() != null) {
                            params.put("buyer_cancel", ((Response_buyingorder) Application_Singleton.selectedOrder).getBuyer_cancel());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getPayment_details() != null) {
                            params.put("payment_details", ((Response_buyingorder) Application_Singleton.selectedOrder).getPayment_details());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getPayment_date() != null) {
                            params.put("payment_date", ((Response_buyingorder) Application_Singleton.selectedOrder).getPayment_date());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getDispatch_date() != null) {
                            params.put("dispatch_date", ((Response_buyingorder) Application_Singleton.selectedOrder).getDispatch_date());

                        }
                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getSeller_company() != null) {
                            params.put("broker_company_name", ((Response_buyingorder) Application_Singleton.selectedOrder).getSeller_company());

                        }
                        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithFile(HttpManager.METHOD.PUTFILEWITHPROGRESS, url + (Application_Singleton.selectedOrder instanceof Response_sellingorder_new ? ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId() : ((Response_buyingorder) Application_Singleton.selectedOrder).getId()) + "/", params, headers, filename, "image/jpg", imageFile, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Gson gson = new Gson();
                                final Response_buyingorder selectedOrder1 = gson.fromJson(response, Response_buyingorder.class);
                                Application_Singleton.selectedOrder = selectedOrder1;
                                SimpleDraweeView imageView = new SimpleDraweeView(mContext);
                                imageView.setPadding(10, 10, 10, 10);
                                imageView.setLayoutParams(new ViewGroup.LayoutParams(StaticFunctions.dpToPx(mContext, 70), StaticFunctions.dpToPx(mContext, 90)));
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image() != null)
                                            showimage(((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image());
                                    }
                                });
                                attachment_container.addView(imageView);
                                //StaticFunctions.loadImage(Activity_OrderDetailsNew.this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), imageView, R.drawable.uploadempty);
                                StaticFunctions.loadFresco(Activity_OrderDetailsNew.this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), imageView);
                                Toast.makeText(Activity_OrderDetailsNew.this, "Attached Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("File", "Attached");

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                            }
                        });
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            linear_pay_button.setVisibility(View.GONE);
            isAllowCache = false;
            orderfetch();
        }
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                edit_buyername.setText(buyer.getCompany_name());
                txt_input_buyer.setErrorEnabled(false);
                txt_input_buyer.setError(null);
            }
        }
    }



    public boolean checkImage(Object object, int ImageNumber) {
        Boolean flag = false;
        if (object instanceof Response_sellingorder) {
            switch (ImageNumber) {
                case 1:
                    flag = (((Response_sellingorder) object).getSales_image() == null || ((Response_sellingorder) object).getSales_image().equals("") || ((Response_sellingorder) object).getSales_image().equals("null"));
                    break;
                case 2:
                    flag = (((Response_sellingorder) object).getSales_image_2() == null || ((Response_sellingorder) object).getSales_image_2().equals("") || ((Response_sellingorder) object).getSales_image_2().equals("null"));
                    break;
                case 3:
                    flag = (((Response_sellingorder) object).getSales_image_3() == null || ((Response_sellingorder) object).getSales_image_3().equals("") || ((Response_sellingorder) object).getSales_image_3().equals("null"));
                    break;
            }
        }
        if (object instanceof Response_sellingorder_new) {
            switch (ImageNumber) {
                case 1:
                    flag = (((Response_sellingorder_new) object).getSales_image() == null || ((Response_sellingorder_new) object).getSales_image().equals("") || ((Response_sellingorder_new) object).getSales_image().equals("null"));
                    break;
                case 2:
                    flag = (((Response_sellingorder_new) object).getSales_image_2() == null || ((Response_sellingorder_new) object).getSales_image_2().equals("") || ((Response_sellingorder_new) object).getSales_image_2().equals("null"));
                    break;
                case 3:
                    flag = (((Response_sellingorder_new) object).getSales_image_3() == null || ((Response_sellingorder_new) object).getSales_image_3().equals("") || ((Response_sellingorder_new) object).getSales_image_3().equals("null"));
                    break;
            }
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        setResult(50);
        finish();
        super.onBackPressed();
    }

    public void setColor(String status, TextView textView) {
        if (status.equalsIgnoreCase("Draft")
                || status.equalsIgnoreCase("Partially Paid")
                || status.equalsIgnoreCase("Cod verification pending")) {
            textView.setTextColor(getResources().getColor(R.color.red));
        } else if(status.equalsIgnoreCase("Cancelled")) {
            textView.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        } else {
            textView.setTextColor(getResources().getColor(R.color.green));
        }
    }

    public void showTransferDialog(final String orderId) {
        ArrayList<BuyersList> buyerslist = new ArrayList<>();
        buyer = null;
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).title("Transfer Sales Order").positiveText("Save").negativeText("Cancel").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).customView(R.layout.dialog_transfer_sales_order, true).build();

        txt_input_buyer = (TextInputLayout) dialog.getCustomView().findViewById(R.id.input_buyername);
        edit_buyername = (TextView) dialog.getCustomView().findViewById(R.id.edit_buyername);
        final LinearLayout buyer_container = (LinearLayout) dialog.getCustomView().findViewById(R.id.buyer_container);
        buyer_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(Activity_OrderDetailsNew.this);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_buyername.getWindowToken(), 0);
                startActivityForResult(new Intent(mContext, Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buyer == null) {
                    if (edit_buyername.getText().toString().isEmpty()) {
                        txt_input_buyer.setErrorEnabled(true);
                        txt_input_buyer.setError(getResources().getString(R.string.empty_field));
                        edit_buyername.requestFocus();
                        return;
                    } else {
                        txt_input_buyer.setErrorEnabled(true);
                        txt_input_buyer.setError("Buyer name is not in network");
                        return;
                    }
                } else {
                    txt_input_buyer.setErrorEnabled(false);
                    txt_input_buyer.setError(null);
                    dialog.dismiss();
                    salesTransfer(orderId, buyer);
                }
            }
        });

        dialog.show();
    }


    public void salesTransfer(String orderId, BuyersList buyer) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("seller_company", buyer.getCompany_id());
        HttpManager.getInstance(Activity_OrderDetailsNew.this).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(mContext, "salesorders_transfer", orderId), params, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(mContext, "Order successfully transferred", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    /*
        set brokerage order  pending when click place_order button
     */
    public void setOrderPending() {
        if (((Response_buyingorder) Application_Singleton.selectedOrder).getProcessing_status().equals("Draft")) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
            Gson gson = new Gson();
            PatchAccept patchAccept = new PatchAccept(((Response_buyingorder) Application_Singleton.selectedOrder).getId(), "Pending");
            HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "brokerageorder", "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Toast.makeText(mContext, "Order created successfully", Toast.LENGTH_SHORT).show();
                    final Response_buyingorder selectedOrder = Application_Singleton.gson.fromJson(response, Response_buyingorder.class);
                    selectedOrder.setBrokerage(true);
                    linear_place_order.setVisibility(View.GONE);
                    btn_place_order.setVisibility(View.GONE);
                    Application_Singleton.selectedOrder = selectedOrder;
                    isAllowCache = false;
                    orderfetch();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    new MaterialDialog.Builder(mContext)
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
            Toast.makeText(mContext, "Order updated successfully", Toast.LENGTH_SHORT).show();
            isAllowCache = false;
            orderfetch();
        }

    }


    public void chatWithSupplier(String suppliername, String supplierChatUser) {
        Intent intent = new Intent(mContext, ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, supplierChatUser);
        intent.putExtra(ConversationUIService.DISPLAY_NAME, suppliername);
        startActivity(intent);
    }

    public void chatWithBuyer(String buyerName, String buyerChatUser) {
        Intent intent = new Intent(mContext, ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, buyerChatUser);
        intent.putExtra(ConversationUIService.DISPLAY_NAME, buyerName);
        startActivity(intent);
    }

    public void initRating(final boolean isSales, final String orderId, final boolean isEdit, final ResponseRating rating) {
        card_rating.setVisibility(View.VISIBLE);
        liner_question_selling.setVisibility(View.GONE);
        liner_question_purchase.setVisibility(View.GONE);
        txt_question_title.setVisibility(View.GONE);
        btn_rating_done.setVisibility(View.GONE);
        btn_rating_cancel.setVisibility(View.GONE);
        if (isSales) {
            btn_rating_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEdit) {
                        if (!check_purchase_one.isChecked() && !check_purchase_two.isChecked() &&
                                !check_purchase_three.isChecked() && !check_purchase_four.isChecked()) {
                            // if no reason selected
                            Toast.makeText(mContext, "Please Select Any one Reason", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            setBuyerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_purchase_reason.getText().toString().trim(),
                                    check_purchase_one.isChecked(), check_purchase_two.isChecked(),
                                    check_purchase_three.isChecked(), check_purchase_four.isChecked(), rating.getId());
                        }
                    } else {
                        if (!check_purchase_one.isChecked() && !check_purchase_two.isChecked() &&
                                !check_purchase_three.isChecked() && !check_purchase_four.isChecked()) {
                            // if no reason selected
                            Toast.makeText(mContext, "Please Select Any one Reason", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            setBuyerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_purchase_reason.getText().toString().trim(),
                                    check_purchase_one.isChecked(), check_purchase_two.isChecked(),
                                    check_purchase_three.isChecked(), check_purchase_four.isChecked(), null);
                        }

                    }

                }
            });

            ((CheckBox) findViewById(R.id.check_purchase_four)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        other_purchase_container.setVisibility(View.VISIBLE);
                        input_other_purchase_reason.setError(null);
                    } else {
                        other_purchase_container.setVisibility(View.GONE);
                        edit_other_purchase_reason.setText("");
                        input_other_purchase_reason.setError(null);
                    }
                }
            });

            edit_other_purchase_reason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!charSequence.toString().isEmpty()) {
                        input_other_purchase_reason.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        } else {
            btn_rating_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEdit) {
                        if (!check_selling_one.isChecked() && !check_selling_two.isChecked() &&
                                !check_purchase_three.isChecked() && !check_selling_four.isChecked()) {
                            // if no reason selected
                            Toast.makeText(mContext, "Please Select Any one Reason", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            setSellerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_reason.getText().toString().trim(),
                                    check_selling_one.isChecked(), check_selling_two.isChecked(),
                                    check_selling_three.isChecked(), check_selling_four.isChecked(), rating.getId());
                        }

                    } else {
                        if (!check_selling_one.isChecked() && !check_selling_two.isChecked() &&
                                !check_selling_three.isChecked() && !check_selling_four.isChecked()) {
                            // if no reason selected
                            Toast.makeText(mContext, "Please Select Any one Reason", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            setSellerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_reason.getText().toString().trim(),
                                    check_selling_one.isChecked(), check_selling_two.isChecked(),
                                    check_selling_three.isChecked(), check_selling_four.isChecked(), null);
                        }
                    }

                }
            });

            check_selling_four.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        other_container.setVisibility(View.VISIBLE);
                        input_other_reason.setError(null);
                    } else {
                        other_container.setVisibility(View.GONE);
                        edit_other_reason.setText("");
                        input_other_reason.setError(null);
                    }
                }
            });
            edit_other_reason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!charSequence.toString().isEmpty()) {
                        input_other_reason.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        if (isEdit) {
            if (isSales) {
                ratingBar.setRating(Float.parseFloat(rating.getBuyer_rating()));
            } else {
                ratingBar.setRating(Float.parseFloat(rating.getSeller_rating()));
            }


            txt_edit_rating.setVisibility(View.VISIBLE);
            ratingBar.setIsIndicator(true);
            txt_edit_rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt_edit_rating.setVisibility(View.GONE);
                    btn_rating_cancel.setVisibility(View.VISIBLE);
                    btn_rating_done.setVisibility(View.VISIBLE);
                    if (isSales) {
                        if (rating.getBuyer_remark() != null && rating.getBuyer_remark().size() > 0) {
                            txt_question_title.setVisibility(View.VISIBLE);
                            liner_question_purchase.setVisibility(View.VISIBLE);
                            for (int i = 0; i < rating.getBuyer_remark().size(); i++) {
                                if (rating.getBuyer_remark().get(i).equals(getResources().getString(R.string.buyer_did_not_receive_the_goods)))
                                    check_purchase_one.setChecked(true);
                                if (rating.getBuyer_remark().get(i).equals(getResources().getString(R.string.buyer_did_not_pay_in_promised_period)))
                                    check_purchase_two.setChecked(true);
                                if (rating.getBuyer_remark().get(i).equals(getResources().getString(R.string.issues_with_wishbook_application)))
                                    check_purchase_three.setChecked(true);
                                if (rating.getBuyer_remark().get(i).equals(getResources().getString(R.string.other))) {
                                    check_purchase_four.setChecked(true);
                                    if (rating.getBuyer_remark_other() != null) {
                                        edit_other_purchase_reason.setText(rating.getBuyer_remark_other().toString());
                                        edit_other_purchase_reason.setSelection(rating.getBuyer_remark_other().length());
                                    }
                                }

                            }
                        }
                    } else {
                        if (rating.getSeller_remark() != null && rating.getSeller_remark().size() > 0) {
                            txt_question_title.setVisibility(View.VISIBLE);
                            liner_question_purchase.setVisibility(View.VISIBLE);
                            for (int i = 0; i < rating.getSeller_remark().size(); i++) {
                                if (rating.getSeller_remark().get(i).equals(getResources().getString(R.string.delivered_products_were_not_as_described)))
                                    check_selling_one.setChecked(true);
                                if (rating.getSeller_remark().get(i).equals(getResources().getString(R.string.delivery_took_more_time_then_mentioned)))
                                    check_selling_two.setChecked(true);
                                if (rating.getSeller_remark().get(i).equals(getResources().getString(R.string.technical_software_issues)))
                                    check_selling_three.setChecked(true);
                                if (rating.getSeller_remark().get(i).equals(getResources().getString(R.string.other))) {
                                    check_selling_four.setChecked(true);
                                    if (rating.getSeller_remark_other() != null) {
                                        edit_other_reason.setText(rating.getSeller_remark_other().toString());
                                        edit_other_reason.setSelection(rating.getSeller_remark_other().length());
                                    }
                                }

                            }
                        }
                    }
                    ratingBar.setIsIndicator(false);
                }
            });
            btn_rating_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt_edit_rating.setVisibility(View.VISIBLE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setIsIndicator(true);
                    initRating(isSales, orderId, true, rating);
                }
            });
        } else {
            ratingBar.setIsIndicator(false);
            txt_edit_rating.setVisibility(View.GONE);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (ratingBar.getRating() <= 4) {
                    if (isSales) {
                        txt_question_title.setVisibility(View.VISIBLE);
                        liner_question_purchase.setVisibility(View.VISIBLE);
                        btn_rating_done.setVisibility(View.VISIBLE);
                    } else {
                        txt_question_title.setVisibility(View.VISIBLE);
                        liner_question_selling.setVisibility(View.VISIBLE);
                        btn_rating_done.setVisibility(View.VISIBLE);
                    }
                } else {

                    // for five star rating submit rate
                    if (isSales) {
                        txt_question_title.setVisibility(View.GONE);
                        liner_question_purchase.setVisibility(View.GONE);
                        if (isEdit) {
                            setBuyerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_purchase_reason.getText().toString().trim(),
                                    check_purchase_one.isChecked(), check_purchase_two.isChecked(),
                                    check_purchase_three.isChecked(), check_purchase_four.isChecked(), rating.getId());
                        } else {
                            setBuyerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_purchase_reason.getText().toString().trim(),
                                    check_purchase_one.isChecked(), check_purchase_two.isChecked(),
                                    check_purchase_three.isChecked(), check_purchase_four.isChecked(), null);
                        }

                    } else {
                        txt_question_title.setVisibility(View.GONE);
                        liner_question_selling.setVisibility(View.GONE);
                        if (isEdit) {
                            setSellerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_reason.getText().toString().trim(),
                                    check_selling_one.isChecked(), check_selling_two.isChecked(),
                                    check_selling_three.isChecked(), check_selling_four.isChecked(), rating.getId());
                        } else {
                            setSellerOrderRating(orderId, String.valueOf(ratingBar.getRating()), edit_other_reason.getText().toString().trim(),
                                    check_selling_one.isChecked(), check_selling_two.isChecked(),
                                    check_selling_three.isChecked(), check_selling_four.isChecked(), null);
                        }
                    }
                }
            }
        });
    }

    private void setBuyerOrderRating(final String orderId, String rating, String otherRemark, boolean isCheck1, boolean isCheck2, boolean isCheck3, boolean isCheck4, String ratingID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        ResponseRating responseRating = new ResponseRating();
        responseRating.setOrder(orderId);
        responseRating.setBuyer_rating(rating);

        ArrayList<String> buyerRemarks = new ArrayList<>();
        if (isCheck1) {
            buyerRemarks.add(getResources().getString(R.string.buyer_did_not_receive_the_goods));

        }
        if (isCheck2) {
            buyerRemarks.add(getResources().getString(R.string.buyer_did_not_pay_in_promised_period));
        }
        if (isCheck3) {
            buyerRemarks.add(getResources().getString(R.string.issues_with_wishbook_application));
        }
        if (isCheck4) {
            buyerRemarks.add(getResources().getString(R.string.other));
            if (otherRemark.isEmpty()) {
                input_other_purchase_reason.setError(getResources().getString(R.string.field_empty));
                edit_other_purchase_reason.requestFocus();
                return;
            } else {
                responseRating.setBuyer_remark_other(otherRemark);
            }

        }


        responseRating.setBuyer_remark(buyerRemarks);


        if (ratingID == null) {
            String ratingjson = new Gson().toJson(responseRating);
            JsonObject jsonObject = new Gson().fromJson(ratingjson, JsonObject.class);
            HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mContext, "add_order_rating", orderId), jsonObject, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    txt_edit_rating.setVisibility(View.VISIBLE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setIsIndicator(true);
                    Toast.makeText(mContext, getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
                    final ResponseRating rating = Application_Singleton.gson.fromJson(response, ResponseRating.class);
                    initRating(true, orderId, true, rating);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } else {
            responseRating.setId(ratingID);
            String ratingjson = new Gson().toJson(responseRating);
            JsonObject jsonObject = new Gson().fromJson(ratingjson, JsonObject.class);

            HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "add_order_rating", "") + ratingID + "/", jsonObject, headers, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    txt_edit_rating.setVisibility(View.VISIBLE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setIsIndicator(true);
                    txt_edit_rating.setVisibility(View.GONE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    Toast.makeText(mContext, getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
                    final ResponseRating rating = Application_Singleton.gson.fromJson(response, ResponseRating.class);
                    initRating(true, orderId, true, rating);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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

    }

    private void setSellerOrderRating(final String orderId, String rating, String otherRemark, boolean isCheck1, boolean isCheck2, boolean isCheck3, boolean isCheck4, String ratingID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        ResponseRating responseRating = new ResponseRating();
        responseRating.setOrder(orderId);
        responseRating.setSeller_rating(rating);
        ArrayList<String> sellerRemark = new ArrayList<>();
        if (isCheck1) {
            sellerRemark.add(getResources().getString(R.string.delivered_products_were_not_as_described));
        }
        if (isCheck2) {
            sellerRemark.add(getResources().getString(R.string.delivery_took_more_time_then_mentioned));
        }
        if (isCheck3) {
            sellerRemark.add(getResources().getString(R.string.technical_software_issues));
        }
        if (isCheck4) {
            sellerRemark.add(getResources().getString(R.string.other));
            if (otherRemark.isEmpty()) {
                input_other_reason.setError(getResources().getString(R.string.field_empty));
                edit_other_reason.requestFocus();
                return;
            } else {
                responseRating.setSeller_remark_other(otherRemark);
            }

        }
        responseRating.setSeller_remark(sellerRemark);

        if (ratingID == null) {
            String ratingjson = new Gson().toJson(responseRating);
            JsonObject jsonObject = new Gson().fromJson(ratingjson, JsonObject.class);
            HttpManager.getInstance(Activity_OrderDetailsNew.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mContext, "add_order_rating", orderId), jsonObject, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    txt_edit_rating.setVisibility(View.VISIBLE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setIsIndicator(true);
                    Toast.makeText(mContext, getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
                    final ResponseRating rating = Application_Singleton.gson.fromJson(response, ResponseRating.class);
                    initRating(false, orderId, true, rating);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } else {
            Log.i("TAG", "setSellerOrderRating: Order Rating ID" + ratingID);
            responseRating.setId(ratingID);
            String ratingjson = new Gson().toJson(responseRating);
            JsonObject jsonObject = new Gson().fromJson(ratingjson, JsonObject.class);
            HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "add_order_rating", "") + ratingID + "/", jsonObject, headers, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    txt_edit_rating.setVisibility(View.VISIBLE);
                    btn_rating_cancel.setVisibility(View.GONE);
                    ratingBar.setIsIndicator(true);
                    Toast.makeText(mContext, getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
                    final ResponseRating rating = Application_Singleton.gson.fromJson(response, ResponseRating.class);
                    initRating(false, orderId, true, rating);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    new MaterialDialog.Builder(Activity_OrderDetailsNew.this)
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

    }

    public void getOrderRating(final String orderId, final boolean isSales) {
        StaticFunctions.showProgressbar(Activity_OrderDetailsNew.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        HttpManager.getInstance(Activity_OrderDetailsNew.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "order_rating", orderId), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_OrderDetailsNew.this);
                Log.v("Rating Response", response);
                final ResponseRating[] ratings = new Gson().fromJson(response, ResponseRating[].class);
                if (ratings.length > 0) {
                    if (!isSales) {
                        Log.i("TAG", "onServerResponse: 1");
                        if (ratings[0].getSeller_rating() != null) {
                            Log.i("TAG", "onServerResponse: 2");
                            initRating(isSales, orderId, true, ratings[0]);
                        } else {
                            Log.i("TAG", "onServerResponse: 3");
                            initRating(isSales, orderId, false, null);
                        }
                    } else {
                        if (ratings[0].getBuyer_rating() != null) {
                            Log.i("TAG", "onServerResponse: 4");
                            initRating(isSales, orderId, true, ratings[0]);
                        } else {
                            Log.i("TAG", "onServerResponse: 5");
                            initRating(isSales, orderId, false, null);
                        }
                    }

                } else {
                    Log.i("TAG", "onServerResponse: 6");
                    initRating(isSales, orderId, false, null);
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void openCreditBottom(Context context, String buyingCompanyId, String buyerCompanyName) {
        BuyerCreditRatingBottomSheetDialogFragment creditBottomDialog = null;
        creditBottomDialog = creditBottomDialog.newInstance(buyingCompanyId, buyerCompanyName);
        creditBottomDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "CreditRating");
    }


    public void buttonCardVisiblityCheck() {
        if (btnAccept.getVisibility() == View.GONE
                && btnTransfer.getVisibility() == View.GONE
                && btnDispatch.getVisibility() == View.GONE
                && btnClose.getVisibility() == View.GONE
                && btnCancel.getVisibility() == View.GONE) {
            cardButtonLayout.setVisibility(View.GONE);
        } else {

        }
    }

    public void initSwipeRefresh() {
        swipe_container = findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                orderfetch();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    @Override
    public void onSuccessRequest() {
        isAllowCache = false;
        orderfetch();
    }

    @Override
    public void onSuccessCancel() {

    }

    public void showDiscountDialog(final String orderID, final Double discount, final Double totalDiscount) {
        LayoutInflater factory = LayoutInflater.from(Activity_OrderDetailsNew.this);
        final View DialogView = factory.inflate(R.layout.additional_discout_dialog, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(Activity_OrderDetailsNew.this);
        alert.setView(DialogView);
        alert.setCancelable(true);
        final EditText add_dicount_rate = DialogView.findViewById(R.id.add_dicount_rate);
        final TextView btn_negative = DialogView.findViewById(R.id.txt_negative_btn);
        final TextView btn_positive = DialogView.findViewById(R.id.txt_positive_btn);
        final TextView seller_discount = DialogView.findViewById(R.id.seller_discount);
        seller_discount.setText(discount + "% Discount");
        final AlertDialog dialog = alert.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additional_discount = add_dicount_rate.getText().toString();
                try {
                    if (!additional_discount.isEmpty()) {

                        if (!additional_discount.isEmpty() && Float.parseFloat(additional_discount) > 0.0 && (discount + Float.parseFloat(additional_discount)) < 100) {
                            postAdditionalData(Double.toString(Double.parseDouble(additional_discount)), orderID);
                            dialog.dismiss();
                        } else {
                            add_dicount_rate.setError("Enter valid discount%");
                        }
                    } else {
                        add_dicount_rate.setError("Discount can't be empty");
                    }
                } catch (Exception e) {
                    add_dicount_rate.setError("Enter valid discount%");
                }

            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        add_dicount_rate.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(add_dicount_rate.findFocus(), 0);
            }
        }, 200);
        dialog.show();
    }

    private void postAdditionalData(final String discount, String orderID) {
        PatchAccept patchAccept = new PatchAccept();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetailsNew.this);
        patchAccept.setSeller_extra_discount_percentage(discount);
        String url = URLConstants.companyUrl(Activity_OrderDetailsNew.this, "salesorder", "") + orderID + '/';

        HttpManager.getInstance(Activity_OrderDetailsNew.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                edit_additional_discount.setVisibility(View.VISIBLE);
                linear_additional_discount.setVisibility(View.VISIBLE);
                txt_add_discount.setText(discount + "");
                offer_discount.setVisibility(View.GONE);
                isAllowCache = false;
                orderfetch();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(mContext)
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

    public void getBuyerDiscount(final String current_discount, final String supplierId, final double seller_additonal_discount,
                                 final boolean isFromPurchase, final String orderType,
                                 final String orderID, final String orderTotalRate,
                                 final String processingStatus, final String paymentStatus) {

        double discount = 0.0;
        if (current_discount != null) {
            discount = Float.parseFloat(current_discount);
            // seller_discount.setText(String.valueOf(Float.parseFloat(current_discount)) + "% Discount");
        }

       /* if (seller_additonal_discount > 0.00) {
            edit_additional_discount.setVisibility(View.VISIBLE);
            offer_discount.setVisibility(View.GONE);
            linear_additional_discount.setVisibility(View.VISIBLE);
            txt_add_discount.setText(seller_additonal_discount + "% Discount");
        } else {
            edit_additional_discount.setVisibility(View.GONE);
            offer_discount.setVisibility(View.VISIBLE);
            linear_additional_discount.setVisibility(View.GONE);
        }*/

        final Double toatalDiscountPer = discount + seller_additonal_discount;
        Double totalDiscountAmt = toatalDiscountPer * (Double.parseDouble(orderTotalRate) / 100);
        if (totalDiscountAmt > 0) {
            relative_seller_add_discount.setVisibility(View.VISIBLE);
        } else {
            relative_seller_add_discount.setVisibility(View.GONE);
        }

        try {
            String temp = String.valueOf(totalDiscountAmt).split("\\.")[0];
            String temp1 = String.valueOf(totalDiscountAmt).split("\\.")[1];
            order_additional_discount.setText("- " + "\u20B9" + temp + "." + temp1.substring(0, 2));
        } catch (Exception e) {
            order_additional_discount.setText("- " + "\u20B9" + totalDiscountAmt);
            //  e.printStackTrace();
        }
       /* if (seller_additonal_discount > 0) {
            order_total_discount_label.setText("Total Discount" + " (" + discount + "%" + "+" + seller_additonal_discount + "%" + ")");
        } else {
            order_total_discount_label.setText("Total Discount" + " (" + discount + "%" + ")");
        }*/


        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        orderAmount.setText("\u20B9" + decimalFormat.format((Double.parseDouble(orderTotalRate) - totalDiscountAmt)) + "");
        final double finalDiscount = discount;

    }




    public void checkWritePermissionAndDownloadInvoice(String invoiceUrl, String invoiceID) {
        temp_download_invoice_id = null;
        temp_download_invoice_url = null;
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadInvoice(invoiceUrl, invoiceID);
            } else {
                temp_download_invoice_url = invoiceUrl;
                temp_download_invoice_id = invoiceID;
                requestPermissions(permissions, REQUESTWRITEPERMISSION);
            }
        } else {
            downloadInvoice(invoiceUrl, invoiceID);
        }

    }

    public void downloadInvoice(String invoiceUrl, String invoiceID) {
        String url = invoiceUrl;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Wishbook Invoice #" + invoiceID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Wishbook/" + invoiceID + ".pdf");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(mContext, "Invoice Downloading start..", Toast.LENGTH_SHORT).show();
    }


    public void initDialogLayout(final View dialogview, final Dialog dialog, Enum type, final HashMap<String, String> param, Context context) {
        TextView btn_negative = dialogview.findViewById(R.id.btn_negative);
        TextView btn_positive = dialogview.findViewById(R.id.btn_positive);
        TextView txt_dialog_title = dialogview.findViewById(R.id.txt_dialog_title);

        final Spinner spinner_reason = dialogview.findViewById(R.id.spinner_reason);
        final String[] reasons_array = getResources().getStringArray(R.array.replacement_reasons);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, R.layout.order_spinner_item, reasons_array
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem_new);
        spinner_reason.setAdapter(spinnerArrayAdapter);
        txt_dialog_title.setText("Select Request type");
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //   createRRC();


            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void createRRCWithDialog(String orderId) {
        final CharSequence[] items = {"Return Request", "Replacement Request"};
        AlertDialog.Builder builder;
        final int[] seleced_item = {-1};
        builder = new AlertDialog.Builder(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setCancelable(false);
        builder.setTitle("Select Request type")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        seleced_item[0] = item;
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (seleced_item[0] == -1) {
                            Toast.makeText(getApplicationContext(), "Please select any one option", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            dialog.dismiss();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("order_id", orderId);
                        Fragment_RRC_OrderItemSelection fragment_rrc_orderItemSelection = new Fragment_RRC_OrderItemSelection();
                        fragment_rrc_orderItemSelection.setArguments(bundle);
                        if (seleced_item[0] == 1) {
                            bundle.putSerializable("request_type", RRCHandler.RRCREQUESTTYPE.REPLACEMENT);
                            Application_Singleton.CONTAINER_TITLE = "Select items of replacement";
                        } else if (seleced_item[0] == 0) {
                            bundle.putSerializable("request_type", RRCHandler.RRCREQUESTTYPE.RETURN);
                            Application_Singleton.CONTAINER_TITLE = "Select items of retrun";
                        }
                        Application_Singleton.CONTAINERFRAG = fragment_rrc_orderItemSelection;
                        mContext.startActivity(new Intent(mContext, OpenContainer.class));
                    }
                });
            }
        });
        alertDialog.show();

    }

    public void callGetRRCOrdersList(String orderID) {
        try {
            String url = URLConstants.companyUrl(mContext, "rrc-eligible-order-list", "") + "?for_rrc=true";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (!isFinishing()) {
                            ArrayList<Response_buyingorder> items = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_buyingorder>>() {
                            }.getType());
                            boolean isOrderEligibleForRRC = false;
                            if (items.size() > 0) {
                                for (Response_buyingorder order :
                                        items) {
                                    if (order.getId().equalsIgnoreCase(orderID)) {
                                        isOrderEligibleForRRC = true;
                                        break;
                                    }
                                }

                                if (isOrderEligibleForRRC) {
                                    //txt_request_replacement.setVisibility(View.VISIBLE);
                                    //txt_request_return.setVisibility(View.VISIBLE);
                                    btn_create_rrc.setVisibility(View.VISIBLE);
                                    card_btn_rrc.setVisibility(View.VISIBLE);
                                    btn_create_rrc.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            createRRCWithDialog(orderID);
                                        }
                                    });

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
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    ///// ############################ Send Wishbook Analysis Data #####################################//
    public void sendScreenAnalytics(String orderId, String type) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("SalesOrderDetails_screen");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", from);
        prop.put("order_id", orderId);
        prop.put("detail_view_type", type);
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);
    }

}