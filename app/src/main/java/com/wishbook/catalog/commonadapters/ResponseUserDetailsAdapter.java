package com.wishbook.catalog.commonadapters;

/**
 * Created by root on 11/11/16.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Buyers;
import com.wishbook.catalog.commonmodels.responses.Downstream_stats;

import java.util.ArrayList;



public class ResponseUserDetailsAdapter extends RecyclerView.Adapter<ResponseUserDetailsAdapter.CustomViewHolder> {
    private ArrayList<Buyers> feedItemList;
    private Context mContext;

    public ResponseUserDetailsAdapter(Context context, ArrayList<Buyers> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView company_name;
        private final TextView opens;
        private final TextView push_downstream;
        private final LinearLayout container;
        private final LinearLayout container1;

        public CustomViewHolder(View view) {
            super(view);
            company_name = (TextView) view.findViewById(R.id.company_name);
            opens = (TextView) view.findViewById(R.id.opens);
            push_downstream = (TextView) view.findViewById(R.id.push_downstream);
            container = (LinearLayout) view.findViewById(R.id.downstream1);
            container1 = (LinearLayout) view.findViewById(R.id.downstream2);

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_details, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        customViewHolder.company_name.setText(feedItemList.get(i).getBuying_company_name());
        customViewHolder.opens.setText(feedItemList.get(i).getTotal_products_viewed());
       if(feedItemList.get(i).getPush_downstream().equals("yes"))
       {
           customViewHolder.container.setVisibility(View.VISIBLE);
           customViewHolder.container1.setVisibility(View.VISIBLE);
           Downstream_stats stats = feedItemList.get(i).getDownstream_stats();
           customViewHolder.push_downstream.setText(stats.getTotal_products_viewed()+" / "+stats.getTotal_buyers());
       }
        else
       {
           customViewHolder.container.setVisibility(View.GONE);
           customViewHolder.container1.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}