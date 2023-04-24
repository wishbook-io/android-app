package com.wishbook.catalog.reseller.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerSharedProductAdapter extends RecyclerView.Adapter<ResellerSharedProductAdapter.CustomViewHolder> {

    private ArrayList<Response_catalogMini> response_catalogMinis;
    private Context mContext;
    private UserInfo userinfo;


    public ResellerSharedProductAdapter(Context context, ArrayList<Response_catalogMini> feedItemList) {
        this.response_catalogMinis = feedItemList;
        this.mContext = context;
        userinfo = UserInfo.getInstance(mContext);
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reseller_catalog_item_grid, viewGroup, false);
        return new ResellerSharedProductAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ResellerSharedProductAdapter.CustomViewHolder customViewHolder, final int i) {
        final Response_catalogMini response_catalogMini = response_catalogMinis.get(i);
        String catimage = response_catalogMini.getImage().getThumbnail_small();
        if (catimage != null && !catimage.equals("")) {
            StaticFunctions.loadFresco(mContext, catimage, customViewHolder.cat_img);
        }

        if (response_catalogMini.getProduct_type() != null
                && response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            customViewHolder.txt_catalog_name.setText(response_catalogMini.getCatalog_title() + "\n" + "(" + response_catalogMini.getId() + ")");
        } else {
            customViewHolder.txt_catalog_name.setText(response_catalogMini.getTitle());
        }


        if (response_catalogMini.getShared_details() != null) {
            if (response_catalogMini.getShared_details().getActual_price() != null) {
                customViewHolder.linear_price_container.setVisibility(View.VISIBLE);
                customViewHolder.txt_price_value.setText("\u20B9" + response_catalogMini.getShared_details().getActual_price() + "/Pc");
            } else {
                customViewHolder.linear_price_container.setVisibility(View.GONE);
            }

            if (response_catalogMini.getShared_details().getResell_price() != null) {
                customViewHolder.linear_resale_container.setVisibility(View.VISIBLE);
                customViewHolder.txt_resale_price_value.setText("\u20B9" + response_catalogMini.getShared_details().getResell_price() + "/Pc");
            } else {
                customViewHolder.linear_resale_container.setVisibility(View.GONE);
            }

        } else {
            customViewHolder.linear_price_container.setVisibility(View.GONE);
            customViewHolder.linear_resale_container.setVisibility(View.GONE);
        }

        customViewHolder.relative_main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_catalogMini.getProduct_type() != null
                        && response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id", response_catalogMini.getId());
                    bundle.putString("from", "Public List");
                    new NavigationUtils().navigateSingleProductDetailPage(mContext, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "Reseller Shared by me");
                    bundle.putString("product_id", response_catalogMini.getId());
                    new NavigationUtils().navigateDetailPage(mContext, bundle);
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_main_container)
        RelativeLayout relative_main_container;

        @BindView(R.id.cat_img)
        SimpleDraweeView cat_img;

        @BindView(R.id.relative_bottom)
        RelativeLayout relative_bottom;

        @BindView(R.id.linear_price_container)
        LinearLayout linear_price_container;

        @BindView(R.id.txt_price_value)
        TextView txt_price_value;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.linear_resale_container)
        LinearLayout linear_resale_container;


        @BindView(R.id.txt_resale_price_value)
        TextView txt_resale_price_value;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
