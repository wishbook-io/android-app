package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductItemAdapter extends Item<ProductItemAdapter.CustomViewHolder> {


    Fragment fragment;
    Context mContext;
    private Response_catalogMini data;
    HashMap<String, String> params;
    String sectionTitle;
    int itemPosition;
    private int height;
    private int width;
    int numberofGridSize = 3;

    public ProductItemAdapter(Response_catalogMini data, Context context, Fragment fragment,
                              String title, HashMap<String, String> params, int position) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
        this.params = params;
        this.itemPosition = position;
        this.sectionTitle = title;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }


    @Override
    public void bind(@NonNull CustomViewHolder viewHolder, int position) {
        if (numberofGridSize == 3) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.2f));
            viewHolder.itemcontainer.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 2.1f), Math.round(height / 3.2f));
            viewHolder.itemcontainer.setLayoutParams(lp);
        }
        StaticFunctions.loadFresco(viewHolder.item_img.getContext(), data.getImage().getThumbnail_small(), viewHolder.item_img);


        viewHolder.item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (params != null) {
                    hashMap.putAll(params);
                }
                if (hashMap != null) {
                    Application_Singleton.deep_link_filter = hashMap;
                } else {
                    Application_Singleton.deep_link_filter = null;
                }
                if (sectionTitle.equalsIgnoreCase(mContext.getResources().getString(R.string.from_brands_you_follow))) {
                    Application_Singleton.CONTAINER_TITLE = sectionTitle;
                    if (sectionTitle.equalsIgnoreCase(mContext.getResources().getString(R.string.from_trusted_seller))) {
                        Application_Singleton.isPublicTrusted = true;
                    }
                    Application_Singleton.CONTAINERFRAG = fragment;
                    Bundle bundle = new Bundle();
                    bundle.putInt(Application_Singleton.adapterFocusPosition, viewHolder.getAdapterPosition());
                    fragment.setArguments(bundle);
                    Intent intent = new Intent(mContext, OpenContainer.class);
                    mContext.startActivity(intent);
                } else {
                    hashMap.put("focus_position", String.valueOf(itemPosition));
                    new DeepLinkFunction(hashMap, mContext);
                }
            }
        });

    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.all_items_item_new;
    }


    @NonNull
    @Override
    public ProductItemAdapter.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new ProductItemAdapter.CustomViewHolder(itemView);
    }


    public class CustomViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.itemcontainer)
        RelativeLayout itemcontainer;

        @BindView(R.id.item_img)
        SimpleDraweeView item_img;

        private ProductItemAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ProductItemAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}
