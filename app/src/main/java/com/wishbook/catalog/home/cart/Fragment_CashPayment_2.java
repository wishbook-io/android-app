package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchAccept;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.CashFreeTokenResponse;
import com.wishbook.catalog.commonmodels.responses.PaymentMethod;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditLines;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.details.Fragment_Pay;
import com.wishbook.catalog.home.payment.MerchantActivity2;
import com.wishbook.catalog.home.payment.MobiKwikMerchantActivity;
import com.wishbook.catalog.home.payment.ZaakPayMerchantActivity;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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


public class Fragment_CashPayment_2 extends GATrackedFragment  {

    private static final float AMOUNT_LIMIT_PAYMENT_OPTION = 10000;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;


    @BindView(R.id.card_shipping_details)
    CardView card_shipping_details;
    @BindView(R.id.linear_shipping_details)
    LinearLayout linear_shipping_details;

    @BindView(R.id.cart_date)
    TextView cart_date;

    @BindView(R.id.total_order_amount)
    TextView total_order_amount;

    @BindView(R.id.discount_amount)
    TextView discount_amount;

    @BindView(R.id.layout_discount)
    LinearLayout layout_discount;

    @BindView(R.id.gst_amount)
    TextView gst_amount;

    @BindView(R.id.delivery_amount)
    TextView delivery_amount;

    @BindView(R.id.layout_delivery)
    LinearLayout layout_delivery;

    @BindView(R.id.total_amount)
    TextView total_amount;


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
    @BindView(R.id.edit_name)
    EditText edit_name;
    @BindView(R.id.edit_mobile_number)
    EditText edit_mobile_number;
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
    @BindView(R.id.invoice_detail)
    TextView invoice_detail;


    TextView buy_on_credit_note, cod_500_note;

    @BindView(R.id.linear_shipping)
    LinearLayout linear_shipping;


    @BindView(R.id.payable_amount)
    TextView payable_amount;

    @BindView(R.id.linear_now_pay)
    LinearLayout linear_now_pay;

    @BindView(R.id.linear_invoice_wb_money)
    LinearLayout linear_invoice_wb_money;

    @BindView(R.id.invoice_wb_money)
    TextView invoice_wb_money;


    // Start Reseller View


    @BindView(R.id.txt_change_resale_amt)
    TextView txt_change_resale_amt;

    @BindView(R.id.linear_reseller_dialog)
    LinearLayout linear_reseller_dialog;

    @BindView(R.id.card_reseller_order)
    CardView card_reseller_order;

    @BindView(R.id.switch_resale_order)
    Switch switch_resale_order;


    @BindView(R.id.txt_resale_order_amount)
    TextView txt_resale_order_amount;

    @BindView(R.id.txt_total_resale_amount)
    TextView txt_total_resale_amount;

    @BindView(R.id.txt_switch_reseller_note)
            TextView txt_switch_reseller_note;

    @BindView(R.id.linear_invoice_reward_point)
            LinearLayout linear_invoice_reward_point;

    @BindView(R.id.invoice_reward_point)
            TextView invoice_reward_point;

    boolean isResellerAmountSet;


    Invoice invoice;
    ShippingAddressResponse[] shippingAddressResponses;
    ArrayList<ShippingAddressResponse> addressResponses;
    ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList;
    AddressAdapter addressAdapter;
    AddressAdapter buyerAddressAdapter;
    String shipping_charge;
    String orginalTotalAmout;
    boolean isBrokerageOrder;
    TextView textViewCodNote;
    public CartCatalogModel cart;
    private View view;
    private Context mContext;
    private Response_buyingorder order;
    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private String State = "";
    private String City = "";
    private String stateId = "";
    private String cityId = "";
    private boolean isAddAddressShow;
    private String codAmount;

    View wishbook_credit_sub_view;
    ResponseCreditLines[] responseCreditLines;
    RadioButton radioWishbookCredit;

    String total_resale_amt = "0.0";

    HashMap<ResponseShipment, RadioButton> radioGroupShipping = new HashMap<>();


    public static String TAG = Fragment_CashPayment_2.class.getSimpleName();


    public Fragment_CashPayment_2() {

    }


