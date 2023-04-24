package com.wishbook.catalog.home.contacts.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.responses.ResponseLeads;
import com.wishbook.catalog.home.contacts.Fragment_CatalogEnquiry;
import com.wishbook.catalog.home.contacts.Fragment_Leads;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.CustomViewHolder> {


    private Context context;
    private ArrayList<ResponseLeads> leads;
    private String filter;
    private Fragment fragment;

    public LeadsAdapter(Context context, ArrayList<ResponseLeads> leads,Fragment fragment) {
        this.context = context;
        this.leads = leads;
        this.fragment = fragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leads_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final ResponseLeads lead = leads.get(position);
        final TextDrawable drawable;
        if(lead.getCompany_name()!=null && !lead.getCompany_name().isEmpty()) {
            drawable = TextDrawable.builder()
                    .buildRound(lead.getCompany_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
        } else {
            drawable = TextDrawable.builder()
                    .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
        }
        
        holder.textInt.setImageDrawable(drawable);
        holder.txt_buyer_company.setText(lead.getCompany_name());
        holder.txt_location.setText(lead.getCity_name() + ", " + lead.getState_name());
        holder.txt_count.setText(lead.getTotal_enquiry() + " enquiries");
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Bundle bundle1 = new Bundle();
                Fragment_CatalogEnquiry fragment = new Fragment_CatalogEnquiry(getFilter());
                bundle1.putString("buying_company_id", lead.getCompany_id());
                bundle1.putBoolean("isEnquiry",false);
                fragment.setArguments(bundle1);
                Application_Singleton.CONTAINER_TITLE = lead.getCompany_name();
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
            }
        });
    }

    public String getFilter() {
        if(fragment instanceof Fragment_Leads){
           return ((Fragment_Leads) fragment).filter;
        }
        return "all";
    }

    @Override
    public int getItemCount() {
        return leads.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textInt)
        ImageView textInt;

        @BindView(R.id.txt_location)
        TextView txt_location;

        @BindView(R.id.txt_buyer_company)
        TextView txt_buyer_company;

        @BindView(R.id.txt_count)
        TextView txt_count;

        private ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
