package com.wishbook.catalog.commonadapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeCategoryItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<?> items;
    private int type;

    public static int DEEPLINKCATEGORY = 1;
    public static int HOMEDEEPLINKCATEGORY = 2;


    public HomeCategoryItemsAdapter(Context context, ArrayList<?> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DEEPLINKCATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_item, parent, false);
            return new CategoryViewHolder(view);
        } else if (viewType == HOMEDEEPLINKCATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_grid, parent, false);
            return new HomeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            if (type == DEEPLINKCATEGORY) {
                if (items.get(0) instanceof ResponseHomeCategories) {
                    bindProductTabCategory((CategoryViewHolder) holder, position);
                }
            }
        } else if (holder instanceof HomeViewHolder) {
            if (type == HOMEDEEPLINKCATEGORY) {
                if (items.get(0) instanceof ResponseHomeCategories) {
                    bindHomeTabCategory((HomeViewHolder) holder, position);
                }
            }
        }
    }

    public void bindProductTabCategory(CategoryViewHolder holder, int position) {
        final ResponseHomeCategories category = (ResponseHomeCategories) items.get(position);
        if (position == 0) {
            ((CategoryViewHolder) holder).spacer.setVisibility(View.VISIBLE);
        } else {
            ((CategoryViewHolder) holder).spacer.setVisibility(View.GONE);
        }
        if (category.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, category.getImage().getThumbnail_small(), ((CategoryViewHolder) holder).category_img);
        }
        ((CategoryViewHolder) holder).txt_title.setText(category.getCategory_name().toString());
        ((CategoryViewHolder) holder).setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("category", category.getId());
                if (StaticFunctions.isOnlyReseller(context)) {
                    hashMap.put("sell_full_catalog", "false");
                }
                new DeepLinkFunction(hashMap, context);
            }
        });
        ((CategoryViewHolder) holder).txt_collection_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("category", category.getId());
                if (StaticFunctions.isOnlyReseller(context)) {
                    hashMap.put("sell_full_catalog", "false");
                }
                new DeepLinkFunction(hashMap, context);
            }
        });

        ((CategoryViewHolder) holder).txt_collection_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("product_type", Constants.PRODUCT_TYPE_CAT);
                hashMap.put("category", category.getId());
                hashMap.put("collection", "false");
                new DeepLinkFunction(hashMap, context);
            }
        });


    }

    public void bindHomeTabCategory(HomeViewHolder holder, int position) {
        final ResponseHomeCategories category = (ResponseHomeCategories) items.get(position);
        if (category.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, category.getImage().getThumbnail_small(), holder.category_img);
        }
        holder.txt_title.setText(category.getCategory_name().toString());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Application_Singleton.trackEvent("Home Category Banner", "Click", category.getCategory_name());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "catalog");
                hashMap.put("ctype", "public");
                hashMap.put("category", category.getId());
                hashMap.put("product_type", Constants.PRODUCT_TYPE_CAT);
                if (StaticFunctions.isOnlyReseller(context)) {
                    hashMap.put("collection", "false");
                }
                new DeepLinkFunction(hashMap, context);
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
                new DeepLinkFunction(hashMap, context);
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
                new DeepLinkFunction(hashMap, context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        CardView cardView;

        @BindView(R.id.category_img)
        SimpleDraweeView category_img;

        @BindView(R.id.txt_title)
        TextView txt_title;

        @BindView(R.id.spacer)
        View spacer;

        @BindView(R.id.txt_collection_catalog)
        TextView txt_collection_catalog;

        @BindView(R.id.txt_collection_product)
        TextView txt_collection_product;

        private ItemClickListener clickListener;

        public CategoryViewHolder(View view) {
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

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.category_img)
        SimpleDraweeView category_img;

        @BindView(R.id.txt_title)
        TextView txt_title;

        @BindView(R.id.txt_collection_catalog)
        TextView txt_collection_catalog;

        @BindView(R.id.txt_collection_product)
        TextView txt_collection_product;

        private ItemClickListener clickListener;

        public HomeViewHolder(View view) {
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
