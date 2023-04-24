package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonadapters.DeepLinkItemsAdapter;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PromotionalProductTabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<?> items;
    private int type;

    public static int DEEPLINKPRODUCT = 0;

    public PromotionalProductTabAdapter(Context context, ArrayList<?> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DEEPLINKPRODUCT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_promotional_tag_item, parent, false);
            return new ProductPromotionViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductPromotionViewHolder) {
            if (type == DEEPLINKPRODUCT) {
                if (items.get(0) instanceof Response_Promotion) {
                    final Response_Promotion promotion = (Response_Promotion) items.get(position);
                    if (promotion.getImage().getThumbnail_small() != null) {
                        ((ProductPromotionViewHolder) holder).txt_collection_catalog.setVisibility(View.VISIBLE);
                        ((ProductPromotionViewHolder) holder).txt_collection_product.setVisibility(View.VISIBLE);
                        StaticFunctions.loadFresco(context, promotion.getImage().getThumbnail_small(), ((ProductPromotionViewHolder) holder).promotional_img);
                    } else {
                        ((ProductPromotionViewHolder) holder).txt_collection_catalog.setVisibility(View.GONE);
                        ((ProductPromotionViewHolder) holder).txt_collection_product.setVisibility(View.GONE);
                    }
                    ((ProductPromotionViewHolder) holder).setClickListener(new DeepLinkItemsAdapter.ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            if (promotion.getImage().getThumbnail_small() != null) {
                                Uri intentUri = Uri.parse(promotion.getUrl());
                                Application_Singleton.trackEvent("Home Small Banner", "Click", promotion.getUrl());
                                HashMap<String, String> param = SplashScreen.getQueryString(intentUri);
                                if (StaticFunctions.isOnlyReseller(context)) {
                                    param.put("sell_full_catalog", "false");
                                }
                                new DeepLinkFunction(param, context);

                                HashMap<String, String> prop = new HashMap<>();
                                prop.put("source", "ProductTab DeepLink Banner");
                                prop.put("banner_type", "DeepLink");
                                prop.put("banner_url", promotion.getUrl());
                                sendBannerClickAnalytics(prop);
                            }
                        }
                    });

                    ((ProductPromotionViewHolder) holder).txt_collection_catalog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri intentUri = Uri.parse(promotion.getUrl());
                            HashMap<String, String> param = SplashScreen.getQueryString(intentUri);
                            new DeepLinkFunction(param, context);
                        }
                    });

                    ((ProductPromotionViewHolder) holder).txt_collection_product.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri intentUri = Uri.parse(promotion.getUrl());
                            HashMap<String, String> param = SplashScreen.getQueryString(intentUri);
                            param.put("collection", "false");
                            new DeepLinkFunction(param, context);
                        }
                    });

                }
            }
        }
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

    public void sendBannerClickAnalytics(HashMap<String, String> prop) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Banner_Clicked");
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(context, wishbookEvent);
    }

    public class ProductPromotionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.promotional_img)
        SimpleDraweeView promotional_img;

        @BindView(R.id.txt_collection_catalog)
        TextView txt_collection_catalog;

        @BindView(R.id.txt_collection_product)
        TextView txt_collection_product;


        private DeepLinkItemsAdapter.ItemClickListener clickListener;

        public ProductPromotionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(DeepLinkItemsAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}

