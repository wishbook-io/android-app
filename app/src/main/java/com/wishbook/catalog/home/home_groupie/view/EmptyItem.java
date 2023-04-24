package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wishbook.catalog.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import butterknife.ButterKnife;

public class EmptyItem extends Item<EmptyItem.CustomViewHolder> {

    Fragment fragment;
    Context mContext;

    public EmptyItem(Context context, Fragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public EmptyItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new EmptyItem.CustomViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull EmptyItem.CustomViewHolder viewHolder, int position) {

    }


    @Override
    public int getLayout() {
        return R.layout.groupie_empty_item;
    }


    public class CustomViewHolder extends ViewHolder {

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
