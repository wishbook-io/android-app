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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gocashfree.cashfreesdk.CFPaymentService;
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
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.CashFreeTokenResponse;
import com.wishbook.catalog.commonmodels.responses.PaymentMethod;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditLines;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.address.DeliveryAddressAddDialog;
import com.wishbook.catalog.home.address.ManageDeliveryAddressBottomDialog;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.orders.details.Fragment_Pay;
import com.wishbook.catalog.home.payment.MerchantActivity2;
import com.wishbook.catalog.home.payment.MobiKwikMerchantActivity;
import com.wishbook.catalog.home.payment.ZaakPayMerchantActivity;
import com.wishbook.catalog.reseller.Fragment_ResellerHolder;

import java.text.DecimalFormat;
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

public class Fragment_Payment extends GATrackedFragment {

    private static final float AMOUNT_LIMIT_PAYMENT_OPTION = 10000;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;


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


    @BindView(R.id.invoice_detail)
    TextView invoice_detail;

    @BindView(R.id.card_invoice)
    CardView card_invoice;

    @BindView(R.id.card_payment_mode)
    CardView card_payment_mode;

    @BindView(R.id.radio_wishbook_transport)
    TextView radio_wishbook_transport;


    TextView buy_on_credit_note, cod_500_note;


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


    // #### Start Address Section #####
    @BindView(R.id.card_address)
    CardView card_address;

    @BindView(R.id.txt_add_new_address)
    TextView txt_add_new_address;

    @BindView(R.id.txt_select_delivery_address)
    TextView txt_select_delivery_address;

    @BindView(R.id.chk_same_address)
    CheckBox chk_same_address;

    @BindView(R.id.txt_change_billing_address)
    TextView txt_change_billing_address;

    @BindView(R.id.txt_billing_address_value)
    TextView txt_billing_address_value;

    @BindView(R.id.txt_delivery_address_value)
    TextView txt_delivery_address_value;

    @BindView(R.id.linear_delivery_action)
    LinearLayout linear_delivery_action;


    @BindView(R.id.linear_shipping)
    LinearLayout linear_shipping;

    @BindView(R.id.txt_select_coupon)
    TextView txt_select_coupon;

    @BindView(R.id.txt_wb_coupon_value)
    TextView txt_wb_coupon_value;

    @BindView(R.id.txt_coupon_remove)
    TextView txt_coupon_remove;

    @BindView(R.id.txt_coupon_code)
    TextView txt_coupon_code;
    @BindView(R.id.txt_coupon_details)
    TextView txt_coupon_details;

    @BindView(R.id.linear_coupon_stage2_container)
    LinearLayout linear_coupon_stage2_container;

    @BindView(R.id.image_coupon)
    ImageView image_coupon;

    @BindView(R.id.linear_invoice_resale_container)
    LinearLayout linear_invoice_resale_container;

    @BindView(R.id.linear_invoice_resell_amount)
    LinearLayout linear_invoice_resell_amount;

    @BindView(R.id.txt_invoice_resell_amount)
    TextView txt_invoice_resell_amount;

    @BindView(R.id.linear_resale_total_profit)
    LinearLayout linear_resale_total_profit;

    @BindView(R.id.txt_resale_total_profit)
    TextView txt_resale_total_profit;

    @BindView(R.id.txt_add_verify_bank_link)
    TextView txt_add_verify_bank_link;

    @BindView(R.id.txt_not_set_resale_amount)
    TextView txt_not_set_resale_amount;

    @BindView(R.id.relative_warning_msg)
    RelativeLayout relative_warning_msg;

    @BindView(R.id.img_warn_close)
    ImageView img_warn_close;

    @BindView(R.id.txt_waring_message)
    TextView txt_waring_message;


    boolean isSameDeliveryAddress;
    String billing_address_id = null;
    String delivery_address_id = null;
    ShippingAddressResponse[] shippingAddressResponses;
    ArrayList<ShippingAddressResponse> addressResponses;
    TextView textViewCodNote;
    public CartCatalogModel cart;
    private View view;
    private Context mContext;
    private String codAmount;
    View wishbook_credit_sub_view;
    ResponseCreditLines[] responseCreditLines;
    RadioButton radioWishbookCredit;

    String total_resale_amt = "0.0";

    HashMap<ResponseShipment, RadioButton> radioGroupShipping = new HashMap<>();

    public static String TAG = Fragment_Payment.class.getSimpleName();

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    boolean isResellerSwitchChange;

    boolean isRefreshPage;

    public Fragment_Payment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {

        view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;

        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_payment, ga_container, true);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        buy_on_credit_note = new TextView(mContext);
        cod_500_note = new TextView(mContext);
        addressResponses = new ArrayList<>();


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wishbook_credit_sub_view = vi.inflate(R.layout.wishbook_credit_state, null);



