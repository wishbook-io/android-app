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

/**
 * Created by tech4 on 10/11/17.
 */

public class AllSectionAdapterNew extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private final AppCompatActivity context;
    private List<AllDataModel> allData;



    public AllSectionAdapterNew(AppCompatActivity context,List<AllDataModel> data) {
        this.allData = data;
        this.context=context;
    }

    @Override
    public int getSectionCount() {
        return allData.size();
    }

    @Override
    public int getItemCount(int section) {
        return 1;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, final int section) {
        AllDataModel sectionitems = allData.get(section);
        String sectionName =sectionitems.getHeaderTitle();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(sectionName);
        sectionViewHolder.sectionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE=allData.get(section).getHeaderTitle();
                Application_Singleton.CONTAINERFRAG= allData.get(section).getFragment();
                StaticFunctions.switchActivity(context, OpenContainer.class);
                // context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main,allData.get(section).getFragment() ).addToBackStack(null).commit();

            }
        });

        if(sectionitems.getImageUrls().size()==0){
            sectionViewHolder.sectionTitle.setVisibility(View.GONE);
            sectionViewHolder.sectionMore.setVisibility(View.GONE);
        }
        else{
            sectionViewHolder.sectionTitle.setVisibility(View.VISIBLE);
            sectionViewHolder.sectionMore.setVisibility(View.VISIBLE);
        }
        sectionViewHolder.itemView.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition, int absolutePosition) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        itemViewHolder.recyclerView.setLayoutManager(layoutManager);
       /* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls(),allData.get(absolutePosition).getNumberofGridsize());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*/

        //todo work for focus particular page
        AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls(),allData.get(absolutePosition).getNumberofGridsize(), allData.get(section).getHeaderTitle(), allData.get(section).getFragment());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);
       /* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*/
        //itemViewHolder.itemTitle.setText(AllItem.get);
        // itemViewHolder.itemImage.setBackgroundColor(Color.parseColor("#01579b"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, boolean header) {
        View v = null;
        if (header) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_item_header, parent, false);
            return new SectionViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_item_container_new, parent, false);
            return new ItemViewHolder(v);
        }
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {


        final TextView sectionTitle;
        private final TextView sectionMore;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            sectionMore = (TextView) itemView.findViewById(R.id.sectionMore);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView recyclerView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerViewItem);
        }
    }
}
