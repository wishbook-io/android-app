package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.Activity_QualityCatalog;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScreenQualityGroupAdapter extends RecyclerView.Adapter<ScreenQualityGroupAdapter.CustomViewHolder> {


    Context context;
    ArrayList<Response_catalog> response_catalogMinis;


    private static String TAG = ScreenQualityGroupAdapter.class.getSimpleName();
    private LinearLayoutManager layoutManager;

    public ScreenQualityGroupAdapter(Context context, ArrayList<Response_catalog> response_catalogMinis) {
        this.context = context;
        this.response_catalogMinis = response_catalogMinis;
    }

    @Override
    public ScreenQualityGroupAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screen_quality_group_item, parent, false);
        return new ScreenQualityGroupAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ScreenQualityGroupAdapter.CustomViewHolder holder, final int position) {
        if (response_catalogMinis.get(position).getTitle() != null) {
            holder.txt_quality_name.setText(response_catalogMinis.get(position).getTitle());
        }
        holder.flexbox_image.removeAllViews();

        if (response_catalogMinis.get(position).getProduct() != null && response_catalogMinis.get(position).getProduct().length > 0) {
            int display_count = 0;
            if (response_catalogMinis.get(position).getProduct().length > 3)
                display_count = 3;
            else
                display_count = response_catalogMinis.get(position).getProduct().length;
            for (int i = 0; i < display_count; i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.quality_subitem, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                RelativeLayout relative_disable_container = view.findViewById(R.id.relative_disable_container);
                if(response_catalogMinis.get(position).getProduct()[i].isSupplier_disabled()) {
                    view.setAlpha(0.5f);
                    relative_disable_container.setVisibility(View.VISIBLE);
                } else {
                    view.setAlpha(1f);
                    relative_disable_container.setVisibility(View.GONE);
                }
                StaticFunctions.loadFresco(context, response_catalogMinis.get(position).getProduct()[i].getImage().getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                final int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigateCatalogDetail(response_catalogMinis.get(position), finalI);
                    }
                });
                holder.flexbox_image.addView(view);

            }
            holder.flexbox_image.addView(holder.view_all_button);
        }


        holder.view_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Activity_QualityCatalog.class);
                intent.putExtra("catalog_id", response_catalogMinis.get(position).getId());
                intent.putExtra("catalog_title", response_catalogMinis.get(position).getTitle());
                intent.putExtra("set_type", response_catalogMinis.get(position).getCatalog_multi_set_type());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }


    public void navigateCatalogDetail(Response_catalog response_catalog, int product_position) {


        Bundle bundle = new Bundle();
        bundle.putString("from", Fragment_BrowseCatalogs.class.getSimpleName());
        bundle.putString("product_id", response_catalog.getProduct()[product_position].getId());
        new NavigationUtils().navigateDetailPage(context,bundle);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_quality_name)
        TextView txt_quality_name;

        @BindView(R.id.flexbox_image)
        FlexboxLayout flexbox_image;

        @BindView(R.id.view_all_button)
        AppCompatButton view_all_button;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}