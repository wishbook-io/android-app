package com.wishbook.catalog.commonadapters;


import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;

import java.util.ArrayList;
import java.util.Locale;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.CustomViewHolder> {

    ArrayList<String> items;
    ArrayList<String> arraylist;
    private Context context;

    private AdapterNotifyListener listener;
    private boolean isLocalHistory;


    public SearchSuggestionAdapter(Context context, ArrayList<String> items, boolean isLocalHistory) {
        this.context = context;
        this.items = items;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(items);
        this.isLocalHistory = isLocalHistory;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_name_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        if(isLocalHistory) {
            holder.img_search_type.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_history_black_24dp));
        } else {
            holder.img_search_type.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_search_black));
        }
        holder.txt_name.setText(items.get(position));
        holder.linear_search_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.itemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arraylist);
        } else {
            for (String wp : arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    items.add(wp);
                }
            }

        }
        notifyDataSetChanged();
    }


    public ArrayList<String> getAllItems() {
        return items;
    }


    public interface AdapterNotifyListener {
        void itemClick(int position);
    }

    public void setSearchItemClick(AdapterNotifyListener listener) {
        this.listener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)
        TextView txt_name;

        @BindView(R.id.img_search_type)
        ImageView img_search_type;

        @BindView(R.id.linear_search_container)
        LinearLayout linear_search_container;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
