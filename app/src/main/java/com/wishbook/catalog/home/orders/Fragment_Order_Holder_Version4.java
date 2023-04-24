package com.wishbook.catalog.home.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseStatistics;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.cart.Fragment_MyCart;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Order_Holder_Version4 extends GATrackedFragment implements View.OnClickListener {


    private View v;
    private Context mContext;
    private static String TAG = Fragment_Order_Holder_Version4.class.getSimpleName();


    @BindView(R.id.linear_purchase_cancel)
    LinearLayout linear_purchase_cancel;

    @BindView(R.id.linear_purchase_dispatch)
    LinearLayout linear_purchase_dispatch;

    @BindView(R.id.linear_purchase_pending)
    LinearLayout linear_purchase_pending;

    @BindView(R.id.linear_purchase_total)
    LinearLayout linear_purchase_total;


    @BindView(R.id.linear_broker_total)
    LinearLayout linear_broker_total;

    @BindView(R.id.linear_broker_cancel)
    LinearLayout linear_broker_cancel;

    @BindView(R.id.linear_broker_dispatch)
    LinearLayout linear_broker_dispatch;

    @BindView(R.id.linear_broker_pending)
    LinearLayout linear_broker_pending;

    @BindView(R.id.linear_sales_cancel)
    LinearLayout linear_sales_cancel;

    @BindView(R.id.linear_sales_dispatch)
    LinearLayout linear_sales_dispatch;

    @BindView(R.id.linear_sales_pending)
    LinearLayout linear_sales_pending;

    @BindView(R.id.linear_sales_total)
    LinearLayout linear_sales_total;

    @BindView(R.id.linear_enquiry_new)
    LinearLayout linear_enquiry_new;

    @BindView(R.id.linear_enquiry_old)
    LinearLayout linear_enquiry_old;


    @BindView(R.id.linear_leads_new)
    LinearLayout linear_leads_new;

    @BindView(R.id.linear_leads_old)
    LinearLayout linear_leads_old;

    @BindView(R.id.linear_manifest_open)
    LinearLayout linear_manifest_open;

    @BindView(R.id.linear_manifest_closed)
    LinearLayout linear_manifest_closed;

    @BindView(R.id.linear_seller_invoice_created)
    LinearLayout linear_seller_invoice_created;

    @BindView(R.id.linear_seller_invoice_uploaded)
    LinearLayout linear_seller_invoice_uploaded;

    @BindView(R.id.linear_enquiries_root)
    LinearLayout linear_enquiries_root;

    @BindView(R.id.linear_purchase_root)
    LinearLayout linear_purchase_root;

    @BindView(R.id.linear_brokerage_root)
    LinearLayout linear_brokerage_root;

    @BindView(R.id.linear_leads_root)
    LinearLayout linear_leads_root;

    @BindView(R.id.linear_sales_root)
    LinearLayout linear_sales_root;

    @BindView(R.id.linear_manifest_root)
    LinearLayout linear_manifest_root;

    @BindView(R.id.linear_seller_invoice_root)
    LinearLayout linear_seller_invoice_root;


    @BindView(R.id.card_enquiries_root)
    CardView card_enquiries_root;

    @BindView(R.id.card_purchase_root)
    CardView card_purchase_root;

    @BindView(R.id.card_brokerage_root)
    CardView card_brokerage_root;

    @BindView(R.id.card_leads_root)
    CardView card_leads_root;

    @BindView(R.id.card_sales_root)
    CardView card_sales_root;

    @BindView(R.id.card_manifest_root)
    CardView card_manifest_root;

    @BindView(R.id.card_seller_invoice_root)
    CardView card_seller_invoice_root;

    @BindView(R.id.holder_toolbar)
    Toolbar holder_toolbar;

    @BindView(R.id.main_scroll)
    ScrollView main_scroll;

    @BindView(R.id.txt_total_sales)
    TextView txt_total_sales;
    @BindView(R.id.txt_sales_pending)
    TextView txt_sales_pending;
    @BindView(R.id.txt_sales_dispatch)
    TextView txt_sales_dispatch;
    @BindView(R.id.txt_sales_cancel)
    TextView txt_sales_cancel;

    @BindView(R.id.txt_purchase_total)
    TextView txt_purchase_total;
    @BindView(R.id.txt_purchase_pending)
    TextView txt_purchase_pending;
    @BindView(R.id.txt_purchase_dispatch)
    TextView txt_purchase_dispatch;
    @BindView(R.id.txt_purchase_cancel)
    TextView txt_purchase_cancel;

    @BindView(R.id.txt_broker_total)
    TextView txt_broker_total;
    @BindView(R.id.txt_broker_pending)
    TextView txt_broker_pending;
    @BindView(R.id.txt_broker_dispatch)
    TextView txt_broker_dispatch;
    @BindView(R.id.txt_broker_cancel)
    TextView txt_broker_cancel;

    @BindView(R.id.txt_leads_total)
    TextView txt_leads_total;
    @BindView(R.id.txt_leads_open)
    TextView txt_leads_open;
    @BindView(R.id.txt_leads_closed)
    TextView txt_leads_closed;

    @BindView(R.id.txt_enquiry_total)
    TextView txt_enquiry_total;
    @BindView(R.id.txt_enquiry_open)
    TextView txt_enquiry_open;
    @BindView(R.id.txt_enquiry_closed)
    TextView txt_enquiry_closed;

    @BindView(R.id.txt_manifest_total)
    TextView txt_manifest_total;
    @BindView(R.id.txt_manifest_open)
    TextView txt_manifest_open;
    @BindView(R.id.txt_manifest_closed)
    TextView txt_manifest_closed;

    @BindView(R.id.txt_seller_invoice_total)
    TextView txt_seller_invoice_total;
    @BindView(R.id.txt_seller_invoice_created)
    TextView txt_seller_invoice_created;
    @BindView(R.id.txt_seller_invoice_uploaded)
    TextView txt_seller_invoice_uploaded;

    @BindView(R.id.grid_quick_action)
    GridLayout grid_quick_action;

    @BindView(R.id.card_action_pending_purchase_order)
    CardView card_action_pending_purchase_order;

    @BindView(R.id.card_action_enquiries)
    CardView card_action_enquiries;

    @BindView(R.id.card_action_pending_sales_order)
    CardView card_action_pending_sales_order;

    @BindView(R.id.card_action_leads)
    CardView card_action_leads;

    @BindView(R.id.txt_action_open_leads)
    TextView txt_action_open_leads;

    @BindView(R.id.txt_action_purchase_order)
    TextView txt_action_purchase_order;

    @BindView(R.id.txt_action_open_enquiries)
    TextView txt_action_open_enquiries;

    @BindView(R.id.txt_action_sales_order)
    TextView txt_action_sales_order;

    @BindView(R.id.linear_quick_action)
    LinearLayout linear_quick_action;

    @BindView(R.id.linear_total_leads)
    LinearLayout linear_total_leads;

    @BindView(R.id.linear_total_enquiries)
    LinearLayout linear_total_enquiries;

    @BindView(R.id.linear_select_date_range)
    LinearLayout linear_select_date_range;

    @BindView(R.id.txt_selected_date)
    TextView txt_selected_date;


    boolean isShowOnlyOrders = false;
    boolean isShowOnlyEnquiries = false, isShowManifest, isShowSellerInvoice;
    String date_range_type = null;
    Menu menu;
    Toolbar toolbar;
    MaterialDialog progress_dailog = null;

    public Fragment_Order_Holder_Version4() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_cart, menu);
        this.menu = menu;
        try {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                menu.findItem(R.id.cart).setVisible(false);
            } else {
                menu.findItem(R.id.cart).setVisible(true);
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cartcount = preferences.getInt("cartcount", 0);
                    if (cartcount == 0) {
                        BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                    } else {
                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cart) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cart_count = preferences.getInt("cartcount", 0);
                Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order_holder_version4, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).setSupportActionBar(holder_toolbar);
        if (UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.main_scroll).setVisibility(View.GONE);
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(), dialog);
                return v;
            } else {
                View dialog = v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog, "orders_tab");
                return v;
            }
        }
        initView();
        getStatistics(getActivity(), null, null);
        return v;
    }

    public void initView() {

        if (getArguments().getBoolean("showOnlyOrders", false)) {
            isShowOnlyOrders = true;
        }

        if (getArguments().getBoolean("showOnlyEnquiries", false)) {
            isShowOnlyEnquiries = true;
        }

        if (getArguments().getBoolean("showOnlyManifest", false)) {
            isShowManifest = true;
        }

        if (getArguments().getBoolean("showOnlySellerInvoice", false)) {
            isShowSellerInvoice = true;
        }

        if (getArguments().getBoolean("isFromHome", false)) {
            holder_toolbar.setVisibility(View.VISIBLE);
        } else {
            holder_toolbar.setVisibility(View.GONE);
        }
        try {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                holder_toolbar.getMenu().findItem(R.id.cart).setVisible(false);
            } else {
                holder_toolbar.getMenu().findItem(R.id.cart).setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupQuickAction();
        setupView();
        linear_purchase_total.setOnClickListener(this);
        linear_purchase_pending.setOnClickListener(this);
        linear_purchase_dispatch.setOnClickListener(this);
        linear_purchase_cancel.setOnClickListener(this);

        linear_sales_total.setOnClickListener(this);
        linear_sales_pending.setOnClickListener(this);
        linear_sales_dispatch.setOnClickListener(this);
        linear_sales_cancel.setOnClickListener(this);

        linear_leads_new.setOnClickListener(this);
        linear_leads_old.setOnClickListener(this);

        linear_enquiry_new.setOnClickListener(this);
        linear_enquiry_old.setOnClickListener(this);

        linear_broker_total.setOnClickListener(this);
        linear_broker_pending.setOnClickListener(this);
        linear_broker_dispatch.setOnClickListener(this);
        linear_broker_cancel.setOnClickListener(this);


        linear_manifest_open.setOnClickListener(this);
        linear_manifest_closed.setOnClickListener(this);

        linear_seller_invoice_created.setOnClickListener(this);
        linear_seller_invoice_uploaded.setOnClickListener(this);

        card_action_enquiries.setOnClickListener(this);
        card_action_leads.setOnClickListener(this);
        card_action_pending_purchase_order.setOnClickListener(this);
        card_action_pending_sales_order.setOnClickListener(this);

        linear_total_enquiries.setOnClickListener(this);
        linear_total_leads.setOnClickListener(this);

        linear_select_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomOrderDateRange bottomOrderDateRange = BottomOrderDateRange.newInstance(null);
                bottomOrderDateRange.setDismissListener(new BottomOrderDateRange.DismissListener() {
                    @Override
                    public void onDismiss(String from_date, String to_date, String displayText) {
                        if (displayText.contains(";")) {
                            date_range_type = displayText;
                            String[] split = displayText.split(";");
                            if (split.length == 2) {
                                txt_selected_date.setText("Start " + split[0] + " - " + "End " + split[1]);
                            }
                        } else {
                            date_range_type = displayText;
                            txt_selected_date.setText(displayText);
                        }
                        getStatistics(getActivity(), from_date, to_date);
                    }
                });
                bottomOrderDateRange.show(getFragmentManager(), "SelectRange");
            }
        });

        initData();
    }


    public void initData() {
        if (PrefDatabaseUtils.getPrefStatistics(mContext) != null) {
            ResponseStatistics statistics = new Gson().fromJson(PrefDatabaseUtils.getPrefStatistics(getActivity()), new TypeToken<ResponseStatistics>() {
            }.getType());

            txt_total_sales.setText(String.valueOf(statistics.getTotal_salesorder()));
            txt_sales_pending.setText(String.valueOf(statistics.getSalesorder_pending()));
            txt_action_sales_order.setText(String.valueOf(statistics.getSalesorder_pending()) + " Sales Orders");
            txt_sales_dispatch.setText(String.valueOf(statistics.getSalesorder_dispatched()));
            txt_sales_cancel.setText(String.valueOf(statistics.getSalesorder_cancelled()));


            txt_purchase_total.setText(String.valueOf(statistics.getTotal_purchaseorder()));
            txt_purchase_pending.setText(String.valueOf(statistics.getPurchaseorder_pending()));
            txt_action_purchase_order.setText(String.valueOf(statistics.getPurchaseorder_pending()) + " Purchase Orders");
            txt_purchase_dispatch.setText(String.valueOf(statistics.getPurchaseorder_dispatched()));
            txt_purchase_cancel.setText(String.valueOf(statistics.getPurchaseorder_cancelled()));


            txt_broker_total.setText(String.valueOf(statistics.getTotal_brokerorder()));
            txt_broker_pending.setText(String.valueOf(statistics.getBrokerorder_pending()));
            txt_broker_dispatch.setText(String.valueOf(statistics.getBrokerorder_dispatched()));
            txt_broker_cancel.setText(String.valueOf(statistics.getBrokerorder_cancelled()));

            txt_leads_total.setText(String.valueOf(statistics.getTotal_cataloglead()));
            txt_leads_open.setText(String.valueOf(statistics.getOpened_cataloglead()));
            txt_action_open_leads.setText(String.valueOf(statistics.getOpened_cataloglead()) + " Leads");
            txt_leads_closed.setText(String.valueOf(statistics.getClosed_cataloglead()));

            txt_enquiry_total.setText(String.valueOf(statistics.getTotal_catalogenquiry()));
            txt_enquiry_open.setText(String.valueOf(statistics.getOpened_catalogenquiry()));
            txt_action_open_enquiries.setText(String.valueOf(statistics.getOpened_catalogenquiry()) + " Enquiries");
            txt_enquiry_closed.setText(String.valueOf(statistics.getClosed_catalogenquiry()));


            txt_manifest_total.setText(String.valueOf(statistics.getTotal_manifest()));
            txt_manifest_open.setText(String.valueOf(statistics.getCreated_manifest()));
            txt_manifest_closed.setText(String.valueOf(statistics.getClosed_manifest()));

            txt_seller_invoice_total.setText(String.valueOf(statistics.getTotal_seller_invoice()));
            txt_seller_invoice_created.setText(String.valueOf(statistics.getCreated_seller_invoice()));
            txt_seller_invoice_uploaded.setText(String.valueOf(statistics.getUploaded_seller_invoice()));


        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.linear_purchase_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 0).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_purchase_pending:
            case R.id.card_action_pending_purchase_order:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 1).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_purchase_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 2).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_purchase_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 3).putExtra("date_range_type", date_range_type));
                break;


            case R.id.linear_sales_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 0).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_sales_pending:
            case R.id.card_action_pending_sales_order:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 1).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_sales_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 2).putExtra("date_range_type", date_range_type));
                break;
            case R.id.linear_sales_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 3).putExtra("date_range_type", date_range_type));
                break;

            case R.id.linear_broker_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "broker").putExtra("position", 0));
                break;
            case R.id.linear_broker_pending:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "broker").putExtra("position", 1));
                break;
            case R.id.linear_broker_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "broker").putExtra("position", 2));
                break;
            case R.id.linear_broker_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "broker").putExtra("position", 3));
                break;

            case R.id.linear_total_leads:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "leads").putExtra("position", 0));
                break;

            case R.id.linear_leads_new:
            case R.id.card_action_leads:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "leads").putExtra("position", 1));
                break;
            case R.id.linear_leads_old:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "leads").putExtra("position", 2));
                break;

            case R.id.linear_total_enquiries:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "enquiry").putExtra("position", 0));
                break;
            case R.id.linear_enquiry_new:
            case R.id.card_action_enquiries:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "enquiry").putExtra("position", 1));
                break;
            case R.id.linear_enquiry_old:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "enquiry").putExtra("position", 2));
                break;

            case R.id.linear_manifest_open:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "manifest").putExtra("position", 0));
                break;

            case R.id.linear_manifest_closed:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "manifest").putExtra("position", 1));
                break;

            case R.id.linear_seller_invoice_created:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "seller_invoice").putExtra("position", 0));
                break;

            case R.id.linear_seller_invoice_uploaded:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "seller_invoice").putExtra("position", 1));
                break;
        }
    }


    public void setupView() {
        Activity_Home.pref = StaticFunctions.getAppSharedPreferences(mContext);
        if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {

                if (isShowOnlyOrders) {
                    linear_sales_root.setVisibility(View.VISIBLE);
                    card_sales_root.setVisibility(View.VISIBLE);
                } else {
                    linear_sales_root.setVisibility(View.GONE);
                    card_sales_root.setVisibility(View.GONE);
                }

                if (isShowOnlyEnquiries) {
                    if (isAllowContextBasedChat(getActivity())) {
                        card_leads_root.setVisibility(View.VISIBLE);
                        linear_leads_root.setVisibility(View.VISIBLE);
                    } else {
                        card_leads_root.setVisibility(View.GONE);
                        linear_leads_root.setVisibility(View.GONE);
                    }
                } else {
                    card_leads_root.setVisibility(View.GONE);
                    linear_leads_root.setVisibility(View.GONE);
                }

                if (isShowManifest) {
                    card_manifest_root.setVisibility(View.VISIBLE);
                    linear_manifest_root.setVisibility(View.VISIBLE);
                } else {
                    card_manifest_root.setVisibility(View.GONE);
                    linear_manifest_root.setVisibility(View.GONE);
                }

                if (isShowSellerInvoice) {
                    card_seller_invoice_root.setVisibility(View.VISIBLE);
                    linear_seller_invoice_root.setVisibility(View.VISIBLE);
                } else {
                    card_seller_invoice_root.setVisibility(View.GONE);
                    linear_seller_invoice_root.setVisibility(View.GONE);
                }

            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                if (isShowOnlyOrders) {
                    card_purchase_root.setVisibility(View.VISIBLE);
                    linear_purchase_root.setVisibility(View.VISIBLE);
                } else {
                    card_purchase_root.setVisibility(View.GONE);
                    linear_purchase_root.setVisibility(View.GONE);
                }

                if (isShowOnlyEnquiries) {
                    card_enquiries_root.setVisibility(View.VISIBLE);
                    linear_enquiries_root.setVisibility(View.VISIBLE);
                } else {
                    card_enquiries_root.setVisibility(View.GONE);
                    linear_enquiries_root.setVisibility(View.GONE);
                }
            } else {
                if (isShowOnlyOrders) {
                    card_sales_root.setVisibility(View.VISIBLE);
                    card_purchase_root.setVisibility(View.VISIBLE);
                    linear_sales_root.setVisibility(View.VISIBLE);
                    linear_purchase_root.setVisibility(View.VISIBLE);
                } else {
                    card_sales_root.setVisibility(View.GONE);
                    card_purchase_root.setVisibility(View.GONE);
                    linear_sales_root.setVisibility(View.GONE);
                    linear_purchase_root.setVisibility(View.GONE);
                }

                if (isShowOnlyEnquiries) {
                    card_enquiries_root.setVisibility(View.VISIBLE);
                    linear_enquiries_root.setVisibility(View.VISIBLE);
                    if (isAllowContextBasedChat(getActivity())) {
                        linear_leads_root.setVisibility(View.VISIBLE);
                        card_leads_root.setVisibility(View.VISIBLE);
                    } else {
                        linear_leads_root.setVisibility(View.GONE);
                        card_leads_root.setVisibility(View.GONE);
                    }
                } else {
                    card_leads_root.setVisibility(View.GONE);
                    card_enquiries_root.setVisibility(View.GONE);
                    linear_leads_root.setVisibility(View.GONE);
                    linear_enquiries_root.setVisibility(View.GONE);
                }

                if (isShowManifest) {
                    card_manifest_root.setVisibility(View.VISIBLE);
                    linear_manifest_root.setVisibility(View.VISIBLE);
                } else {
                    card_manifest_root.setVisibility(View.GONE);
                    linear_manifest_root.setVisibility(View.GONE);
                }


                if (isShowSellerInvoice) {
                    card_seller_invoice_root.setVisibility(View.VISIBLE);
                    linear_seller_invoice_root.setVisibility(View.VISIBLE);
                } else {
                    card_seller_invoice_root.setVisibility(View.GONE);
                    linear_seller_invoice_root.setVisibility(View.GONE);
                }

                if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                    if (isShowOnlyOrders) {
                        card_brokerage_root.setVisibility(View.VISIBLE);
                        linear_brokerage_root.setVisibility(View.VISIBLE);
                    } else {
                        card_brokerage_root.setVisibility(View.GONE);
                        linear_brokerage_root.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            // sales man
            if (isShowOnlyOrders) {
                linear_sales_root.setVisibility(View.VISIBLE);
                card_sales_root.setVisibility(View.VISIBLE);
            } else {
                linear_sales_root.setVisibility(View.GONE);
                card_sales_root.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                holder_toolbar.getMenu().findItem(R.id.cart).setVisible(false);
            } else {
                holder_toolbar.getMenu().findItem(R.id.cart).setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            holder_toolbar.getMenu().findItem(R.id.cart).setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAllowContextBasedChat(Context context) {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("CONTEXT_BASED_ENQUIRY_FEATURE_IN_APP")) {
                    try {
                        if (Integer.parseInt(data.get(i).getValue()) == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        return true;
    }

    public void setupQuickAction() {
        if (getArguments().getBoolean("isFromHome", false)) {
            int grid_count = 0;
            if (PrefDatabaseUtils.getPrefStatistics(mContext) != null) {
                ResponseStatistics statistics = new Gson().fromJson(PrefDatabaseUtils.getPrefStatistics(getActivity()), new TypeToken<ResponseStatistics>() {
                }.getType());

                grid_quick_action.removeAllViews();
                if (Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                    if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                        if (statistics.getSalesorder_pending() > 0) {
                            card_action_pending_sales_order.setVisibility(View.VISIBLE);
                            grid_quick_action.addView(card_action_pending_sales_order);
                        }

                        if (statistics.getOpened_cataloglead() > 0) {
                            card_action_leads.setVisibility(View.VISIBLE);
                            grid_quick_action.addView(card_action_leads);
                        }


                    } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                        /**
                         * changes done April-5 2019
                         * changes remove quick action purchase,enquiries
                         */
                        if (statistics.getPurchaseorder_pending() > 0) {
                           /* card_action_pending_purchase_order.setVisibility(View.GONE);
                            grid_quick_action.addView(card_action_pending_purchase_order);*/
                        }

                        if (statistics.getOpened_catalogenquiry() > 0) {
                            /*card_action_enquiries.setVisibility(View.GONE);
                            grid_quick_action.addView(card_action_enquiries);*/
                        }

                    } else {
                        if (statistics.getSalesorder_pending() > 0) {
                            grid_count++;
                            card_action_pending_sales_order.setVisibility(View.VISIBLE);
                            grid_quick_action.addView(card_action_pending_sales_order);
                        } else {
                            card_action_pending_sales_order.setVisibility(View.GONE);
                        }

                        if (statistics.getOpened_cataloglead() > 0) {
                            grid_count++;
                            card_action_leads.setVisibility(View.VISIBLE);
                            grid_quick_action.addView(card_action_leads);
                        } else {
                            card_action_leads.setVisibility(View.GONE);
                        }

                        if (statistics.getPurchaseorder_pending() > 0) {
                            /*grid_count++;
                            card_action_pending_purchase_order.setVisibility(View.GONE);
                            grid_quick_action.addView(card_action_pending_purchase_order);*/
                        } else {
                            card_action_pending_purchase_order.setVisibility(View.GONE);
                        }

                        if (statistics.getOpened_catalogenquiry() > 0) {
                         /*   grid_count++;
                            card_action_enquiries.setVisibility(View.GONE);
                            grid_quick_action.addView(card_action_enquiries);*/
                        } else {
                            card_action_enquiries.setVisibility(View.GONE);
                        }

                  /*  if (grid_count % 2 == 0) {
                        Log.e(TAG, "setupQuickAction:  Even Number");
                        // Even number grid
                        updateGridLayout();
                        grid_quick_action.setColumnCount(2);
                        grid_quick_action.setRowCount(1);
                        grid_quick_action.requestLayout();
                    } else {
                        // Odd number grid
                        if (grid_count == 3) {
                            Log.e(TAG, "setupQuickAction:  Odd Number");
                            updateGridLayout();
                            grid_quick_action.setColumnCount(3);
                            grid_quick_action.setRowCount(1);
                        }
                    }*/

                    }
                }

                if (grid_quick_action.getChildCount() > 0) {
                    linear_quick_action.setVisibility(View.VISIBLE);
                } else {
                    linear_quick_action.setVisibility(View.GONE);
                }
            }
        } else {
            linear_quick_action.setVisibility(View.GONE);
        }
    }

    private void getStatistics(final Context activity, String from_date, String to_date) {
        String url = null;
        url = URLConstants.companyUrl(activity, "statistics", "");

        if (from_date != null) {
            String from_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, from_date);
            String to_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, to_date);
            url += "?" + "from_date=" + from_date_string + "&to_date=" + to_date_string;
            progress_dailog = StaticFunctions.showProgress(getActivity());
            progress_dailog.show();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) activity);
        HttpManager.getInstance((Activity) activity).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (progress_dailog != null) {
                        progress_dailog.dismiss();
                    }
                    if (isAdded() && !isDetached()) {
                        ResponseStatistics responseStatistics = Application_Singleton.gson.fromJson(response, ResponseStatistics.class);
                        PrefDatabaseUtils.setPrefStatistics(activity, new Gson().toJson(responseStatistics));
                        UserInfo.getInstance(activity).setWishlistCount(responseStatistics.getWishlist());
                        initView();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress_dailog != null) {
                    progress_dailog.dismiss();
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        if (progress_dailog != null) {
            progress_dailog.dismiss();
        }
        super.onDestroyView();
    }
}
