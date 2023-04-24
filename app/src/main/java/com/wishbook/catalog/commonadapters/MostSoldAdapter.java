package com.wishbook.catalog.commonadapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.AllDataModel;

import java.util.List;


public class MostSoldAdapter extends RecyclerView.Adapter<MostSoldAdapter.TopItemViewHolder> {

    private final AppCompatActivity context;
    private List<AllDataModel> allData;


    public MostSoldAdapter(AppCompatActivity context, List<AllDataModel> data) {
        this.allData = data;
        this.context = context;
    }

    @Override
    public MostSoldAdapter.TopItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_item_new_gradient, parent, false);
        return new MostSoldAdapter.TopItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MostSoldAdapter.TopItemViewHolder holder, int position) {

        MostSoldAdapter.TopItemViewHolder itemViewHolder = (MostSoldAdapter.TopItemViewHolder) holder;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        itemViewHolder.recyclerView.setLayoutManager(layoutManager);

        itemViewHolder.txt_title.setText(allData.get(position).getHeaderTitle());
        itemViewHolder.sectionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Home"+allData.get(holder.getAdapterPosition()).getHeaderTitle(), "Click","See More");

                Application_Singleton.CONTAINER_TITLE = allData.get(holder.getAdapterPosition()).getHeaderTitle();
                Application_Singleton.CONTAINERFRAG = allData.get(holder.getAdapterPosition()).getFragment();

                if (allData.get(holder.getAdapterPosition()).getParams() != null) {
                    Application_Singleton.deep_link_filter = allData.get(holder.getAdapterPosition()).getParams();
                } else
                    Application_Singleton.deep_link_filter = null;
                StaticFunctions.switchActivity(context, OpenContainer.class);
            }
        });

        AllItemAdapter allItemAdapter = new AllItemAdapter(context, allData.get(position).getImageUrls(), allData.get(position).getNumberofGridsize(), allData.get(position).getHeaderTitle(), allData.get(position).getFragment(),allData.get(position).getParams());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);

    }

    @Override
    public int getItemCount() {
        return allData.size();
    }


    public static class TopItemViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView recyclerView;
        private final TextView txt_title, sectionMore;

        public TopItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewItem);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            sectionMore = (TextView) itemView.findViewById(R.id.sectionMore);
        }
    }
}