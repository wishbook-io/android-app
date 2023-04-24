package com.wishbook.catalog.home.orderNew.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchAccept;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelOrder;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.BuyerDiscountRequest;
import com.wishbook.catalog.commonmodels.responses.CashFreeTokenResponse;
import com.wishbook.catalog.commonmodels.responses.PaymentMethod;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditLines;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.orderNew.adapters.AddressAdapter;
import com.wishbook.catalog.home.orders.details.Fragment_Pay;
import com.wishbook.catalog.home.payment.MerchantActivity2;
import com.wishbook.catalog.home.payment.MobiKwikMerchantActivity;
import com.wishbook.catalog.home.payment.ZaakPayMerchantActivity;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_NOTIFY_URL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_PAYMENT_MODES;


public class Fragment_CashPayment extends GATrackedFragment  {

    private View view;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;


    @BindView(R.id.card_shipping_details)
    CardView card_shipping_details;
    @BindView(R.id.linear_shipping_details)
    LinearLayout linear_shipping_details;

    // invoice Detail
    @BindView(R.id.invoice_order_no)
    TextView txtInvoiceOrderNo;
    @BindView(R.id.invoice_supplier_name)
    TextView txtInvoiceSellerName;
    @BindView(R.id.invoice_order_date)
    TextView txtInvoiceOrderDate;
    @BindView(R.id.invoice_catalog_container)
    LinearLayout invoice_catalog_container;
    @BindView(R.id.txt_payable_amt)
    TextView txtPayableAmt;
    @BindView(R.id.relative_seller_discount)
    RelativeLayout relativeSellerDiscount;
    @BindView(R.id.txt_seller_discount)
    TextView txtSellerDiscount;
    @BindView(R.id.relative_tax_class_1)
    RelativeLayout relativeTaxClass1;
    @BindView(R.id.linear_shipping)
    LinearLayout linear_shipping;
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
    @BindView(R.id.discount_type)
    TextView discount_type;
    @BindView(R.id.txt_discount_percentage)
    TextView txt_discount_percentage;
    @BindView(R.id.invoice_note)
    TextView txtInvoiceNote;

    @BindView(R.id.relative_error_pincode)
    RelativeLayout relative_error_pincode;

    @BindView(R.id.linear_shipping_container)
    LinearLayout linear_shipping_container;

    @BindView(R.id.txt_shipping_amount)
    TextView txt_shipping_amount;


    @BindView(R.id.payment_radio_group)
    RadioGroup radioGroupPayment;
    @BindView(R.id.discount_note)
    TextView discount_note;

    @BindView(R.id.btn_payment)
    AppCompatButton btnPayment;
    @BindView(R.id.btn_payment_credit)
    AppCompatButton btnPaymentCredit;
    @BindView(R.id.btn_cancel_order)
    AppCompatButton buttonCancel;

    @BindView(R.id.recycler_address)
    RecyclerView recyclerAddress;


    @BindView(R.id.spinner_state)
    Spinner spinner_state;
    @BindView(R.id.spinner_city)
    Spinner spinner_city;


    @BindView(R.id.input_addline1)
    TextInputLayout inputAddLin1;
    @BindView(R.id.edit_addline1)
    EditText editAddLine1;
    @BindView(R.id.input_addline2)
    TextInputLayout inputAddLin2;
    @BindView(R.id.edit_addline2)
    EditText editAddLine2;
    @BindView(R.id.btn_save_address)
    AppCompatButton btnSaveAddress;
    @BindView(R.id.input_pincode)
    TextInputLayout inputPincode;
    @BindView(R.id.edit_pincode)
    EditText editPincode;
    @BindView(R.id.btn_add_address)
    AppCompatButton btnAddAddress;
    @BindView(R.id.linear_add_address)
    LinearLayout linearAddAddress;
    @BindView(R.id.btn_cancel_address)
    AppCompatButton btnCancelAddress;


    @BindView(R.id.buyer_address_container)
    RelativeLayout buyer_address_container;
    @BindView(R.id.recycler_buyeraddress)
    RecyclerView recycler_buyeraddress;
    @BindView(R.id.txt_broker_address)
    TextView txt_broker_address;

    @BindView(R.id.card_cancel_order)
    CardView card_cancel_order;


    @BindView(R.id.final_amount)
    TextView now_pay_amount;

    @BindView(R.id.linear_now_pay)
    LinearLayout linear_now_pay;

    @BindView(R.id.linear_invoice_wb_money)
    LinearLayout linear_invoice_wb_money;

    @BindView(R.id.invoice_wb_money)
    TextView invoice_wb_money;

    @BindView(R.id.relative_supplier_container)
    RelativeLayout relative_supplier_container;

    @BindView(R.id.linear_invoice_reward_point)
    LinearLayout linear_invoice_reward_point;

    @BindView(R.id.invoice_reward_point)
    TextView invoice_reward_point;


    String creditDiscount, cashDiscount;
    double seller_additional_discount;

    Invoice invoice;

    private Context mContext;
    private static final float AMOUNT_LIMIT_PAYMENT_OPTION = 10000;
    private Response_buyingorder order;
    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private String State = "";
    private String City = "";
    private String stateId = "";
    private String cityId = "";
    ShippingAddressResponse[] shippingAddressResponses;
    ArrayList<ShippingAddressResponse> addressResponses;
    ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList;
    AddressAdapter addressAdapter;
    AddressAdapter buyerAddressAdapter;
    private boolean isAddAddressShow;
    private String shipping_charge;
    private String orginalTotalAmout;
    private boolean isBrokerageOrder;
    private String codAmount;
    TextView textViewCodNote;


    View wishbook_credit_sub_view;
    ResponseCreditLines[] responseCreditLines;
    RadioButton radioWishbookCredit;

    TextView cod_500_note;

    private static String TAG = Fragment_CashPayment.class.getSimpleName();
    HashMap<ResponseShipment, RadioButton> radioGroupShipping = new HashMap<>();

    public Fragment_CashPayment() {

    }


