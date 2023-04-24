package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.HomeCategoryItemsAdapter;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeCategoryItem extends Item<HomeCategoryItem.HomeViewHolder> {


    Fragment fragment;
    Context mContext;
    private ResponseHomeCategories data;
    private int span_grid = 2;


    public HomeCategoryItem(ResponseHomeCategories data, Context context, Fragment fragment, int span_grid) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
        this.span_grid = span_grid;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.home_category_grid;
    }


    @NonNull
    @Override
    public HomeCategoryItem.HomeViewHolder createViewHolder(@NonNull View itemView) {
        return new HomeCategoryItem.HomeViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull HomeViewHolder holder, int position) {
        final ResponseHomeCategories category = (ResponseHomeCategories) data;
        if (category.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(mContext, category.getImage().getThumbnail_small(), holder.category_img);
        }
        holder.txt_title.setText(category.getCategory_name().toString());
        holder.setClickListener(new HomeCategoryItemsAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("category", category.getId());
                hashMap.put("product_type", Constants.PRODUCT_TYPE_CAT);
                if (StaticFunctions.isOnlyReseller(mContext)) {
                    hashMap.put("collection", "false");
                }
                new DeepLinkFunction(hashMap, mContext);
            }
        });
        holder.txt_collection_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("category", category.getId());
                new DeepLinkFunction(hashMap, mContext);
            }
        });

        holder.txt_collection_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("product_type", Constants.PRODUCT_TYPE_CAT);
                hashMap.put("category", category.getId());
                hashMap.put("collection", "false");
                new DeepLinkFunction(hashMap, mContext);
            }
        });
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount / span_grid;
    }


    public class HomeViewHolder extends ViewHolder implements View.OnClickListener {


        @BindView(R.id.category_img)
        SimpleDraweeView category_img;

        @BindView(R.id.txt_title)
        TextView txt_title;

        @BindView(R.id.txt_collection_catalog)
        TextView txt_collection_catalog;

        @BindView(R.id.txt_collection_product)
        TextView txt_collection_product;

        private HomeCategoryItemsAdapter.ItemClickListener clickListener;

        public HomeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(HomeCategoryItemsAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}
