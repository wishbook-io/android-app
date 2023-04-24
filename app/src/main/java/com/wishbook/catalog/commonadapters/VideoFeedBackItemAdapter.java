package com.wishbook.catalog.commonadapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFeedBackItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<?> items;
    private int type;


    public VideoFeedBackItemAdapter(Context context, ArrayList<?> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_feedback_item, parent, false);
        return new VideoFeedBackItemAdapter.VideoFeedBackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoFeedBackItemAdapter.VideoFeedBackViewHolder) {
            if (items.get(0) instanceof Response_Promotion) {
                final Response_Promotion promotion = (Response_Promotion) items.get(position);
                if (promotion.getImage().getBanner() != null) {
                    StaticFunctions.loadFresco(context, promotion.getImage().getBanner(), ((VideoFeedBackViewHolder) holder).thumb_img);
                }
                if (promotion.getReview_type() != null && promotion.getReview_type().equalsIgnoreCase("ResellVideo")) {
                    ((VideoFeedBackViewHolder) holder).img_play_btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_youtube_play));
                } else {
                    ((VideoFeedBackViewHolder) holder).img_play_btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_filled_black_24dp));
                }
                ((VideoFeedBackViewHolder) holder).setClickListener(new ItemClickListener() {
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
                                context.startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                try {
                                    appIntent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(promotion.getUrl()));
                                    context.startActivity(appIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
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


    public class VideoFeedBackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