    // pass isBrokeOrder
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_cash_payment, ga_container, true);

        mContext = getActivity();
        ButterKnife.bind(this, view);
        mContext = getActivity();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerAddress.setLayoutManager(layoutManager1);
        recyclerAddress.setHasFixedSize(false);
        recyclerAddress.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_buyeraddress.setLayoutManager(layoutManager2);
        recycler_buyeraddress.setHasFixedSize(false);
        recycler_buyeraddress.setNestedScrollingEnabled(false);

        addressResponses = new ArrayList<>();
        isAddAddressShow = false;
        linear_shipping_container.setVisibility(View.GONE);
        linearAddAddress.setVisibility(View.GONE);
        cod_500_note = new TextView(getActivity());
        getstates();

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wishbook_credit_sub_view = vi.inflate(R.layout.wishbook_credit_state, null);

        String total = "0";
        if (Application_Singleton.purchaseInvoice != null) {
            invoice = Application_Singleton.purchaseInvoice;
        }


        //SPANNABLE STRING
        final String url = "http://www.wishbook.io/offer-tnc/";
        SpannableString ss = new SpannableString(getActivity().getResources().getString(R.string.invoice_cashback_note).toString());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
            ss.setSpan(clickableSpan, 123, 134, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (UserInfo.getInstance(getActivity()).getLanguage().equals("hi")) {
            ss.setSpan(clickableSpan, 98, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        txtInvoiceNote.setText(ss);
        txtInvoiceNote.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(txtInvoiceNote, Linkify.WEB_URLS);


        if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
            order = (Response_buyingorder) Application_Singleton.selectedOrder;
            if (order.getProcessing_status().equals("Dispatched")) {
                card_shipping_details.setVisibility(View.GONE);
                Log.i("TAG", "onCreateView: Order Status Dispatched");
            }

            seller_additional_discount = order.getSeller_extra_discount_percentage();
            if (getArguments().getBoolean("isBrokerageOrder")) {
                Log.i("TAG", "getIsBroker Fragment==>" + true);
                isBrokerageOrder = true;
                fetchAddressBuyers(order.getCompany());
            } else {
                Log.i("TAG", "getIsBroker Fragment==>" + false);
                isBrokerageOrder = false;
                fetchAddress();
            }
            //getShippingCharges(order.getId());
            getSellerDiscount(order.getSeller_company());
            txtInvoiceOrderNo.setText(order.getOrder_number());
            /**
             * Hide Supplier Details
             * WB-3704
             */
            relative_supplier_container.setVisibility(View.GONE);
            if (order.getSeller_company_name() != null)
                txtInvoiceSellerName.setText(StringUtils.capitalize(order.getSeller_company_name().toLowerCase().trim()));
            txtInvoiceOrderDate.setText(getformatedDate(order.getDate()));
            addCatalogList(order.getCatalogs(), invoice_catalog_container);
            Log.i("TAG", "Total Amt: " + invoice.getPending_amount());
            orginalTotalAmout = invoice.getPending_amount();
            if (invoice.getShipping_charges() != null && !invoice.getShipping_charges().equals("0.00")) {
                orginalTotalAmout = String.valueOf(Float.parseFloat(invoice.getPending_amount()) - Float.parseFloat(invoice.getShipping_charges()) + invoice.getWbmoney_points_used());
                invoice.setPending_amount(orginalTotalAmout);
            }
           /* if (invoice.getWbmoney_points_used() > 0) {
                chk_wb_money.setChecked(true);
                orginalTotalAmout = String.valueOf(Float.parseFloat(orginalTotalAmout) - invoice.getWbmoney_points_used());
            }*/

            //getWBMoneyDashboard();
            txtPayableAmt.setText("\u20B9" + orginalTotalAmout);

            //set Discount tax
            if (invoice.getTax_class_1() != null) {
                relativeTaxClass1.setVisibility(View.VISIBLE);
                taxClass1Name.setText(invoice.getTax_class_1());
                txtClass1Value.setText("+ " + "\u20B9" + invoice.getTotal_tax_value_1());
            } else {
                relativeTaxClass1.setVisibility(View.GONE);
            }

            if (invoice.getTax_class_2() != null) {
                relativeTaxClass2.setVisibility(View.VISIBLE);
                taxClass2Name.setText(invoice.getTax_class_2());
                txtClass2Value.setText("+ " + "\u20B9" + invoice.getTotal_tax_value_2());
            } else {
                relativeTaxClass2.setVisibility(View.GONE);
            }

            if (invoice.getSeller_discount() != null && !invoice.getSeller_discount().equals("0.00")) {
                relativeSellerDiscount.setVisibility(View.VISIBLE);
                txtSellerDiscount.setText("- " + "\u20B9" + invoice.getSeller_discount());
            } else {
                relativeSellerDiscount.setVisibility(View.GONE);
            }


            if (order.getPayment_status() != null && !order.getPayment_status().equals("null")) {

                if (order.getPayment_status().equals("Paid")
                        || order.getPayment_status().equals("Partially Paid")
                        || order.getProcessing_status().equals("Dispatched")
                        || order.getProcessing_status().equals("Closed")
                        || order.getProcessing_status().equals("Delivered")
                        || order.getProcessing_status().equals("In Progress")) {

                    card_cancel_order.setVisibility(View.GONE);
                    buttonCancel.setVisibility(View.GONE);
                }
            }

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Order Cancel")
                            .content("Are you sure you want to cancel order?")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    cancelOrder("purchaseorder", order.getId());
                                }
                            })
                            .negativeText("Cancel")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            });
        }

        return view;
    }


    public void getSellerDiscount(final String supplierId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + "?company=" + supplierId, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_Suppliers[] response_seller = new Gson().fromJson(response, Response_Suppliers[].class);
                if (response_seller != null && Application_Singleton.configResponse != null) {
                    if (response_seller.length > 0) {
                        Log.i("TAG", "onServerResponse: Response Seller greater 0");
                        Application_Singleton.selectedSupplier = response_seller[0];
                        cashDiscount = order.getCurrent_discount();
                        creditDiscount = response_seller[0].getDiscount();
                        if (isBrokerageOrder) {
                            fetchPayment(/*order.getSeller_company(), order.getCompany(),*/ order.getId());
                        } else {
                            fetchPayment(/*order.getSeller_company(), UserInfo.getInstance(getActivity()).getCompany_id(),*/ order.getId());
                        }
                    } else {
                        getBuyerDiscount(supplierId);
                    }
                } else {
                    getBuyerDiscount(supplierId);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    public void getBuyerDiscount(String supplierId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.BUYER_DISCOUNT_URL + "?supplier=" + supplierId, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                BuyerDiscountRequest buyerDiscountRequests[] = Application_Singleton.gson.fromJson(response, BuyerDiscountRequest[].class);
                if (buyerDiscountRequests.length > 0) {
                    for (BuyerDiscountRequest discount : buyerDiscountRequests) {
                        if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_public))) {
                            creditDiscount = order.getCurrent_discount();
                            cashDiscount = discount.getCash_discount();
                        }
                    }
                }
                if (isBrokerageOrder) {
                    fetchPayment(/*order.getSeller_company(), order.getCompany(),*/ order.getId());
                } else {
                    fetchPayment(/*order.getSeller_company(), UserInfo.getInstance(getActivity()).getCompany_id(),*/ order.getId());
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void payVia(String type, final String mode, String amount, final String orderID) {
        Fragment_Pay payFragment = new Fragment_Pay();
        final String url;
        if (type.equals("invoice")) {
            url = URLConstants.companyUrl(getActivity(), "invoice_payment", orderID);
        } else {
            url = URLConstants.companyUrl(getActivity(), "payment", orderID);
        }
        if (amount != null) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            bundle.putString("type", mode);
            payFragment.setArguments(bundle);
        }
        payFragment.setListener(new Fragment_Pay.Listener() {
            @Override
            public void onDismiss(String date, String details, String amount) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("mode", mode);
                params.put("details", details);
                params.put("date", date);
                params.put("amount", amount);
                HttpManager.getInstance(getActivity()).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Application_Singleton.trackEvent("PayEnterDetails", "Click", "PayEnterDetails");
                        setOrderPending(false);
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });


        payFragment.show(getFragmentManager(), "pay");
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

    private void addCatalogList(ArrayList<Response_sellingoder_catalog> catalogs, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //root.removeAllViews();
        if (catalogs != null) {
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
                }
                String cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";
                catalogValue.setText("\u20B9" + String.valueOf(totalPriceCatalog));
                catalogName.setText(cname);
                root.addView(v);
            }
        }
    }

    public void cancelOrder(final String type, final String id) {
        new MaterialDialog.Builder(getActivity())
                .title("Cancel")
                .content("Order cancel note")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        Gson gson = new Gson();
                        if (input.toString().equals("")) {
                            Toast.makeText(getActivity(), "Cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Cancelled", "Cancelled", "" + input.toString());
                        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), type, "") + id + '/', gson.fromJson(gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {

                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Application_Singleton.trackEvent("NewPurchaseCancel", "Click", "NewPurchaseCancel");
                                Toast.makeText(getActivity(), "Order has been cancelled", Toast.LENGTH_SHORT).show();
                                getActivity().setResult(Activity.RESULT_OK);
                                getActivity().finish();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Cash Payment ");
        super.onActivityResult(requestCode, resultCode, data);
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
                    btnPayment.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                    getActivity().finish();

                    response = "OrderID : " + inResponse.getString("ORDERID") + "\n"
                            + "Status : " + status + "\n"
                            + "Amount : " + inResponse.getString("TXNAMOUNT") + "\n";
                }
            } else {
                response = data.getStringExtra("response");
            }
            if (status.equals("Failure")) {
                new MaterialDialog.Builder(mContext).title("Transaction Failure ").content(response).positiveText("Ok").show();
            } else {
                new MaterialDialog.Builder(mContext).title("Transaction Success").positiveText("Ok").content(response).show();

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
                    new MaterialDialog.Builder(mContext).title("Transaction Failure ").content(response).positiveText("Ok").show();
                } else {
                    btnPayment.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                    getActivity().finish();

                    new MaterialDialog.Builder(mContext).title("Transaction Success").positiveText("Ok").content(response).show();

                }
            }
        }
    }

    public void fetchPayment(/*String supplierId, String buyerId,*/ final String orderId) {
        final Response_buyingorder selectedOrder1 = (Response_buyingorder) Application_Singleton.selectedOrder;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
       /* params.put("supplier", supplierId);
        params.put("buyer", buyerId);*/
        params.put("order", orderId);
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PAYMENT_METHOD_URL, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                PaymentMethod[] paymentMethods = Application_Singleton.gson.fromJson(response, PaymentMethod[].class);
                ArrayList<PaymentMethod> creditPyament = new ArrayList<PaymentMethod>();
                ArrayList<PaymentMethod> offlinePayment = new ArrayList<PaymentMethod>();
                ArrayList<PaymentMethod> onlinePayment = new ArrayList<PaymentMethod>();
                ArrayList<PaymentMethod> codPayment = new ArrayList<PaymentMethod>();
                for (int i = 0; i < paymentMethods.length; i++) {
                    if (paymentMethods[i].getPayment_type().equalsIgnoreCase("Credit")) {
                        creditPyament.add(paymentMethods[i]);
                    }
                    if (paymentMethods[i].getPayment_type().equalsIgnoreCase("Offline")) {
                        offlinePayment.add(paymentMethods[i]);
                    }
                    if (paymentMethods[i].getPayment_type().equalsIgnoreCase("Online")) {
                        onlinePayment.add(paymentMethods[i]);
                    }

                    if (paymentMethods[i].getPayment_type().equalsIgnoreCase("Cash on Delivery")) {
                        codPayment.add(paymentMethods[i]);
                    }
                }
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 30);
                radioGroupPayment.removeAllViews();
                for (int i = 0; i < onlinePayment.size(); i++) {
                    if (i == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(onlinePayment.get(i).getPayment_type());
                        radioGroupPayment.addView(textView);
                    }
                    if (Float.parseFloat(invoice.getTotal_amount()) > AMOUNT_LIMIT_PAYMENT_OPTION) {
                        if (!onlinePayment.get(i).getName().equalsIgnoreCase("PAYTM") &&
                                !onlinePayment.get(i).getName().equalsIgnoreCase("MOBIKWIK")) {
                            RadioButton radioButtonView = new RadioButton(getActivity());
                            radioButtonView.setPadding(40, 0, 0, 0);
                            radioButtonView.setId(Integer.parseInt(onlinePayment.get(i).getId()));
                            radioButtonView.setText(onlinePayment.get(i).getDisplay_name());
                            radioButtonView.setTag(onlinePayment.get(i).getName());
                            radioButtonView.setLayoutParams(lp);
                            radioGroupPayment.addView(radioButtonView);
                        }

                    } else {
                        RadioButton radioButtonView = new RadioButton(getActivity());
                        radioButtonView.setPadding(40, 0, 0, 0);
                        radioButtonView.setId(Integer.parseInt(onlinePayment.get(i).getId()));
                        radioButtonView.setText(onlinePayment.get(i).getDisplay_name());
                        radioButtonView.setTag(onlinePayment.get(i).getName());
                        radioButtonView.setLayoutParams(lp);
                        radioGroupPayment.addView(radioButtonView);
                    }

                }

                for (int i = 0; i < codPayment.size(); i++) {
                    if (i == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(codPayment.get(i).getPayment_type());
                        radioGroupPayment.addView(textView);
                    }
                    RadioButton radioButtonView = new RadioButton(getActivity());
                    radioButtonView.setPadding(40, 0, 0, 0);
                    radioButtonView.setId(Integer.parseInt(codPayment.get(i).getId()));
                    radioButtonView.setText(codPayment.get(i).getDisplay_name());
                    //radioButtonView.setLayoutParams(lp);
                    radioButtonView.setTag(codPayment.get(i).getName());
                    radioGroupPayment.addView(radioButtonView);
                    if (codPayment.get(i).getName().equals("cod")) {
                        textViewCodNote = new TextView(getActivity());
                        codAmount = codPayment.get(i).getAmount();
                        changeCodAmount();
                        textViewCodNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        textViewCodNote.setTextColor(getResources().getColor(R.color.purchase_medium_gray));

                        cod_500_note = new TextView(getActivity());
                        cod_500_note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        cod_500_note.setTextColor(getResources().getColor(R.color.purchase_medium_gray));
                        cod_500_note.setText(getResources().getString(R.string.cod_500_subtext));
                        cod_500_note.setPadding(0, 0, 0, 30);
                        cod_500_note.setVisibility(View.GONE);
                        radioGroupPayment.addView(textViewCodNote);
                        radioGroupPayment.addView(cod_500_note);
                    }
                    if (Double.parseDouble(invoice.getPending_amount()) < 500) {
                        radioButtonView.setEnabled(false);
                        cod_500_note.setVisibility(View.VISIBLE);
                    }
                }

                for (int i = 0; i < offlinePayment.size(); i++) {
                    if (i == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(offlinePayment.get(i).getPayment_type());
                        radioGroupPayment.addView(textView);

                     /*   TextView textViewNote = new TextView(getActivity());
                        textViewNote.setText(getResources().getString(R.string.offline_note));
                        textViewNote.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
                        textViewNote.setTextColor(getResources().getColor(R.color.green));
                        LinearLayoutCompat.LayoutParams params1 = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0,0,0,80);
                        textViewNote.setLayoutParams(params1);
                        radioGroupPayment.addView(textViewNote);*/
                    }

                    RadioButton radioButtonView = new RadioButton(getActivity());
                    radioButtonView.setPadding(40, 0, 0, 0);
                    radioButtonView.setId(Integer.parseInt(offlinePayment.get(i).getId()));
                    radioButtonView.setText(offlinePayment.get(i).getDisplay_name());
                    radioButtonView.setLayoutParams(lp);
                    radioButtonView.setTag(offlinePayment.get(i).getName());
                    radioGroupPayment.addView(radioButtonView);
                }


                for (int i = 0; i < creditPyament.size(); i++) {
                    if (i == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(creditPyament.get(i).getPayment_type());
                        radioGroupPayment.addView(textView);
                    }
                    RadioButton radioButtonView = new RadioButton(getActivity());
                    radioButtonView.setPadding(40, 0, 0, 0);
                    radioButtonView.setId(Integer.parseInt(creditPyament.get(i).getId()));
                    if (i == 0) {
                        if (isBrokerageOrder) {
                            radioButtonView.setText(getResources().getString(R.string.buy_on_suppliers_credit));
                        } else {
                            radioButtonView.setText(creditPyament.get(i).getDisplay_name());
                        }
                    } else {
                        radioButtonView.setText(creditPyament.get(i).getDisplay_name());
                    }
                    radioButtonView.setTag(creditPyament.get(i).getName());
                    radioButtonView.setLayoutParams(lp);
                    radioGroupPayment.addView(radioButtonView);

                    if (creditPyament.get(i).getName().equalsIgnoreCase("WISHBOOKCREDIT")) {
                        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        wishbook_credit_sub_view = vi.inflate(R.layout.wishbook_credit_state, null);
                        wishbook_credit_sub_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        radioButtonView.setEnabled(false);
                        getCreditLine(radioButtonView);
                        radioGroupPayment.addView(wishbook_credit_sub_view);
                    }
                }


                radioGroupPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        Log.e(TAG, "onCheckedChanged: " + radioGroupPayment.getCheckedRadioButtonId());

                        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                            RadioButton radioButton = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId()));
                            if (check.equalsIgnoreCase("CREDIT") || check.equalsIgnoreCase("WISHBOOKCREDIT")) {
                                if (textViewCodNote != null) {
                                    textViewCodNote.setVisibility(View.GONE);
                                }
                                if (creditDiscount != null) {
                                    if (creditDiscount.equals("0.00")) {
                                        discount_type.setVisibility(View.GONE);
                                        txt_discount_percentage.setVisibility(View.GONE);
                                        // discount_note.setVisibility(View.GONE);
                                    } else {

                                        updateInvoiceUI();
                                    }
                                } else {
                                    txt_discount_percentage.setVisibility(View.GONE);
                                    discount_note.setVisibility(View.GONE);
                                    discount_type.setVisibility(View.GONE);
                                }

                                btnPaymentCredit.setVisibility(View.VISIBLE);
                                btnPayment.setVisibility(View.GONE);
                            } else {
                                if (textViewCodNote != null) {
                                    textViewCodNote.setVisibility(View.GONE);
                                }
                                if (check.equals("NEFT") || check.equals("OTHER") || check.equals("CHEQUE")) {
                                    btnPayment.setText("Enter Payment Details");
                                } else {
                                    btnPayment.setText("Proceed for Payment");
                                }

                                if (check.equalsIgnoreCase("cod")) {
                                    showCODDialog();
                                }
                                if (cashDiscount != null) {
                                    if (cashDiscount.equals("0.00")) {
                                        discount_type.setVisibility(View.GONE);
                                        txt_discount_percentage.setVisibility(View.GONE);
                                    } else {
                                        discount_type.setVisibility(View.VISIBLE);
                                        if (invoice.getSeller_discount() != null && !invoice.getSeller_discount().equals("0.00")) {
                                            if (order.getSeller_extra_discount_percentage() > 0) {
                                                discount_type.setText("(Cash " + Float.parseFloat(cashDiscount) + "%+" + order.getSeller_extra_discount_percentage() + "%)");
                                            } else {
                                                discount_type.setText("(Cash " + Float.parseFloat(cashDiscount) + "%)");
                                            }

                                            //txt_discount_percentage.setText(Math.round(Float.parseFloat(cashDiscount)) + "%");
                                            // txt_discount_percentage.setVisibility(View.VISIBLE);
                                            relativeSellerDiscount.setVisibility(View.VISIBLE);
                                            txtSellerDiscount.setText("- " + "\u20B9" + invoice.getSeller_discount());
                                        } else {
                                            relativeSellerDiscount.setVisibility(View.GONE);
                                        }
                                        txtPayableAmt.setText("\u20B9" + String.valueOf(Double.parseDouble(invoice.getPending_amount()) - invoice.getWbmoney_points_used()));
                                        // txtPayableAmt.setText("\u20B9" + invoice.getPending_amount());
                                       /* discount_note.setVisibility(View.VISIBLE);

                                        discount_note.setText(Application_Singleton.selectedSupplier.getCash_discount()+"% off");*/
                                    }

                                } else {
                                    discount_type.setVisibility(View.GONE);
                                    txt_discount_percentage.setVisibility(View.GONE);
                                    //discount_note.setVisibility(View.GONE);
                                }
                                btnPayment.setVisibility(View.VISIBLE);
                                btnPaymentCredit.setVisibility(View.GONE);
                            }
                        }

                    }
                });
                try {
                    Log.i("TAG", "Radio Checked:===> Try");
                    radioGroupPayment.clearCheck();
                    if (!((RadioButton) radioGroupPayment.getChildAt(1)).isChecked()) {
                        ((RadioButton) radioGroupPayment.getChildAt(1)).setChecked(true);
                    }
                } catch (Exception e) {
                    Log.i("TAG", "Radio Checked:===> Catch");
                    e.printStackTrace();
                }
                btnPaymentCredit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String addressid = "";
                        if (!order.getProcessing_status().equals("Dispatched") && order.getShip_to() == null) {
                            if (isBrokerageOrder) {
                                if (buyerAddressAdapter != null) {
                                    if (buyerAddressAdapter.getSelected() != null) {
                                        addressid = buyerAddressAdapter.getSelected();

                                    } else if (addressAdapter != null && addressAdapter.getSelected() != null) {
                                        addressid = addressAdapter.getSelected();
                                    } else {
                                        Toast.makeText(getActivity(), "Please select shipping address", Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                }
                            } else {
                                if (addressAdapter != null) {
                                    String isAddressselected = addressAdapter.getSelected();
                                    if (isAddressselected == null) {
                                        Toast.makeText(getActivity(), "Please select shipping address", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        addressid = addressAdapter.getSelected();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Please Enter any one shipping address", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }


                        } else {
                            boolean isShippingMethodChecked = false;
                            if (radioGroupShipping.size() == 0) {
                                isShippingMethodChecked = false;

                            } else {
                                for (Map.Entry<ResponseShipment, RadioButton> entry : radioGroupShipping.entrySet()) {
                                    ResponseShipment key = entry.getKey();
                                    if (entry.getValue().isChecked()) {
                                        isShippingMethodChecked = true;
                                        shipping_charge = String.valueOf(entry.getKey().getShipping_charge());
                                    }
                                }
                            }

                            if (!isShippingMethodChecked) {
                                Toast.makeText(getActivity(), "Please Select Shipping Method", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
                                if (ship != null) {
                                    addressid = ship.getId();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        setLogistick1(addressid, shipping_charge);

                      /*  if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                            switch (check) {
                                case "CREDIT":
                                    Application_Singleton.trackEvent("BuyOnCredit", "Click", "BuyOnCredit");
                                    setOrderPending(true);
                                    break;

                                case "WISHBOOKCREDIT":
                                    Application_Singleton.trackEvent("WishbookCredit", "Click", "WishbookCredit");
                                    callOrderPayment("invoice", "Wishbook Credit", "Wishbook Credit", getCurrentDate(), invoice.getPending_amount(), invoice.getId());
                                    break;
                            }
                        }*/
                    }
                });
                btnPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String addressid = "";
                        if (!order.getProcessing_status().equals("Dispatched") && order.getShip_to() == null) {
                            if (isBrokerageOrder) {
                                if (buyerAddressAdapter != null) {
                                    if (buyerAddressAdapter.getSelected() != null) {
                                        addressid = buyerAddressAdapter.getSelected();

                                    } else if (addressAdapter != null && addressAdapter.getSelected() != null) {
                                        addressid = addressAdapter.getSelected();
                                    } else {
                                        Toast.makeText(getActivity(), "Please select shipping address", Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                }
                            } else if (addressAdapter != null) {
                                String isAddressselected = addressAdapter.getSelected();
                                if (isAddressselected == null) {
                                    Toast.makeText(getActivity(), "Please select shipping address", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    addressid = addressAdapter.getSelected();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please Enter any one shipping address", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {
                            boolean isShippingMethodChecked = false;
                            if (radioGroupShipping.size() == 0) {
                                isShippingMethodChecked = false;

                            } else {
                                for (Map.Entry<ResponseShipment, RadioButton> entry : radioGroupShipping.entrySet()) {
                                    ResponseShipment key = entry.getKey();
                                    if (entry.getValue().isChecked()) {
                                        isShippingMethodChecked = true;
                                        shipping_charge = String.valueOf(entry.getKey().getShipping_charge());
                                    }
                                }
                            }

                            if (!isShippingMethodChecked) {
                                Toast.makeText(getActivity(), "Please Select Shipping Method", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
                                if (ship != null) {
                                    addressid = ship.getId();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        setLogistick1(addressid, shipping_charge);
                        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                            switch (check) {
                                case "ZAAKPAY":
                                    Application_Singleton.trackEvent("PAYZAAKPAY", "Click", "PAYZAAKPAY");
                                    Intent intentToZaakpay = new Intent(getActivity(), ZaakPayMerchantActivity.class);
                                    intentToZaakpay.putExtra("orderid", invoice.getId());
                                    intentToZaakpay.putExtra("order_amount", invoice.getPending_amount());
                                    Fragment_CashPayment.this.startActivityForResult(intentToZaakpay, 3);
                                    break;

                                case "CASHFREE":
                                    Application_Singleton.trackEvent("PAYCASHFREE", "Click", "PAYCASHFREE");
                                    String name = UserInfo.getInstance(getActivity()).getFirstName();
                                    String mContact = UserInfo.getInstance(getActivity()).getMobile();
                                    String mEmail = UserInfo.getInstance(getActivity()).getEmail();
                                    initCashFree(invoice.getId(), String.valueOf(invoice.getPending_amount()), invoice.getId(), name, mContact, mEmail);
                                    break;
                                case "COD":
                                    if (codAmount != null) {
                                        Application_Singleton.trackEvent("PAYCASHFREE", "Click", "COD");
                                        String name2 = UserInfo.getInstance(getActivity()).getFirstName();
                                        String mContact2 = UserInfo.getInstance(getActivity()).getMobile();
                                        String mEmail2 = UserInfo.getInstance(getActivity()).getEmail();
                                        initCashFree(invoice.getId(), String.valueOf(codAmount), invoice.getId(), name2, mContact2, mEmail2);
                                        /*Intent intentToCOD = new Intent(getActivity(), ZaakPayMerchantActivity.class);
                                        intentToCOD.putExtra("orderid", invoice.getId());
                                        intentToCOD.putExtra("order_amount", codAmount);
                                        Fragment_CashPayment.this.startActivityForResult(intentToCOD, 3);*/
                                    }
                                    break;
                                case "PAYTM":
                                    Application_Singleton.trackEvent("PAYPAYTM", "Click", "PAYPAYTM");
                                    Intent i = new Intent(getActivity(), MerchantActivity2.class);
                                    i.putExtra("orderid", invoice.getId());
                                    i.putExtra("order_amount", invoice.getPending_amount());
                                    Fragment_CashPayment.this.startActivityForResult(i, 2);
                                    break;
                                case "MOBIKWIK":
                                    Application_Singleton.trackEvent("PAYMOBIKWIK", "Click", "PAYMOBIKWIK");
                                    Intent intentToMobikwik = new Intent(getActivity(), MobiKwikMerchantActivity.class);
                                    intentToMobikwik.putExtra("orderid", invoice.getId());
                                    intentToMobikwik.putExtra("order_amount", invoice.getPending_amount());
                                    Fragment_CashPayment.this.startActivityForResult(intentToMobikwik, 3);
                                    break;
                                case "NEFT":
                                    payVia("invoice", check, invoice.getPending_amount(), invoice.getId());
                                    break;
                                case "CHEQUE":
                                    payVia("invoice", check, invoice.getPending_amount(), invoice.getId());
                                    break;
                                case "OTHER":
                                    payVia("invoice", check, invoice.getPending_amount(), invoice.getId());
                                    break;
                                default:
                                    payVia("invoice", check, invoice.getPending_amount(), invoice.getId());
                                    break;
                            }
                        }
                    }
                });

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }


    public void setLogistick1(String addressid, String shippingcharge) {

        final MaterialDialog progress = StaticFunctions.showProgress(getActivity());
        progress.setCancelable(false);

        SalesOrderCreate order = new SalesOrderCreate();
        order.setPreffered_shipping_provider(getResources().getString(R.string.wb_provided));
        order.setShipping_charges(shippingcharge);

        try {
            int add = (int) Double.parseDouble(addressid);
            order.setShip_to(String.valueOf(add));
        } catch (NumberFormatException e) {
            order.setShip_to(addressid);
        }
        String orderjson = new Gson().toJson(order);
        Log.i("TAG", "setLogistick1: " + orderjson);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        if (isBrokerageOrder) {
            url = "brokerageorder";
        } else {
            url = "purchaseorder";
        }

        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        progress_dialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                if (isBrokerageOrder) {
                    selectedOrder.setBrokerage(true);
                }
                Application_Singleton.selectedOrder = selectedOrder;

                if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                    String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                    switch (check) {
                        case "CREDIT":
                            Application_Singleton.trackEvent("BuyOnCredit", "Click", "BuyOnCredit");
                            setOrderPending(true);
                            break;

                        case "WISHBOOKCREDIT":
                            Application_Singleton.trackEvent("WishbookCredit", "Click", "WishbookCredit");
                            callOrderPayment("invoice", "Wishbook Credit", "Wishbook Credit", getCurrentDate(), invoice.getPending_amount(), invoice.getId());
                            break;
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                    String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                    switch (check) {
                        case "CREDIT":
                            btnPaymentCredit.setVisibility(View.VISIBLE);
                            break;

                        case "WISHBOOKCREDIT":
                            btnPaymentCredit.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void patchShipping(String addressid, double shippingcharge, String ship_method) {
        Log.e(TAG, "=======patchShipping: ============");
        final MaterialDialog progress = StaticFunctions.showProgress(getActivity());
        progress.setCancelable(false);

        SalesOrderCreate order = new SalesOrderCreate();
        order.setPreffered_shipping_provider(getResources().getString(R.string.wb_provided));
        order.setShipping_charges(String.valueOf(shippingcharge));
        order.setShipping_method(ship_method);

        try {
            int add = (int) Double.parseDouble(addressid);
            order.setShip_to(String.valueOf(add));
        } catch (NumberFormatException e) {
            order.setShip_to(addressid);
        }
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        if (isBrokerageOrder) {
            url = "brokerageorder";
        } else {
            url = "purchaseorder";
        }
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        progress_dialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                if (isBrokerageOrder) {
                    selectedOrder.setBrokerage(true);
                }

                Application_Singleton.selectedOrder = selectedOrder;
                refreshInvoice();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void refreshInvoice() {
        Log.e(TAG, "=======Refresh Invoice: ============");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "create_invoice", "") + invoice.getId();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Invoice invoice_temp = Application_Singleton.gson.fromJson(response, Invoice.class);
                    invoice = invoice_temp;
                    changeCodAmount();
                    updateInvoiceUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }


        });
    }


    public void setOrderPending(boolean isCreditOrder) {
        if (((Response_buyingorder) Application_Singleton.selectedOrder).getProcessing_status().equals("Cart") || ((Response_buyingorder) Application_Singleton.selectedOrder).getProcessing_status().equals("Draft")) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            Gson gson = new Gson();
            String url = "";
            if (isBrokerageOrder) {
                url = "brokerageorder";
            } else {
                url = "purchaseorder";
            }

            PatchAccept patchAccept = new PatchAccept(((Response_buyingorder) Application_Singleton.selectedOrder).getId(), "Pending");
            if (isCreditOrder) {
                patchAccept.setPayment_details("BUY ON CREDIT");
            }

            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + '/', gson.fromJson(gson.toJson(patchAccept), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Toast.makeText(getActivity(), "Order created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                    getActivity().finish();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                    if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                        String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                        switch (check) {
                            case "CREDIT":
                                btnPaymentCredit.setVisibility(View.VISIBLE);
                                break;

                            case "WISHBOOKCREDIT":
                                btnPaymentCredit.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Order updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
            getActivity().finish();
        }

    }

    private void getstates() {
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (allstates != null) {
                    SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem, R.id.spintext, allstates);
                    spinner_state.setAdapter(spinAdapter_state);
                }
                if (allstates != null) {
                    if (State != null) {
                        for (int i = 0; i < allstates.length; i++) {
                            if (State.equals(allstates[i].getState_name())) {
                                spinner_state.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    Log.i("TAG", "onItemSelected:  State" + stateId);

                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (allcities != null) {
                                SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, allcities);
                                spinner_city.setAdapter(spinAdapter_city);
                                if (State != null && State != "") {
                                    if (City != null) {
                                        for (int i = 0; i < allcities.length; i++) {
                                            if (City.equals(allcities[i].getCity_name())) {
                                                spinner_city.setSelection(i);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = allcities[position].getId();
                Log.i("TAG", "onItemSelected:  City" + cityId);
                HashMap<String, String> params = new HashMap<>();
                params.put("city", cityId);
                params.put("state", stateId);
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "companylist", ""), params, null, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.btn_save_address)
    public void validateAddress() {
        if (isEmptyValidation(editAddLine1)) {
            editAddLine1.requestFocus();
            editAddLine1.setError("Please Enter Address");
            return;
        }

        if (stateId == "") {
            spinner_state.requestFocus();
            Toast.makeText(getActivity(), "Please select State", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (((Response_States) spinner_state.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                spinner_state.requestFocus();
                Toast.makeText(getActivity(), "Please select State", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cityId == "") {
            spinner_city.requestFocus();
            Toast.makeText(getActivity(), "Please select City", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (((Response_Cities) spinner_city.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                spinner_city.requestFocus();
                Toast.makeText(getActivity(), "Please select City", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isEmptyValidation(editPincode)) {
            editPincode.requestFocus();
            editPincode.setError("Please enter valid pincode");
            return;
        }

        saveAddress(editAddLine1.getText().toString().trim(), editAddLine2.getText().toString().trim(), editPincode.getText().toString().trim());

    }

    private boolean isEmptyValidation(EditText editText) {
        if (editText.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Application_Singleton.isChangeCreditScrore) {
            setCreditLine(responseCreditLines, radioWishbookCredit, true);
        }

    }

    public void saveAddress(String add1, String add2, String pincode) {
        ShippingAddressResponse ship;
        String orderAddressid = null;
        try {
            ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
            if (ship != null) {
                orderAddressid = ship.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        if (!add2.isEmpty()) {
            params.put("street_address", add1 + " , " + add2);
        } else {
            params.put("street_address", add1);
        }
        params.put("pincode", pincode);
        params.put("city", cityId);
        params.put("state", stateId);
        params.put("country", "1");
        params.put("name", "Work");
        final String finalOrderAddressid = orderAddressid;
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", ""), params, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {


                hideAddAddress();
                if (isBrokerageOrder) {
                    txt_broker_address.setVisibility(View.VISIBLE);
                } else {
                    txt_broker_address.setVisibility(View.GONE);
                }
                AddressResponse newAddress = Application_Singleton.gson.fromJson(response, AddressResponse.class);
                ShippingAddressResponse shippingAddressResponse = new ShippingAddressResponse(newAddress.getPincode(), newAddress.getId(), newAddress.getName(), newAddress.getStreet_address(), false);
                ShippingAddressResponse.City city = shippingAddressResponse.new City();
                city.setId(newAddress.getCity());
                city.setCity_name(getCityName(newAddress.getCity()));

                ShippingAddressResponse.State state = shippingAddressResponse.new State();
                state.setId(newAddress.getState());
                state.setState_name(getStateName(newAddress.getState()));
                shippingAddressResponse.setCity(city);
                shippingAddressResponse.setState(state);
                addressResponses.add(shippingAddressResponse);
                if (addressAdapter != null) {
                    addressAdapter.notifyDataSetChanged();
                } else {
                    addressAdapter = new AddressAdapter(getActivity(), addressResponses, new AddressAdapter.ChangeListener() {
                        @Override
                        public void onEditAddress(int position, ShippingAddressResponse response) {
                            editAddLine1.setText(response.getStreet_address());
                            editPincode.setText(response.getPincode());
                            spinner_state.setSelection(Integer.parseInt(response.getState().getId()));
                        }
                    }, recyclerAddress, Fragment_CashPayment.this, isBrokerageOrder, finalOrderAddressid);
                    recyclerAddress.setAdapter(addressAdapter);
                }
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

    public void fetchAddress() {
        ShippingAddressResponse ship;
        String orderAddressid = null;
        try {
            ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
            if (ship != null) {
                orderAddressid = String.valueOf(Math.round(Float.parseFloat(ship.getId())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final String finalOrderAddressid = orderAddressid;
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", ""), null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                shippingAddressResponses = Application_Singleton.gson.fromJson(response, ShippingAddressResponse[].class);
                if (shippingAddressResponses.length > 0) {
                    addressResponses = new ArrayList<ShippingAddressResponse>(Arrays.asList(shippingAddressResponses));
                    addressAdapter = new AddressAdapter(getActivity(), addressResponses, new AddressAdapter.ChangeListener() {
                        @Override
                        public void onEditAddress(int position, ShippingAddressResponse response) {
                            editAddLine1.setText(response.getStreet_address());
                            editPincode.setText(response.getPincode());
                            if (allstates != null) {
                                if (response.getState().getState_name() != null) {
                                    for (int i = 0; i < allstates.length; i++) {
                                        if (response.getState().getState_name().equals(allstates[i].getState_name())) {
                                            spinner_state.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (allcities != null) {
                                if (response.getCity().getCity_name() != null) {
                                    for (int i = 0; i < allcities.length; i++) {
                                        if (response.getCity().getCity_name().equals(allcities[i].getCity_name())) {
                                            spinner_city.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }, recyclerAddress, Fragment_CashPayment.this, false, finalOrderAddressid);
                    recyclerAddress.setAdapter(addressAdapter);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }

    public void fetchAddressBuyers(String buyerComapnyId) {

        ShippingAddressResponse ship;
        String orderAddressid = null;
        try {
            ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
            if (ship != null) {
                orderAddressid = ship.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final String finalOrderAddressid = orderAddressid;
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", "") + "?company=" + buyerComapnyId, null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                shippingAddressResponses = Application_Singleton.gson.fromJson(response, ShippingAddressResponse[].class);
                if (shippingAddressResponses.length > 0) {
                    for (int i = 0; i < shippingAddressResponses.length; i++) {
                        shippingAddressResponses[i].setBuyerAddress(true);
                    }
                }

                if (shippingAddressResponses.length > 0) {
                    buyer_address_container.setVisibility(View.VISIBLE);
                    shippingAddressResponseArrayList = new ArrayList<ShippingAddressResponse>(Arrays.asList(shippingAddressResponses));
                    buyerAddressAdapter = new AddressAdapter(getActivity(), shippingAddressResponseArrayList, new AddressAdapter.ChangeListener() {
                        @Override
                        public void onEditAddress(int position, ShippingAddressResponse response) {
                            editAddLine1.setText(response.getStreet_address());
                            editPincode.setText(response.getPincode());
                            if (allstates != null) {
                                if (response.getState().getState_name() != null) {
                                    for (int i = 0; i < allstates.length; i++) {
                                        if (response.getState().getState_name().equals(allstates[i].getState_name())) {
                                            spinner_state.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (allcities != null) {
                                if (response.getCity().getCity_name() != null) {
                                    for (int i = 0; i < allcities.length; i++) {
                                        if (response.getCity().getCity_name().equals(allcities[i].getCity_name())) {
                                            spinner_city.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }, recycler_buyeraddress, Fragment_CashPayment.this, true, finalOrderAddressid);
                    recycler_buyeraddress.setAdapter(buyerAddressAdapter);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
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

    public String getCityName(String id) {
        if (allcities != null) {
            for (int i = 0; i < allcities.length; i++) {
                if (id.equals(allcities[i].getId())) {
                    return allcities[i].getCity_name();
                }
            }
        }
        return null;
    }

    public String getStateName(String id) {
        if (allstates != null) {
            for (int i = 0; i < allstates.length; i++) {
                if (id.equals(allstates[i].getId())) {
                    return allstates[i].getState_name();
                }
            }
        }
        return null;
    }

    @OnClick(R.id.btn_add_address)
    public void showNewAddress() {
        if (isAddAddressShow) {
            isAddAddressShow = false;
            linearAddAddress.setVisibility(View.GONE);
        } else {
            isAddAddressShow = true;
            linearAddAddress.setVisibility(View.VISIBLE);
            editAddLine1.requestFocus();
            scrollView.scrollBy(0, 200);
        }
    }

    @OnClick(R.id.btn_cancel_address)
    public void hideAddAddress() {
        isAddAddressShow = false;
        linearAddAddress.setVisibility(View.GONE);
        editAddLine1.setText("");
        editPincode.setText("");
        cityId = "";
        stateId = "";
        State = "";
        City = "";
    }

    public void getShippingCharges(String orderid) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.SHIPPING_CHARGE + "?order=" + orderid, null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    final ArrayList<ResponseShipment> shipments = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseShipment>>() {
                    }.getType());
                    addSubShipment(shipments, linear_shipping);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (error.getErrorkey().contains("Pincode")) {
                    Animation shake;
                    StaticFunctions.showResponseFailedDialog(error);
                }


            }
        });
    }

    private void addSubShipment(ArrayList<ResponseShipment> shipments, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        CompoundButton.OnCheckedChangeListener shipment_checked_listner = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (Map.Entry<ResponseShipment, RadioButton> entry : radioGroupShipping.entrySet()) {
                        ResponseShipment key = entry.getKey();
                        if (compoundButton == radioGroupShipping.get(key)) {
                            try {
                                String addressid = null;
                                ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) order.getShip_to())), ShippingAddressResponse.class);
                                if (ship != null) {
                                    addressid = ship.getId();
                                    shipping_charge = String.valueOf(entry.getKey().getShipping_charge());
                                    patchShipping(addressid, entry.getKey().getShipping_charge(), entry.getKey().getShipping_method_id());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            radioGroupShipping.get(key).setChecked(false);
                        }
                    }

                }
            }
        };
        for (int i = 0; i < shipments.size(); i++) {
            View v = vi.inflate(R.layout.shipping_item, null);
            RadioButton rb = v.findViewById(R.id.ship_method);
            RelativeLayout relative_sub_ship_charges = v.findViewById(R.id.relative_sub_ship_charges);
            TextView txt_sub_charge = v.findViewById(R.id.txt_sub_charge);
            RelativeLayout relative_sub_ship_duration = v.findViewById(R.id.relative_sub_ship_duration);
            TextView txt_shipping_duration = v.findViewById(R.id.txt_shipping_duration);

            rb.setText(shipments.get(i).getShipping_method_name());

            txt_sub_charge.setText("" + shipments.get(i).getShipping_charge());


            if (shipments.get(i).getShipping_method_duration() != null && !shipments.get(i).getShipping_method_duration().isEmpty()) {
                txt_shipping_duration.setText(shipments.get(i).getShipping_method_duration());
                relative_sub_ship_duration.setVisibility(View.VISIBLE);
            }


            rb.setOnCheckedChangeListener(shipment_checked_listner);
            radioGroupShipping.put(shipments.get(i), rb);
            if (shipments.get(i).isIs_default()) {
                rb.setChecked(true);
            }
            root.addView(v);
        }

    }

    private void calculateShipping(boolean isWishBook) {

        if (isWishBook) {
            if (shipping_charge != null && !shipping_charge.equals("")) {
                Float newTotalAmount = Float.parseFloat(orginalTotalAmout) + Float.parseFloat(shipping_charge);
                invoice.setTotal_amount(String.valueOf(newTotalAmount));
                linear_shipping_container.setVisibility(View.VISIBLE);
                txt_shipping_amount.setText("+" + "\u20B9" + shipping_charge);
                Log.i("TAG", "onCheckedChanged: " + newTotalAmount);
            } else {
                invoice.setTotal_amount(orginalTotalAmout);
            }

        }

    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    private void changeCodAmount() {
        calculateCod();
        if (textViewCodNote != null) {
            float cod_amount = Float.parseFloat(codAmount);
            float remain_amount = Float.parseFloat(invoice.getPending_amount()) - cod_amount;


            String s = String.format(getResources().getString(R.string.cod_sub_text), String.valueOf(cod_amount), String.valueOf(remain_amount));
            SpannableString ss = new SpannableString(s);


            textViewCodNote.setText(ss);
            textViewCodNote.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(textViewCodNote, Linkify.WEB_URLS);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(StaticFunctions.dpToPx(mContext, 30), 0, 0, StaticFunctions.dpToPx(mContext, 20));
            textViewCodNote.setLayoutParams(params);
        }
    }

    private void calculateCod() {
      /*  float cod = Float.parseFloat(invoice.getPending_amount()) * 10 / 100;
        if (cod > 500)
            codAmount = String.valueOf(cod);
        else
            codAmount = "500";*/
    }


    /**
     * CashFree Payment  page initialize
     *
     * @param orderID
     * @param orderAmt
     * @param orderNote
     * @param customerName
     * @param customerPhone
     * @param customerEmail
     */
    public void initCashFree(String orderID, String orderAmt, String orderNote, String customerName, String customerPhone, String customerEmail) {
        // Flow Changes first get Token from server and after redirect to Payment Gateway
        getCashFreeToken(orderID, orderAmt, orderNote, customerName, customerPhone, customerEmail);
    }

    public void handleCashFreeResponse(Bundle bundle) {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            if (bundle.getString(key) != null) {
                map.put(key, bundle.getString(key));
                Log.d("TAG", key + " : " + bundle.getString(key));
            }
        }

        if (map.containsKey("txStatus")) {
            if (map.get("txStatus").equalsIgnoreCase("SUCCESS")) {
                onSuccess(map);
            } else if (map.get("txStatus").equalsIgnoreCase("FAILED")) {
                onFailure(map);
            }
        }

    }

    public void getCashFreeToken(String orderID, String orderAmt, String orderNote,
                                 String customerName, String customerPhone, String customerEmail) {
        String url = URLConstants.CASHFREE_TOKEN;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_id", orderID);
        params.put("order_amount", orderAmt);
        HttpManager.getInstance(getActivity()).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    CashFreeTokenResponse cashFreeTokenResponse = Application_Singleton.gson.fromJson(response, CashFreeTokenResponse.class);
                    if (cashFreeTokenResponse.getCftoken() != null && !cashFreeTokenResponse.getCftoken().isEmpty()) {
                        String stage = "TEST";
                        if (BuildConfig.DEBUG) {
                            stage = "TEST";
                        } else {
                            stage = "PROD";
                        }


                        Map<String, String> params = new HashMap<>();
                        params.put(PARAM_APP_ID, BuildConfig.CASHFREE_APPID);
                        params.put(PARAM_ORDER_ID, orderID);
                        params.put(PARAM_ORDER_AMOUNT, orderAmt);
                        params.put(PARAM_ORDER_NOTE, orderNote);
                        params.put(PARAM_CUSTOMER_NAME, customerName);
                        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
                        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);
                        params.put(PARAM_NOTIFY_URL, URLConstants.CASHFREE_RETURN);
                        params.put(PARAM_PAYMENT_MODES, "");

                        for (Map.Entry entry : params.entrySet()) {
                            Log.d("CFSKDSample", entry.getKey() + " " + entry.getValue());
                        }
                        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                        cfPaymentService.setOrientation(getActivity(), 0);
                        cfPaymentService.doPayment(getActivity(), params, cashFreeTokenResponse.getCftoken(), stage, "#0d80c1", "#FFFFFF");
                        //cfPaymentService.doPayment(getActivity(), params, URLConstants.CASHFREE_CHECKSUM, Fragment_Payment.this, stage);
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
    /**
     * Cashfree Payment Sucess Response Handle
     *
     * @param map
     */
    public void onSuccess(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");

        String orderId = "", status = "", amt = "";
        if (map.containsKey("orderId")) {
            orderId = map.get("orderId");
        }
        if (map.containsKey("txStatus")) {
            status = map.get("txStatus");
        }
        if (map.containsKey("orderAmount")) {
            amt = map.get("orderAmount");
        }
        String response =
                "OrderID : " + orderId + "\n"
                        + "Status : " + status + "\n"
                        + "Amount : " + amt + "\n";
        try {
            if (mContext != null) {
                new MaterialDialog.Builder(mContext)
                        .title("Transaction Success")
                        .content(response)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                                getActivity().finish();
                            }
                        }).positiveText("Ok").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cashfree Payment Failure Response Handle
     *
     * @param map
     */
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        if (mContext != null) {
            try {
                if (mContext != null) {
                    new MaterialDialog.Builder(mContext)
                            .title("Transaction Failure")
                            .content("")
                            .cancelable(false)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                                    getActivity().finish();
                                }
                            }).positiveText("Ok").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onNavigateBack() {
        Log.d("CFSDKSample", "Back Pressed");
        //new MaterialDialog.Builder(mContext).title("Transaction Failure due to back pressed").content("").positiveText("Ok").show();
    }

    private void showCODDialog() {
        //SPANNABLE STRING
        calculateCod();
        float cod_amount = Float.parseFloat(codAmount);
        float remain_amount = Float.parseFloat(invoice.getPending_amount()) - cod_amount;
        String s = String.format(getResources().getString(R.string.cod_dialog_text1), String.valueOf(cod_amount), String.valueOf(remain_amount));
        SpannableString ss = new SpannableString(s);
        final String url = "http://www.wishbook.io/cod-tnc";
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
            ss.setSpan(clickableSpan, s.indexOf("*COD"), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (UserInfo.getInstance(getActivity()).getLanguage().equals("hi")) {
            ss.setSpan(clickableSpan, s.indexOf("*COD"), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.cod_dialog_title1))
                .content(ss)
                .cancelable(false)
                .positiveText(getResources().getString(R.string.cod_dialog_poistive1))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        if (textViewCodNote != null)
                            textViewCodNote.setVisibility(View.VISIBLE);
                    }
                })
                .negativeText(getResources().getString(R.string.cod_dialog_negative1))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        try {
                            ((RadioButton) radioGroupPayment.getChildAt(1)).setChecked(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .show();


    }

    public String getCurrentDate() {
        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        Date date = new Date();
        return sdf.format(date);
    }

    private void callOrderPayment(String type, String mode, String details, String date, String amount, String orderID) {
        final String url;
        if (type.equals("invoice")) {
            url = URLConstants.companyUrl(getActivity(), "invoice_payment", orderID);
        } else {
            url = URLConstants.companyUrl(getActivity(), "payment", orderID);
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mode", mode);
        params.put("details", details);
        params.put("date", date);
        params.put("amount", amount);
        HttpManager.getInstance(getActivity()).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Application_Singleton.trackEvent("PayEnterDetails", "Click", "PayEnterDetails");
                setOrderPending(false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getCreditLine(final RadioButton radioButton) {
        String url = URLConstants.companyUrl(getActivity(), "credit-approved-line", "");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        responseCreditLines = Application_Singleton.gson.fromJson(response, ResponseCreditLines[].class);
                        setCreditLine(responseCreditLines, radioButton, false);
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

    public View setCreditLine(ResponseCreditLines[] responseCreditLines, RadioButton radioButton, boolean isAfterApplyCredit) {
        View v = wishbook_credit_sub_view;
        LinearLayout credit_apply_state = v.findViewById(R.id.credit_apply_state);
        LinearLayout credit_processing_state = v.findViewById(R.id.credit_processing_state);
        LinearLayout credit_final_state = v.findViewById(R.id.credit_final_state);
        TextView txt_credit_line_detail = v.findViewById(R.id.txt_credit_line_detail);
        TextView txt_credit_line_error = v.findViewById(R.id.txt_credit_line_error);
        TextView txt_processing_state = v.findViewById(R.id.txt_processing_state);
        final AppCompatButton txt_apply_credit = v.findViewById(R.id.txt_apply_credit);

        String s = getActivity().getResources().getString(R.string.wishbook_credit_state2);
        SpannableString ss = new SpannableString(getActivity().getResources().getString(R.string.wishbook_credit_state2).toString());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity())));
                getActivity().startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        try {
            ss.setSpan(clickableSpan, s.indexOf(PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity())), s.indexOf(PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity())) + 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        txt_processing_state.setText(ss);
        txt_processing_state.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(txt_processing_state, Linkify.WEB_URLS);

        if (responseCreditLines != null && responseCreditLines.length > 0 && responseCreditLines[0].getIs_active().equalsIgnoreCase("true")) {
            credit_final_state.setVisibility(View.VISIBLE);
            if (Double.parseDouble(invoice.getPending_amount()) >= Double.parseDouble(responseCreditLines[0].getAvailable_line())) {
                if (radioButton != null)
                    radioButton.setEnabled(false);
                txt_credit_line_error.setVisibility(View.VISIBLE);
                txt_credit_line_detail.setVisibility(View.VISIBLE);
                String detail = "<font>Approved Limit: \u20B9" + responseCreditLines[0].getApproved_line() + "<br /><br />Used Limit:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   \u20B9" + responseCreditLines[0].getUsed_line() + "<br /><br />Available Limit: \u20B9" + responseCreditLines[0].getAvailable_line() + "</font>";
                txt_credit_line_detail.setText(Html.fromHtml(detail), TextView.BufferType.SPANNABLE);
            } else {
                if (radioButton != null)
                    radioButton.setEnabled(true);
                txt_credit_line_error.setVisibility(View.GONE);
                String detail = "<font>Approved Limit: \u20B9" + responseCreditLines[0].getApproved_line() + "<br /><br />Used Limit:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   \u20B9" + responseCreditLines[0].getUsed_line() + "<br /><br />Available Limit: \u20B9" + responseCreditLines[0].getAvailable_line() + "</font>";
                txt_credit_line_detail.setText(Html.fromHtml(detail), TextView.BufferType.SPANNABLE);
            }
        } else {
            if (isAfterApplyCredit) {
                if (radioButton != null)
                    radioButton.setEnabled(false);
                credit_apply_state.setVisibility(View.GONE);
                credit_processing_state.setVisibility(View.VISIBLE);
            } else {
                if (UserInfo.getInstance(getActivity()).getCreditScore().equals("null")) {
                    // Add Mode Apply
                    if (radioButton != null)
                        radioButton.setEnabled(false);
                    credit_apply_state.setVisibility(View.VISIBLE);
                    txt_apply_credit.setText("APPLY NOW");
                } else {
                    // Edit Mode Apply
                    if (radioButton != null)
                        radioButton.setEnabled(false);
                    credit_processing_state.setVisibility(View.VISIBLE);

               /* if (Integer.parseInt(UserInfo.getInstance(getActivity()).getCreditScore()) > 0) {

                } else {
                    // CRIF not apply, so edit mode
                    if(radioButton!=null)
                        radioButton.setEnabled(false);
                    credit_processing_state.setVisibility(View.VISIBLE);
                    txt_apply_credit.setText("EDIT");
                }*/


                }
            }

        }

        txt_apply_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("type", "credit_rating");
                if (txt_apply_credit.getText().toString().equalsIgnoreCase("APPLY NOW")) {
                    hashmap.put("isEdit", "false");
                } else {
                    hashmap.put("isEdit", "true");
                }
                new DeepLinkFunction(hashmap, getActivity());
            }
        });

        return v;

    }

    public void patchWBMoney(final String wb_money) {
        final MaterialDialog progress = StaticFunctions.showProgress(getActivity());
        progress.setCancelable(false);
        SalesOrderCreate order = new SalesOrderCreate();
        order.setWbmoney_points_used(wb_money);
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        url = "create_invoice";
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + (invoice.getId()) + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progress != null) {
                    progress.dismiss();
                }
                Gson gson = new Gson();
                Invoice invoice_temp = Application_Singleton.gson.fromJson(response, Invoice.class);
                invoice = invoice_temp;
                updateInvoiceUI();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress != null) {
                    progress.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getWBMoneyDashboard() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "wbmoney-log-dashboard", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
               /* try {
                    final ResponseWBMoneyDashboard dashboard = Application_Singleton.gson.fromJson(response, new TypeToken<ResponseWBMoneyDashboard>() {
                    }.getType());

                    if (dashboard.getTotal_available() != null && Integer.parseInt(dashboard.getTotal_available()) > 0) {
                        linear_apply_wb_money.setVisibility(View.VISIBLE);
                        txt_available_wb_money.setText(dashboard.getTotal_available() + " WB Money available");
                        chk_wb_money.setEnabled(true);
                        chk_wb_money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                                if (isCheck) {
                                    if (Integer.parseInt(dashboard.getTotal_available()) > Double.parseDouble(invoice.getPending_amount())) {
                                        // cart total amount apply
                                        patchWBMoney(String.valueOf(Math.round(Double.parseDouble(invoice.getPending_amount()))));
                                    } else {
                                        // all wb available amount apply
                                        patchWBMoney(dashboard.getTotal_available());
                                    }

                                } else {
                                    patchWBMoney("0");
                                }
                            }
                        });
                    } else {
                        chk_wb_money.setEnabled(false);
                        linear_apply_wb_money.setVisibility(View.VISIBLE);
                        txt_available_wb_money.setText(dashboard.getTotal_available() + " WB Money available");
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void updateInvoiceUI() {

        txtPayableAmt.setText("\u20B9" + invoice.getPending_amount());
        if (invoice.getShipping_charges() != null && !invoice.getShipping_charges().equals("0.0")) {
            linear_shipping_container.setVisibility(View.VISIBLE);
            txt_shipping_amount.setText("+" + "\u20B9" + shipping_charge);
        } else {
            linear_shipping_container.setVisibility(View.GONE);
        }

        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
            RadioButton radioButton = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId()));
            Log.e(TAG, "updateInvoiceUI: =====>" + check);
            if (check.equalsIgnoreCase("CREDIT")) {
                if (textViewCodNote != null) {
                    textViewCodNote.setVisibility(View.GONE);
                }
                if (creditDiscount != null) {
                    updatePaymentAmt();
                } else {
                    creditDiscount = "0";
                    updatePaymentAmt();
                }
            } else {
                Log.e(TAG, "updatePaymentAmt 2: ===>");
                txtPayableAmt.setText("\u20B9" + String.valueOf(Double.parseDouble(invoice.getTotal_amount())));
            }
        } else {
            Log.e(TAG, "updatePaymentAmt 1: ===>");
            txtPayableAmt.setText("\u20B9" + String.valueOf(Double.parseDouble(invoice.getTotal_amount())));
        }

        updateWBMoneyUI();

    }

    public void updatePaymentAmt() {
        float creditdiscountamt = (float) (Float.parseFloat(invoice.getAmount()) * (Float.parseFloat(creditDiscount) + order.getSeller_extra_discount_percentage())) / 100;
        if (creditdiscountamt != 0.00) {
            relativeSellerDiscount.setVisibility(View.VISIBLE);
            discount_type.setVisibility(View.VISIBLE);
            if (order.getSeller_extra_discount_percentage() > 0) {
                discount_type.setText("(Credit " + Float.parseFloat(creditDiscount) + "%+" + order.getSeller_extra_discount_percentage() + "%)");
            } else {
                discount_type.setText("(Credit " + Float.parseFloat(creditDiscount) + "%)");
            }

            txtSellerDiscount.setText("- " + "\u20B9" + String.valueOf(creditdiscountamt));
        } else {
            relativeSellerDiscount.setVisibility(View.GONE);
        }

        if (shipping_charge != null && !shipping_charge.equals("")) {
            linear_shipping_container.setVisibility(View.VISIBLE);
            txt_shipping_amount.setText("+" + "\u20B9" + shipping_charge);
        }

        if (shipping_charge != null && !shipping_charge.equals("")) {
            txtPayableAmt.setText("\u20B9" + String.valueOf(Float.parseFloat(invoice.getPending_amount())
                    + Float.parseFloat(invoice.getSeller_discount())
                    + invoice.getWbmoney_points_used()));
        } else {
            txtPayableAmt.setText("\u20B9" + String.valueOf(Float.parseFloat(invoice.getPending_amount())
                    - +Float.parseFloat(invoice.getSeller_discount()) + invoice.getWbmoney_points_used()));
        }
    }

    public void updateWBMoneyUI() {

        if (invoice.getWbmoney_redeem_amt() > 0) {

            if(invoice.getWbmoney_points_used()>0){
                linear_invoice_wb_money.setVisibility(View.VISIBLE);
                invoice_wb_money.setText("- " + "\u20B9 " + invoice.getWbmoney_points_used());
            }

            if(invoice.getWbpoints_used()>0){
                linear_invoice_reward_point.setVisibility(View.VISIBLE);
                invoice_reward_point.setText("- " + "\u20B9 " + invoice.getWbpoints_used());
            }

            linear_now_pay.setVisibility(View.VISIBLE);
            now_pay_amount.setText("\u20B9 " + (invoice.getPending_amount()));
        } else {
            linear_invoice_wb_money.setVisibility(View.GONE);
            linear_invoice_reward_point.setVisibility(View.GONE);
            linear_now_pay.setVisibility(View.GONE);
        }
    }


}
