package com.wishbook.catalog.home.orders;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseStatistics;
import com.wishbook.catalog.home.Activity_Home;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Order_Holder_Version3 extends GATrackedFragment implements View.OnClickListener {

    private View v;
    private Context mContext;
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

    boolean isShowOnlyOrders = false;
    boolean isShowOnlyEnquiries = false;


    public Fragment_Order_Holder_Version3() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order_version7, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, v);
        if (UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.main_scroll).setVisibility(View.GONE);
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            Log.d("TAG", "onCreateView:  Guest");
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                Log.d("TAG", "onCreateView:  Guest Pending");
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(),dialog);
                return v;
            } else {
                Log.d("TAG", "onCreateView:  Guest Login");
                View dialog = v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog,"orders_tab");
                return v;
            }
        }
        initView();
        return v;
    }

    public void initView() {

        if (getArguments().getBoolean("showOnlyOrders", false)) {
            isShowOnlyOrders = true;
        }

        if (getArguments().getBoolean("showOnlyEnquiries", false)) {
            isShowOnlyEnquiries = true;
        }

        if (getArguments().getBoolean("isFromHome", false)) {
            holder_toolbar.setVisibility(View.VISIBLE);
        } else {
            holder_toolbar.setVisibility(View.GONE);
        }

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

        initData();
    }


    public void initData() {
        if (PrefDatabaseUtils.getPrefStatistics(mContext) != null) {
            ResponseStatistics statistics = new Gson().fromJson(PrefDatabaseUtils.getPrefStatistics(getActivity()), new TypeToken<ResponseStatistics>() {
            }.getType());

            txt_total_sales.setText(String.valueOf(statistics.getTotal_salesorder()));
            txt_sales_pending.setText(String.valueOf(statistics.getSalesorder_pending()));
            txt_sales_dispatch.setText(String.valueOf(statistics.getSalesorder_dispatched()));
            txt_sales_cancel.setText(String.valueOf(statistics.getSalesorder_cancelled()));


            txt_purchase_total.setText(String.valueOf(statistics.getTotal_purchaseorder()));
            txt_purchase_pending.setText(String.valueOf(statistics.getPurchaseorder_pending()));
            txt_purchase_dispatch.setText(String.valueOf(statistics.getPurchaseorder_dispatched()));
            txt_purchase_cancel.setText(String.valueOf(statistics.getPurchaseorder_cancelled()));


            txt_broker_total.setText(String.valueOf(statistics.getTotal_brokerorder()));
            txt_broker_pending.setText(String.valueOf(statistics.getBrokerorder_pending()));
            txt_broker_dispatch.setText(String.valueOf(statistics.getBrokerorder_dispatched()));
            txt_broker_cancel.setText(String.valueOf(statistics.getBrokerorder_cancelled()));

            txt_leads_total.setText(String.valueOf(statistics.getTotal_cataloglead()));
            txt_leads_open.setText(String.valueOf(statistics.getOpened_cataloglead()));
            txt_leads_closed.setText(String.valueOf(statistics.getClosed_cataloglead()));

            txt_enquiry_total.setText(String.valueOf(statistics.getTotal_catalogenquiry()));
            txt_enquiry_open.setText(String.valueOf(statistics.getOpened_catalogenquiry()));
            txt_enquiry_closed.setText(String.valueOf(statistics.getClosed_catalogenquiry()));



        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.linear_purchase_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "purchase").putExtra("position", 0));
                break;
            case R.id.linear_purchase_pending:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "purchase").putExtra("position", 1));
                break;
            case R.id.linear_purchase_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "purchase").putExtra("position", 2));
                break;
            case R.id.linear_purchase_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "purchase").putExtra("position", 3));
                break;


            case R.id.linear_sales_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "sale").putExtra("position", 0));
                break;
            case R.id.linear_sales_pending:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "sale").putExtra("position", 1));
                break;
            case R.id.linear_sales_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "sale").putExtra("position", 2));
                break;
            case R.id.linear_sales_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "sale").putExtra("position", 3));
                break;

            case R.id.linear_broker_total:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "broker").putExtra("position", 0));
                break;
            case R.id.linear_broker_pending:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "broker").putExtra("position", 1));
                break;
            case R.id.linear_broker_dispatch:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "broker").putExtra("position", 2));
                break;
            case R.id.linear_broker_cancel:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("order_date_range_type", "broker").putExtra("position", 3));
                break;

            case R.id.linear_leads_new:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("order_date_range_type", "leads").putExtra("position", 0));
                break;
            case R.id.linear_leads_old:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("order_date_range_type", "leads").putExtra("position", 1));
                break;
            case R.id.linear_enquiry_new:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("order_date_range_type", "enquiry").putExtra("position", 0));
                break;
            case R.id.linear_enquiry_old:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("order_date_range_type", "enquiry").putExtra("position", 1));
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
                    card_leads_root.setVisibility(View.VISIBLE);
                    linear_leads_root.setVisibility(View.VISIBLE);
                } else {
                    card_leads_root.setVisibility(View.GONE);
                    linear_leads_root.setVisibility(View.GONE);
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
                    card_leads_root.setVisibility(View.VISIBLE);
                    card_enquiries_root.setVisibility(View.VISIBLE);
                    linear_leads_root.setVisibility(View.VISIBLE);
                    linear_enquiries_root.setVisibility(View.VISIBLE);
                } else {
                    card_leads_root.setVisibility(View.GONE);
                    card_enquiries_root.setVisibility(View.GONE);
                    linear_leads_root.setVisibility(View.GONE);
                    linear_enquiries_root.setVisibility(View.GONE);
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
}
