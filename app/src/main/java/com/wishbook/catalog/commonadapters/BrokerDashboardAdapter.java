package com.wishbook.catalog.commonadapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_Broker_Report;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import java.util.ArrayList;

public class BrokerDashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<Response_Broker_Report> response_broker_reports;
    private static final int HEADER = 0;
    private static final int ITEMS = 1;

    public BrokerDashboardAdapter(Context context, ArrayList<Response_Broker_Report> response_broker_reports) {
        this.context = context;
        this.response_broker_reports = response_broker_reports;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.broker_dashboard_header, viewGroup, false);
            return new ViewHolderHeader(view);
        } else if (i == ITEMS) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brokerage_dashboard_item_2, viewGroup, false);
            return new CustomViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        if (viewHolder instanceof CustomViewHolder) {
            final Response_Broker_Report report = response_broker_reports.get(position - 1);
            //holder.txt_cmpny_name.setText(report.getCompany());
            //holder.txt_cmpny_location.setText(report.getState() + ", " + report.getCity());
            if (report.getBrokerage_in().size() == 3) {
                ((CustomViewHolder) viewHolder).txt_cmpny_name.setText(report.getCompany());
                ((CustomViewHolder) viewHolder).txt_1month_value.setText("\u20B9" + report.getBrokerage_in().get(0).getTotal_brokerage());
                ((CustomViewHolder) viewHolder).txt_3month_value.setText("\u20B9" + report.getBrokerage_in().get(1).getTotal_brokerage());
                ((CustomViewHolder) viewHolder).txt_1year_value.setText("\u20B9" + report.getBrokerage_in().get(2).getTotal_brokerage());
            }

            ((CustomViewHolder) viewHolder).txt_cmpny_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("sellerid", report.getSupplier_id());
                    bundle.putString("sellerCompanyid", report.getCompany_id());
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);

                    Application_Singleton.CONTAINERFRAG = supplier;
                    Intent intent = new Intent(context, OpenContainer.class);
                    context.startActivity(intent);
                }
            });
            if (position % 2 == 0) {
                // even row
                //((CustomViewHolder) viewHolder).linear_row.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                // odd row
                //((CustomViewHolder) viewHolder).linear_row.setBackgroundColor(context.getResources().getColor(R.color.contacts_home_grey));
            }


        } else {

        }


    }

    @Override
    public int getItemCount() {
        return response_broker_reports.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == HEADER)
            return HEADER;
        else
            return ITEMS;
    }


    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cmpny_name, txt_cmpny_location,
                txt_1month_value, txt_3month_value, txt_1year_value,
                txt_1_month_label, txt_3_month_label, txt_1_year_label;
        private ImageView img_chat, img_contact;
        private LinearLayout linear_supplier_container, linear_row;

        public CustomViewHolder(View view) {
            super(view);
            linear_supplier_container = (LinearLayout) view.findViewById(R.id.linear_supplier_container);
            txt_cmpny_name = (TextView) view.findViewById(R.id.txt_cmpnay_name);
            txt_cmpny_location = (TextView) view.findViewById(R.id.txt_cmpny_location);
            txt_1month_value = (TextView) view.findViewById(R.id.txt_1month_value);
            txt_3month_value = (TextView) view.findViewById(R.id.txt_3month_value);
            txt_1year_value = (TextView) view.findViewById(R.id.txt_1year_value);
            linear_row = (LinearLayout) view.findViewById(R.id.linear_row);
        }
    }
}