    // pass isBrokeOrder
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_cash_payment_2, ga_container, true);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        mContext = getActivity();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recyclerAddress.setLayoutManager(layoutManager1);
        recyclerAddress.setHasFixedSize(false);
        recyclerAddress.setNestedScrollingEnabled(false);
        buy_on_credit_note = new TextView(getActivity());
        cod_500_note = new TextView(getActivity());

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_buyeraddress.setLayoutManager(layoutManager2);
        recycler_buyeraddress.setHasFixedSize(false);
        recycler_buyeraddress.setNestedScrollingEnabled(false);

        addressResponses = new ArrayList<>();
        isAddAddressShow = false;
        linearAddAddress.setVisibility(View.GONE);
        getstates();

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

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wishbook_credit_sub_view = vi.inflate(R.layout.wishbook_credit_state, null);
        getCartData((AppCompatActivity) getActivity());
        assert getArguments() != null;
        return view;
    }

    public void initResaleView() {
        txt_change_resale_amt.setPaintFlags(txt_change_resale_amt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_change_resale_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortBottom(false);
            }
        });

        if (cart != null) {
            txt_resale_order_amount.setText("\u20B9 " + cart.getTotal_amount());
        }

        if (total_resale_amt != null) {
            txt_total_resale_amount.setText("\u20B9 " + total_resale_amt);
        } else {
            txt_total_resale_amount.setText("\u20B9 " + 0);
        }

        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
            String mode = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
            if (switch_resale_order.isChecked()) {
                linear_reseller_dialog.setVisibility(View.VISIBLE);
            } else {
                linear_reseller_dialog.setVisibility(View.GONE);
            }
        }
    }

    private void payVia(String type, final String mode, double amount, final String cartID) {
        Fragment_Pay payFragment = new Fragment_Pay();
        final String url;
        url = URLConstants.companyUrl(getActivity(), "cart-payment", cartID);
        if (amount > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", String.valueOf(amount));
            bundle.putString("type", mode);
            payFragment.setArguments(bundle);
        }
        payFragment.setListener(new Fragment_Pay.Listener() {
            @Override
            public void onDismiss(String date, String details, String amount) {
                callCartPayment(mode, details, date, amount, cartID);
            }
        });


        payFragment.show(getFragmentManager(), "pay");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Cash Payment ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            try {
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
                        ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
                        clearCartPref();
                        try {
                            Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                sendChekoutCompledAnalytics(status.equals("Failure") ? false : true, "PAYTM");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {

            try {
                String response = null;
                String orderID = data.getStringExtra("mOrderID");
                String amount = data.getStringExtra("mOrderAmount");
                String status = data.getStringExtra("transaction");
                if (status != null) {
                    response =
                            "OrderID : " + orderID + "\n"
                                    + "Status : " + status + "\n"
                                    + "Amount : " + amount + "\n";


                    String title_text = "";
                    if (status.equals("Failure")) {
                        if (mContext != null) {
                            title_text = "Transaction Failure";
                        }
                    } else {
                        title_text = "Transaction Success";
                    }

                    sendChekoutCompledAnalytics(status.equals("Failure") ? false : true, "ZAAKPAY");

                    if (mContext != null) {
                        new MaterialDialog.Builder(mContext)
                                .title(title_text)
                                .content(response)
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
                                        clearCartPref();
                                        try {
                                            Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        getActivity().finish();
                                    }
                                }).positiveText("Ok").show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 9800 && resultCode == Activity.RESULT_OK) {
            try {
                if (data.getSerializableExtra("param") != null) {
                    total_resale_amt = String.valueOf(data.getDoubleExtra("total_resale_amt", 0.0));
                    initResaleView();
                    HashMap<String, String> hashMap = (HashMap<String, String>) data.getSerializableExtra("param");
                    for (int i = 0; i < cart.getCatalogs().size(); i++) {
                        if (hashMap.containsKey("item_price_" + i)) {
                            cart.getCatalogs().get(i).setCatalog_display_amount(Double.parseDouble(hashMap.get("item_price_" + i)));
                        }

                    }
                    isResellerAmountSet = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void callCartPayment(final String mode, String details, String date, String amount, String cartID) {
        String url = URLConstants.companyUrl(getActivity(), "cart-payment", cartID);
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
                try {
                    sendChekoutCompledAnalytics(true, mode.toUpperCase());
                    Application_Singleton.trackEvent("PayEnterDetails", "Click", "PayEnterDetails");
                    ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
                    clearCartPref();
                    try {
                        Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((AppCompatActivity) mContext).finish();
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

    public void fetchPayment(final String orderId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("cart", orderId);
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
                try {
                    hideProgress();
                    PaymentMethod[] paymentMethods = Application_Singleton.gson.fromJson(response, PaymentMethod[].class);


                    ArrayList<PaymentMethod> creditPyament = new ArrayList<PaymentMethod>();
                    ArrayList<PaymentMethod> offlinePayment = new ArrayList<PaymentMethod>();
                    ArrayList<PaymentMethod> onlinePayment = new ArrayList<PaymentMethod>();
                    ArrayList<PaymentMethod> codPayment = new ArrayList<PaymentMethod>();

                    if (cart.getPending_amount() < 1) {
                        PaymentMethod paymentMethod = new PaymentMethod();
                        paymentMethod.setDisplay_name("OTHER");
                        paymentMethod.setId("100");
                        paymentMethod.setName("Other");
                        paymentMethod.setPayment_type("Offline");
                        offlinePayment.add(paymentMethod);

                        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 30);
                        radioGroupPayment.removeAllViews();

                        for (int i = 0; i < offlinePayment.size(); i++) {
                            if (i == 0) {
                                TextView textView = new TextView(getActivity());
                                textView.setText(offlinePayment.get(i).getPayment_type());
                                radioGroupPayment.addView(textView);
                            }

                            RadioButton radioButtonView = new RadioButton(getActivity());
                            radioButtonView.setPadding(40, 0, 0, 0);
                            radioButtonView.setId(Integer.parseInt(offlinePayment.get(i).getId()));
                            radioButtonView.setText(offlinePayment.get(i).getDisplay_name());
                            radioButtonView.setLayoutParams(lp);
                            radioButtonView.setTag(offlinePayment.get(i).getName());
                            radioGroupPayment.addView(radioButtonView);
                        }


                        btnPaymentCredit.setVisibility(View.GONE);
                        btnPayment.setVisibility(View.VISIBLE);
                        btnPayment.setText("Done");

                    } else {

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
                            if (cart.getPending_amount() > AMOUNT_LIMIT_PAYMENT_OPTION) {
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

                            if (cart.getPending_amount() < 100) {
                                radioButtonView.setEnabled(false);
                                cod_500_note.setVisibility(View.VISIBLE);
                            }
                        }

                        for (int i = 0; i < offlinePayment.size(); i++) {
                            if (i == 0) {
                                TextView textView = new TextView(getActivity());
                                textView.setText(offlinePayment.get(i).getPayment_type());
                                radioGroupPayment.addView(textView);
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
                                radioButtonView.setText(creditPyament.get(i).getDisplay_name());
                            } else {
                                radioButtonView.setText(creditPyament.get(i).getDisplay_name());
                            }
                            radioButtonView.setTag(creditPyament.get(i).getName());
                            RadioGroup.LayoutParams lp1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp1.setMargins(0, 0, 0, 5);
                            radioButtonView.setLayoutParams(lp1);
                            radioGroupPayment.addView(radioButtonView);
                            if (creditPyament.get(i).getName() != null && !creditPyament.get(i).getName().equalsIgnoreCase("WISHBOOKCREDIT")) {
                                buy_on_credit_note = new TextView(getActivity());
                                buy_on_credit_note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                buy_on_credit_note.setPadding(50, 0, 0, 0);
                                buy_on_credit_note.setTextColor(getResources().getColor(R.color.purchase_medium_gray));
                                buy_on_credit_note.setText(getResources().getString(R.string.buy_on_credit_cart_note));
                                radioGroupPayment.addView(buy_on_credit_note);
                            }

                            if (creditPyament.get(i).getName().equalsIgnoreCase("WISHBOOKCREDIT")) {
                                wishbook_credit_sub_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                getCreditLine(radioButtonView);
                                radioButtonView.setEnabled(false);
                                radioGroupPayment.addView(wishbook_credit_sub_view);
                            }


                        }
                    }


                    radioGroupPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                            if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                                initResaleView();
                                String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
                                RadioButton radioButton = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId()));
                                if (check.equalsIgnoreCase("CREDIT") || check.equalsIgnoreCase("WISHBOOKCREDIT")) {
                                    if (textViewCodNote != null) {
                                        textViewCodNote.setVisibility(View.GONE);
                                    }


                                    btnPaymentCredit.setVisibility(View.VISIBLE);
                                    btnPayment.setVisibility(View.GONE);
                                    buy_on_credit_note.setVisibility(View.VISIBLE);
                                    if (check.equalsIgnoreCase("WISHBOOKCREDIT")) {
                                        buy_on_credit_note.setVisibility(View.GONE);
                                    }
                                    updateCartInvoiceUI();

                                } else {
                                    if (buy_on_credit_note != null)
                                        buy_on_credit_note.setVisibility(View.GONE);
                                    // updateCartInvoiceUI();
                                    if (textViewCodNote != null) {
                                        textViewCodNote.setVisibility(View.GONE);
                                    }
                                    if (check.equalsIgnoreCase("NEFT") || check.equalsIgnoreCase("OTHER") || check.equalsIgnoreCase("CHEQUE")) {
                                        btnPayment.setText("Enter Payment Details");
                                    } else {
                                        btnPayment.setText("Proceed for Payment");
                                    }

                                    if (check.equalsIgnoreCase("cod")) {
                                        showCODDialog();
                                    }
                                    updateCartInvoiceUI();
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
                            if (cart.getShip_to() == null) {
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
                                    String ship = cart.getShip_to().toString();
                                    if (ship != null) {
                                        addressid = ship;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            if (switch_resale_order.isChecked()) {
                                if (total_resale_amt == null) {
                                    Toast.makeText(getActivity(), "Please Enter Resell Amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DecimalFormat decimalFormat = new DecimalFormat("#0.##");
                                String cartTotal = String.valueOf(decimalFormat.format(cart.getTotal_amount()));
                                if (total_resale_amt != null && Double.parseDouble(total_resale_amt) < Double.parseDouble(cartTotal)) {
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error), Toast.LENGTH_SHORT).show();
                                    openSortBottom(true);
                                    return;
                                }
                                patchReseller(true, total_resale_amt);
                            } else {
                                patchReseller(false, "0");
                            }
                            btnPaymentCredit.setVisibility(View.GONE);
                            setLogistick1(addressid, shipping_charge);


                        }
                    });
                    btnPayment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String addressid = "";
                            if (cart.getShip_to() == null) {
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
                                    String ship = cart.getShip_to().toString();
                                    if (ship != null) {
                                        addressid = ship;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            setLogistick1(addressid, shipping_charge);
                            if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                                String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                                String mode = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
                                if (switch_resale_order.isChecked()) {
                                    if (total_resale_amt == null) {
                                        Toast.makeText(getActivity(), "Please Enter Resell Amount", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    DecimalFormat decimalFormat = new DecimalFormat("#0.##");
                                    String cartTotal = String.valueOf(decimalFormat.format(cart.getTotal_amount()));
                                    if (total_resale_amt != null && Double.parseDouble(total_resale_amt) < Double.parseDouble(cartTotal)) {
                                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error), Toast.LENGTH_SHORT).show();
                                        openSortBottom(true);
                                        return;
                                    }
                                    patchReseller(true, total_resale_amt);
                                } else {
                                    patchReseller(false, "0");
                                }
                                switch (check) {
                                    case "ZAAKPAY":
                                        sendChekoutPaymentIntializedAnalytics("ZAAKPAY");
                                        Application_Singleton.trackEvent("PAYZAAKPAY", "Click", "PAYZAAKPAY");
                                        Intent intentToZaakpay = new Intent(getActivity(), ZaakPayMerchantActivity.class);
                                        intentToZaakpay.putExtra("orderid", "C" + cart.getId());
                                        intentToZaakpay.putExtra("order_amount", "" + cart.getPending_amount());
                                        Fragment_CashPayment_2.this.startActivityForResult(intentToZaakpay, 3);
                                        break;

                                    case "CASHFREE":
                                        sendChekoutPaymentIntializedAnalytics("CASHFREE");
                                        Application_Singleton.trackEvent("PAYCASHFREE", "Click", "PAYCASHFREE");
                                        String name = UserInfo.getInstance(getActivity()).getFirstName();
                                        String mContact = UserInfo.getInstance(getActivity()).getMobile();
                                        String mEmail = UserInfo.getInstance(getActivity()).getEmail();
                                        initCashFree("C" + cart.getId(), String.valueOf(cart.getPending_amount()), cart.getId(), name, mContact, mEmail);
                                        break;
                                    case "COD":
                                        sendChekoutPaymentIntializedAnalytics("COD");
                                        if (codAmount != null) {
                                            Application_Singleton.trackEvent("PAYCASHFREE", "Click", "COD");
                                            String name2 = UserInfo.getInstance(getActivity()).getFirstName();
                                            String mContact2 = UserInfo.getInstance(getActivity()).getMobile();
                                            String mEmail2 = UserInfo.getInstance(getActivity()).getEmail();
                                            initCashFree("C" + cart.getId(), String.valueOf(codAmount), cart.getId(), name2, mContact2, mEmail2);
//                                            Intent intentToCOD = new Intent(getActivity(), ZaakPayMerchantActivity.class);
//                                            intentToCOD.putExtra("orderid", "C" + cart.getId());
//                                            intentToCOD.putExtra("order_amount", codAmount);
//                                            Fragment_CashPayment_2.this.startActivityForResult(intentToCOD, 3);
                                        }
                                        break;
                                    case "PAYTM":
                                        sendChekoutPaymentIntializedAnalytics("PAYTM");
                                        Application_Singleton.trackEvent("PAYPAYTM", "Click", "PAYPAYTM");
                                        Intent i = new Intent(getActivity(), MerchantActivity2.class);
                                        i.putExtra("orderid", "cart" + cart.getId());
                                        i.putExtra("order_amount", "" + String.valueOf(cart.getPending_amount()));
                                        Fragment_CashPayment_2.this.startActivityForResult(i, 2);
                                        break;
                                    case "MOBIKWIK":
                                        sendChekoutPaymentIntializedAnalytics("MOBIKWIK");
                                        Application_Singleton.trackEvent("PAYMOBIKWIK", "Click", "PAYMOBIKWIK");
                                        Intent intentToMobikwik = new Intent(getActivity(), MobiKwikMerchantActivity.class);
                                        intentToMobikwik.putExtra("orderid", cart.getId());
                                        intentToMobikwik.putExtra("order_amount", "" + cart.getPending_amount());
                                        Fragment_CashPayment_2.this.startActivityForResult(intentToMobikwik, 3);
                                        break;
                                    case "NEFT":
                                        sendChekoutPaymentIntializedAnalytics("NEFT");
                                        payVia("invoice", mode, cart.getPending_amount(), cart.getId());
                                        break;
                                    case "CHEQUE":
                                        sendChekoutPaymentIntializedAnalytics("CHEQUE");
                                        payVia("invoice", mode, cart.getPending_amount(), cart.getId());
                                        break;
                                    case "OTHER":
                                        sendChekoutPaymentIntializedAnalytics("OTHER");
                                        if (cart.getPending_amount() < 1) {
                                            callCartPayment(mode, "FULL WB MONEY USED", getCurrentDate(), String.valueOf(cart.getPending_amount()), cart.getId());
                                        } else {
                                            payVia("invoice", mode, cart.getPending_amount(), cart.getId());
                                        }
                                        break;
                                    default:
                                        payVia("invoice", mode, cart.getPending_amount(), cart.getId());
                                        break;
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        progress.show();
        try {
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
                url = "cart";
            }
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        if (progress != null) {
                            progress.dismiss();
                        }

                        try {
                            CartCatalogModel temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                            temp.setCatalogs(cart.getCatalogs());
                            cart = temp;
                            updateCartInvoiceUI();

                            if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                                String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
                                switch (check) {
                                    case "CREDIT":
                                        sendChekoutPaymentIntializedAnalytics("CREDIT");
                                        Application_Singleton.trackEvent("BuyOnCredit", "Click", "BuyOnCredit");
                                        callCartToOrderPending();
                                        break;
                                    case "WISHBOOKCREDIT":
                                        sendChekoutPaymentIntializedAnalytics("WISHBOOKCREDIT");
                                        Application_Singleton.trackEvent("WishbookCredit", "Click", "WishbookCredit");
                                        callCartPayment("Wishbook Credit", "Wishbook Credit", getCurrentDate(), String.valueOf(cart.getPending_amount()), cart.getId());
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress != null) {
                        progress.dismiss();
                    }
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void patchShippingCharge(String addressid, double shippingcharge, String shipping_method) {
        Log.e(TAG, "=====patchShippingCharge: =====");
        final MaterialDialog progress = StaticFunctions.showProgress(getActivity());
        progress.setCancelable(false);
        progress.show();
        try {
            SalesOrderCreate order = new SalesOrderCreate();
            order.setPreffered_shipping_provider(getResources().getString(R.string.wb_provided));
            order.setShipping_charges(String.valueOf(shippingcharge));
            order.setShipping_method(shipping_method);
            try {
                int add = (int) Double.parseDouble(addressid);
                order.setShip_to(String.valueOf(add));
            } catch (NumberFormatException e) {
                order.setShip_to(addressid);
            }
            String orderjson = new Gson().toJson(order);
            JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            String url = "cart";

            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        if (progress != null) {
                            progress.dismiss();
                        }
                        try {
                            CartCatalogModel temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                            temp.setCatalogs(cart.getCatalogs());
                            cart = temp;
                            changeCodAmount();
                            updateCartInvoiceUI();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress != null) {
                        progress.dismiss();
                    }
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            if (progress != null) {
                progress.dismiss();
            }
            e.printStackTrace();
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
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
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
                try {
                    if (allstates != null) {
                        stateId = allstates[position].getId();

                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.btn_save_address)
    public void validateAddress() {
        if (isEmptyValidation(edit_name)) {
            edit_name.requestFocus();
            edit_name.setError("Please Enter Name");
            return;
        }

        if (isEmptyValidation(edit_mobile_number)) {
            edit_mobile_number.requestFocus();
            edit_mobile_number.setError("Please Enter Phone number");
            return;
        }

        if (!edit_mobile_number.getText().toString().isEmpty()) {
            if (!isValidMobile(edit_mobile_number.getText().toString())) {
                edit_mobile_number.requestFocus();
                edit_mobile_number.setError("Please Enter Valid Phone number");
            }
        }
        if (isEmptyValidation(editAddLine1)) {
            editAddLine1.requestFocus();
            editAddLine1.setError("Please Enter Address");
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

        if (editPincode.getText().toString().length() != 6) {
            editPincode.requestFocus();
            editPincode.setError("Please enter valid pincode");
            return;
        }
        saveAddress(editAddLine1.getText().toString().trim(),
                editAddLine2.getText().toString().trim(),
                editPincode.getText().toString().trim(), edit_name.getText().toString().trim(),
                edit_mobile_number.getText().toString());

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

    public void saveAddress(String add1, String add2, String pincode, String name, String phone_number) {
        ShippingAddressResponse ship;
        String orderAddressid = null;

        try {
            String shipp = cart.getShip_to().toString();
            if (shipp != null) {
                orderAddressid = shipp;
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
        if (!name.isEmpty()) {
            params.put("name", name);
        } else {
            params.put("name", "Work");
        }

        params.put("phone_number", phone_number);


        final String finalOrderAddressid = orderAddressid;
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", ""), params, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

                try {
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

                    if (newAddress.getPhone_number() != null) {
                        shippingAddressResponse.setPhone_number(newAddress.getPhone_number());
                    }
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
                        }, recyclerAddress, Fragment_CashPayment_2.this, isBrokerageOrder, finalOrderAddressid, cart);
                        recyclerAddress.setAdapter(addressAdapter);
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

    public void fetchAddress() {
        ShippingAddressResponse ship;
        String orderAddressid = null;

        try {
            String shipp = String.valueOf(Integer.parseInt(cart.getShip_to().toString()));
            if (shipp != null) {
                orderAddressid = shipp;
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
                try {
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
                        }, recyclerAddress, Fragment_CashPayment_2.this, false, finalOrderAddressid, cart);
                        recyclerAddress.setAdapter(addressAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                }

            }
        });
    }

    public void fetchAddressBuyers(String buyerComapnyId) {

        ShippingAddressResponse ship;
        String orderAddressid = null;
        try {
            ship = new Gson().fromJson(new Gson().toJson(order.getShip_to()), ShippingAddressResponse.class);
            if (ship != null) {
                orderAddressid = ship.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final String finalOrderAddressid = orderAddressid;
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", "") + "?company=" + buyerComapnyId, null, headers, true, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

                try {

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
                        }, recycler_buyeraddress, Fragment_CashPayment_2.this, true, finalOrderAddressid, cart);
                        recycler_buyeraddress.setAdapter(buyerAddressAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                }
            }
        });
    }

    public String getCityName(@NonNull String id) {
        if (allcities != null) {
            for (int i = 0; i < allcities.length; i++) {
                if (id.equals(allcities[i].getId())) {
                    return allcities[i].getCity_name();
                }
            }
        }
        return null;
    }

    public String getStateName(@NonNull String id) {
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
            edit_name.setText(UserInfo.getInstance(getActivity()).getCompanyname());
            edit_mobile_number.setText(UserInfo.getInstance(getActivity()).getMobile());
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
        edit_name.setText("");
        edit_mobile_number.setText("");
        cityId = "";
        stateId = "";
        State = "";
        City = "";
    }

    public void getShippingCharges(@NonNull String cartID) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.SHIPPING_CHARGE + "?cart=" + cartID, null, headers, false, new HttpManager.customCallBack() {

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
                    shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                } else {
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
                            patchShippingCharge(cart.getShip_to(), key.getShipping_charge(), key.getShipping_method_id());
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


    void changeCodAmount() {
        calculateCod();
        if (textViewCodNote != null) {
            float cod_amount = Float.parseFloat(codAmount);
            float remain_amount = (float) cart.getPending_amount() - cod_amount;
            DecimalFormat df = new DecimalFormat("#.##");
            String formatted_cod = df.format(cod_amount);
            String s = String.format(getResources().getString(R.string.cod_sub_text), String.valueOf(formatted_cod), String.valueOf(remain_amount));
            SpannableString ss;

            if (switch_resale_order.isChecked()) {
                s = String.format(getResources().getString(R.string.cod_sub_text_reseller), String.valueOf(formatted_cod));
                ss = new SpannableString(s);
                textViewCodNote.setText(ss);
            } else {
                ss = new SpannableString(s);
                textViewCodNote.setText(ss);
            }
            textViewCodNote.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(textViewCodNote, Linkify.WEB_URLS);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(StaticFunctions.dpToPx(mContext, 30), 0, 0, StaticFunctions.dpToPx(mContext, 20));
            textViewCodNote.setLayoutParams(params);
        }
    }

    private void calculateCod() {
        float cod = (float) cart.getPending_amount() * 10 / 100;
        if (cod > 100)
            codAmount = String.valueOf(cod);
        else
            codAmount = "100";
    }


    private void showCODDialog() {
        //SPANNABLE STRING
        calculateCod();
        float cod_amount = Float.parseFloat(codAmount);
        float remain_amount = (float) cart.getPending_amount() - cod_amount;
        String s;
        if (switch_resale_order.isChecked()) {
            try {
                s = String.format(getResources().getString(R.string.resell_cod_note), String.valueOf(Math.round(cod_amount)));
            } catch (Exception e) {
                s = String.format(getResources().getString(R.string.resell_cod_note), String.valueOf(cod_amount));
            }
        } else {
            try {
                s = String.format(getResources().getString(R.string.cod_dialog_text1), String.valueOf(Math.round(cod_amount)), String.valueOf(Math.round(remain_amount)));
            } catch (Exception e) {
                s = String.format(getResources().getString(R.string.cod_dialog_text1), String.valueOf(cod_amount), String.valueOf(remain_amount));
            }
        }

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
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
            ss.setSpan(clickableSpan, s.indexOf("*COD"), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (UserInfo.getInstance(getActivity()).getLanguage().equals("hi")) {
            ss.setSpan(clickableSpan, s.indexOf("*COD"), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        try {
            new MaterialDialog.Builder(getActivity())
                    .title(getResources().getString(R.string.cod_dialog_title1))
                    .content(ss)
                    .cancelable(false)
                    .positiveText(getResources().getString(R.string.cod_dialog_poistive1))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            try {
                                dialog.dismiss();
                                if (textViewCodNote != null)
                                    textViewCodNote.setVisibility(View.VISIBLE);
                                if (switch_resale_order.isChecked() && !isResellerAmountSet) {
                                    openSortBottom(false);
                                }
                            } catch (Exception e) {
                            }
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
        } catch (Exception e) {
        }

    }

    public void getCartData(final @NonNull AppCompatActivity context) {
        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        try {
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/catalogwise/";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            showProgress();
            HttpManager.getInstance(context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) throws ParseException {

                    try {
                        hideProgress();
                        cart = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                        if (cart.getDisplay_amount() > 0) {
                            total_resale_amt = String.valueOf(cart.getDisplay_amount());
                        }
                        initResaleView();
                 /*       if (cart.getWbmoney_redeem_amt() > 0) {
                            chk_wb_money.setChecked(true);
                        }*/
                        //getWBMoneyDashboard();
                        if (cart.getShipping_charges() != null) {
                            orginalTotalAmout = String.valueOf(cart.getPending_amount() - Double.parseDouble(cart.getShipping_charges()) - cart.getWb_money_used());
                            shipping_charge = cart.getShipping_charges();
                            //  calculateShipping(true);
                        } else {
                            orginalTotalAmout = String.valueOf(cart.getPending_amount() - cart.getWb_money_used());
                        }

                        card_reseller_order.setVisibility(View.VISIBLE);
                        switch_resale_order.setVisibility(View.GONE);
                        switch_resale_order.setChecked(false);
                        if (UserInfo.getInstance(getActivity()).getOnline_retailer_reseller()) {
                            card_reseller_order.setVisibility(View.VISIBLE);
                            switch_resale_order.setVisibility(View.VISIBLE);
                            switch_resale_order.setChecked(true);
                            txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_on_note));
                        } else {
                            card_reseller_order.setVisibility(View.GONE);
                            switch_resale_order.setVisibility(View.GONE);
                            switch_resale_order.setChecked(false);
                            txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_off_note));
                        }

                        switch_resale_order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (b) {
                                    txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_on_note));
                                    fetchPayment(cart.getId());
                                } else {
                                    txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_off_note));
                                    fetchPayment(cart.getId());
                                }
                            }
                        });


                      /*  if(cart.getDisplay_amount() > 0 ){
                            edit_resell_amount.setText(String.valueOf(cart.getDisplay_amount()));
                        }*/


                        updateCartInvoiceUI();
                        /**
                         * WB-3811 Invoice see details not required
                         */
                        invoice_detail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Application_Singleton.CONTAINER_TITLE = "Invoice Details";
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", cart);
                                bundle.putBoolean("isWishbookTransport", true);
                                if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                                    String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
                                    RadioButton radioButton = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId()));
                                    if (check.equalsIgnoreCase("CREDIT")) {
                                        bundle.putBoolean("isBuyonCredit", true);
                                    }
                                }

                                /*if (chk_wb_money.isChecked()) {
                                    bundle.putBoolean("isWbMoneyApplied", true);
                                }*/

                                Fragment_Cart_Invoice fragment_cart_invoice = new Fragment_Cart_Invoice();
                                fragment_cart_invoice.setArguments(bundle);
                                Application_Singleton.CONTAINERFRAG = fragment_cart_invoice;
                                StaticFunctions.switchActivity(context, OpenContainer.class);
                            }
                        });
                        if (getArguments() != null) {
                            if (getArguments().getBoolean("isBrokerageOrder")) {
                                Log.i("TAG", "getIsBroker Fragment==>" + true);
                                isBrokerageOrder = true;
                                fetchAddressBuyers(order.getCompany());
                            } else {
                                Log.i("TAG", "getIsBroker Fragment==>" + false);
                                isBrokerageOrder = false;
                                fetchAddress();
                            }
                        }


                        if (isBrokerageOrder) {
                            fetchPayment(cart.getId());
                        } else {
                            fetchPayment(cart.getId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCartInvoiceUI() {
        try {
            total_order_amount.setText("\u20B9 " + cart.getAmount());
            if (discount_amount != null && !discount_amount.equals("0.0")) {
                layout_discount.setVisibility(View.VISIBLE);
                discount_amount.setText("- \u20B9 " + cart.getSeller_discount());
            } else {
                layout_discount.setVisibility(View.GONE);
            }


            cart_date.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date()));
            gst_amount.setText("+ \u20B9 " + cart.getTaxes());


            if (cart.getShipping_charges() != null && !cart.getShipping_charges().equals("0.0")) {
                layout_delivery.setVisibility(View.VISIBLE);
                delivery_amount.setText("+ \u20B9 " + cart.getShipping_charges());
            } else {
                layout_delivery.setVisibility(View.GONE);
            }


            /*if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
                String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
                RadioButton radioButton = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId()));
                if (check.equalsIgnoreCase("CREDIT")) {
                    layout_discount.setVisibility(View.GONE);
                    if (cart.getSeller_discount() != null && !cart.getSeller_discount().equals("null") && !cart.getSeller_discount().equals("0.0")) {
                        total_amount.setText("\u20B9 " + String.format("%.2f", (cart.getPending_amount() + cart.getWb_money_used() + Double.parseDouble(cart.getSeller_discount()))));
                    } else {
                        total_amount.setText("\u20B9 " + (cart.getPending_amount() + cart.getWb_money_used()));
                    }
                } else {
                    total_amount.setText("\u20B9 " + (cart.getPending_amount() + cart.getWb_money_used()));
                }
            } else {
                total_amount.setText("\u20B9 " + (cart.getPending_amount() + cart.getWb_money_used()));
            }*/

            total_amount.setText("\u20B9 " + (cart.getTotal_amount()));


            if (cart.getWbmoney_redeem_amt() > 0) {

                if(cart.getWb_money_used()>0){
                    linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    invoice_wb_money.setText("- " + "\u20B9 " + cart.getWb_money_used());
                }

                if(cart.getWbpoints_used()>0){
                    linear_invoice_reward_point.setVisibility(View.VISIBLE);
                    invoice_reward_point.setText("- " + "\u20B9 " + cart.getWbpoints_used());
                }

                linear_now_pay.setVisibility(View.VISIBLE);
                payable_amount.setText("\u20B9 " + (cart.getPending_amount()));
            } else {
                linear_invoice_wb_money.setVisibility(View.GONE);
                linear_now_pay.setVisibility(View.GONE);
            }

            if (cart.getWbmoney_redeem_amt() > 0) {

                if(cart.getWb_money_used()>0){
                    linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    invoice_wb_money.setText("- " + "\u20B9 " + cart.getWb_money_used());
                }

                if(cart.getWbpoints_used()>0){
                    linear_invoice_reward_point.setVisibility(View.VISIBLE);
                    invoice_reward_point.setText("- " + "\u20B9 " + cart.getWbpoints_used());
                }

                linear_now_pay.setVisibility(View.VISIBLE);
                payable_amount.setText("\u20B9 " + (cart.getPending_amount()));
            } else {
                linear_invoice_wb_money.setVisibility(View.GONE);
                linear_now_pay.setVisibility(View.GONE);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callCartToOrderPending() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        if (isBrokerageOrder) {
            url = "brokerageorder";
            setOrderPending(true);
            return;
        } else {
            url = "cart";
        }
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cart_status", "Converted");
            jsonObject.addProperty("order_type", "Credit");
            jsonObject.addProperty("processing_status", "Pending");
            jsonObject.addProperty("cart_status", "Converted");
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        CartCatalogModel temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                        temp.setCatalogs(cart.getCatalogs());
                        cart = temp;
                        updateCartInvoiceUI();
                        try {
                            Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        clearCartPref();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOrderPending(boolean isCreditOrder) {
        if (((Response_buyingorder) Application_Singleton.selectedOrder).getProcessing_status().equals("Draft")) {
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
                    try {
                        Toast.makeText(getActivity(), "Order created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Order updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class).putExtra("isAfterPayment", true));
            getActivity().finish();
        }

    }

    public ArrayList<String> assumptionOrderCount() {
        ArrayList<String> seller = new ArrayList<>();
        for (int i = 0; i < cart.getItems().size(); i++) {
            if (!seller.contains(cart.getItems().get(i).getSelling_company())) {
                seller.add(cart.getItems().get(i).getSelling_company());
            }
        }

        return seller;

    }

    private void clearCartPref() {
        ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
        SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        preferences.edit().putInt("cartcount", 0).commit();
        preferences.edit().putString("cartId", "").commit();
        preferences.edit().putString("cartdata", "").commit();
        try {
            Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        cfPaymentService.doPayment(getActivity(), params, cashFreeTokenResponse.getCftoken(), stage, "#000000", "#FFFFFF");
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
                sendChekoutCompledAnalytics(true, "CASHFREE");
                new MaterialDialog.Builder(mContext)
                        .title("Transaction Success")
                        .content(response)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
                                clearCartPref();
                                try {
                                    Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                                    dialog.dismiss();
                                   /* ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK);
                                    clearCartPref();
                                    try {
                                        Application_Singleton.New_ORDER_COUNT = assumptionOrderCount().size();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    getActivity().finish();*/
                                }
                            }).positiveText("Ok").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onNavigateBack() {
        if (mContext != null) {
            Log.d("CFSDKSample", "Back Pressed");
        }
    }

    public String getCurrentDate() {
        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        Date date = new Date();
        return sdf.format(date);
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


    public View setCreditLine(ResponseCreditLines[] responseCreditLines, RadioButton radioButton, boolean isAfterApply) {
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
            if (cart.getPending_amount() >= Double.parseDouble(responseCreditLines[0].getAvailable_line())) {
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
            if (isAfterApply) {
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
              /*  if (Integer.parseInt(UserInfo.getInstance(getActivity()).getCreditScore()) > 0) {

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
        SalesOrderCreate order = new SalesOrderCreate();
        order.setWbmoney_points_used(wb_money);
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Log.e(TAG, "patchWBMoney: " + cart.getId());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "cart", "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.e(TAG, "onServerResponse: " + response);
                    CartCatalogModel temp = new CartCatalogModel();
                    temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                    temp.setWb_money_used(Integer.parseInt(wb_money));
                    temp.setCatalogs(cart.getCatalogs());
                    cart = temp;
                    Log.e("TAG", "=========Patch WB Money========" + cart.getWb_money_used());

                    // calculateShipping(true);
                    updateCartInvoiceUI();
                    refreshPage();
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

    private boolean isValidMobile(String editText) {
        if (editText.toString().trim().length() != 10)
            return false;
        else {
            if (!editText.isEmpty()) {
                if (editText.startsWith("6") ||
                        editText.startsWith("7") ||
                        editText.startsWith("8") ||
                        editText.startsWith("9")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

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
                /*try {
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
                                    if (Integer.parseInt(dashboard.getTotal_available()) > cart.getPending_amount()) {
                                        // cart total amount apply
                                        patchWBMoney(String.valueOf(Math.round(cart.getPending_amount())));
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


    public void refreshPage() {
        getCartData((AppCompatActivity) getActivity());
    }

    public void sendChekoutPaymentIntializedAnalytics(String paymentMethod) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CheckoutPayment_Initiated");
        HashMap<String, String> prop = new HashMap<>();
        if (cart != null)
            prop.put("cart_id", cart.getId());
        prop.put("payment_method", paymentMethod);
        prop.putAll(getCartAttributes());
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendChekoutCompledAnalytics(boolean status, String paymentMethod) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Checkout_Completed");
        HashMap<String, String> prop = new HashMap<>();
        if (cart != null)
            prop.put("cart_id", cart.getId());
        if (status)
            prop.put("status", "Success");
        else
            prop.put("status", "Fail");
        prop.put("payment_method", paymentMethod);
        prop.putAll(getCartAttributes());
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(getActivity(), wishbookEvent);
    }


    public HashMap<String, String> getCartAttributes() {
        HashMap<String, String> prop = new HashMap<>();
        try {
            if (cart != null) {
                String cart_full_set_flag = null;
                prop.put("cart_value", String.valueOf(cart.getTotal_amount()));
                prop.put("shipping_fee", String.valueOf(cart.getShipping_charges()));
                prop.put("discount", String.valueOf(cart.getSeller_discount()));
                prop.put("wb_money_used", String.valueOf(cart.getWb_money_used()));
                prop.put("cart_units", cart.getTotal_qty());
                Double cart_avg_price = (cart.getTotal_amount() / Integer.parseInt(cart.getTotal_qty()));
                prop.put("cart_unit_avg_price", String.valueOf(cart_avg_price));
                ArrayList<String> product_id = new ArrayList<>();
                ArrayList<String> product_type = new ArrayList<>();
                ArrayList<String> catalog_names = new ArrayList<>();
                ArrayList<String> product_categories = new ArrayList<>();
                ArrayList<String> product_cart_images = new ArrayList<>();
                ArrayList<String> catalog_total_amt = new ArrayList<>();
                if (cart.getCatalogs() != null) {
                    for (int i = 0; i < cart.getCatalogs().size(); i++) {
                        product_id.add(cart.getCatalogs().get(i).getProduct_id());
                        product_type.add(cart.getCatalogs().get(i).getProducts().get(0).getProduct_type());
                        catalog_names.add(cart.getCatalogs().get(i).getCatalog_title());
                        product_categories.add(cart.getCatalogs().get(i).getCatalog_category());
                        catalog_total_amt.add(cart.getCatalogs().get(i).getCatalog_total_amount());
                        if (cart.getCatalogs().get(i).isIs_full_catalog())
                            product_cart_images.add(cart.getCatalogs().get(i).getCatalog_image());
                        else
                            product_cart_images.add(cart.getCatalogs().get(i).getProducts().get(0).getProduct_image());

                        if(cart.getCatalogs().get(i).isIs_full_catalog()) {
                            if(cart_full_set_flag == null) {
                                cart_full_set_flag = "true";
                            }
                        }

                    }
                    prop.put("product_ids", StaticFunctions.ArrayListToString(product_id, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("catalog_item_names", StaticFunctions.ArrayListToString(catalog_names, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("product_types", StaticFunctions.ArrayListToString(product_type, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("product_categories", StaticFunctions.ArrayListToString(product_categories, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("cart_image", StaticFunctions.ArrayListToString(product_cart_images, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("catalog_total_amt", StaticFunctions.ArrayListToString(catalog_total_amt, StaticFunctions.COMMASEPRATEDSPACE));
                    if(cart_full_set_flag == null) {
                        prop.put("cart_fullset_flag","false");
                    } else {
                        prop.put("cart_fullset_flag",cart_full_set_flag);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prop;
    }

    public void patchReseller(boolean isResellerOrder, final String display_amount) {
        try {
            final MaterialDialog progress_dialog = StaticFunctions.showProgressDialog(getActivity(), "Please wait..", "Loading..", false);
            progress_dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("reseller_order", isResellerOrder);
            jsonObject.addProperty("display_amount", display_amount);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "cart", "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progress_dialog != null && progress_dialog.isShowing()) {
                        progress_dialog.dismiss();
                    }
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress_dialog != null && progress_dialog.isShowing()) {
                        progress_dialog.dismiss();
                    }
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSortBottom(boolean paymentRedirected) {
        String check ="OTHER";
        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
             check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
        }
        Intent intent = new Intent(getActivity(), Activity_ResaleAmt.class);
        intent.putExtra("data", cart);
        intent.putExtra("payment_redirected", paymentRedirected);
        intent.putExtra("selected_payment",check);
        Fragment_CashPayment_2.this.startActivityForResult(intent, 9800);
    }

    public List<CartProductModel.Items> cartItemCreate(int position, double displayAmt, int quantity) {
        if (cart != null) {
            CartCatalogModel cart_temp = cart;
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel.Items cartItem = null;
            for (CartCatalogModel.Products product : cart_temp.getCatalogs().get(position).getProducts()) {
                if (cartItem != null && cartItem.getProduct().equals(product.getProduct())) {
                    items.clear();
                }
                cartItem = new CartProductModel.Items(product.getRate(), String.valueOf(quantity), product.getProduct(), product.getIs_full_catalog(), product.getNote());
                cartItem.setDisplay_amount(displayAmt);
                if (product.getNo_of_pcs() > 0) {
                    cartItem.setQuantity(String.valueOf(quantity * product.getNo_of_pcs()));
                } else {
                    cartItem.setQuantity(String.valueOf(quantity));
                }
                items.add(cartItem);
            }
            return items;
        }
        return null;
    }

    public void patchDisplayAmt(final @NonNull Context context) {
        final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        try {
            ArrayList<CartProductModel.Items> items = new ArrayList<>();
            for (int i = 0; i < cart.getCatalogs().size(); i++) {
                ArrayList<CartProductModel.Items> temp_items = (ArrayList<CartProductModel.Items>) cartItemCreate(i, 0, cart.getCatalogs().get(i).getProducts().get(0).getQuantity());
                if (temp_items != null) {
                    items.addAll(temp_items);
                }
            }
            CartProductModel cartProductModel = new CartProductModel(items);
            cartProductModel.setReseller_order(true);
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
