package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.RequestRating;
import com.wishbook.catalog.home.catalog.details.Activity_ProductPhotos;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAllReviewAdapter extends RecyclerView.Adapter<ProductAllReviewAdapter.ReviewViewHolder> {


    private Context mContext;
    private ArrayList<RequestRating> requestRatings;

    public ProductAllReviewAdapter(Context mContext, ArrayList<RequestRating> requestRatings) {
        this.mContext = mContext;
        this.requestRatings = requestRatings;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.buyer_product_rating_item, viewGroup, false);
        return new ProductAllReviewAdapter.ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder reviewViewHolder, int position) {
        RequestRating rating = requestRatings.get(position);
        StringBuffer buyername = new StringBuffer();
        if (rating.getFirst_name() != null && !rating.getFirst_name().isEmpty()) {
            buyername.append(rating.getFirst_name());
        }
        if (rating.getLast_name() != null && !rating.getLast_name().isEmpty()) {
            buyername.append(" " + rating.getLast_name());
        }
        try {
            if(buyername!=null && !buyername.toString().isEmpty()) {
                reviewViewHolder.txt_buyer_name.setText(buyername);
            } else {
                reviewViewHolder.txt_buyer_name.setText("Wishbook User");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reviewViewHolder.txt_rating.setText(rating.getRating());

        if (rating.getReview() != null && !rating.getReview().isEmpty()) {
            reviewViewHolder.txt_review.setVisibility(View.VISIBLE);
            reviewViewHolder.txt_review.setText(rating.getReview());
        } else {
            reviewViewHolder.txt_review.setVisibility(View.GONE);
        }

        if (rating.getReview_photos().size() > 0) {
            reviewViewHolder.flex_img_container.setVisibility(View.VISIBLE);
            for (int j = 0; j < rating.getReview_photos().size(); j++) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.thumb_1_1_item, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (rating.getReview_photos().get(j).getThumbnail_small() != null) {
                    StaticFunctions.loadFresco(mContext, rating.getReview_photos().get(j).getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photos_intent = new Intent(mContext, Activity_ProductPhotos.class);
                        photos_intent.putExtra("review", rating);
                        photos_intent.putExtra("position", 0);
                        mContext.startActivity(photos_intent);
                    }
                });
                reviewViewHolder.flex_img_container.addView(view);
            }
        } else {
            reviewViewHolder.flex_img_container.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return requestRatings.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_buyer_name)
        TextView txt_buyer_name;

        @BindView(R.id.txt_review)
        TextView txt_review;

        @BindView(R.id.txt_rating)
        TextView txt_rating;

        @BindView(R.id.flex_img_container)
        FlexboxLayout flex_img_container;

        ReviewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
