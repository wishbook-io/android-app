package com.wishbook.catalog.commonadapters;

/**
 * Created by root on 11/11/16.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Products;

import java.util.ArrayList;


public class ResponseDesignDetailsAdapter extends RecyclerView.Adapter<ResponseDesignDetailsAdapter.CustomViewHolder> {
    private ArrayList<Products> feedItemList;
    private Context mContext;

    public ResponseDesignDetailsAdapter(Context context, ArrayList<Products> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView product_name;
        private final TextView opens;
        private final TextView total_shares;


        public CustomViewHolder(View view) {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            opens = (TextView) view.findViewById(R.id.opens);
            total_shares = (TextView) view.findViewById(R.id.total_shares);

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.design_details, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        customViewHolder.product_name.setText(feedItemList.get(i).getSku());
        customViewHolder.opens.setText(""+feedItemList.get(i).getTotal_buyers_viewed());
        customViewHolder.total_shares.setText(""+feedItemList.get(i).getTotal_shares());
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}