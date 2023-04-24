package com.wishbook.catalog.home.orders.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.postpatchmodels.Shipments;
import com.wishbook.catalog.commonmodels.responses.OrderStep;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orderNew.details.Fragment_ShipTrack;
import com.wishbook.catalog.home.orderNew.details.OrderShareBottomSheetDialogFragment;
import com.wishbook.catalog.home.orders.Fragment_PurchaseOrders_Holder;
import com.wishbook.catalog.home.rrc.Fragment_RRC_OrderItemSelection;
import com.wishbook.catalog.home.rrc.RRCHandler;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PurchaseOrderAdapterNew extends RecyclerView.Adapter<PurchaseOrderAdapterNew.CustomViewHolder> {


    private ArrayList<Response_buyingorder> feedItemList;
    private Context mContext;
    private boolean isBrokerage;

    private int holderHeight;

    boolean isRRCSelection ;
    RRCHandler.RRCREQUESTTYPE rrcrequesttype;


    public PurchaseOrderAdapterNew(Context context, ArrayList<Response_buyingorder> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public PurchaseOrderAdapterNew(Context context, ArrayList<Response_buyingorder> feedItemList, boolean isRRCSelection, RRCHandler.RRCREQUESTTYPE rrcrequesttype) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.isRRCSelection = isRRCSelection;
        this.rrcrequesttype = rrcrequesttype;
    }

    public void setData(ArrayList<Response_buyingorder> filteredModelList) {
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
        customViewHolder.linear_buyer.setVisibility(View.GONE);
        customViewHolder.linear_brokerage.setVisibility(View.GONE);
        customViewHolder.linear_burer_credit_rating.setVisibility(View.GONE);
        customViewHolder.txt_new_order.setVisibility(View.GONE);
        customViewHolder.txt_share_order.setVisibility(View.GONE);


        if(feedItemList.get(position).getBroker_company()!=null && !feedItemList.get(position).getBroker_company().isEmpty()){
            customViewHolder.linear_broker.setVisibility(View.VISIBLE);
            customViewHolder.txt_broker_name.setText(StringUtils.capitalize(feedItemList.get(position).getBroker_company_name().toUpperCase().trim()));
        } else {
            customViewHolder.linear_broker.setVisibility(View.GONE);
        }

        if(feedItemList.get(position).getImages()!=null && feedItemList.get(position).getImages().size() > 0) {
            StaticFunctions.loadFresco(mContext,feedItemList.get(position).getImages().get(0),customViewHolder.prod_img);
        }
        customViewHolder.txt_seller_name.setText(StringUtils.capitalize(feedItemList.get(position).getSeller_company_name().toLowerCase().trim()));
        customViewHolder.txt_seller_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hyperlinking
                StaticFunctions.hyperLinking("supplier", feedItemList.get(customViewHolder.getAdapterPosition()).getBuyer_table_id(), mContext);
            }
        });
        customViewHolder.txt_order_date.setText(getformatedDate(StringUtils.capitalize(feedItemList.get(position).getDate().toLowerCase().trim())));


        if(feedItemList.get(position).getOrder_number() !=null && !feedItemList.get(position).getOrder_number().isEmpty())
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
        if (feedItemList.get(position).getPayment_status() != null) {
            if(feedItemList.get(position).getPayment_status()!=null &&
                    feedItemList.get(position).getPayment_status().equalsIgnoreCase("Partially paid")) {
                customViewHolder.txt_payemnt_status.setText("COD");
            } else {
                customViewHolder.txt_payemnt_status.setText(StringUtils.capitalize(feedItemList.get(position).getPayment_status().toLowerCase().trim()));
            }
            setColor(feedItemList.get(position).getPayment_status(), customViewHolder.txt_payemnt_status);
        }


        customViewHolder.txt_share_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderShareBottomSheetDialogFragment shareBottomSheetDialogFragment = OrderShareBottomSheetDialogFragment.newInstance(feedItemList.get(position).getId());
                shareBottomSheetDialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Share");
            }
        });

        customViewHolder.main_container_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    KeyboardUtils.hideKeyboard((Activity) mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isRRCSelection) {
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", feedItemList.get(customViewHolder.getAdapterPosition()).getId());
                    bundle.putSerializable("request_type", rrcrequesttype);
                    Fragment_RRC_OrderItemSelection fragment_rrc_orderItemSelection = new Fragment_RRC_OrderItemSelection();
                    fragment_rrc_orderItemSelection.setArguments(bundle);
                    if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
                        Application_Singleton.CONTAINER_TITLE = "Select items of replacement";
                    } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
                        Application_Singleton.CONTAINER_TITLE = "Select items of cancellation";
                    } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
                        Application_Singleton.CONTAINER_TITLE = "Select items of retrun";
                    }
                    Application_Singleton.CONTAINERFRAG = fragment_rrc_orderItemSelection;
                    mContext.startActivity(new Intent(mContext, OpenContainer.class));
                    ((Activity) mContext).finish();
                } else {
                    Application_Singleton.trackEvent("Order", "Order Card", "Purchase" + feedItemList.get(customViewHolder.getAdapterPosition()).getProcessing_status());
                    customViewHolder.txt_new_order.setVisibility(View.GONE);
                    Response_buyingorder order = new Response_buyingorder(feedItemList.get(customViewHolder.getAdapterPosition()).getId());
                    Application_Singleton.selectedOrder = order;
                    if (Fragment_PurchaseOrders_Holder.fragment != null && Fragment_PurchaseOrders_Holder.fragment.isAdded()) {
                        Fragment_PurchaseOrders_Holder.fragment.startActivityForResult(new Intent(mContext, Activity_OrderDetailsNew.class).putExtra("from", "Purchase Order List"), ResponseCodes.PurchaseResponse);
                    } else {
                        mContext.startActivity(new Intent(mContext, Activity_OrderDetailsNew.class).putExtra("from", "Purchase Order List"));
                    }
                }

            }
        });


        if(Application_Singleton.New_ORDER_COUNT > 0){
            if(position<=Application_Singleton.New_ORDER_COUNT-1){
                customViewHolder.txt_new_order.setVisibility(View.VISIBLE);
            }
        }

        if(!isRRCSelection)
            setOrderTrack(feedItemList.get(position).getLastest_shipment(),customViewHolder,feedItemList.get(position).getProcessing_status());

    }

    private void setOrderTrack(final Shipments shipments, CustomViewHolder holder, final String orderStatus) {
        holder.linear_main_order_track.setVisibility(View.VISIBLE);
        ArrayList<OrderStep> orderSteps = new ArrayList<>();
        orderSteps.add(new OrderStep("Order Placed", OrderStep.STEP_COMPLETED));
        if(orderStatus!=null &&
                (orderStatus.equalsIgnoreCase("Transferred")
                        || orderStatus.equalsIgnoreCase("Cancelled"))) {
            orderSteps.add(new OrderStep(orderStatus, OrderStep.STEP_COMPLETED));
        } else {
            if (shipments!=null) {
                int step = 0;
                if (shipments.getStatus() != null && (shipments.getStatus().equalsIgnoreCase(Constants.SHIPMENT_READYTOSHIP)
                        || shipments.getStatus().equalsIgnoreCase(Constants.MANIFESTED))) {
                    step = 1;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                } else if (shipments.getStatus() != null && shipments.getStatus().equalsIgnoreCase(Constants.SHIPMENT_DISPATCHED)
                || shipments.getStatus().equalsIgnoreCase(Constants.SHIPMENT_OUT_FOR_DELIVERY) ) {
                    step = 2;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                } else if (shipments.getStatus() != null && shipments.getStatus().equalsIgnoreCase(Constants.SHIPMENT_DELIVERED)) {
                    step = 3;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_COMPLETED));
                } else if (shipments.getStatus() !=null && shipments.getStatus().equalsIgnoreCase(Constants.SHIPMENT_RETURNED)) {
                    orderSteps.add(new OrderStep("Returned", OrderStep.STEP_COMPLETED));
                } else {
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                }
            } else {
                orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_REMAIN));
                orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
            }
        }

        holder.img_view_step_two.setVisibility(View.GONE);
        holder.img_step_three.setVisibility(View.GONE);
        holder.txt_step_three.setVisibility(View.GONE);
        holder.txt_view_step_two.setVisibility(View.GONE);

        holder.img_view_step_three.setVisibility(View.GONE);
        holder.img_step_four.setVisibility(View.GONE);
        holder.txt_step_four.setVisibility(View.GONE);
        holder.txt_view_step_three.setVisibility(View.GONE);

        for (int i = 0; i < orderSteps.size(); i++) {
            if (i == 0) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.txt_step_one.setText(orderSteps.get(i).getName());
                    holder.txt_step_one.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
                    holder.img_step_one.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.txt_step_one.setText(orderSteps.get(i).getName());
                    holder.txt_step_one.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                    holder.img_step_one.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_active_round));
                }
            } else if (i == 1) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.img_view_step_one.setBackgroundColor(ContextCompat.getColor(mContext,R.color.purchase_medium_gray));
                    holder.txt_step_two.setText(orderSteps.get(i).getName());
                    holder.txt_step_two.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
                    holder.img_step_two.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_one.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_primary));
                    holder.txt_step_two.setText(orderSteps.get(i).getName());
                    holder.txt_step_two.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                    holder.img_step_two.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_active_round));
                }
            } else if (i == 2) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.img_view_step_two.setVisibility(View.VISIBLE);
                    holder.img_step_three.setVisibility(View.VISIBLE);
                    holder.txt_step_three.setVisibility(View.VISIBLE);
                    holder.txt_view_step_two.setVisibility(View.VISIBLE);

                    holder.img_view_step_two.setBackgroundColor(ContextCompat.getColor(mContext,R.color.purchase_medium_gray));
                    holder.txt_step_three.setText(orderSteps.get(i).getName());
                    holder.txt_step_three.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
                    holder.img_step_three.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_two.setVisibility(View.VISIBLE);
                    holder.img_step_three.setVisibility(View.VISIBLE);
                    holder.txt_step_three.setVisibility(View.VISIBLE);
                    holder.txt_view_step_two.setVisibility(View.VISIBLE);

                    holder.img_view_step_two.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_primary));
                    holder.txt_step_three.setText(orderSteps.get(i).getName());
                    holder.txt_step_three.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                    holder.img_step_three.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_active_round));
                } else {

                }
            } else if (i == 3) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.img_view_step_three.setVisibility(View.VISIBLE);
                    holder.img_step_four.setVisibility(View.VISIBLE);
                    holder.txt_step_four.setVisibility(View.VISIBLE);
                    holder.txt_view_step_three.setVisibility(View.VISIBLE);

                    holder.img_view_step_three.setBackgroundColor(ContextCompat.getColor(mContext,R.color.purchase_medium_gray));
                    holder.txt_step_four.setText(orderSteps.get(i).getName());
                    holder.txt_step_four.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_medium_gray));
                    holder.img_step_four.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_three.setVisibility(View.VISIBLE);
                    holder.img_step_four.setVisibility(View.VISIBLE);
                    holder.txt_step_four.setVisibility(View.VISIBLE);
                    holder.txt_view_step_three.setVisibility(View.VISIBLE);

                    holder.img_view_step_three.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_primary));
                    holder.txt_step_four.setText(orderSteps.get(i).getName());
                    holder.txt_step_four.setTextColor(ContextCompat.getColor(mContext, R.color.color_primary));
                    holder.img_step_four.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_order_track_active_round));
                }
            }

        }


        if (shipments!=null) {

            if (shipments.getStatus()!=null && shipments.getStatus().contains(Constants.SHIPMENT_DISPATCHED)) {
                holder.btn_track_order.setVisibility(View.VISIBLE);

                holder.btn_track_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Fragment_ShipTrack fragment_shipTrack = new Fragment_ShipTrack();
                        bundle.putString("track_url", shipments.getAws_url());
                        fragment_shipTrack.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE = "Tracking Details";
                        Application_Singleton.CONTAINERFRAG = fragment_shipTrack;
                        mContext.startActivity(new Intent(mContext, OpenContainer.class));
                    }
                });

            } else {
                holder.btn_track_order.setVisibility(View.GONE);
            }
        } else {
            holder.btn_track_order.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
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

        @BindView(R.id.linear_broker)
        LinearLayout linear_broker;

        @BindView(R.id.txt_broker_name)
        TextView txt_broker_name;

        @BindView(R.id.linear_burer_credit_rating)
        LinearLayout linear_burer_credit_rating;

        @BindView(R.id.main_container_2)
        LinearLayout main_container_2;

        @BindView(R.id.main_container)
        RelativeLayout main_container;


        @BindView(R.id.card_order)
        CardView card_order;

        @BindView(R.id.txt_new_order)
        TextView txt_new_order;

        @BindView(R.id.txt_share_order)
        TextView txt_share_order;


        ImageView img_step_one, img_step_two, img_step_three, img_step_four;

        View img_view_step_one, img_view_step_two, img_view_step_three, txt_view_step_one, txt_view_step_two, txt_view_step_three;

        TextView txt_step_one, txt_step_two, txt_step_three, txt_step_four;

        TextView btn_track_order, order_track_note;

        LinearLayout linear_main_order_track;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            initOrderTrack(view);
            holderHeight = view.getHeight();
        }

        public void initOrderTrack(View view) {
            linear_main_order_track = view.findViewById(R.id.linear_main_order_track);
            img_step_one = view.findViewById(R.id.img_step_one);
            img_step_two = view.findViewById(R.id.img_step_two);
            img_step_three = view.findViewById(R.id.img_step_three);
            img_step_four = view.findViewById(R.id.img_step_four);

            txt_step_one = view.findViewById(R.id.txt_step_one);
            txt_step_two = view.findViewById(R.id.txt_step_two);
            txt_step_three = view.findViewById(R.id.txt_step_three);
            txt_step_four = view.findViewById(R.id.txt_step_four);

            txt_step_one.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            txt_step_two.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            txt_step_three.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            txt_step_four.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);



            img_view_step_one = view.findViewById(R.id.img_view_step_one);
            img_view_step_two = view.findViewById(R.id.img_view_step_two);
            img_view_step_three = view.findViewById(R.id.img_view_step_three);

            txt_view_step_one = view.findViewById(R.id.txt_view_step_one);
            txt_view_step_two = view.findViewById(R.id.txt_view_step_two);
            txt_view_step_three = view.findViewById(R.id.txt_view_step_three);

            btn_track_order = view.findViewById(R.id.btn_track_order);

            order_track_note = view.findViewById(R.id.order_track_note);

        }
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
        if (status.equalsIgnoreCase("Draft")
                || status.equalsIgnoreCase("Partially Paid")
                || status.equalsIgnoreCase("Cod verification pending")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if(status.equalsIgnoreCase("Cancelled")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.purchase_dark_gray));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        }
    }
}
