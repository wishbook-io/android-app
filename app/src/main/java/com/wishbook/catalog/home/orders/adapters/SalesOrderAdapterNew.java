package com.wishbook.catalog.home.orders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orderNew.details.BuyerCreditRatingBottomSheetDialogFragment;
import com.wishbook.catalog.home.orders.Fragment_SalesOrders_Holder;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SalesOrderAdapterNew extends RecyclerView.Adapter<SalesOrderAdapterNew.CustomViewHolder> {


    private ArrayList<Response_sellingorder> feedItemList;
    private Context mContext;
    private boolean isBrokerage;

    public SalesOrderAdapterNew(Context context, ArrayList<Response_sellingorder> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public SalesOrderAdapterNew(Context context, ArrayList<Response_sellingorder> feedItemList, boolean isBrokerage) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.isBrokerage = isBrokerage;
    }

    public void setData(ArrayList<Response_sellingorder> filteredModelList) {
        this.feedItemList = filteredModelList;
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_order_item_new, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
        customViewHolder.linear_seller.setVisibility(View.GONE);
        customViewHolder.linear_brokerage.setVisibility(View.GONE);


        /*if (feedItemList.get(position).getBuyer_credit_rating() != null && !feedItemList.get(position).getBuyer_credit_rating().isEmpty()) {
            customViewHolder.linear_burer_credit_rating.setVisibility(View.VISIBLE);
            if(feedItemList.get(position).getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_GOOD)){
                customViewHolder.img_good_credit.setVisibility(View.VISIBLE);
                customViewHolder.txt_credit_see_details.setVisibility(View.VISIBLE);
                customViewHolder.txt_unrated.setVisibility(View.GONE);
            } else if(feedItemList.get(position).getBuyer_credit_rating().equals(Constants.BUYER_CREDIT_RATING_UNRATED)){
                customViewHolder.img_good_credit.setVisibility(View.GONE);
                customViewHolder.txt_credit_see_details.setVisibility(View.GONE);
                customViewHolder.txt_unrated.setVisibility(View.VISIBLE);
            }
        } else {
            customViewHolder.linear_burer_credit_rating.setVisibility(View.GONE);
        }*/
        if (isBrokerage) {
            customViewHolder.linear_seller.setVisibility(View.VISIBLE);
            customViewHolder.linear_brokerage.setVisibility(View.GONE);
            customViewHolder.linear_broker.setVisibility(View.GONE);
            if (feedItemList.get(position).getBrokerage_fees() != null) {
                customViewHolder.linear_brokerage.setVisibility(View.VISIBLE);
                String brokerage = "\u20B9" + feedItemList.get(position).getBrokerage() + "(" + feedItemList.get(position).getBrokerage_fees() + "%)";
                customViewHolder.txt_brokerage.setText(brokerage);
            }
        } else {
            customViewHolder.linear_seller.setVisibility(View.GONE);
            customViewHolder.linear_brokerage.setVisibility(View.GONE);


            if (feedItemList.get(position).getBroker_company() != null && !feedItemList.get(position).getBroker_company().isEmpty()) {
                customViewHolder.linear_broker.setVisibility(View.VISIBLE);
                if(feedItemList.get(position).getBroker_company_name()!=null)
                    customViewHolder.txt_broker_name.setText(StringUtils.capitalize(feedItemList.get(position).getBroker_company_name().toLowerCase().trim()));
            } else {
                customViewHolder.linear_broker.setVisibility(View.GONE);
            }


        }
        if (feedItemList.get(position).getImages() != null && feedItemList.get(position).getImages().size() > 0) {
            StaticFunctions.loadFresco(mContext, feedItemList.get(position).getImages().get(0), customViewHolder.prod_img);
        }


        if (feedItemList.get(position).getSeller_company_name() != null) {
            customViewHolder.txt_seller_name.setText(feedItemList.get(position).getSeller_company_name());
        }
        if (feedItemList.get(position).getCompany_name() != null) {
            customViewHolder.txt_buyer_name.setText(StringUtils.capitalize(feedItemList.get(position).getCompany_name().toLowerCase().trim()));

            customViewHolder.txt_buyer_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hyperlinking
                    StaticFunctions.hyperLinking("buyer", feedItemList.get(position).getBuyer_table_id(), mContext);
                }
            });
        }
        customViewHolder.txt_order_date.setText(getformatedDate(StringUtils.capitalize(feedItemList.get(position).getDate().toLowerCase().trim())));

        if (feedItemList.get(position).getOrder_number() != null && !feedItemList.get(position).getOrder_number().isEmpty())
            customViewHolder.txt_order_no.setText("Order #" + feedItemList.get(position).getOrder_number().toUpperCase().trim());
        else
            customViewHolder.txt_order_no.setText("Order #" + feedItemList.get(position).getId().trim());

        customViewHolder.txt_order_value.setText("\u20B9" + feedItemList.get(position).getTotal_rate());

        if(feedItemList.get(position).getProcessing_status().toLowerCase().trim().equalsIgnoreCase("pending")){
            customViewHolder.txt_order_status.setText(Constants.PENDING_ORDER_DISPLAY_TEXT);
        } else {
            customViewHolder.txt_order_status.setText(StringUtils.capitalize(StringUtils.capitalize(feedItemList.get(position).getProcessing_status().toLowerCase().trim())));
        }

        setColor(feedItemList.get(position).getProcessing_status(), customViewHolder.txt_order_status);


        if(feedItemList.get(position).getCompany()!=null && feedItemList.get(position).getCompany().equalsIgnoreCase("10")){
            customViewHolder.linear_payment_status.setVisibility(View.GONE);
        } else {
            customViewHolder.linear_payment_status.setVisibility(View.VISIBLE);
            if (feedItemList.get(position).getPayment_status() != null) {
                customViewHolder.txt_payemnt_status.setText(StringUtils.capitalize(feedItemList.get(position).getPayment_status().toLowerCase().trim()));
                setColor(feedItemList.get(position).getPayment_status(), customViewHolder.txt_payemnt_status);
            }
        }

        customViewHolder.linear_burer_credit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(customViewHolder.img_good_credit.getVisibility() == View.VISIBLE){
                    openCreditBottom(mContext, feedItemList.get(position).getCompany(), StringUtils.capitalize(feedItemList.get(position).getCompany_name().toLowerCase().trim()));
                }
            }
        });

        customViewHolder.main_container_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.trackEvent("Order", "Order Card", "Sales" + feedItemList.get(position).getProcessing_status());

                /*ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeScaleUpAnimation(customViewHolder.main_container_2, 0, 0, customViewHolder.main_container_2.getWidth(), customViewHolder.main_container_2.getHeight());*/


                if (isBrokerage) {
                    Response_buyingorder order = new Response_buyingorder(feedItemList.get(position).getId());
                    order.setBrokerage(true);
                    Application_Singleton.selectedOrder = order;
                } else {
                    Response_sellingorder order = new Response_sellingorder(feedItemList.get(position).getId());
                    Application_Singleton.selectedOrder = order;
                }


                Intent intent = new Intent(mContext, Activity_OrderDetailsNew.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("supplier", true);
                bundle.putString("from","Sales Order List");
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    if (Fragment_SalesOrders_Holder.fragment != null) {
                        Log.d("salesorderadapter", "in fragcode");
                        Fragment_SalesOrders_Holder.fragment.startActivityForResult(intent, ResponseCodes.SalesResponse);
                    } else {
                        Log.d("salesorderadapter", "in startActivity");
                        mContext.startActivity(intent);
                    }
                } else {

                    if (Fragment_SalesOrders_Holder.fragment != null) {
                        Log.d("salesorderadapter", "in fragcode <J");
                        Fragment_SalesOrders_Holder.fragment.startActivityForResult(intent, ResponseCodes.SalesResponse);
                    } else {
                        Log.d("salesorderadapter", "in startActivity <J");
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    private void openCreditBottom(Context context, String buyingCompanyId, String buyerCompanyName) {
        BuyerCreditRatingBottomSheetDialogFragment creditBottomDialog = null;
        creditBottomDialog = creditBottomDialog.newInstance(buyingCompanyId,buyerCompanyName);
        creditBottomDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "CreditRating");
    }

    private String getformatedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd, yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setColor(String status, TextView textView) {
        if (status.equalsIgnoreCase("Draft") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Partially Paid")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_order_no)
        TextView txt_order_no;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.txt_buyer_name)
        TextView txt_buyer_name;
        @BindView(R.id.txt_seller_name)
        TextView txt_seller_name;
        @BindView(R.id.txt_order_value)
        TextView txt_order_value;
        @BindView(R.id.txt_brokerage)
        TextView txt_brokerage;
        @BindView(R.id.txt_payemnt_status)
        TextView txt_payemnt_status;
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;

        @BindView(R.id.linear_buyer)
        LinearLayout linear_buyer;
        @BindView(R.id.linear_seller)
        LinearLayout linear_seller;
        @BindView(R.id.linear_order_value)
        LinearLayout linear_order_value;
        @BindView(R.id.linear_brokerage)
        LinearLayout linear_brokerage;
        @BindView(R.id.linear_payment_status)
        LinearLayout linear_payment_status;
        @BindView(R.id.linear_order_status)
        LinearLayout linear_order_status;
        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.txt_credit_see_details)
        TextView txt_credit_see_details;

        @BindView(R.id.linear_broker)
        LinearLayout linear_broker;

        @BindView(R.id.txt_broker_name)
        TextView txt_broker_name;

        @BindView(R.id.linear_burer_credit_rating)
        LinearLayout linear_burer_credit_rating;

        @BindView(R.id.img_good_credit)
        ImageView img_good_credit;

        @BindView(R.id.txt_unrated)
        TextView txt_unrated;

        @BindView(R.id.main_container_2)
        LinearLayout main_container_2;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
