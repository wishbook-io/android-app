package com.wishbook.catalog.commonadapters;

/**
 * Created by Vigneshkarnika on 22/03/16.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.BrandFollowed;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.HashMap;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    private final AppCompatActivity context;
    private ArrayList<Response_Brands> mDataset;
    Fragment fragment;

    public ArrayList<Response_Brands> getCurrentData() {
        return mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected SimpleDraweeView imageView;
        protected TextView textView;
        protected TextView count;
        protected LinearLayout brand_item_main_container;
        protected AppCompatButton btn_brand_follow;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (SimpleDraweeView) view.findViewById(R.id.brand_img);
            this.textView = (TextView) view.findViewById(R.id.brand_name);
            this.count = (TextView) view.findViewById(R.id.catalogs_count);
            this.btn_brand_follow = (AppCompatButton) view.findViewById(R.id.btn_brand_follow);
            this.brand_item_main_container = (LinearLayout) view.findViewById(R.id.brand_item_main_container);
        }
    }

    public BrandsAdapter(AppCompatActivity context, ArrayList<Response_Brands> myDataset, Fragment fragment_brands) {
        mDataset = myDataset;
        fragment = fragment_brands;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mybrand_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getName());
        String image = mDataset.get(position).getImage().getThumbnail_small();
        if (UserInfo.getInstance(context).getCompanyType().equals("seller")) {
            holder.btn_brand_follow.setVisibility(View.GONE);
        } else {
            holder.btn_brand_follow.setVisibility(View.VISIBLE);
        }
        if(mDataset.get(position).getIs_followed()!=null){
            updateFollowUI(true,holder);
        } else {
            updateFollowUI(false,holder);
        }

        holder.btn_brand_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(UserInfo.getInstance(context).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(context,null);
                    return;
                }

                if(mDataset.get(holder.getAdapterPosition()).getIs_followed() == null) {
                    callFollowBrand(holder,mDataset.get(holder.getAdapterPosition()));
                } else {
                    callUnfollowBrand(holder,mDataset.get(holder.getAdapterPosition()));
                }
            }
        });
        if (mDataset.get(position).getTotal_catalogs() > 1) {
            holder.count.setText(mDataset.get(position).getTotal_catalogs() + " Catalogs");
        } else {
            holder.count.setText(mDataset.get(position).getTotal_catalogs() + " Catalog");
        }
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(context, image, holder.imageView, R.drawable.uploadempty);
            StaticFunctions.loadFresco(context, image, holder.imageView);
            //Picasso.with(context).load(image).into(holder.imageView);
        }
        holder.brand_item_main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{


                    HashMap<String, String> params = new HashMap<>();
                    params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                    params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                    params.put("brand", mDataset.get(holder.getAdapterPosition()).getId());
                    params.put("page_title",mDataset.get(holder.getAdapterPosition()).getName());
                    Application_Singleton.deep_link_filter = params;
                    Fragment_BrowseProduct fragment_browseProduct = new Fragment_BrowseProduct();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isfrom_brand_filter", true);
                    fragment_browseProduct.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Products";
                    Application_Singleton.CONTAINERFRAG = fragment_browseProduct;
                    Intent intent2 = new Intent(context, OpenContainer.class);
                    context.startActivity(intent2);
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void updateFollowUI(boolean isFollow,ViewHolder viewHolder) {
        if (isFollow) {
            viewHolder.btn_brand_follow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            viewHolder.btn_brand_follow.setText(context.getResources().getString(R.string.btn_followed));
            viewHolder.btn_brand_follow.setTextColor(Color.WHITE);
        } else {
            viewHolder.btn_brand_follow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_edge_less));
            viewHolder.btn_brand_follow.setText(context.getResources().getString(R.string.btn_follow));
            viewHolder.btn_brand_follow.setTextColor(context.getResources().getColor(R.color.color_primary));
        }
    }

    private void callFollowBrand(final ViewHolder viewHolder, final Response_Brands response_brands) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", response_brands.getId());
        HttpManager.getInstance(context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "brands-follow", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("follow Cache response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("follow response", response);
                BrandFollowed brandFollowed = Application_Singleton.gson.fromJson(response, BrandFollowed.class);
                updateFollowUI(true,viewHolder);
                response_brands.setIs_followed(brandFollowed.getId());
                notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
               StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void callUnfollowBrand(final ViewHolder viewHolder,final Response_Brands response_brands) {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", response_brands.getId());
        HttpManager.getInstance(context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(context, "brands-follow", response_brands.getIs_followed()), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //     Log.v("Unfollow response", response);
                updateFollowUI(false,viewHolder);
                response_brands.setIs_followed(null);
                notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
               StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}