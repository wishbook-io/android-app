package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Photos;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductThumbAdapter extends RecyclerView.Adapter<ProductThumbAdapter.ThumbViewHolder> {


    private Context mContext;
    private ArrayList<Photos> photos;
    private int selected_position = 0;
    OnThumbSelectorListener onThumbSelectorListener;


    public ProductThumbAdapter(Context mContext, ArrayList<Photos> photos) {
        this.mContext = mContext;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ProductThumbAdapter.ThumbViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_thumb_item, viewGroup, false);
        return new ProductThumbAdapter.ThumbViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductThumbAdapter.ThumbViewHolder thumbViewHolder, final int position) {
        Photos photo = photos.get(position);
        if (selected_position == position) {
            thumbViewHolder.relative_container.setBackground(mContext.getResources().getDrawable(R.drawable.thumb_select));
        } else {
            thumbViewHolder.relative_container.setBackground(null);
        }

        if (photo.getImage() != null && photo.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(mContext, photo.getImage().getThumbnail_small(), thumbViewHolder.product_img);
        }

        thumbViewHolder.relative_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_position = position;
                notifyDataSetChanged();
                if(onThumbSelectorListener!=null){
                    onThumbSelectorListener.onSelect(position);
                }
            }
        });


    }


    public void setSelected_position(int selected_position) {
        this.selected_position = selected_position;
    }

    public interface OnThumbSelectorListener {
        void onSelect(int  position);
    }



    public void setOnThumbSelectorListener(OnThumbSelectorListener onThumbSelectorListener) {
        this.onThumbSelectorListener = onThumbSelectorListener;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class ThumbViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_container)
        RelativeLayout relative_container;

        @BindView(R.id.product_img)
        SimpleDraweeView product_img;


        ThumbViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
