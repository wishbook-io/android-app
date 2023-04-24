package com.wishbook.catalog.home.home_groupie;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jay.widget.StickyHeaders;
import com.wishbook.catalog.home.home_groupie.decoration.StickyItem;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;

public class StickyGroupAdapter extends GroupAdapter implements StickyHeaders {


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean isStickyHeader(int position) {
        Item item = getItem(position);
        if(item instanceof StickyItem && ((StickyItem) item).isSticky()) {
            Log.d("TAG", "isStickyHeader: "+"  --------- TRUE ------------");

        }
        return item instanceof StickyItem && ((StickyItem) item).isSticky();
    }
}
