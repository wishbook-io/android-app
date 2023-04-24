package com.wishbook.catalog.home.orders.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatCallbackStatus;
import com.freshchat.consumer.sdk.UnreadCountCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.ShipmentSummaryItemAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.InvoiceItemSet;
import com.wishbook.catalog.commonmodels.postpatchmodels.OrderInvoiceCreate;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchAccept;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelInvoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelOrder;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchDispatch;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchFinalizeOrder;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchVerifyOrder;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder_new;
import com.wishbook.catalog.home.orders.adapters.expandable_adapter;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;
import com.wishbook.catalog.home.payment.MerchantActivity2;
import com.wishbook.catalog.home.payment.MobiKwikMerchantActivity;
import com.wishbook.catalog.home.payment.ZaakPayMerchantActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_OrderDetails extends AppCompatActivity {

    private static final float AMOUNT_LIMIT_PAYMENT_OPTION = 10000;
    TextView badgeTextView;
    FloatingActionButton support_chat_fab;
    @BindView(R.id.total_quantity_ordered)
    TextView shipment_total_quantity_ordered;
    @BindView(R.id.in_progress_note)
    TextView in_progress_note;
    @BindView(R.id.total_value)
    TextView shipment_total_value;
    @BindView(R.id.order_status)
    TextView shipment_order_status;
    @BindView(R.id.payment_status)
    TextView shipment_payment_status;
    @BindView(R.id.items_shipped_recyclerview)
    RecyclerView items_shipped_recyclerview;
    @BindView(R.id.order_summary_container)
    LinearLayout order_summary_container;
    @BindView(R.id.shipment_summary_container)
    LinearLayout shipment_summary_container;
    @BindView(R.id.btn_close)
    AppCompatButton btn_close;
    ShipmentSummaryItemAdapter shipmentSummaryItemAdapter;
    private RecyclerView mRecyclerView;
    private ImageButton screenshot;
    private ImageButton attchfile;
    private SimpleDraweeView screenimage;
    private SimpleDraweeView screenimage1;
    private SimpleDraweeView screenimage2;
    private Bitmap bitmap;
    private SharedPreferences sharedPreferences;
    private LinearLayout sellerattachmentContainer;
    private LinearLayout buyerattachmentContainer;
    private LinearLayout screenshotContainer;
    private AppCompatButton btn_finilize;
    private AppCompatButton btn_delete;
    private AppCompatButton btn_accept;
    private AppCompatButton btn_dispatch;
    private AppCompatButton btn_verify;
    private AppCompatButton btn_cancel;
    private AppCompatButton btn_pay;
    private TextView attachmentLabel;
    private TextView buyerattachmentLabel;
    private TextView datetex;
    private TextView notetex;
    private TextView company_name;
    private TextView selling_company_name;
    private TextView selling_company_name_value;

    @Override
    protected void onResume() {
        super.onResume();
        String id = null;


        Freshchat.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on
                if (unreadCount > 0) {
                    badgeTextView.setVisibility(View.VISIBLE);
                    badgeTextView.setText("" + unreadCount);
                } else {
                    badgeTextView.setVisibility(View.GONE);
                }
            }
        });

        if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
            id = ((Response_sellingorder) Application_Singleton.selectedOrder).getId();


            if (((Response_sellingorder) Application_Singleton.selectedOrder).getSales_image() != null) {
               // StaticFunctions.loadImage(this, ((Response_sellingorder) Application_Singleton.selectedOrder).getSales_image(), screenimage, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_sellingorder) Application_Singleton.selectedOrder).getSales_image(), screenimage);
            } else {
                sellerattachmentContainer.setVisibility(View.GONE);
            }

            if (((Response_sellingorder) Application_Singleton.selectedOrder).getPurchase_image() != null) {
               // StaticFunctions.loadImage(this, ((Response_sellingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_sellingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2);
            } else {
                buyerattachmentContainer.setVisibility(View.GONE);
            }

        }
        if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
            id = ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId();

            if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image() != null) {
               // StaticFunctions.loadImage(this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), screenimage, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), screenimage);
            } else {
                sellerattachmentContainer.setVisibility(View.GONE);
            }

            if (((Response_sellingorder_new) Application_Singleton.selectedOrder).getPurchase_image() != null) {
                //StaticFunctions.loadImage(this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2);
            } else {
                buyerattachmentContainer.setVisibility(View.GONE);
            }

        }
        if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
            id = ((Response_buyingorder) Application_Singleton.selectedOrder).getId();

            if (((Response_buyingorder) Application_Singleton.selectedOrder).getSales_image() != null) {
               // StaticFunctions.loadImage(this, ((Response_buyingorder) Application_Singleton.selectedOrder).getSales_image(), screenimage, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_buyingorder) Application_Singleton.selectedOrder).getSales_image(), screenimage);
            } else {
                sellerattachmentContainer.setVisibility(View.GONE);
            }

            if (((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image() != null) {
               // StaticFunctions.loadImage(this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2, R.drawable.uploadempty);
                StaticFunctions.loadFresco(this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2);
            } else {
                buyerattachmentContainer.setVisibility(View.GONE);
            }

        }
        if (id != null) {


            // String uriat = sharedPreferences.getString("attch" + id, "");
            /*if () {

                String uri = sharedPreferences.getString("attch" + id, "");
                File file = new File(uri);
                if (file.exists()) {
                    screenimage.setImageURI(Uri.fromFile(file));
                }


            } else {
                sellerattachmentContainer.setVisibility(View.GONE);
            }
*/
            String urisc = sharedPreferences.getString("screen" + id, "");
            if (!urisc.equals("")) {

                Uri uri = Uri.parse(urisc);
                screenimage1.setImageURI(uri);
            } else {
                screenshotContainer.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG", "onCreate: Activity Order Details Called");
        setContentView(R.layout.activity_order_details);

        ButterKnife.bind(this);

        badgeTextView = (TextView) findViewById(R.id.badge_textview);
        support_chat_fab = (FloatingActionButton) findViewById(R.id.support_chat_fab);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_OrderDetails.this, LinearLayoutManager.VERTICAL, false);
        items_shipped_recyclerview.setLayoutManager(layoutManager);
        items_shipped_recyclerview.setNestedScrollingEnabled(true);

        orderfetch();


    }

    private void orderfetch() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        screenshot = (ImageButton) findViewById(R.id.screenshot);
        attchfile = (ImageButton) findViewById(R.id.attchfile);
        btn_pay = (AppCompatButton) findViewById(R.id.btn_pay);
        btn_pay.setVisibility(View.GONE);
        btn_finilize = (AppCompatButton) findViewById(R.id.btn_finilize);
        datetex = (TextView) findViewById(R.id.datetex);
        btn_finilize.setVisibility(View.GONE);
        btn_delete = (AppCompatButton) findViewById(R.id.btn_delete);
        btn_delete.setVisibility(View.GONE);
        btn_accept = (AppCompatButton) findViewById(R.id.btn_accept);
        btn_accept.setVisibility(View.GONE);
        btn_dispatch = (AppCompatButton) findViewById(R.id.btn_dispatch);
        btn_dispatch.setVisibility(View.GONE);
        btn_verify = (AppCompatButton) findViewById(R.id.btn_verify);
        btn_verify.setVisibility(View.GONE);
        btn_cancel = (AppCompatButton) findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        notetex = (TextView) findViewById(R.id.notetex);

        company_name = (TextView) findViewById(R.id.company_name);

        selling_company_name = (TextView) findViewById(R.id.supplier_company_name);
        selling_company_name_value = (TextView) findViewById(R.id.supplier_company_name_value);


        screenimage = (SimpleDraweeView) findViewById(R.id.screenimage);
        attachmentLabel = (TextView) findViewById(R.id.attachmentLabel);
        buyerattachmentLabel = (TextView) findViewById(R.id.buyerattachmentLabel);


        sellerattachmentContainer = (LinearLayout) findViewById(R.id.sellerattachmentContainer);
        buyerattachmentContainer = (LinearLayout) findViewById(R.id.buyerattachmentContainer);
        screenshotContainer = (LinearLayout) findViewById(R.id.screenshotContainer);
        screenimage1 = (SimpleDraweeView) findViewById(R.id.screenimage1);
        screenimage2 = (SimpleDraweeView) findViewById(R.id.screenimage2);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_OrderDetails.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        if (Application_Singleton.selectedOrder != null) {
            if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
                Log.i("TAG", "orderfetch: Configure Selling Order Called");
                attachmentLabel.setText("Seller Attachment");
                configSellingOrder();
            } else if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
                Log.i("TAG", "orderfetch: Configure Selling Order Called");
                attachmentLabel.setText("Seller Attachment");
                configSellingOrder();
            } else {
                Log.i("TAG", "orderfetch: Configure Purchase Order Called");
                buyerattachmentLabel.setText("Buyer Attachment");
                configPurchaseOrder();
            }
        }
    }


    private void configPurchaseOrder() {
        final Response_buyingorder selectedOrder1 = (Response_buyingorder) Application_Singleton.selectedOrder;
        (findViewById(R.id.broker)).setVisibility(View.GONE);
        (findViewById(R.id.broker1)).setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
        HttpManager.getInstance(Activity_OrderDetails.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorders_catalogwise", selectedOrder1.getId()), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Log.v("res", "" + response);
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                Application_Singleton.selectedOrder = selectedOrder;
                ((TextView) findViewById(R.id.ordernum)).setText(selectedOrder.getOrder_number());
                ((TextView) findViewById(R.id.companyname)).setText(StringUtils.capitalize(selectedOrder.getSeller_company_name().toLowerCase().trim()));
                ((TextView) findViewById(R.id.date)).setText(getformatedDate(selectedOrder.getDate()));
                /*((TextView) findViewById(R.id.notetex)).setText(selectedOrder.getBuyer_cancel());
                if(((TextView) findViewById(R.id.notetex)).getText().equals("")){
                    ((LinearLayout) findViewById(R.id.notecon)).setVisibility(View.GONE);
                }*/
                ((TextView) findViewById(R.id.totalproducts)).setText(selectedOrder.getTotal_products() + " Pcs");
                ((TextView) findViewById(R.id.totalprice)).setText("\u20B9" + selectedOrder.getTotal_rate());
                ((TextView) findViewById(R.id.processstaus)).setText(StringUtils.capitalize(selectedOrder.getProcessing_status().toLowerCase().trim()));
                ((TextView) findViewById(R.id.buyerstaus)).setText(StringUtils.capitalize(selectedOrder.getCustomer_status().toLowerCase().trim()));

                if (selectedOrder.getIs_supplier_approved() != null && !selectedOrder.getIs_supplier_approved()) {
                    ((TextView) findViewById(R.id.supplier_approved_note)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.supplier_approved_note)).setText(StringUtils.capitalize("Note : This seller hasn't approved you as a buyer yet. Please pay for this order to get it processed."));
                } else {
                    ((TextView) findViewById(R.id.supplier_approved_note)).setVisibility(View.GONE);
                }

                company_name.setText("Supplier");

                if (!UserInfo.getInstance(Activity_OrderDetails.this).equals("2")) {
                    if (selectedOrder.getBuyer_table_id() != null) {
                        ((TextView) findViewById(R.id.companyname)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //hyperlinking
                                StaticFunctions.hyperLinking("supplier", selectedOrder.getBuyer_table_id(), Activity_OrderDetails.this);

                            }
                        });
                    } else {
                        ((TextView) findViewById(R.id.companyname)).setTextColor(getResources().getColor(R.color.black));
                    }
                } else {
                    ((TextView) findViewById(R.id.companyname)).setTextColor(getResources().getColor(R.color.black));
                }

                screenimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (selectedOrder.getSales_image() != null) {
                            showimage(selectedOrder.getSales_image());
                        }

                    }
                });
                screenimage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedOrder.getPurchase_image() != null) {
                            showimage(selectedOrder.getPurchase_image());
                        }
                    }
                });


                if (selectedOrder.getSales_image() != null) {
                    //StaticFunctions.loadImage(Activity_OrderDetails.this, selectedOrder.getSales_image(), screenimage, R.drawable.uploadempty);
                    StaticFunctions.loadFresco(Activity_OrderDetails.this, selectedOrder.getSales_image(), screenimage);
                } else {
                    sellerattachmentContainer.setVisibility(View.GONE);
                }

                if (selectedOrder.getPurchase_image() != null) {
                    //StaticFunctions.loadImage(Activity_OrderDetails.this, selectedOrder.getPurchase_image(), screenimage2, R.drawable.uploadempty);
                    StaticFunctions.loadFresco(Activity_OrderDetails.this, selectedOrder.getPurchase_image(), screenimage2);
                } else {
                    buyerattachmentContainer.setVisibility(View.GONE);
                }

                if (selectedOrder.getCustomer_status().equals("Draft")) {
                    btn_finilize.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                } else {
                    btn_finilize.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.GONE);

                }

                if (!(selectedOrder.getProcessing_status().equals("Cancelled") || selectedOrder.getCustomer_status().equals("Cancelled") || selectedOrder.getCustomer_status().equals("Draft"))) {

                }

                if (selectedOrder.getProcessing_status().equals("Pending") || selectedOrder.getProcessing_status().equals("Accepted")) {
                    if (selectedOrder.getInvoice() != null && selectedOrder.getInvoice().size() < 1) {
                        if (!(selectedOrder.getCustomer_status().equals("Paid") || selectedOrder.getCustomer_status().equals("Payment Confirmed") )) {
                            btn_pay.setVisibility(View.VISIBLE);
                        } else {
                            btn_pay.setVisibility(View.GONE);

                        }


                        btn_cancel.setVisibility(View.VISIBLE);
                    }


                } else {
                    btn_pay.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.GONE);
                }


                if (selectedOrder.getProcessing_status().equals("In Progress")) {
                    btn_cancel.setVisibility(View.GONE);
                    btn_pay.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.processstaus)).setText("Partially Dispatched");
                }

                if (selectedOrder.getProcessing_status().equals("Cancelled") && selectedOrder.getProcessing_status().equals("Cancelled")) {
                    btn_cancel.setVisibility(View.GONE);
                    btn_pay.setVisibility(View.GONE);
                }

               /* if (selectedOrder.getPayment_details() != null && !selectedOrder.getPayment_details().equals("null") && !selectedOrder.getPayment_details().equals("")) {
                    if (selectedOrder.getPayment_date() != null && !selectedOrder.getPayment_date().equals("null") && !selectedOrder.getPayment_date().equals("")) {
                        notetex.setText(StringUtils.capitalize(selectedOrder.getPayment_details().toString().trim() + "\nDate : " + getformatedDate(selectedOrder.getPayment_date())));
                    } else {
                        notetex.setText(StringUtils.capitalize(selectedOrder.getPayment_details().toString().trim()));
                    }
                }*/

                if (selectedOrder.getSupplier_cancel() != null && !selectedOrder.getSupplier_cancel().equals("") && !selectedOrder.getSupplier_cancel().equals("null")) {
                    datetex.setVisibility(View.VISIBLE);
                    datetex.setText(StringUtils.capitalize(selectedOrder.getSupplier_cancel().toString().trim()));
                }
                /*if (selectedOrder.getBuyer_cancel() != null && !selectedOrder.getBuyer_cancel().equals("") && !selectedOrder.getBuyer_cancel().equals("null")) {
                    notetex.setText(StringUtils.capitalize(selectedOrder.getBuyer_cancel().trim()));
                }*/
                if (selectedOrder.getTracking_details() != null && !selectedOrder.getTracking_details().equals("null") && !selectedOrder.getTracking_details().equals("")) {
                    if (selectedOrder.getDispatch_date() != null && !selectedOrder.getDispatch_date().equals("null") && !selectedOrder.getDispatch_date().equals("")) {
                        datetex.setVisibility(View.VISIBLE);
                        datetex.setText(StringUtils.capitalize(selectedOrder.getTracking_details().trim() + "\nDate : " + getformatedDate(selectedOrder.getDispatch_date())));
                    } else {
                        datetex.setVisibility(View.VISIBLE);
                        datetex.setText(StringUtils.capitalize(selectedOrder.getTracking_details().trim()));
                    }
                }

                //configuring Shipment In sales order
                ArrayList<Invoice> invoices = new ArrayList<>();
                if (selectedOrder.getInvoice() != null) {
                    if (selectedOrder.getInvoice().size() > 0) {
                        for (Invoice invoice : selectedOrder.getInvoice()) {
                            //if ((invoice.getShipments() != null && invoice.getShipments().length > 0) || (invoice.getPayments() != null && invoice.getPayments().length > 0) ) {
                            invoices.add(invoice);
                            // }

                        }

                        //adding to adapter
                        if (invoices.size() > 0) {
                            shipment_total_value.setText(selectedOrder.getTotal_rate());
                            shipment_total_quantity_ordered.setText(selectedOrder.getTotal_products());
                            shipment_order_status.setText(selectedOrder.getProcessing_status());
                            shipment_payment_status.setText(selectedOrder.getCustomer_status());
                            shipment_summary_container.setVisibility(View.VISIBLE);
                            shipmentSummaryItemAdapter = new ShipmentSummaryItemAdapter(Activity_OrderDetails.this, invoices, "purchase");
                            items_shipped_recyclerview.setAdapter(shipmentSummaryItemAdapter);
                            items_shipped_recyclerview.addItemDecoration(new DividerItemDecoration(Activity_OrderDetails.this, null));
                        } else {
                            shipment_summary_container.setVisibility(View.GONE);
                        }
                    }
                }

                if(selectedOrder.getInvoice().size() > 0){
                    float total_invoice_sum = 0;
                    for (Invoice invoice:
                            selectedOrder.getInvoice()) {
                        total_invoice_sum += Float.parseFloat(invoice.getAmount());
                        Log.i("TAG", "onTotal ==>: "+total_invoice_sum);
                    }
                    if(total_invoice_sum == Float.parseFloat(selectedOrder.getTotal_rate())){
                        Log.i("TAG", "onTotal Invoice Rate: if");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } else {
                        Log.i("TAG", "onTotal Invoice Rate: Else");
                    }

                }

                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
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
                        HttpManager.getInstance(Activity_OrderDetails.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "create_invoice", ""), (gson1.fromJson(gson1.toJson(orderInvoiceCreate), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Invoice invoice = Application_Singleton.gson.fromJson(response, Invoice.class);
                                if (invoice != null)
                                    paymentOptionsWithInvoice("invoice", invoice.getId(), invoice.getTotal_amount(), invoice.getAmount(), invoice.getTaxes());
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });


                        //paymentOptions("order",selectedOrder.getId(),selectedOrder.getTotal_rate());
                    }
                });

                btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchAccept patchAccept = new PatchAccept(selectedOrder.getId(), "Accepted");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.buyerstaus)).setText("Accepted");
                                btn_accept.setVisibility(View.GONE);
                                orderfetch();
                                setResult(Activity.RESULT_OK);

                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });
                btn_finilize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchFinalizeOrder patchFinalizeOrder = new PatchFinalizeOrder(selectedOrder.getId(), "Placed");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchFinalizeOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                orderfetch();
                                btn_finilize.setVisibility(View.GONE);
                                btn_delete.setVisibility(View.GONE);
                                btn_pay.setVisibility(View.VISIBLE);
                                btn_cancel.setVisibility(View.VISIBLE);
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        HttpManager.getInstance(Activity_OrderDetails.this).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorder", "") + selectedOrder.getId() + '/', null, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                setResult(Activity.RESULT_OK);
                                finish();
                                btn_delete.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                    }
                });
                btn_verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchVerifyOrder patchVerifyOrder = new PatchVerifyOrder(selectedOrder.getId(), "Payment Confirmed");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchVerifyOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.buyerstaus)).setText("Payment Confirmed");
                                orderfetch();
                                btn_verify.setVisibility(View.GONE);
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        cancelOrder("purchaseorder", selectedOrder.getId());

                    }
                });
                List<CatalogListItem> catalogListItem = new ArrayList<>();
                for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
                    catalogListItem.add(new CatalogListItem(selectedOrder.getCatalogs().get(i).getName().toString(), selectedOrder.getCatalogs().get(i).getBrand().toString(), selectedOrder.getCatalogs().get(i).getTotal_products().toString(), selectedOrder.getCatalogs().get(i).getProducts()));
                }

                expandable_adapter adapter = new expandable_adapter(Activity_OrderDetails.this, catalogListItem);

                // ProductItemAdapter productItemAdapter = new ProductItemAdapter(Activity_OrderDetails.this, selectedOrder.getItems());
                mRecyclerView.setAdapter(adapter);
                screenshot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeScreenshot();
                       /* Intent intent = new Intent(Activity_OrderDetails.this, WebPrintActivity.class);
                        startActivity(intent);*/
                    }
                });

                attchfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        // intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), 1);
                    }
                });
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });


    }

    public void paymentOptions(final String type, final String id, final String total) {
        try {
        int array;
        if (Float.parseFloat(total) > AMOUNT_LIMIT_PAYMENT_OPTION) {

            array = R.array.paymentchoice_option2;
            new MaterialDialog.Builder(Activity_OrderDetails.this)
                    .title(R.string.paymentoption)
                    .items(array)
                    .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    Intent intentToZaakpay = new Intent(Activity_OrderDetails.this, ZaakPayMerchantActivity.class);
                                    intentToZaakpay.putExtra("orderid", id);
                                    intentToZaakpay.putExtra("order_amount", total);
                                    startActivityForResult(intentToZaakpay, 3);
                                    break;
                                case 1:
                                    payVia(type, text.toString(), total, id);
                                    break;
                                case 2:
                                    payVia(type, text.toString(), total, id);
                                    break;
                                case 3:
                                    payVia(type, text.toString(), total, id);
                                    break;
                            }

                            return true;
                        }
                    })
                    .positiveText("Ok")
                    .show();
        } else {
            array = R.array.paymentchoice;
            new MaterialDialog.Builder(Activity_OrderDetails.this)
                    .title(R.string.paymentoption)
                    .items(array)
                    .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    Intent intentToZaakpay = new Intent(Activity_OrderDetails.this, ZaakPayMerchantActivity.class);
                                    intentToZaakpay.putExtra("orderid", id);
                                    intentToZaakpay.putExtra("order_amount", total);
                                    startActivityForResult(intentToZaakpay, 3);
                                    break;
                                case 1:
                                    Intent i = new Intent(Activity_OrderDetails.this, MerchantActivity2.class);
                                    i.putExtra("orderid", id);
                                    i.putExtra("order_amount", total);
                                    startActivityForResult(i, 2);
                                    break;
                                case 2:
                                    Intent intentToMobikwik = new Intent(Activity_OrderDetails.this, MobiKwikMerchantActivity.class);
                                    intentToMobikwik.putExtra("orderid", id);
                                    intentToMobikwik.putExtra("order_amount", total);
                                    startActivityForResult(intentToMobikwik, 3);
                                    break;
                                case 3:
                                    payVia(type, text.toString(), total, id);
                                    break;
                                case 4:
                                    payVia(type, text.toString(), total, id);
                                    break;
                                case 5:
                                    payVia(type, text.toString(), total, id);
                                    break;
                            }

                            return true;
                        }
                    })
                    .positiveText("Ok")
                    .show();
        }
        } catch (Exception e) {
            Toast.makeText(Activity_OrderDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    public void paymentOptionsWithInvoice(final String type, final String id, final String total, final String amnt, final String taxes) {

        try {

            String popup_content = "Amount : " + amnt + "\n"
                    + "GST : " + taxes + "\n"
                    + "Total Amount : " + total + "\n";
            new MaterialDialog.Builder(Activity_OrderDetails.this).title("Invoice Details")
                    .content(popup_content)
                    .positiveText("Choose Payment Method")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            paymentOptions(type, id, total);
                        }
                    })
                    .show();
        } catch (Exception e) {
            Toast.makeText(Activity_OrderDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void payVia(String type, final String mode, String amount, final String orderID) {
        Fragment_Pay payFragment = new Fragment_Pay();
        final String url;
        if (type.equals("invoice")) {
            url = URLConstants.companyUrl(Activity_OrderDetails.this, "invoice_payment", orderID);
        } else {
            url = URLConstants.companyUrl(Activity_OrderDetails.this, "payment", orderID);
        }


        if (amount != null) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            payFragment.setArguments(bundle);
        }
        payFragment.setListener(new Fragment_Pay.Listener() {
            @Override
            public void onDismiss(String date, String details, String amount) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("mode", mode);
                params.put("details", details);
                params.put("date", date);
                params.put("amount", amount);
                HttpManager.getInstance(Activity_OrderDetails.this).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        ((TextView) findViewById(R.id.buyerstaus)).setText("Paid");
                        orderfetch();
                        btn_pay.setVisibility(View.GONE);
                        setResult(Activity.RESULT_OK);
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                        //  progressDialog.dismiss();
                    }
                });
            }
        });
        payFragment.show(getSupportFragmentManager(), "pay");
    }

    private void configSellingOrder() {

        String id = "";
        if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
            final Response_sellingorder selectedOrder1 = (Response_sellingorder) Application_Singleton.selectedOrder;
            id = selectedOrder1.getId();
        }

        if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
            final Response_sellingorder_new selectedOrder1 = (Response_sellingorder_new) Application_Singleton.selectedOrder;
            id = selectedOrder1.getId();
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
        HttpManager.getInstance(Activity_OrderDetails.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorders_catalogwise", id), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Log.v("res", "" + response);
                Gson gson = new Gson();
                final Response_sellingorder_new selectedOrder = gson.fromJson(response, Response_sellingorder_new.class);
                Application_Singleton.selectedOrder = selectedOrder;
                ((TextView) findViewById(R.id.ordernum)).setText(selectedOrder.getOrder_number());

                // change for bug #WB-850
//                if (selectedOrder.getCompany_name() != null)
//                    ((TextView) findViewById(R.id.companyname)).setText(StringUtils.capitalize(selectedOrder.getCompany_name().toLowerCase().trim()));

                if(selectedOrder.getBuying_company_name() != null)
                    ((TextView) findViewById(R.id.companyname)).setText(StringUtils.capitalize(selectedOrder.getBuying_company_name().toLowerCase().trim()));

                ((TextView) findViewById(R.id.date)).setText(getformatedDate(selectedOrder.getDate()));

                //added supplier showing
                selling_company_name.setVisibility(View.VISIBLE);
                selling_company_name_value.setVisibility(View.VISIBLE);
                selling_company_name_value.setText(StringUtils.capitalize(selectedOrder.getSeller_company_name()));


                if (selectedOrder.getBroker_company_name() != null) {
                    (findViewById(R.id.broker)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.broker1)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.brokername)).setText(selectedOrder.getBroker_company_name());
                } else {
                    (findViewById(R.id.broker)).setVisibility(View.GONE);
                    (findViewById(R.id.broker1)).setVisibility(View.GONE);
                }

                if (selectedOrder.getSales_image() != null) {
                    sellerattachmentContainer.setVisibility(View.VISIBLE);
                    //StaticFunctions.loadImage(Activity_OrderDetails.this, selectedOrder.getSales_image(), screenimage, R.drawable.uploadempty);
                    StaticFunctions.loadFresco(Activity_OrderDetails.this, selectedOrder.getSales_image(), screenimage);
                } else {
                    sellerattachmentContainer.setVisibility(View.GONE);
                }

                if (selectedOrder.getPurchase_image() != null) {
                    buyerattachmentContainer.setVisibility(View.VISIBLE);
                    //StaticFunctions.loadImage(Activity_OrderDetails.this, selectedOrder.getPurchase_image(), screenimage2, R.drawable.uploadempty);
                    StaticFunctions.loadFresco(Activity_OrderDetails.this, selectedOrder.getPurchase_image(), screenimage2);
                } else {
                    buyerattachmentContainer.setVisibility(View.GONE);
                }

                screenimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (selectedOrder.getSales_image() != null) {
                            showimage(selectedOrder.getSales_image());
                        }

                    }
                });
                screenimage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedOrder.getPurchase_image() != null) {
                            showimage(selectedOrder.getPurchase_image());
                        }
                    }
                });
              /*  ((TextView) findViewById(R.id.notetex)).setText(selectedOrder.getSupplier_cancel());
                if(((TextView) findViewById(R.id.notetex)).getText().equals("")){
                    ((LinearLayout) findViewById(R.id.notecon)).setVisibility(View.GONE);
                }*/
                ((TextView) findViewById(R.id.totalproducts)).setText(selectedOrder.getTotal_products() + " Pcs");
                ((TextView) findViewById(R.id.totalprice)).setText("\u20B9" + selectedOrder.getTotal_rate());


                ((TextView) findViewById(R.id.processstaus)).setText(StringUtils.capitalize(selectedOrder.getProcessing_status().toLowerCase().trim()));
                ((TextView) findViewById(R.id.buyerstaus)).setText(StringUtils.capitalize(selectedOrder.getCustomer_status().toLowerCase().trim()));


                //SPANNABLE STRING
                SpannableString ss = new SpannableString("Note : This order is not from your approved buyer. We recommend you ask this buyer (" + selectedOrder.getCompany_number() + ") for pre-payment before processing this order");
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + selectedOrder.getCompany_number()));
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(true);
                    }
                };
                ss.setSpan(clickableSpan, 84, 97, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (selectedOrder.getIs_supplier_approved() != null && !selectedOrder.getIs_supplier_approved()) {
                    ((TextView) findViewById(R.id.supplier_approved_note)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.supplier_approved_note)).setMovementMethod(LinkMovementMethod.getInstance());
                    ((TextView) findViewById(R.id.supplier_approved_note)).setClickable(true);
                    ((TextView) findViewById(R.id.supplier_approved_note)).setText(ss);
                } else {
                    ((TextView) findViewById(R.id.supplier_approved_note)).setVisibility(View.GONE);
                }

                company_name.setText("Buyer");

                if (selectedOrder.getBuyer_table_id() != null) {
                    ((TextView) findViewById(R.id.companyname)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //hyperlinking
                            StaticFunctions.hyperLinking("buyer", selectedOrder.getBuyer_table_id(), Activity_OrderDetails.this);
                        }
                    });
                } else {
                    ((TextView) findViewById(R.id.companyname)).setTextColor(getResources().getColor(R.color.black));
                }


                if (UserInfo.getInstance(Activity_OrderDetails.this).getGroupstatus().equals("2") && selectedOrder.getCustomer_status().equals("Draft")) {
                    btn_finilize.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                } else {
                    btn_finilize.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.GONE);
                }

                if (UserInfo.getInstance(Activity_OrderDetails.this).getGroupstatus().equals("1")) {
                    if (selectedOrder.getProcessing_status().equals("Pending")) {
                        btn_accept.setVisibility(View.VISIBLE);
                    }
                    if (selectedOrder.getProcessing_status().equals("Accepted")) {
                        btn_accept.setVisibility(View.GONE);
                        if (selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() < 1) {
                            btn_dispatch.setVisibility(View.VISIBLE);
                        }
                    } else {
                        btn_dispatch.setVisibility(View.GONE);
                    }
                    if (selectedOrder.getCustomer_status().equals("Paid")) {
                        // btn_verify.setVisibility(View.VISIBLE);
                    }
                    if ((selectedOrder.getInvoices() != null && selectedOrder.getInvoices().size() < 1) && !(selectedOrder.getCustomer_status().equals("Cancelled") || selectedOrder.getProcessing_status().equals("Cancelled") || selectedOrder.getProcessing_status().equals("Delivered") || selectedOrder.getProcessing_status().equals("Dispatched"))) {
                        btn_cancel.setVisibility(View.VISIBLE);
                    }
                }


                if (selectedOrder.getProcessing_status().equals("In Progress")) {
                    btn_cancel.setVisibility(View.GONE);
                    btn_accept.setVisibility(View.GONE);
                    btn_dispatch.setVisibility(View.GONE);
                    btn_verify.setVisibility(View.GONE);
                    // in_progress_note.setText("Note: You need to process this order from Web Application");
                    btn_close.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.processstaus)).setText("Partially Dispatched");
                } else {
                    btn_close.setVisibility(View.GONE);
                }

                if (selectedOrder.getProcessing_status().equals("Cancelled") && selectedOrder.getProcessing_status().equals("Cancelled")) {
                    btn_cancel.setVisibility(View.GONE);
                    btn_accept.setVisibility(View.GONE);
                    btn_dispatch.setVisibility(View.GONE);
                    btn_verify.setVisibility(View.GONE);
                }

                /*if (selectedOrder.getPayment_details() != null && !selectedOrder.getPayment_details().equals("null") && !selectedOrder.getPayment_details().equals("")) {
                    if (selectedOrder.getPayment_date() != null && !selectedOrder.getPayment_date().equals("null") && !selectedOrder.getPayment_date().equals("")) {
                        notetex.setText(StringUtils.capitalize(selectedOrder.getPayment_details().toString().trim() + "\nDate : " + getformatedDate(selectedOrder.getPayment_date())));
                    } else {
                        notetex.setText(StringUtils.capitalize(selectedOrder.getPayment_details().toString().trim()));
                    }
                }*/
                if (selectedOrder.getSupplier_cancel() != null && !selectedOrder.getSupplier_cancel().equals("") && !selectedOrder.getSupplier_cancel().equals("null")) {
                    datetex.setVisibility(View.VISIBLE);
                    datetex.setText(StringUtils.capitalize(selectedOrder.getSupplier_cancel().toString().trim()));
                }



                /*if (selectedOrder.getBuyer_cancel() != null && !selectedOrder.getBuyer_cancel().equals("") && !selectedOrder.getBuyer_cancel().equals("null")) {
                    notetex.setText(StringUtils.capitalize(selectedOrder.getBuyer_cancel().trim()));
                }*/
                if (selectedOrder.getTracking_details() != null && !selectedOrder.getTracking_details().equals("null") && !selectedOrder.getTracking_details().equals("")) {
                    if (selectedOrder.getDispatch_date() != null && !selectedOrder.getDispatch_date().equals("null") && !selectedOrder.getDispatch_date().equals("")) {
                        datetex.setVisibility(View.VISIBLE);
                        datetex.setText(StringUtils.capitalize(selectedOrder.getTracking_details().trim() + "\nDate : " + getformatedDate(selectedOrder.getDispatch_date())));
                    } else {
                        datetex.setVisibility(View.VISIBLE);
                        datetex.setText(StringUtils.capitalize(selectedOrder.getTracking_details().trim()));
                    }
                }


                //configuring Shipment In sales order
                ArrayList<Invoice> invoices = new ArrayList<>();
                if (selectedOrder.getInvoices() != null) {
                    if (selectedOrder.getInvoices().size() > 0) {
                        for (Invoice invoice : selectedOrder.getInvoices()) {
                            //  if ((invoice.getShipments() != null && invoice.getShipments().length > 0) || (invoice.getPayments() != null && invoice.getPayments().length > 0) ) {
                            invoices.add(invoice);
                            //  }
                        }

                        //adding to adapter
                        if (invoices.size() > 0) {
                            shipment_total_value.setText(selectedOrder.getTotal_rate());
                            shipment_total_quantity_ordered.setText(selectedOrder.getTotal_products());
                            shipment_order_status.setText(selectedOrder.getProcessing_status());
                            shipment_payment_status.setText(selectedOrder.getCustomer_status());
                            shipment_summary_container.setVisibility(View.VISIBLE);
                            shipmentSummaryItemAdapter = new ShipmentSummaryItemAdapter(Activity_OrderDetails.this, invoices, "sales");
                            items_shipped_recyclerview.setAdapter(shipmentSummaryItemAdapter);
                            items_shipped_recyclerview.addItemDecoration(new DividerItemDecoration(Activity_OrderDetails.this, null));
                        } else {
                            shipment_summary_container.setVisibility(View.GONE);
                        }
                    }
                }

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchAccept patchAccept = new PatchAccept(selectedOrder.getId(), "Closed");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.processstaus)).setText("Closed");
                                btn_cancel.setVisibility(View.GONE);
                                btn_accept.setVisibility(View.GONE);
                                btn_dispatch.setVisibility(View.GONE);
                                orderfetch();
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });

                btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchAccept patchAccept = new PatchAccept(selectedOrder.getId(), "Accepted");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.processstaus)).setText("Accepted");
                                btn_accept.setVisibility(View.GONE);
                                orderfetch();
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });
                btn_finilize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchFinalizeOrder patchFinalizeOrder = new PatchFinalizeOrder(selectedOrder.getId(), "Placed");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchFinalizeOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                orderfetch();
                                btn_finilize.setVisibility(View.GONE);
                                btn_delete.setVisibility(View.GONE);
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        HttpManager.getInstance(Activity_OrderDetails.this).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + selectedOrder.getId() + '/', null, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                setResult(Activity.RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                    }
                });
                btn_verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        PatchVerifyOrder patchVerifyOrder = new PatchVerifyOrder(selectedOrder.getId(), "Payment Confirmed");
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + selectedOrder.getId() + '/', gson.fromJson(gson.toJson(patchVerifyOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.processstaus)).setText("Payment Confirmed");
                                orderfetch();
                                btn_verify.setVisibility(View.GONE);
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                });
                btn_dispatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchOrder(selectedOrder.getId(), "order");

                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder("salesorder", selectedOrder.getId());
                    }
                });
                List<CatalogListItem> catalogListItem = new ArrayList<>();
                for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
                    catalogListItem.add(new CatalogListItem(selectedOrder.getCatalogs().get(i).getName(), selectedOrder.getCatalogs().get(i).getBrand(), selectedOrder.getCatalogs().get(i).getTotal_products(), selectedOrder.getCatalogs().get(i).getProducts()));
                }

                expandable_adapter adapter = new expandable_adapter(Activity_OrderDetails.this, catalogListItem);
                // ProductItemAdapter productItemAdapter = new ProductItemAdapter(Activity_OrderDetails.this, selectedOrder.getItems());
                mRecyclerView.setAdapter(adapter);
                screenshot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeScreenshot();
                    }
                });

                attchfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        // intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), 1);
                    }
                });
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    public void cancelOrder(final String type, final String id) {
        new MaterialDialog.Builder(Activity_OrderDetails.this)
                .title("Cancel")
                .content("Order cancel note")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                        Gson gson = new Gson();
                        if (input.toString().equals("")) {
                            Toast.makeText(Activity_OrderDetails.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Cancelled", "Cancelled", "" + input.toString());
                        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                ((TextView) findViewById(R.id.processstaus)).setText("Cancelled");
                                btn_cancel.setVisibility(View.GONE);
                                btn_accept.setVisibility(View.GONE);
                                btn_pay.setVisibility(View.GONE);
                                btn_dispatch.setVisibility(View.GONE);
                                btn_verify.setVisibility(View.GONE);
                                orderfetch();
                                setResult(Activity.RESULT_OK);
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                                //  progressDialog.dismiss();
                            }
                        });
                    }
                }).show();
    }

    public void cancelInvoice(final String type, final String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
        Gson gson = new Gson();
        PatchCancelInvoice patchCancelOrder = new PatchCancelInvoice("Cancelled");
        HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(Activity_OrderDetails.this, type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ((TextView) findViewById(R.id.processstaus)).setText("Cancelled");
                btn_cancel.setVisibility(View.GONE);
                btn_accept.setVisibility(View.GONE);
                btn_pay.setVisibility(View.GONE);
                btn_dispatch.setVisibility(View.GONE);
                btn_verify.setVisibility(View.GONE);
                orderfetch();
                setResult(Activity.RESULT_OK);
                // progressDialog.dismiss();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
                //  progressDialog.dismiss();
            }
        });
    }

    public void dispatchOrder(final String id, final String type) {
        final String url;
        if (type.equals("invoice")) {
            url = URLConstants.companyUrl(Activity_OrderDetails.this, "invoice_dispatch", id);
        } else {
            url = URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "") + id + '/';
        }


        Fragment_Dispatch dispatchFragment = new Fragment_Dispatch();
        dispatchFragment.setListener(new Fragment_Dispatch.Listener() {
            @Override
            public void onDismiss(String date, String mode, String transporter, String tracking_number) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
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
                    HttpManager.getInstance(Activity_OrderDetails.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, url, gson.fromJson(gson.toJson(patchDispatch), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            ((TextView) findViewById(R.id.processstaus)).setText("Dispatched");
                            btn_dispatch.setVisibility(View.GONE);
                            orderfetch();
                            btn_cancel.setVisibility(View.GONE);
                            setResult(Activity.RESULT_OK);
                            // progressDialog.dismiss();
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                            //  progressDialog.dismiss();
                        }
                    });
                } else {
                    //Patch
                    HttpManager.getInstance(Activity_OrderDetails.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, gson.fromJson(gson.toJson(patchDispatch), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            ((TextView) findViewById(R.id.processstaus)).setText("Dispatched");
                            btn_dispatch.setVisibility(View.GONE);
                            orderfetch();
                            btn_cancel.setVisibility(View.GONE);
                            setResult(Activity.RESULT_OK);
                            // progressDialog.dismiss();
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                            //  progressDialog.dismiss();
                        }
                    });
                }
            }
        });
        dispatchFragment.show(getSupportFragmentManager(), "dispatch");
    }

    public void showImage(Uri imagepath) {
        Dialog mSplashDialog = new Dialog(Activity_OrderDetails.this, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        ImageView image = (ImageView) mSplashDialog.findViewById(R.id.myimg);
        image.setImageURI(imagepath);
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File imageFile = null;
        File outputrenamed = null;
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Bundle inResponse = data.getBundleExtra("response");
            String response = null;
            String status = data.getStringExtra("transaction");
            if (inResponse != null) {
                String message = data.getStringExtra("message");
                if (message != null) {
                    response = "Message : " + message + "\n" +
                            "OrderID : " + inResponse.getString("ORDERID") + "\n"
                            + "Status : " + status + "\n"
                            + "Amount : " + inResponse.getString("TXNAMOUNT") + "\n";

                } else {
                    ((TextView) findViewById(R.id.buyerstaus)).setText("Paid");
                    orderfetch();
                    btn_pay.setVisibility(View.GONE);

                    response = "OrderID : " + inResponse.getString("ORDERID") + "\n"
                            + "Status : " + status + "\n"
                            + "Amount : " + inResponse.getString("TXNAMOUNT") + "\n";
                }
            } else {
                response = data.getStringExtra("response");
            }
            if (status.equals("Failure")) {
                new MaterialDialog.Builder(Activity_OrderDetails.this).title("Transaction Failure ").content(response).positiveText("Ok").show();
            } else {
                new MaterialDialog.Builder(Activity_OrderDetails.this).title("Transaction Success").positiveText("Ok").content(response).show();

            }

        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            String response = null;
            String orderID = data.getStringExtra("mOrderID");
            String amount = data.getStringExtra("mOrderAmount");
            String status = data.getStringExtra("transaction");
            if (status != null) {
                response =
                        "OrderID : " + orderID + "\n"
                                + "Status : " + status + "\n"
                                + "Amount : " + amount + "\n";

                if (status.equals("Failure")) {
                    new MaterialDialog.Builder(Activity_OrderDetails.this).title("Transaction Failure ").content(response).positiveText("Ok").show();
                } else {
                    ((TextView) findViewById(R.id.buyerstaus)).setText("Paid");
                    orderfetch();
                    btn_pay.setVisibility(View.GONE);

                    new MaterialDialog.Builder(Activity_OrderDetails.this).title("Transaction Success").positiveText("Ok").content(response).show();

                }
            }
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)

        {
            try {

                String selectedImagePath = getAbsolutePath(data.getData());
                //
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                try {
                   /* File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/Wishbook/");
                    final File output = new File(mDirectory, "preview.jpg");
                    Log.v("selected image", output.getAbsolutePath());*/
                    // image naming and path  to include sd card  appending name you choose for file

                    // create bitmap screen capture


                    imageFile = new File(selectedImagePath);
                    // if(output.exists()) {
                       /* outputrenamed = new File(mDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                        try {
                            StaticFunctions.copy(output, outputrenamed);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                    String url = "";
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_OrderDetails.this);
                    HashMap<String, String> params = new HashMap<>();
                    String filename = null;
                    if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
                        url = URLConstants.companyUrl(Activity_OrderDetails.this, "salesorder", "");
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

                        HttpManager.getInstance(Activity_OrderDetails.this).requestwithFile(HttpManager.METHOD.PUTFILEWITHPROGRESS, url + (Application_Singleton.selectedOrder instanceof Response_sellingorder_new ? ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId() : ((Response_buyingorder) Application_Singleton.selectedOrder).getId()) + "/", params, headers, filename, "image/jpg", imageFile, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Gson gson = new Gson();
                                Response_sellingorder_new selectedOrder = gson.fromJson(response, Response_sellingorder_new.class);
                                Application_Singleton.selectedOrder = selectedOrder;
                                Toast.makeText(Activity_OrderDetails.this, "Attached Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("File", "Attached");
                               // StaticFunctions.loadImage(Activity_OrderDetails.this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), screenimage, R.drawable.uploadempty);
                                StaticFunctions.loadFresco(Activity_OrderDetails.this, ((Response_sellingorder_new) Application_Singleton.selectedOrder).getSales_image(), screenimage);
                                sellerattachmentContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {

                            }
                        });
                    }
                    if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
                        url = URLConstants.companyUrl(Activity_OrderDetails.this, "purchaseorder", "");
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
                        HttpManager.getInstance(Activity_OrderDetails.this).requestwithFile(HttpManager.METHOD.PUTFILEWITHPROGRESS, url + (Application_Singleton.selectedOrder instanceof Response_sellingorder_new ? ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId() : ((Response_buyingorder) Application_Singleton.selectedOrder).getId()) + "/", params, headers, filename, "image/jpg", imageFile, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Gson gson = new Gson();
                                final Response_buyingorder selectedOrder1 = gson.fromJson(response, Response_buyingorder.class);
                                Application_Singleton.selectedOrder = selectedOrder1;
                               // StaticFunctions.loadImage(Activity_OrderDetails.this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2, R.drawable.uploadempty);
                                StaticFunctions.loadFresco(Activity_OrderDetails.this, ((Response_buyingorder) Application_Singleton.selectedOrder).getPurchase_image(), screenimage2);
                                Toast.makeText(Activity_OrderDetails.this, "Attached Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("File", "Attached");
                                buyerattachmentContainer.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {

                            }
                        });
                    }


                    //     }

                /*    FileOutputStream outputStream = new FileOutputStream(output);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();*/
                   /* String id = null;
                    if(Application_Singleton.selectedOrder instanceof  Response_sellingorder)
                    {
                        id = ((Response_sellingorder) Application_Singleton.selectedOrder).getId();
                    }
                    if(Application_Singleton.selectedOrder instanceof  Response_sellingorder_new)
                    {
                        id = ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId();

                    }
                    if(Application_Singleton.selectedOrder instanceof Response_buyingorder)
                    {
                        id = ((Response_buyingorder) Application_Singleton.selectedOrder).getId();

                    }
                    if(id!=null) {
                        sharedPreferences.edit().putString("attch" + id, selectedImagePath).commit();
                    }*/


                    //Toast.makeText(Activity_OrderDetails.this, "ScreenShot Captured", Toast.LENGTH_SHORT).show();
                } catch (Throwable e) {
                    // Several error may come out with file handling or OOM
                    e.printStackTrace();
                }
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            setImage(imageFile);
            Toast.makeText(Activity_OrderDetails.this, "Screenshot Captured", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            Log.e("Exception", e.toString());
            e.printStackTrace();
        }
    }

    private void setImage(File imageFile) {
        String id = null;
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(Activity_OrderDetails.this, getApplicationContext().getPackageName() + ".provider", imageFile);
        } else {
            uri = Uri.fromFile(imageFile);
        }

        if (Application_Singleton.selectedOrder instanceof Response_sellingorder) {
            id = ((Response_sellingorder) Application_Singleton.selectedOrder).getId();
        }
        if (Application_Singleton.selectedOrder instanceof Response_sellingorder_new) {
            id = ((Response_sellingorder_new) Application_Singleton.selectedOrder).getId();

        }
        if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
            id = ((Response_buyingorder) Application_Singleton.selectedOrder).getId();

        }
        if (id != null) {
            sharedPreferences.edit().putString("screen" + id, uri.toString()).commit();
        }
        screenimage1.setImageURI(uri);
        screenshotContainer.setVisibility(View.VISIBLE);
    }

    private String getformatedDate(String dat) {

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showimage(String imagepath) {
        Dialog mSplashDialog = new Dialog(Activity_OrderDetails.this, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(Activity_OrderDetails.this, imagepath, image, R.drawable.uploadempty);
            StaticFunctions.loadFresco(Activity_OrderDetails.this, imagepath, image);

            //  Picasso.with(mContext).load(imagepath).into(image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    protected void onDestroy() {
        super.onDestroy();
        StaticFunctions.updateStatics(Activity_OrderDetails.this);
    }
}
