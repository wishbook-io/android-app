package com.wishbook.catalog.home.more.brandDiscount.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscountExpand;
import com.wishbook.catalog.home.more.brandDiscount.ActivityBrandwiseDiscountList;
import com.wishbook.catalog.home.more.brandDiscount.FragmentAddBrandDiscountVersion2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBrandWiseDiscountListAdapter extends RecyclerView.Adapter<MyBrandWiseDiscountListAdapter.CustomViewHolder> {


    private Context context;
    private ArrayList<ResponseBrandDiscountExpand> requestBrandDiscounts;

    public MyBrandWiseDiscountListAdapter(Context context, ArrayList<ResponseBrandDiscountExpand> requestBrandDiscounts) {
        this.context = context;
        this.requestBrandDiscounts = requestBrandDiscounts;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_discount_item, parent, false);
        return new MyBrandWiseDiscountListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ResponseBrandDiscountExpand discount = requestBrandDiscounts.get(position);
        holder.txt_discount_name.setText(discount.getName());
        if (discount.getAll_brands()) {
            holder.txt_brand_list_value.setText("All brands");
        } else {
            if (discount.getBrands() != null) {
                StringBuilder brandsString = new StringBuilder();
                for (int i = 0; i < discount.getBrands().size(); i++) {
                    brandsString.append(discount.getBrands().get(i).getName() + StaticFunctions.COMMASEPRATEDSPACE);
                }
                try {
                    if(brandsString.length() > 1) {
                        holder.txt_brand_list_value.setText(brandsString.substring(0, brandsString.length() - StaticFunctions.COMMASEPRATEDSPACE.length()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.txt_brand_list_value.setText(brandsString);
                }
            }
        }
        if (discount.getDiscount_type().equals(Constants.DISCOUNT_TYPE_PRIVATE)) {


        } else {
            holder.linear_brands.setVisibility(View.VISIBLE);
            holder.txt_cash_discount_value.setVisibility(View.VISIBLE);
            holder.txt_credit_discount_value.setVisibility(View.GONE);
            holder.txt_cash_discount_value.setText("Discount " + discount.getCash_discount() + "%");


            /**
             * Changes done by Bhavik Gandhi April-26 (Jay Patel)
             * Show Single Pc. Discount any case
             */
            /*if(discount.getSingle_pcs_discount() > 0){
                holder.linear_single_pc_discount_container.setVisibility(View.VISIBLE);
                holder.txt_single_pc_discount_value.setText("Discount " + discount.getSingle_pcs_discount() + "%");
            } else {
                holder.linear_single_pc_discount_container.setVisibility(View.GONE);
            }*/

            holder.linear_single_pc_discount_container.setVisibility(View.VISIBLE);
            holder.txt_single_pc_discount_value.setText("Discount " + discount.getSingle_pcs_discount() + "%");
        }


        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                FragmentAddBrandDiscountVersion2 fragmentAddBrandDiscount = new FragmentAddBrandDiscountVersion2();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", true);
                bundle.putString("discount_id", discount.getId());
                fragmentAddBrandDiscount.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = discount.getName();
                Application_Singleton.CONTAINERFRAG = fragmentAddBrandDiscount;
                Intent intent = new Intent(context, OpenContainer.class);
                ((AppCompatActivity) context).startActivityForResult(intent, ActivityBrandwiseDiscountList.ADD_EDIT_DISCOUNT_REQUEST);
            }
        });

    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    @Override
    public int getItemCount() {
        return requestBrandDiscounts.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        @BindView(R.id.txt_discount_name)
        TextView txt_discount_name;

        @BindView(R.id.txt_brand_list_value)
        TextView txt_brand_list_value;

        @BindView(R.id.txt_cash_discount_value)
        TextView txt_cash_discount_value;

        @BindView(R.id.txt_credit_discount_value)
        TextView txt_credit_discount_value;

        @BindView(R.id.linear_brands)
        LinearLayout linear_brands;

        @BindView(R.id.linear_single_pc_discount_container)
        LinearLayout linear_single_pc_discount_container;

        @BindView(R.id.txt_single_pc_discount_value)
        TextView txt_single_pc_discount_value;


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
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onClick(v, getPosition(), true);
            return true;
        }


    }
}
