package com.wishbook.catalog.commonadapters;


import android.content.Context;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeepLinkItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<?> items;
    private int type;

    public static int DEEPLINKPRICE = 0;

    public DeepLinkItemsAdapter(Context context, ArrayList<?> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DEEPLINKPRICE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_deeplink_price_item, parent, false);
            return new DeepLinkPriceViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeepLinkPriceViewHolder) {
            if(type == DEEPLINKPRICE) {
                if(items.get(0) instanceof Response_Promotion) {
                    final Response_Promotion promotion = (Response_Promotion) items.get(position);
                    if(position == 0) {
                        ((DeepLinkPriceViewHolder) holder).spacer.setVisibility(View.VISIBLE);
                    } else {
                        ((DeepLinkPriceViewHolder) holder).spacer.setVisibility(View.GONE);
                    }
                    if(promotion.getImage().getThumbnail_small()!=null) {
                        StaticFunctions.loadFresco(context,promotion.getImage().getThumbnail_small(),((DeepLinkPriceViewHolder) holder).deeplink_img);
                    }
                    ((DeepLinkPriceViewHolder) holder).setClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Uri intentUri = Uri.parse(promotion.getUrl());
                            Application_Singleton.trackEvent("Home Small Banner", "Click", promotion.getUrl());
                            HashMap<String,String> param = SplashScreen.getQueryString(intentUri);
                            if(StaticFunctions.isOnlyReseller(context)) {
                                param.put("sell_full_catalog","false");
                            }
                            new DeepLinkFunction(param,context);

                            HashMap<String,String> prop = new HashMap<>();
                            prop.put("source","Home DeepLink Banner");
                            prop.put("banner_type","DeepLink");
                            prop.put("banner_url",promotion.getUrl());
                            sendBannerClickAnalytics(prop);

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

    public void sendBannerClickAnalytics(HashMap<String,String> prop) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Banner_Clicked");
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(context, wishbookEvent);
    }

    public class DeepLinkPriceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        CardView cardView;


        @BindView(R.id.deeplink_img)
        SimpleDraweeView deeplink_img;

        @BindView(R.id.spacer)
        View spacer;

        private ItemClickListener clickListener;

        public DeepLinkPriceViewHolder(View view) {
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
