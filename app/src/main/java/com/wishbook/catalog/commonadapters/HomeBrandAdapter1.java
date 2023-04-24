package com.wishbook.catalog.commonadapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeBrandAdapter1 extends RecyclerView.Adapter<HomeBrandAdapter1.CustomViewHolder> {

    Context context;
    ArrayList<Response_Brands> response_brands;
    RecyclerView recyclerView;

    public HomeBrandAdapter1(Context context, ArrayList<Response_Brands> response_brands,RecyclerView recyclerView) {
        this.context = context;
        this.response_brands = response_brands;
        this.recyclerView = recyclerView;
    }

    @Override
    public HomeBrandAdapter1.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View list = layoutInflater.inflate(R.layout.home_brand_list_item, viewGroup, false);
        return new CustomViewHolder(list);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        if(position > 0 ){
            holder.dummySpace.setVisibility(View.GONE);
        } else {
            holder.dummySpace.setVisibility(View.VISIBLE);
        }

        holder.brand_name.setText(response_brands.get(position).getName());
        String image = response_brands.get(position).getImage().getThumbnail_small();
        if (image != null & !image.equals("")) {
            StaticFunctions.loadFresco(context, image, holder.brand_img);
        }
        holder.brand_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("type","tab");
                hashMap.put("page","catalogs/brands");
                hashMap.put("focus_position", String.valueOf(holder.getAdapterPosition()));
                new DeepLinkFunction(hashMap,context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return response_brands.size();
    }

    @Override
    public void onViewDetachedFromWindow(HomeBrandAdapter1.CustomViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView brand_name;
        private SimpleDraweeView brand_img;
        private RelativeLayout dummySpace;

        public CustomViewHolder(View view) {
            super(view);
            brand_name = (TextView) view.findViewById(R.id.brand_name);
            brand_img = (SimpleDraweeView) view.findViewById(R.id.brand_img);
            dummySpace = view.findViewById(R.id.dummyFirstPosition);
        }
    }
}
