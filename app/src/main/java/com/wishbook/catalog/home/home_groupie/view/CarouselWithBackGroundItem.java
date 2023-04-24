package com.wishbook.catalog.home.home_groupie.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CarouselWithBackGroundItem extends Item<CarouselWithBackGroundItem.CustomViewHolder> {

    private GroupAdapter adapter;
    private RecyclerView.ItemDecoration carouselDecoration;
    OnSeeMoreClickListener onSeeMoreClickListener;
    private String all_brand_bg_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/banner_1.jpg";
    private float realDx;

    public CarouselWithBackGroundItem(RecyclerView.ItemDecoration itemDecoration, GroupAdapter adapter) {
        this.carouselDecoration = itemDecoration;
        this.adapter = adapter;
    }


    @Override
    public void bind(@NonNull CustomViewHolder viewHolder, int position) {
        viewHolder.recycler_view_brand.setAdapter(adapter);
        StaticFunctions.loadFresco(viewHolder.img_allbrand_bg.getContext(), all_brand_bg_url, viewHolder.img_allbrand_bg);
        viewHolder.txt_brand_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSeeMoreClickListener != null) {
                    onSeeMoreClickListener.onSeeMoreClick();
                }
            }
        });
    }

    @NonNull
    @Override
    public CarouselWithBackGroundItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        CarouselWithBackGroundItem.CustomViewHolder viewHolder = new CarouselWithBackGroundItem.CustomViewHolder(itemView);
        viewHolder.recycler_view_brand.addItemDecoration(carouselDecoration);
        viewHolder.recycler_view_brand.setLayoutManager(new LinearLayoutManager(viewHolder.recycler_view_brand.getContext(), LinearLayoutManager.HORIZONTAL, false));
        animateContentOnScroll(viewHolder.recycler_view_brand, viewHolder.brand_bg_container);
        return viewHolder;
    }


    @Override
    public int getLayout() {
        return R.layout.include_brand_recycler;
    }

    private void animateContentOnScroll(RecyclerView recyclerView, RelativeLayout relativeLayout) {
        // set animation image on scroll here
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                realDx = realDx + dx;
                if (realDx <= 300 && realDx > 0) {
                    relativeLayout.setAlpha(1 - realDx / 500);
                }
            }
        });
    }


    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    public void setOnSeeMoreClickListener(OnSeeMoreClickListener onSeeMoreClickListener) {
        this.onSeeMoreClickListener = onSeeMoreClickListener;
    }

    public class CustomViewHolder extends com.xwray.groupie.ViewHolder {

        @BindView(R.id.recycler_view_brand)
        RecyclerView recycler_view_brand;

        @BindView(R.id.img_background)
        ImageView img_background;

        @BindView(R.id.img_allbrand_bg)
        SimpleDraweeView img_allbrand_bg;

        @BindView(R.id.brand_bg_container)
        RelativeLayout brand_bg_container;

        @BindView(R.id.txt_brand_see_more)
        TextView txt_brand_see_more;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