        hintLinkBind();
        hideForStep1();
        getCartData((AppCompatActivity) getActivity());


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Application_Singleton.isChangeCreditScrore) {
            setCreditLine(responseCreditLines, radioWishbookCredit, true);
        }

    }

    private void hideForStep1() {

        hideInvoiceDetailUI();
        hidePaymentUI();
        hideResellUI();
        disablePayButtons();
    }


    private void moveToStep2() {
        getShippingMethods(cart.getId());
    }

    private void moveToStep3() {
        changeCodAmount();
        updateCartInvoiceUI();
        initResaleView(true);
        if(UserInfo.getInstance(getActivity()).isOrderDisabled()) {
            showOrderDisableWaringMessage();
        } else {
            hideOrderDisableWarningMessage();
        }
        fetchPayment(cart.getId());
    }

    private void finalStepPlaceOrder() {

        if (placeOrderValidations()) {
            if (switch_resale_order.isChecked()) {
                if (total_resale_amt == null) {
                    Toast.makeText(getActivity(), "Please Enter Resell Amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                DecimalFormat decimalFormat = new DecimalFormat("#0.##");
                String cartTotal = String.valueOf(decimalFormat.format(cart.getTotal_amount()));
                if (total_resale_amt != null && Double.parseDouble(total_resale_amt) < Double.parseDouble(cartTotal) || !checkAllItemResalePrice(true)) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error), Toast.LENGTH_SHORT).show();
                    openResaleBottom(true);
                    return;
                }
                patchReseller(true, total_resale_amt);
            } else {
                patchReseller(false, "0");
            }
        }


        //paymentHandler();
    }

    private boolean checkAllItemResalePrice(boolean isShowMessage) {
        if (cart != null) {
            for (CartCatalogModel.Catalogs catalogs :
                    cart.getCatalogs()) {
                if (catalogs.getCatalog_display_amount() <= 0 || (catalogs.getCatalog_display_amount() < Double.parseDouble(catalogs.getCatalog_total_amount()) + Double.parseDouble(catalogs.getCatalog_shipping_charges()))) {
                    Toast.makeText(mContext, String.format(mContext.getResources().getString(R.string.resell_amount_error_item)), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "checkAllItemResalePrice: 1");
                    return false;
                }
                if (catalogs.getCatalog_display_amount() > ((Double.parseDouble(catalogs.getCatalog_total_amount())
                        + Double.parseDouble(catalogs.getCatalog_shipping_charges())) * 3)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.resell_amount_error_3x_item), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "checkAllItemResalePrice: 2");
                    return false;
                }

            }
            return true;
        }
        return false;
    }


    private void paymentHandler() {

        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
            String mode = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();

            switch (check) {
                case "CREDIT":
                    //sendChekoutPaymentIntializedAnalytics("CREDIT");
                    //Application_Singleton.trackEvent("BuyOnCredit", "Click", "BuyOnCredit");
                    //callCartToOrderPending();
                    break;
                case "WISHBOOKCREDIT":
                    sendChekoutPaymentIntializedAnalytics("WISHBOOKCREDIT");
                    Application_Singleton.trackEvent("WishbookCredit", "Click", "WishbookCredit");
                    callCartPayment("Wishbook Credit", "Wishbook Credit", getCurrentDate(), String.valueOf(cart.getPending_amount()), cart.getId());
                    break;

                case "ZAAKPAY":
                    sendChekoutPaymentIntializedAnalytics("ZAAKPAY");
                    Application_Singleton.trackEvent("PAYZAAKPAY", "Click", "PAYZAAKPAY");
                    Intent intentToZaakpay = new Intent(getActivity(), ZaakPayMerchantActivity.class);
                    intentToZaakpay.putExtra("orderid", "C" + cart.getId());
                    intentToZaakpay.putExtra("order_amount", "" + cart.getPending_amount());
                    Fragment_Payment.this.startActivityForResult(intentToZaakpay, 3);
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
                    if (codAmount != null && Double.parseDouble(codAmount) > 1) {
                        Application_Singleton.trackEvent("PAYCASHFREE", "Click", "COD");
                        String name2 = UserInfo.getInstance(getActivity()).getFirstName();
                        String mContact2 = UserInfo.getInstance(getActivity()).getMobile();
                        String mEmail2 = UserInfo.getInstance(getActivity()).getEmail();
                        initCashFree("C" + cart.getId(), String.valueOf(codAmount), cart.getId(), name2, mContact2, mEmail2);
//                                            Intent intentToCOD = new Intent(getActivity(), ZaakPayMerchantActivity.class);
//                                            intentToCOD.putExtra("orderid", "C" + cart.getId());
//                                            intentToCOD.putExtra("order_amount", codAmount);
//                                            Fragment_Payment.this.startActivityForResult(intentToCOD, 3);
                    } else if (codAmount != null && Double.parseDouble(codAmount) == 0) {
                        // Handle this code in PatchReseller Response
                        PrefDatabaseUtils.setPrefLastCartFullCodVerify(getActivity(), cart.getId());
                        callCartPayment("COD", "COD", getCurrentDate(), "0", cart.getId());
                    }
                    break;
                case "PAYTM":
                    sendChekoutPaymentIntializedAnalytics("PAYTM");
                    Application_Singleton.trackEvent("PAYPAYTM", "Click", "PAYPAYTM");
                    Intent i = new Intent(getActivity(), MerchantActivity2.class);
                    i.putExtra("orderid", "cart" + cart.getId());
                    i.putExtra("order_amount", "" + String.valueOf(cart.getPending_amount()));
                    Fragment_Payment.this.startActivityForResult(i, 2);
                    break;
                case "MOBIKWIK":
                    sendChekoutPaymentIntializedAnalytics("MOBIKWIK");
                    Application_Singleton.trackEvent("PAYMOBIKWIK", "Click", "PAYMOBIKWIK");
                    Intent intentToMobikwik = new Intent(getActivity(), MobiKwikMerchantActivity.class);
                    intentToMobikwik.putExtra("orderid", cart.getId());
                    intentToMobikwik.putExtra("order_amount", "" + cart.getPending_amount());
                    Fragment_Payment.this.startActivityForResult(intentToMobikwik, 3);
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


    private void hideResellUI() {
        card_reseller_order.setVisibility(View.GONE);
    }

    private void hidePaymentUI() {
        card_payment_mode.setVisibility(View.GONE);
    }

    private void hideInvoiceDetailUI() {
        card_invoice.setVisibility(View.GONE);
    }

    private void showOrderDisableWaringMessage() {
        relative_warning_msg.setVisibility(View.VISIBLE);
        txt_waring_message.setText(getResources().getString(R.string.order_disable_note));
        img_warn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relative_warning_msg.setVisibility(View.GONE);
            }
        });
    }

    private void hideOrderDisableWarningMessage() {
        relative_warning_msg.setVisibility(View.GONE);
    }

    private void showStageOneCouponUI() {
        image_coupon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cart_coupon));
        txt_select_coupon.setVisibility(View.VISIBLE);
        linear_coupon_stage2_container.setVisibility(View.GONE);

    }

    private void showStageTwoCouponUI(CartCatalogModel cart) {
        image_coupon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_correct));
        txt_select_coupon.setVisibility(View.GONE);
        linear_coupon_stage2_container.setVisibility(View.VISIBLE);


        if (cart != null) {
            txt_coupon_code.setText(cart.getWb_coupon().getCode());
            txt_wb_coupon_value.setText("- \u20B9 " + (cart.getWb_coupon_discount()));
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

                        fetchAddress(null);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hintLinkBind() {
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
        try {
            if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
                ss.setSpan(clickableSpan, 123, 134, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (UserInfo.getInstance(getActivity()).getLanguage().equals("hi")) {
                ss.setSpan(clickableSpan, 98, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            String add_verify_bank = getActivity().getResources().getString(R.string.add_verify_bank_resale_hint).toString();
            String link_part = "Add/Verify your bank details";
            SpannableString resale_ss = new SpannableString(getString(R.string.add_verify_bank_resale_hint));

            ClickableSpan resale_bank_verify_span = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    navigateReseller("bankaccount");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
            resale_ss.setSpan(resale_bank_verify_span, add_verify_bank.indexOf(link_part), add_verify_bank.indexOf(link_part) + link_part.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_add_verify_bank_link.setText(resale_ss);
            txt_add_verify_bank_link.setMovementMethod(LinkMovementMethod.getInstance());
            txt_add_verify_bank_link.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initResaleView(boolean isFirstTime) {
        txt_change_resale_amt.setPaintFlags(txt_change_resale_amt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_change_resale_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResaleBottom(false);
            }
        });

        if (cart != null) {
            txt_resale_order_amount.setText("\u20B9 " + cart.getTotal_amount());
        }

        if (total_resale_amt != null) {
            txt_total_resale_amount.setText("\u20B9 " + total_resale_amt);
            txt_invoice_resell_amount.setText("\u20B9 " + total_resale_amt);
            if (Double.parseDouble(total_resale_amt) <= 0) {
                linear_resale_total_profit.setVisibility(View.GONE);
                linear_invoice_resell_amount.setVisibility(View.GONE);
                txt_add_verify_bank_link.setVisibility(View.GONE);
                txt_not_set_resale_amount.setText(getResources().getString(R.string.not_set_resale_amt_error));
                txt_not_set_resale_amount.setVisibility(View.VISIBLE);
            } else {
                txt_not_set_resale_amount.setVisibility(View.GONE);

                linear_invoice_resell_amount.setVisibility(View.VISIBLE);
                txt_add_verify_bank_link.setVisibility(View.VISIBLE);
                if (checkAllItemResalePrice(false)) {
                    linear_resale_total_profit.setVisibility(View.VISIBLE);
                    double resale_profit = (Double.parseDouble(total_resale_amt) - cart.getPending_amount());
                    txt_resale_total_profit.setText("\u20B9 " + String.valueOf(decimalFormat.format(resale_profit)));
                } else {
                    txt_not_set_resale_amount.setVisibility(View.VISIBLE);
                    txt_not_set_resale_amount.setText("You have not set resale value for every item");
                }
            }

        } else {
            txt_total_resale_amount.setText("\u20B9 " + 0);
            txt_invoice_resell_amount.setText("\u20B9 " + 0);
            txt_resale_total_profit.setText("\u20B9 " + 0);
        }

        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
            String check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
            String mode = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString();
            if (switch_resale_order.isChecked()) {
                linear_reseller_dialog.setVisibility(View.VISIBLE);
                linear_invoice_resale_container.setVisibility(View.VISIBLE);
            } else {
                linear_reseller_dialog.setVisibility(View.GONE);

                linear_invoice_resale_container.setVisibility(View.GONE);
            }
        }
        if (isFirstTime) {
            card_reseller_order.setVisibility(View.VISIBLE);
            switch_resale_order.setVisibility(View.GONE);
            if (!isResellerSwitchChange) {
                switch_resale_order.setChecked(false);
            }

            /**
             * Chnages done by Bhavik on March-29
             * WB-4641
             */


            if (UserInfo.getInstance(getActivity()).getOnline_retailer_reseller() || StaticFunctions.isOnlyRetailer(getActivity())) {
                card_reseller_order.setVisibility(View.VISIBLE);
                switch_resale_order.setVisibility(View.VISIBLE);
                linear_invoice_resale_container.setVisibility(View.VISIBLE);
                if (!isResellerSwitchChange) {
                    if (StaticFunctions.isOnlyRetailer(getActivity())) {
                        switch_resale_order.setChecked(false);
                        isResellerSwitchChange = true;
                        txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_off_note));
                    } else {
                        switch_resale_order.setChecked(true);
                        isResellerSwitchChange = true;
                        txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_on_note));
                    }
                }
            } else {
                card_reseller_order.setVisibility(View.GONE);
                linear_invoice_resale_container.setVisibility(View.GONE);
                switch_resale_order.setVisibility(View.GONE);
                switch_resale_order.setChecked(false);
                isResellerSwitchChange = true;
                txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_off_note));
            }

            switch_resale_order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        isResellerSwitchChange = true;
                        txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_on_note));
                        fetchPayment(cart.getId());
                    } else {
                        isResellerSwitchChange = true;
                        txt_switch_reseller_note.setText(getResources().getString(R.string.resell_switch_off_note));
                        fetchPayment(cart.getId());
                    }
                }
            });
        }

    }

    public void initBillingShippingView(boolean isNewAddressAdd) {
        setBillingAddress();
        if (cart.getShip_to() != null && !isNewAddressAdd) {
            delivery_address_id = cart.getShip_to();
            setDeliveryAddress(cart.getShip_to());
        }
        txt_add_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryAddressAddDialog deliveryAddressAddDialog = DeliveryAddressAddDialog.newInstance(null);
                deliveryAddressAddDialog.show(getFragmentManager(), "Add Address");
                deliveryAddressAddDialog.setDeliveryAddAddressListener(new DeliveryAddressAddDialog.DeliveryAddAddressListener() {
                    @Override
                    public void onAdd(String address_id) {
                        fetchAddress(address_id);
                    }
                });
            }
        });

        txt_select_delivery_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (delivery_address_id != null) {
                    bundle.putString("selected_delivery_address", delivery_address_id);
                }
                ManageDeliveryAddressBottomDialog addressBottomDialog = ManageDeliveryAddressBottomDialog.newInstance(bundle);
                addressBottomDialog.setTargetFragment(Fragment_Payment.this, 1600);
                addressBottomDialog.show(getFragmentManager(), "ManageAddress");
            }
        });

        txt_change_billing_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Profile fragment_profile = new Fragment_Profile();
                Bundle bundle = new Bundle();
                bundle.putBoolean("showAsDialog", true);
                fragment_profile.setArguments(bundle);
                fragment_profile.setTargetFragment(Fragment_Payment.this, 1700);
                fragment_profile.show(getFragmentManager(), "PROFILE");
            }
        });


        chk_same_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isSameDeliveryAddress = true;
                    setDeliveryAddress(billing_address_id);
                } else {
                    isSameDeliveryAddress = false;
                    setDeliveryAddress(delivery_address_id);
                }
            }
        });

        if (isSameDeliveryAddress) {
            chk_same_address.setChecked(true);
        }
    }


    private void setBillingAddress() {
        if (addressResponses != null && addressResponses.size() > 0) {
            for (int i = 0; i < addressResponses.size(); i++) {
                if (addressResponses.get(i).is_default()) {
                    if (addressResponses.get(i).getStreet_address() != null && addressResponses.get(i).getCity() != null && addressResponses.get(i).getState() != null) {
                        String address_txt = addressResponses.get(i).getName() + "\n" + addressResponses.get(i).getStreet_address() + "\n" +
                                addressResponses.get(i).getCity().getCity_name() + "\n" +
                                addressResponses.get(i).getState().getState_name() + "\n" +
                                addressResponses.get(i).getPincode();
                        txt_billing_address_value.setText(address_txt);
                        txt_change_billing_address.setText("Change");

                        chk_same_address.setVisibility(View.VISIBLE);
                        txt_add_new_address.setVisibility(View.VISIBLE);
                        txt_select_delivery_address.setVisibility(View.VISIBLE);
                    } else {
                        txt_billing_address_value.setText("No Billing Address");
                        txt_change_billing_address.setText("Add Address");

                        chk_same_address.setVisibility(View.GONE);
                        txt_add_new_address.setVisibility(View.GONE);
                        txt_select_delivery_address.setVisibility(View.GONE);
                    }

                    billing_address_id = addressResponses.get(i).getId();
                    return;
                }
            }
        }
    }

    private void setDeliveryAddress(String addressid) {

        if (addressResponses != null && addressResponses.size() > 0 && addressid != null) {
            Log.e(TAG, "setDeliveryAddress: ====>" + addressid);
            for (int i = 0; i < addressResponses.size(); i++) {
                if (addressResponses.get(i).getId().equalsIgnoreCase(addressid)) {
                    String address_txt = addressResponses.get(i).getName() + "\n" + addressResponses.get(i).getStreet_address() + "\n" +
                            addressResponses.get(i).getCity().getCity_name() + "\n" +
                            addressResponses.get(i).getState().getState_name() + "\n" +
                            addressResponses.get(i).getPincode();
                    txt_delivery_address_value.setText(address_txt);
                    delivery_address_id = addressResponses.get(i).getId();
                    setShipTo(delivery_address_id);
                    break;
                }
            }
        }

        Log.d(TAG, "setDeliveryAddress: ====>" + isSameDeliveryAddress);
        if (isSameDeliveryAddress) {
            txt_delivery_address_value.setVisibility(View.GONE);
            linear_delivery_action.setVisibility(View.GONE);
        } else {
            txt_delivery_address_value.setVisibility(View.VISIBLE);
            linear_delivery_action.setVisibility(View.VISIBLE);
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
        Application_Singleton.CONTAINERFRAG = this;
        if (requestCode == CFPaymentService.REQ_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Log.e(TAG, "onActivityResult: CashFree Data is null======>");
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    for (String key : bundle.keySet()) {
                        if (bundle.getString(key) != null) {
                            Log.d(TAG, key + " : " + bundle.getString(key));
                        }
                    }
            } else {
                Log.e(TAG, "onActivityResult: CashFree Data is null======>");
            }
        }
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
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    total_resale_amt = String.valueOf(decimalFormat.format(data.getDoubleExtra("total_resale_amt", 0.0)));
                    HashMap<String, String> hashMap = (HashMap<String, String>) data.getSerializableExtra("param");
                    for (int i = 0; i < cart.getCatalogs().size(); i++) {
                        if (hashMap.containsKey("item_price_" + i)) {
                            cart.getCatalogs().get(i).setCatalog_display_amount(Double.parseDouble(hashMap.get("item_price_" + i)));
                        }

                    }
                    initResaleView(false);
                    isResellerAmountSet = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 9900 && resultCode == Activity.RESULT_OK) {
            try {
                refreshPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1600 && resultCode == Activity.RESULT_OK) {
            try {
                if (data.getStringExtra("delivery_address_id") != null) {
                    isSameDeliveryAddress = false;
                    chk_same_address.setChecked(false);
                    fetchAddress(data.getStringExtra("delivery_address_id"));
                    //setDeliveryAddress(data.getStringExtra("delivery_address_id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1700 && resultCode == Activity.RESULT_OK) {
            try {
                if (isSameDeliveryAddress) {
                    fetchAddress(billing_address_id);
                } else {
                    fetchAddress(null);
                }

            } catch (Exception e) {
                e.printStackTrace();
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
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PAYMENT_METHOD_URL, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                //hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    //hideProgress();
                    PaymentMethod[] paymentMethods = Application_Singleton.gson.fromJson(response, PaymentMethod[].class);

                    card_payment_mode.setVisibility(View.VISIBLE);

                    if (cart.getPending_amount() < 1) {
                        updatePaymentUIForFullWBMoneyOrder();

                    } else {
                        updatePaymentUI(paymentMethods);
                    }

                    enablePayButtons();
                    radioGroupPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                            radioGroupPaymentOnChangeListener(radioGroup, i);
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                // hideProgress();
            }
        });
    }

    private void updatePaymentUI(PaymentMethod[] paymentMethods) {


        ArrayList<PaymentMethod> creditPyament = new ArrayList<PaymentMethod>();
        ArrayList<PaymentMethod> offlinePayment = new ArrayList<PaymentMethod>();
        ArrayList<PaymentMethod> onlinePayment = new ArrayList<PaymentMethod>();
        final ArrayList<PaymentMethod> codPayment = new ArrayList<PaymentMethod>();

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

            if (codPayment.get(i).isIs_blocked()) {
                radioButtonView.setEnabled(false);
                cod_500_note.setText(getResources().getString(R.string.cod_block));
                cod_500_note.setVisibility(View.VISIBLE);
            }

            if (!codPayment.get(i).isIs_show()) {
                radioButtonView.setEnabled(false);
                cod_500_note.setText(getResources().getString(R.string.cod_not_show));
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

    private void updatePaymentUIForFullWBMoneyOrder() {

        ArrayList<PaymentMethod> offlinePayment = new ArrayList<PaymentMethod>();

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
    }


    public void setShipTo(String addressid) {

        hideForStep1();

        SalesOrderCreate order = new SalesOrderCreate();
        order.setShip_to(addressid);
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        progress_dialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "cart", "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }
                try {
                    CartCatalogModel temp = new CartCatalogModel();
                    temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                    temp.setCatalogs(cart.getCatalogs());
                    cart = temp;

                    if (isAddressValidate()) {
                        //call set 2
                        moveToStep2();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void patchShippingMethod(String addressid, double shippingcharge, String shipping_method) {
        Log.e(TAG, "=====patchShippingMethod: =====");
        hideForStep1();
        final MaterialDialog progress = StaticFunctions.showProgress(getActivity());
        progress.show();
        try {

            SalesOrderCreate order = new SalesOrderCreate();
            order.setPreffered_shipping_provider(getResources().getString(R.string.wb_provided));
            order.setShipping_charges(String.valueOf(shippingcharge));
            order.setShipping_method(shipping_method);


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

                            //move to step3
                            moveToStep3();


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

    public void patchRemoveCoupon(String coupon_code) {
        try {
            final MaterialDialog progress_dialog = StaticFunctions.showProgressDialog(mContext, "Please wait..", "Loading..", false);
            progress_dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("wb_coupon_code", coupon_code);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "coupon-apply", cart.getId()), jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progress_dialog != null && progress_dialog.isShowing()) {
                        progress_dialog.dismiss();
                    }
                    showStageOneCouponUI();
                    refreshPage();
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


    public void fetchAddress(final String delivery_address_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
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

                        // if any default address id to set
                        if (delivery_address_id != null) {
                            initBillingShippingView(true);
                            setDeliveryAddress(delivery_address_id);
                        } else {
                            initBillingShippingView(false);
                        }

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
                    e.printStackTrace();
                }

            }
        });
    }


    public void getShippingMethods(@NonNull String cartID) {

        hideForStep1();


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
                    updateShippingMethodUI(shipments, linear_shipping);
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


    private void updateShippingMethodUI(ArrayList<ResponseShipment> shipments, LinearLayout root) {

        radio_wishbook_transport.setVisibility(View.VISIBLE);

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();


        CompoundButton.OnCheckedChangeListener shipment_checked_listner = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (Map.Entry<ResponseShipment, RadioButton> entry : radioGroupShipping.entrySet()) {
                        ResponseShipment key = entry.getKey();
                        if (compoundButton == radioGroupShipping.get(key)) {
                            patchShippingMethod(cart.getShip_to(), key.getShipping_charge(), key.getShipping_method_id());
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
            RelativeLayout relative_sub_delivery_duration = v.findViewById(R.id.relative_sub_delivery_duration);
            TextView txt_shipping_duration = v.findViewById(R.id.txt_shipping_duration);
            TextView txt_delivery_duration = v.findViewById(R.id.txt_delivery_duration);

            rb.setText(shipments.get(i).getShipping_method_name());

            txt_sub_charge.setText("" + shipments.get(i).getShipping_charge());


           /* if (shipments.get(i).getShipping_method_duration() != null && !shipments.get(i).getShipping_method_duration().isEmpty()) {
                txt_shipping_duration.setText(shipments.get(i).getShipping_method_duration());
                relative_sub_ship_duration.setVisibility(View.VISIBLE);
            }*/

            if (shipments.get(i).getShipping_method_delivery_duration() != null && !shipments.get(i).getShipping_method_delivery_duration().isEmpty()) {
                txt_delivery_duration.setText(shipments.get(i).getShipping_method_delivery_duration());
                relative_sub_delivery_duration.setVisibility(View.VISIBLE);
            }


            rb.setOnCheckedChangeListener(shipment_checked_listner);
            radioGroupShipping.put(shipments.get(i), rb);
            if (!isRefreshPage) {
                if (shipments.get(i).isIs_default()) {
                    rb.setChecked(true);
                }
            } else {
                if (cart.getShipping_method() != null && shipments.get(i).getShipping_method_id().equalsIgnoreCase(cart.getShipping_method())) {
                    rb.setChecked(true);
                }
            }
            root.addView(v);
        }

    }


    void changeCodAmount() {

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


    private void showCODDialog() {
        //SPANNABLE STRING

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
                                    openResaleBottom(false);
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


    public void updateCartInvoiceUI() {

        card_invoice.setVisibility(View.VISIBLE);

        try {
            total_order_amount.setText("\u20B9 " + cart.getAmount());
            try {
                if (cart.getSeller_discount() != null && Double.parseDouble(cart.getSeller_discount()) > 0) {
                    layout_discount.setVisibility(View.VISIBLE);
                    discount_amount.setText("- \u20B9 " + cart.getSeller_discount());
                } else {
                    layout_discount.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            cart_date.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date()));
            gst_amount.setText("+ \u20B9 " + cart.getTaxes());


            if (cart.getShipping_charges() != null && !cart.getShipping_charges().equals("0.0")) {
                layout_delivery.setVisibility(View.VISIBLE);
                delivery_amount.setText("+ \u20B9 " + cart.getShipping_charges());
            } else {
                layout_delivery.setVisibility(View.GONE);
            }
            total_amount.setText("\u20B9 " + (cart.getTotal_amount()));
            linear_now_pay.setVisibility(View.VISIBLE);
            payable_amount.setText("\u20B9 " + (cart.getPending_amount()));
            boolean isShowPayableAmt = false;

            if (cart.getWbmoney_redeem_amt() > 0) {
                if (cart.getWb_money_used() > 0) {
                    linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    invoice_wb_money.setText("- " + "\u20B9 " + cart.getWb_money_used());
                }

                if (cart.getWbpoints_used() > 0) {
                    linear_invoice_reward_point.setVisibility(View.VISIBLE);
                    invoice_reward_point.setText("- " + "\u20B9 " + cart.getWbpoints_used());
                }

            } else {
                linear_invoice_wb_money.setVisibility(View.GONE);
            }

            if (cart.getWbmoney_redeem_amt() > 0) {
                if (cart.getWb_money_used() > 0) {
                    linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    invoice_wb_money.setText("- " + "\u20B9 " + cart.getWb_money_used());
                }

                if (cart.getWbpoints_used() > 0) {
                    linear_invoice_reward_point.setVisibility(View.VISIBLE);
                    invoice_reward_point.setText("- " + "\u20B9 " + cart.getWbpoints_used());
                }

                linear_now_pay.setVisibility(View.VISIBLE);
                payable_amount.setText("\u20B9 " + (cart.getPending_amount()));
            } else {
                linear_invoice_wb_money.setVisibility(View.GONE);
            }


            if (cart.getWb_coupon() != null && cart.getWb_coupon_discount() > 0) {
                linear_now_pay.setVisibility(View.VISIBLE);
                payable_amount.setText("\u20B9 " + (cart.getPending_amount()));
                showStageTwoCouponUI(cart);
            } else {
                showStageOneCouponUI();
            }


        } catch (Exception e) {
            e.printStackTrace();
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
        preferences.edit().putString("cartdataProducts", "").commit();
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


    public void refreshPage() {
        isRefreshPage = true;
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

                        if (cart.getCatalogs().get(i).isIs_full_catalog()) {
                            if (cart_full_set_flag == null) {
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
                    if (cart_full_set_flag == null) {
                        prop.put("cart_fullset_flag", "false");
                    } else {
                        prop.put("cart_fullset_flag", cart_full_set_flag);
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

                        paymentHandler();

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

    private void openResaleBottom(boolean paymentRedirected) {
        String check = "OTHER";
        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {
            check = ((RadioButton) radioGroupPayment.findViewById(radioGroupPayment.getCheckedRadioButtonId())).getTag().toString().toUpperCase();
        }
        Intent intent = new Intent(getActivity(), Activity_ResaleAmt.class);
        intent.putExtra("data", cart);
        intent.putExtra("payment_redirected", paymentRedirected);
        intent.putExtra("selected_payment", check);
        Fragment_Payment.this.startActivityForResult(intent, 9800);
    }

    private void disablePayButtons() {
        btnPayment.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
        btnPayment.setEnabled(false);
        btnPaymentCredit.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
        btnPaymentCredit.setEnabled(false);
    }

    private void enablePayButtons() {
        btnPayment.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.orange));
        btnPayment.setEnabled(true);
        btnPaymentCredit.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.orange));
        btnPaymentCredit.setEnabled(true);
    }


    public boolean isValidAddress() {
        if (addressResponses != null && addressResponses.size() > 0) {
            for (int i = 0; i < addressResponses.size(); i++) {
                if (addressResponses.get(i).is_default()) {
                    ShippingAddressResponse address1 = addressResponses.get(i);
                    if (address1.getPincode() == null || address1.getPincode().isEmpty() || address1.getPincode().equalsIgnoreCase("null") || address1.getPincode().length() != 6) {
                        Toast.makeText(getActivity(), "Please enter valid pincode in Billing Address", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (address1.getStreet_address() == null || address1.getStreet_address().isEmpty() || address1.getStreet_address().equalsIgnoreCase("null")) {
                        Toast.makeText(getActivity(), "Please enter valid address in Billing Address", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (address1.getState().getId() == null || address1.getState().getState_name().isEmpty() || address1.getState().getState_name().equalsIgnoreCase("-")) {
                        Toast.makeText(getActivity(), "Please Select valid State in Billing Address", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (address1.getCity().getId() == null || address1.getCity().getCity_name().isEmpty() || address1.getCity().getCity_name().equalsIgnoreCase("-")) {
                        Toast.makeText(getActivity(), "Please Select valid City in Billing Address", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (address1.getName() == null || address1.getName().isEmpty()) {
                        Toast.makeText(getActivity(), "Please Enter Billing name", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }

        return true;
    }


    void radioGroupPaymentOnChangeListener(RadioGroup radioGroup, @IdRes int i) {

        if (radioGroupPayment.getCheckedRadioButtonId() != -1) {


            initResaleView(false);
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
                if (textViewCodNote != null) {
                    textViewCodNote.setVisibility(View.GONE);
                }
                if (check.equalsIgnoreCase("NEFT") || check.equalsIgnoreCase("OTHER") || check.equalsIgnoreCase("CHEQUE")) {
                    btnPayment.setText("Enter Payment Details");
                } else {
                    if (check.equalsIgnoreCase("COD") && codAmount != null && Double.parseDouble(codAmount) == 0) {
                        btnPayment.setText("Confirm COD Order");
                    } else {
                        btnPayment.setText("Proceed for Payment");
                    }
                }

                if (codAmount != null && Double.parseDouble(codAmount) > 0 && check.equalsIgnoreCase("cod")) {
                    showCODDialog();
                }
                updateCartInvoiceUI();
                btnPayment.setVisibility(View.VISIBLE);
                btnPaymentCredit.setVisibility(View.GONE);
            }
        }


    }


    @OnClick(R.id.btn_payment)
    public void buttonPaymentClickListener() {

        finalStepPlaceOrder();

    }


    @OnClick(R.id.btn_payment_credit)
    public void buttonCreditPaymentClickListener() {

        finalStepPlaceOrder();
        btnPaymentCredit.setVisibility(View.GONE);
    }


    @OnClick(R.id.txt_select_coupon)
    public void buttonCouponSelectClickListener() {
        try {
            Bundle data = new Bundle();
            data.putString("cart_id", cart.getId());
            Application_Singleton.CONTAINER_TITLE = "Select Coupon Code";
            Fragment_Coupon_List fragment_coupon_list = new Fragment_Coupon_List();
            fragment_coupon_list.setArguments(data);
            Application_Singleton.CONTAINERFRAG = fragment_coupon_list;
            Fragment_Payment.this.startActivityForResult(new Intent(getActivity(), OpenContainer.class), 9900);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.txt_coupon_remove)
    public void couponRemoveClickListener() {
        patchRemoveCoupon("remove");
    }

    private boolean placeOrderValidations() {
        if (isAddressValidate()) {
            boolean isShippingMethodChecked = false;
            if (radioGroupShipping.size() == 0) {
                isShippingMethodChecked = false;

            } else {
                for (Map.Entry<ResponseShipment, RadioButton> entry : radioGroupShipping.entrySet()) {
                    ResponseShipment key = entry.getKey();
                    if (entry.getValue().isChecked()) {
                        isShippingMethodChecked = true;
                    }
                }
            }

            if (!isShippingMethodChecked) {
                Toast.makeText(getActivity(), "Please Select Shipping Method", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        return true;

    }

    private boolean isAddressValidate() {
        if (addressResponses == null) {
            Toast.makeText(getActivity(), "Please Enter billing address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (billing_address_id != null && !isValidAddress()) {

            // validation failed to open billing address dialog
            openUserProfilePage();

            return false;
        }
        if (cart.getShip_to() == null) {
            Toast.makeText(getActivity(), "Please Select any one delivery address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void openUserProfilePage() {
        Fragment_Profile fragment_profile = new Fragment_Profile();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showAsDialog", true);
        fragment_profile.setArguments(bundle);
        fragment_profile.setTargetFragment(Fragment_Payment.this, 1700);
        fragment_profile.show(getFragmentManager(), "PROFILE");
    }

    public void navigateReseller(String position) {
        Bundle reseller_bundle = new Bundle();
        Fragment_ResellerHolder fragment_resellerHolder = new Fragment_ResellerHolder();
        reseller_bundle.putString("from", Fragment_Payment.class.getSimpleName());
        reseller_bundle.putString("position", position);
        fragment_resellerHolder.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "Reseller Hub";
        Application_Singleton.CONTAINERFRAG = fragment_resellerHolder;
        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
    }

}

