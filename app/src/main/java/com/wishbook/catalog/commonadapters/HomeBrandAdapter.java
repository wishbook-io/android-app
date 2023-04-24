package com.wishbook.catalog.commonadapters;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.Branditem;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;

import java.util.List;

public class HomeBrandAdapter extends RecyclerView.Adapter<HomeBrandAdapter.CustomViewHolder> {
    private List<Branditem> feedItemList;
    private AppCompatActivity mContext;

    public HomeBrandAdapter(AppCompatActivity context, List<Branditem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView brand_name;
        private final SimpleDraweeView brand_img;

        public CustomViewHolder(View view) {
            super(view);
            brand_name = (TextView) view.findViewById(R.id.brand_name);
            brand_img= (SimpleDraweeView) view.findViewById(R.id.brand_img);
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brand_item, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
      customViewHolder.brand_name.setText(feedItemList.get(i).getBrandName());
        String image = feedItemList.get(i).getBrandImage();
        if (image != null & !image.equals("")) {
           // StaticFunctions.loadImage(mContext,image,customViewHolder.brand_img,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.brand_img);
         //   Picasso.with(mContext).load(image).into(customViewHolder.brand_img);
        }
        customViewHolder.brand_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("filtertype","brand");
                bundle.putString("filtervalue",feedItemList.get(customViewHolder.getAdapterPosition()).getId());
                Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                fragmentCatalogs.setArguments(bundle);
                mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentCatalogs).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}