package com.wishbook.catalog.home.more.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.wishbook.catalog.R;

/**
 * Created by root on 31/8/16.
 */
public class CustomListviewAdapter extends RecyclerView.Adapter<CustomListviewAdapter.CustomViewHolder> {

    Context context;
    private List<RowItem> rowItems=new ArrayList<>();

    public CustomListviewAdapter(Context context,
                                 List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        public CustomViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.icon);

        }
    }


        @Override
    public void onBindViewHolder(CustomListviewAdapter.CustomViewHolder holder, int position) {

            holder.image.setImageResource(rowItems.get(position).getImageId());
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_help, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
}