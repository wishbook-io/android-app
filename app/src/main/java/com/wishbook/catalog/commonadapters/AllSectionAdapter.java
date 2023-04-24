package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.AllDataModel;

import java.util.HashMap;
import java.util.List;


public class AllSectionAdapter extends RecyclerView.Adapter<AllSectionAdapter.ItemViewHolder> {

    private final AppCompatActivity context;
    private List<AllDataModel> allData;



    public AllSectionAdapter(AppCompatActivity context,List<AllDataModel> data) {
        this.allData = data;
        this.context=context;
    }

    /*@Override
    public int getSectionCount() {
        return allData.size();
    }

    @Override
    public int getItemCount(int section) {
        return 1;
    }*/

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_item_container_new, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        itemViewHolder.recyclerView.setLayoutManager(layoutManager);
       /* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls(),allData.get(absolutePosition).getNumberofGridsize());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*/

       itemViewHolder.txt_title.setText(allData.get(position).getHeaderTitle());
        if(allData.get(position).getImageUrls().size() > 4) {
            itemViewHolder.sectionMore.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.sectionMore.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.linear_container.getLayoutParams();
        if(allData.get(position).getHeaderTitle().equalsIgnoreCase("Your Most View Catalog")) {
            layoutParams.setMargins(0,0,0,StaticFunctions.dpToPx(context,0));
            holder.linear_container.setLayoutParams(layoutParams);

        } else {
            holder.linear_container.setLayoutParams(layoutParams);
        }
        itemViewHolder.sectionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (allData != null && allData.size() > 0 && allData.get(holder.getAdapterPosition()) != null) {

                        Application_Singleton.trackEvent("Home" + allData.get(holder.getAdapterPosition()).getHeaderTitle(), "Click", "See More");

                        Application_Singleton.CONTAINER_TITLE = allData.get(holder.getAdapterPosition()).getHeaderTitle();
                        Application_Singleton.CONTAINERFRAG = allData.get(holder.getAdapterPosition()).getFragment();

                       if(allData.get(holder.getAdapterPosition()).getHeaderTitle().equalsIgnoreCase(context.getResources().getString(R.string.from_brands_you_follow))){

                           Intent intent = new Intent(context, OpenContainer.class);

                          // intent.putExtra("toolbarCategory", OpenContainer.BROWSE);



                           if (allData.get(holder.getAdapterPosition()).getParams() != null) {
                               Application_Singleton.deep_link_filter = allData.get(holder.getAdapterPosition()).getParams();
                           } else
                               Application_Singleton.deep_link_filter = null;


                           if (allData.get(holder.getAdapterPosition()).getHeaderTitle().equalsIgnoreCase(context.getResources().getString(R.string.from_trusted_seller))) {
                               Application_Singleton.isPublicTrusted = true;
                           }
                           context.startActivity(intent);


                       } else {
                           Log.e("TAG", "onClick: See More Called" );
                           Application_Singleton.trackEvent("Home", "Click","See More");
                           HashMap<String,String> hashMap = new HashMap<>();
                           new DeepLinkFunction(allData.get(holder.getAdapterPosition()).getParams(), context);
                       }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(allData.get(position).getParams()==null) {
            //todo work for focus particular page
            AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(position).getImageUrls(),allData.get(position).getNumberofGridsize(), allData.get(position).getHeaderTitle(), allData.get(position).getFragment());
            itemViewHolder.recyclerView.setAdapter(allItemAdapter);
        } else {
            AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(position).getImageUrls(),allData.get(position).getNumberofGridsize(), allData.get(position).getHeaderTitle(), allData.get(position).getFragment(),allData.get(position).getParams());
            itemViewHolder.recyclerView.setAdapter(allItemAdapter);
        }

       /* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*/
        //itemViewHolder.itemTitle.setText(AllItem.get);
        // itemViewHolder.itemImage.setBackgroundColor(Color.parseColor("#01579b"));
    }

    @Override
    public int getItemCount() {
        return allData.size();
    }

   /* @Override
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
        sectionViewHolder.main_container.setVisibility(View.VISIBLE);
        //sectionViewHolder.itemView.setVisibility(View.GONE);
    }*/

   /* @Override
    public void onBindViewHolder(ItemViewHolder holder, int section, int relativePosition, int absolutePosition) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        itemViewHolder.recyclerView.setLayoutManager(layoutManager);
       *//* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls(),allData.get(absolutePosition).getNumberofGridsize());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*//*

        //todo work for focus particular page
        AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls(),allData.get(absolutePosition).getNumberofGridsize(), allData.get(section).getHeaderTitle(), allData.get(section).getFragment());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);
       *//* AllItemAdapter allItemAdapter=new AllItemAdapter(context,allData.get(absolutePosition).getImageUrls());
        itemViewHolder.recyclerView.setAdapter(allItemAdapter);*//*
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
*/

    public static class SectionViewHolder extends RecyclerView.ViewHolder {


        final TextView sectionTitle;
        private final TextView sectionMore;
        private RelativeLayout main_container;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            sectionMore = (TextView) itemView.findViewById(R.id.sectionMore);
            main_container = (RelativeLayout) itemView.findViewById(R.id.main_container);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView recyclerView;
        private final TextView txt_title,sectionMore;
        private LinearLayout linear_container;

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerViewItem);
            txt_title  = (TextView) itemView.findViewById(R.id.txt_title);
            sectionMore = (TextView) itemView.findViewById(R.id.sectionMore);
            linear_container = itemView.findViewById(R.id.linear_container);
        }
    }
}