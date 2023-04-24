package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.HomeBanner;
import com.wishbook.catalog.home.Fragment_Home2;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerItem extends Item<BannerItem.CustomViewHolder> {


    Fragment fragment;
    Context mContext;
    private HomeBanner data;

    public BannerItem(HomeBanner data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }

    @Override
    public void bind(@NonNull CustomViewHolder holder, int position) {
        final HomeBanner promotion = data;
        if (promotion.getBanner_url() != null) {
            StaticFunctions.loadFresco(mContext, promotion.getBanner_url(), holder.banner_img);
        }

        holder.setClickListener(new BannerItem.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (promotion.getDeep_link() != null) {
                    Uri intentUri = Uri.parse(promotion.getDeep_link());
                    HashMap<String, String> param = SplashScreen.getQueryString(intentUri);
                    if (StaticFunctions.isOnlyReseller(mContext)) {
                        param.put("sell_full_catalog", "false");
                    }
                    new DeepLinkFunction(param, mContext);

                    HashMap<String, String> prop = new HashMap<>();
                    prop.put("source", "Home DeepLink Banner");
                    prop.put("banner_type", "DeepLink");
                    prop.put("banner_url", promotion.getDeep_link());
                    if (fragment instanceof Fragment_Home2) {
                        ((Fragment_Home2) fragment).sendBannerClickAnalytics(prop);

                    }

                }

            }
        });
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.groupie_banner_item;
    }


    @NonNull
    @Override
    public BannerItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new BannerItem.CustomViewHolder(itemView);
    }

    public class CustomViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.banner_img)
        SimpleDraweeView banner_img;


        private BannerItem.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(BannerItem.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}
