package com.wishbook.catalog.home.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SimilarProductAdapter extends RecyclerView.Adapter<SimilarProductAdapter.ViewHolder> {


    Fragment fragment;
    Context context;
    private List<Response_catalogMini> data;

    public SimilarProductAdapter(List<Response_catalogMini> data, Context context, Fragment fragment) {
        this.data = data;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.fragment_recycler_similarproduct, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (data != null) {
            Response_catalogMini response_catalogMini = data.get(position);
            holder.catalog_name.setText(response_catalogMini.getTitle());
            String price_range = response_catalogMini.getPrice_range();
            if (price_range != null) {
                holder.catalog_price.setVisibility(View.VISIBLE);
                if (price_range.contains("-")) {
                    String[] priceRangeMultiple = price_range.split("-");
                    holder.catalog_price.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                } else {
                    String priceRangeSingle = price_range;
                    holder.catalog_price.setText("\u20B9" + priceRangeSingle + "/Pc.");
                }
            } else {
                holder.catalog_price.setVisibility(View.GONE);
            }
            StaticFunctions.loadFresco(context, data.get(position).getThumbnail().getThumbnail_small(), holder.thumbnail);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.catalog_name)
        TextView catalog_name;

        @BindView(R.id.catalog_price)
        TextView catalog_price;

        @BindView(R.id.thumbnail)
        SimpleDraweeView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}

