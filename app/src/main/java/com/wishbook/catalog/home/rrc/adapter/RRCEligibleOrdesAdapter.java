package com.wishbook.catalog.home.rrc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RRCEligibleOrdesAdapter extends RecyclerView.Adapter<RRCEligibleOrdesAdapter.RRCOrderViewHolder> {


    ArrayList<Response_buyingorder> itemsArrayList;
    Context context;

    public RRCEligibleOrdesAdapter(Context context, ArrayList<Response_buyingorder> feedItemList) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RRCEligibleOrdesAdapter.RRCOrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.replacement_request_item, viewGroup, false);
        return new RRCEligibleOrdesAdapter.RRCOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RRCEligibleOrdesAdapter.RRCOrderViewHolder viewHolder, int i) {
        Response_buyingorder order = itemsArrayList.get(i);
        if (order.getImages() != null && order.getImages().size() > 0) {
            StaticFunctions.loadFresco(context, order.getImages().get(0), viewHolder.prod_img);
        }
        viewHolder.txt_request_date.setText(DateUtils.getFormattedDate(StringUtils.capitalize(order.getDate().toLowerCase().trim())));


        if (order.getOrder_number() != null && !order.getOrder_number().isEmpty())
            viewHolder.txt_order_no.setText("Order #" + order.getOrder_number().toUpperCase().trim());
        else
            viewHolder.txt_order_no.setText("Order #" + order.getId().trim());


        viewHolder.txt_order_value.setText("\u20B9" + order.getTotal_rate());
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class RRCOrderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_request)
        CardView card_request;

        @BindView(R.id.txt_order_no)
        TextView txt_order_no;

        @BindView(R.id.txt_request_date)
        TextView txt_request_date;

        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.txt_order_value)
        TextView txt_order_value;

        @BindView(R.id.txt_request_status)
        TextView txt_request_status;

        @BindView(R.id.txt_update_request)
        TextView txt_update_request;

        public RRCOrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
