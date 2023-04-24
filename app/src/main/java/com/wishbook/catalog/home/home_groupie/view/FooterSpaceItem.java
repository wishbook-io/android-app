package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;

import com.wishbook.catalog.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public class FooterSpaceItem extends Item<FooterSpaceItem.CustomViewHolder> {

    Fragment fragment;
    Context mContext;

    public FooterSpaceItem(Context context, Fragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public FooterSpaceItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new FooterSpaceItem.CustomViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull CustomViewHolder viewHolder, int position) {

    }


    @Override
    public int getLayout() {
        return R.layout.groupie_footer_space_item;
    }


    public class CustomViewHolder extends ViewHolder {

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }
}
