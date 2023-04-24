package com.wishbook.catalog.home.orderNew.adapters;


import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartMultipleSellerAdapter extends RecyclerView.Adapter<CartMultipleSellerAdapter.CartMultipleSellerHolder> {

    boolean flag;
    Context context;
    private RecyclerView seller_list;
    String current_seller;
    private ArrayList<CartCatalogModel.Sellers> sellers;

    public CartMultipleSellerAdapter(ArrayList<CartCatalogModel.Sellers> sellers, Context context, RecyclerView seller_list,String current_seller) {
        this.sellers = sellers;
        this.context = context;
        this.seller_list = seller_list;
        this.current_seller=current_seller;
        flag = true;
        for(int i=0;i<sellers.size();i++) {
            if (sellers.get(i).getCompany_id().equals(current_seller)) {
                sellers.get(i).setSelected(true);
                postAndNotifyAdapter(new Handler(), seller_list, CartMultipleSellerAdapter.this);
            }
        }
    }


    @NonNull
    @Override
    public CartMultipleSellerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_seller_item, parent, false);


        return new CartMultipleSellerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartMultipleSellerHolder holder, int position) {
        try {
            final int pos = position;
            holder.seller_selector.setOnCheckedChangeListener(null);
            if (flag) {
                if (sellers.get(pos).getCompany_id().equals(current_seller)) {
                    sellers.get(pos).setSelected(true);
                    postAndNotifyAdapter(new Handler(), seller_list, CartMultipleSellerAdapter.this);
                }
            }
            flag = false;
            ArrayList<ResponseSellerPolicy> policies = sellers.get(position).getSeller_policy();
            String delivery_policy = null, return_policy = null;
            for (int i = 0; i < policies.size(); i++) {
                if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                     delivery_policy= "<font color='#777777' size='14'>Delivery time: </font><font color='#3a3a3a' size='14'>" + policies.get(i).getPolicy() + "</font>";

                } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                   return_policy = "<font color='#777777' size='14'>Return Policy: </font><font color='#3a3a3a' size='14'>" + policies.get(i).getPolicy() + "</font>";
                }
            }
            if(delivery_policy!=null && return_policy !=null){
                holder.seller_delivery_time.setVisibility(View.VISIBLE);
                holder.seller_delivery_time.setText(Html.fromHtml(delivery_policy+"<br /><br />"+return_policy), TextView.BufferType.SPANNABLE);
            } else {
                if(delivery_policy!=null){
                    holder.seller_delivery_time.setVisibility(View.VISIBLE);
                    holder.seller_delivery_time.setText(Html.fromHtml(delivery_policy), TextView.BufferType.SPANNABLE);
                } else if(return_policy!=null){
                    holder.seller_delivery_time.setVisibility(View.VISIBLE);
                    holder.seller_delivery_time.setText(Html.fromHtml(return_policy), TextView.BufferType.SPANNABLE);
                } else {
                    holder.seller_delivery_time.setVisibility(View.GONE);
                }
            }

            if (sellers.get(position).getSeller_policy() != null && sellers.get(position).getSeller_policy().size() > 0 && sellers.get(position).getSeller_policy().get(0) != null) {

            } else {
                holder.seller_delivery_time.setVisibility(View.GONE);
            }

            String seller_name = "<font color='#3a3a3a' size='16'>Sold by: " + sellers.get(position).getCompany_name() + "</font>";
            holder.seller_location.setText(sellers.get(position).getState_name() + ", " + sellers.get(position).getCity_name());
            holder.seller_name.setText(Html.fromHtml(seller_name), TextView.BufferType.SPANNABLE);
            if (sellers.get(position).getTrusted_seller()) {
                holder.seller_trusted.setVisibility(View.VISIBLE);
            }
            holder.select_supplier_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < sellers.size(); i++) {
                        sellers.get(i).setSelected(false);
                        if (pos == i) {
                            sellers.get(i).setSelected(true);
                        }
                    }
                    postAndNotifyAdapter(new Handler(), seller_list, CartMultipleSellerAdapter.this);
                }
            });
            holder.seller_selector.setChecked(sellers.get(position).isSelected());
            holder.seller_selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int i = 0; i < sellers.size(); i++) {
                        sellers.get(i).setSelected(false);
                        if (pos == i) {
                            sellers.get(i).setSelected(true);
                        }
                    }
                    postAndNotifyAdapter(new Handler(), seller_list, CartMultipleSellerAdapter.this);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyDataSetChanged();
                    handler.removeCallbacks(null);
                } else {
                    postAndNotifyAdapter(handler, recyclerView, adapter);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return sellers.size();
    }


    public class CartMultipleSellerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.seller_name)
        TextView seller_name;

        @BindView(R.id.seller_location)
        TextView seller_location;

        @BindView(R.id.seller_delivery_time)
        TextView seller_delivery_time;

        @BindView(R.id.seller_price)
        TextView seller_price;

        @BindView(R.id.seller_trusted)
        ImageView seller_trusted;

        @BindView(R.id.seller_selector)
        RadioButton seller_selector;

        @BindView(R.id.select_supplier_layout)
        RelativeLayout select_supplier_layout;

        private CartMultipleSellerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}

