package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.home.Fragment_Home2;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CarouselFilterItem extends Item<CarouselFilterItem.DeepLinkViewHolder> {

    Fragment fragment;
    Context mContext;
    private Response_Promotion data;


    public CarouselFilterItem(Response_Promotion data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.home_deeplink_price_item;
    }

    @NonNull
    @Override
    public DeepLinkViewHolder createViewHolder(@NonNull View itemView) {
        return new DeepLinkViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull DeepLinkViewHolder holder, int position) {
        final Response_Promotion promotion = (Response_Promotion) data;
        if (position == 0) {
            holder.spacer.setVisibility(View.VISIBLE);
        } else {
            holder.spacer.setVisibility(View.GONE);
        }
        if (promotion.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(mContext, promotion.getImage().getThumbnail_small(), holder.deeplink_img);
        }
        holder.setClickListener(new CarouselFilterItem.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Uri intentUri = Uri.parse(promotion.getUrl());
                Application_Singleton.trackEvent("Home Small Banner", "Click", promotion.getUrl());
                HashMap<String, String> param = SplashScreen.getQueryString(intentUri);
                if (StaticFunctions.isOnlyReseller(mContext)) {
                    param.put("sell_full_catalog", "false");
                }
                new DeepLinkFunction(param, mContext);
                HashMap<String, String> prop = new HashMap<>();
                prop.put("source", "Home DeepLink Banner");
                prop.put("banner_type", "DeepLink");
                prop.put("banner_url", promotion.getUrl());

                if (fragment instanceof Fragment_Home2)
                    ((Fragment_Home2) fragment).sendBannerClickAnalytics(prop);
            }
        });
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class DeepLinkViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        CardView cardView;


        @BindView(R.id.deeplink_img)
        SimpleDraweeView deeplink_img;

        @BindView(R.id.spacer)
        View spacer;

        private CarouselFilterItem.ItemClickListener clickListener;

        public DeepLinkViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(CarouselFilterItem.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
