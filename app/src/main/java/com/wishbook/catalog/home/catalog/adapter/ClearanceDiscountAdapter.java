package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.home.models.ProductObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ClearanceDiscountAdapter extends RecyclerView.Adapter<ClearanceDiscountAdapter.ClearanceProductViewHolder> {


    private Context mContext;
    private ArrayList<ProductObj> productObjArrayList;
    private double discount_per;
    private ProductMyDetail productMyDetail;
    DecimalFormat decimalFormat;
    String isAlreadyDiscountedPer;

    public ClearanceDiscountAdapter(Context mContext, ArrayList<ProductObj> productObjArrayList, ProductMyDetail productMyDetail) {
        this.mContext = mContext;
        this.productObjArrayList = productObjArrayList;
        this.productMyDetail = productMyDetail;
        decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public ClearanceDiscountAdapter.ClearanceProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.clearance_discount_product_item, viewGroup, false);
        return new ClearanceDiscountAdapter.ClearanceProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClearanceDiscountAdapter.ClearanceProductViewHolder holder, int position) {
        ProductObj productObj = productObjArrayList.get(position);

        if(productObj.getImage()!=null && productObj.getImage().getThumbnail_small()!=null)
            StaticFunctions.loadFresco(mContext,productObj.getImage().getThumbnail_small(),holder.product_img);

        if(productObj.getSku()!=null)
            holder.txt_design_name_value.setText(productObj.getSku());

        holder.txt_product_code_value.setText(productObj.getId());

        if(discount_per > 0) {
            double temp_full_billing_price = getProductFullBillingPrice(productObj);
            double temp_single_billing_price = getProductSingleBillingPrice(productObj);
            double final_billing_price= (temp_full_billing_price -  (temp_full_billing_price * (discount_per /100)));
            double final_single_billing_price= (temp_single_billing_price -  (temp_single_billing_price * (discount_per /100)));
            holder.txt_full_billing_price.setText("\u20B9 "+decimalFormat.format(final_billing_price));
            holder.txt_single_billing_price.setText("\u20B9 "+decimalFormat.format(final_single_billing_price));

        } else {
            double temp_full_billing_price = getProductFullBillingPrice(productObj);
            double temp_single_billing_price = getProductSingleBillingPrice(productObj);
            holder.txt_full_billing_price.setText("\u20B9 "+decimalFormat.format(temp_full_billing_price));
            holder.txt_single_billing_price.setText("\u20B9 "+decimalFormat.format(temp_single_billing_price));
        }

        if(productMyDetail.isI_am_selling_sell_full_catalog()) {
            holder.linear_single_billing_price.setVisibility(View.GONE);
            holder.txt_single_billing_price.setVisibility(View.GONE);
        } else {
            holder.linear_single_billing_price.setVisibility(View.VISIBLE);
            holder.txt_single_billing_price.setVisibility(View.VISIBLE);
        }

    }


    public double getProductFullBillingPrice(ProductObj productObj) {
        if(productMyDetail!=null) {
            for (int i = 0;i<productMyDetail.getProducts().size();i++) {
                if(productObj.getId().equalsIgnoreCase(productMyDetail.getProducts().get(i).getId())) {
                    if(isAlreadyDiscountedPer!=null) {
                        double temp_already_discount = Double.parseDouble(isAlreadyDiscountedPer);
                        return  Math.round(((100/(100-temp_already_discount))*productMyDetail.getProducts().get(i).getFull_price()));
                    } else {
                        Log.e("TAG", "Is AlreadyDiscount  Null: " +productMyDetail.getProducts().get(i).getFull_price());
                        return  productMyDetail.getProducts().get(i).getFull_price();
                    }

                }
            }
        }
        return 0;
    }


    public double getProductSingleBillingPrice(ProductObj productObj) {
        if(productMyDetail!=null) {
            for (int i = 0;i<productMyDetail.getProducts().size();i++) {
                if(productObj.getId().equalsIgnoreCase(productMyDetail.getProducts().get(i).getId())) {
                    if(isAlreadyDiscountedPer!=null) {
                        double temp_already_discount = Double.parseDouble(isAlreadyDiscountedPer);
                        return  Math.round(((100/(100-temp_already_discount))*productMyDetail.getProducts().get(i).getSingle_piece_price()));
                    } else {
                        return  productMyDetail.getProducts().get(i).getSingle_piece_price();
                    }
                }
            }
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return productObjArrayList.size();
    }

    public double getDiscount_per() {
        return discount_per;
    }

    public void setDiscount_per(double discount_per) {
        this.discount_per = discount_per;
    }

    public String getIsAlreadyDiscountedPer() {
        return isAlreadyDiscountedPer;
    }

    public void setIsAlreadyDiscountedPer(String isAlreadyDiscountedPer) {
        this.isAlreadyDiscountedPer = isAlreadyDiscountedPer;
    }

    class ClearanceProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        @BindView(R.id.txt_design_name_value)
        TextView txt_design_name_value;

        @BindView(R.id.txt_product_code_value)
        TextView txt_product_code_value;

        @BindView(R.id.txt_single_billing_price)
        TextView txt_single_billing_price;

        @BindView(R.id.txt_full_billing_price)
        TextView txt_full_billing_price;

        @BindView(R.id.linear_single_billing_price)
        LinearLayout linear_single_billing_price;


        ClearanceProductViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}