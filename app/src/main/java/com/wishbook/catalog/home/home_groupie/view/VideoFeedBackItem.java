package com.wishbook.catalog.home.home_groupie.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.VideoFeedBackItemAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFeedBackItem extends Item<VideoFeedBackItem.VideoFeedBackViewHolder> {


    Fragment fragment;
    Context mContext;
    private Response_Promotion data;

    public VideoFeedBackItem(Response_Promotion data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.video_feedback_item;
    }


    @NonNull
    @Override
    public VideoFeedBackItem.VideoFeedBackViewHolder createViewHolder(@NonNull View itemView) {
        return new VideoFeedBackItem.VideoFeedBackViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull VideoFeedBackViewHolder viewHolder, int position) {
        final Response_Promotion promotion = data;
        if (promotion.getImage().getBanner() != null) {
            StaticFunctions.loadFresco(mContext, promotion.getImage().getBanner(), viewHolder.thumb_img);
        }
        if (promotion.getReview_type() != null && promotion.getReview_type().equalsIgnoreCase("ResellVideo")) {
            viewHolder.img_play_btn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_youtube_play));
        } else {
            viewHolder.img_play_btn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play_circle_filled_black_24dp));
        }
        viewHolder.setClickListener(new VideoFeedBackItemAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (promotion.getUrl() != null) {
                    HashMap<String, String> youtube_hassh = SplashScreen.getQueryString(Uri.parse(promotion.getUrl()));
                    Intent appIntent = null;
                    if (youtube_hassh.containsKey("v")) {
                        appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtube_hassh.get("v")));
                    } else {
                        appIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(promotion.getUrl()));
                    }
                    try {
                        mContext.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        try {
                            appIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(promotion.getUrl()));
                            mContext.startActivity(appIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public class VideoFeedBackViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        CardView card_view;


        @BindView(R.id.thumb_img)
        SimpleDraweeView thumb_img;

        @BindView(R.id.img_play_btn)
        ImageView img_play_btn;


        private VideoFeedBackItemAdapter.ItemClickListener clickListener;

        public VideoFeedBackViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(VideoFeedBackItemAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}
