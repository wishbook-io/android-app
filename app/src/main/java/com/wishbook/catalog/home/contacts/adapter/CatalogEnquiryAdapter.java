package com.wishbook.catalog.home.contacts.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.OpenContextBasedApplogicChat;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogEnquiry;
import com.wishbook.catalog.home.contacts.Fragment_CatalogEnquiry;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogEnquiryAdapter extends RecyclerView.Adapter<CatalogEnquiryAdapter.CustomViewHolder> {


    private Context context;
    private ArrayList<ResponseCatalogEnquiry> catalogEnquiries;
    private String buyerName;
    private boolean isEnquiry;
    private Fragment fragment;

    public CatalogEnquiryAdapter(Context context, ArrayList<ResponseCatalogEnquiry> catalogEnquiries, String buyerName, boolean isEnquiry, Fragment fragment) {
        this.context = context;
        this.catalogEnquiries = catalogEnquiries;
        this.buyerName = buyerName;
        this.isEnquiry = isEnquiry;
        this.fragment = fragment;
    }

    @Override
    public CatalogEnquiryAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_enquiry_item, parent, false);
        return new CatalogEnquiryAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        final ResponseCatalogEnquiry catalogEnquiry = catalogEnquiries.get(position);

        if (catalogEnquiry.getThumbnail() != null && !catalogEnquiry.getThumbnail().isEmpty()) {
            StaticFunctions.loadFresco(context, catalogEnquiry.getThumbnail(), holder.catalog_img);
        }
        holder.txt_catalog_name.setText(catalogEnquiry.getCatalog_title());
        try {
            holder.txt_time.setText(DateUtils.getTimeAgo(catalogEnquiry.getCreated(), context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(catalogEnquiry.getStatus()!=null && catalogEnquiry.getStatus().toLowerCase().equals("created")){
            holder.txt_status.setText("Status: "+"Open");
        } else {
            holder.txt_status.setText("Status: "+catalogEnquiry.getStatus());
        }

        String view_string = "<font color='#777777' size='14'>Enquired about </font><font color='#3a3a3a' size='14'>"+catalogEnquiry.getText()+"</font>";
        holder.txt_enquired_about_value.setText(Html.fromHtml(view_string), TextView.BufferType.NORMAL);

        if(!isEnquiry){
            /**
             * https://wishbook.atlassian.net/browse/WB-2844
             * Remove Sales Order
             */
            holder.txt_create_sales_order.setVisibility(View.GONE);
            holder.txt_create_sales_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createSalesOrder(buyerName, catalogEnquiry.getBuying_company(), catalogEnquiry.getCatalog());
                }
            });
        } else {
            holder.txt_create_sales_order.setVisibility(View.GONE);
        }

        holder.main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_CatalogEnquiry.ENQUIRY_CLICK_POSITION = holder.getAdapterPosition();
                if(!isEnquiry){
                    catalogEnquiry.setBuyerName(buyerName);
                    new OpenContextBasedApplogicChat(context, catalogEnquiry, OpenContextBasedApplogicChat.SUPPLIERTOBUYER, fragment);
                } else {
                    catalogEnquiry.setBuyerName(buyerName);
                    new OpenContextBasedApplogicChat(context, catalogEnquiry, OpenContextBasedApplogicChat.BUYERTOSUPPLIER, fragment);
                }
            }
        });

    }

    public void createSalesOrder(String buyingCompanyName, String buyingCompanyID, String catalogID) {
        CatalogMinified catalogMinified = new CatalogMinified();
        catalogMinified.setId(catalogID);
        Application_Singleton.selectedshareCatalog = catalogMinified;
        Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
        Bundle bundle = new Bundle();
        if (Application_Singleton.selectedshareCatalog != null) {
            bundle.putString("ordertype", "catalog");
            bundle.putString("ordervalue", catalogID);
        }
        bundle.putString("buyerselected", buyingCompanyName);
        bundle.putString("buyer_selected_company_id",buyingCompanyID);
        BuyersList buyersList = new BuyersList(buyingCompanyID,buyingCompanyName,null);
        bundle.putSerializable("buyer",buyersList);
        createOrderFrag.setArguments(bundle);

        Application_Singleton.CONTAINER_TITLE=context.getResources().getString(R.string.new_sales_order);
        Application_Singleton.CONTAINERFRAG=createOrderFrag;
        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
    }

    @Override
    public int getItemCount() {
        return catalogEnquiries.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.catalog_img)
        SimpleDraweeView catalog_img;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.txt_time)
        TextView txt_time;

        @BindView(R.id.txt_enquired_about_value)
        TextView txt_enquired_about_value;

        @BindView(R.id.txt_status)
        TextView txt_status;

        @BindView(R.id.txt_create_sales_order)
        TextView txt_create_sales_order;

        @BindView(R.id.main_container)
        LinearLayout main_container;

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
