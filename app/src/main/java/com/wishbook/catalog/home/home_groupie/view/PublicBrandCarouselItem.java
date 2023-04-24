package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.Response_Brands;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PublicBrandCarouselItem extends Item<PublicBrandCarouselItem.CustomViewHolder> {


    Fragment fragment;
    Context mContext;
    private Response_Brands data;

    public PublicBrandCarouselItem(Response_Brands data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }


    @Override
    public void bind(@NonNull CustomViewHolder holder, int position) {
        if (data != null) {
            if (position > 0) {
                holder.dummySpace.setVisibility(View.GONE);
            } else {
                holder.dummySpace.setVisibility(View.VISIBLE);
            }

            holder.brand_name.setText(data.getName());
            String image = data.getImage().getThumbnail_small();
            if (image != null & !image.equals("")) {
                StaticFunctions.loadFresco(mContext, image, holder.brand_img);
            }
            holder.brand_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", "tab");
                    hashMap.put("page", "catalogs/brands");
                    hashMap.put("focus_position", String.valueOf(holder.getAdapterPosition()));
                    new DeepLinkFunction(hashMap, mContext);
                }
            });
        }

    }


    @Override
    public int getLayout() {
        return R.layout.home_brand_list_item;
    }


    @NonNull
    @Override
    public PublicBrandCarouselItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new PublicBrandCarouselItem.CustomViewHolder(itemView);
    }


    public class CustomViewHolder extends ViewHolder {


        @BindView(R.id.brand_name)
        TextView brand_name;

        @BindView(R.id.brand_img)
        SimpleDraweeView brand_img;

        @BindView(R.id.dummyFirstPosition)
        RelativeLayout dummySpace;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}