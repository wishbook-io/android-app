package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wishbook.catalog.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingItem extends Item<LoadingItem.LoadingViewHolder> {


    Fragment fragment;
    Context mContext;

    public LoadingItem(Context context, Fragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
    }


    @Override
    public void bind(@NonNull LoadingViewHolder viewHolder, int position) {

    }

    @Override
    public int getLayout() {
        return R.layout.groupie_loading_view;
    }


    @NonNull
    @Override
    public LoadingItem.LoadingViewHolder createViewHolder(@NonNull View itemView) {
        return new LoadingItem.LoadingViewHolder(itemView);
    }

    public class LoadingViewHolder extends ViewHolder {

        @BindView(R.id.relative_page_progress)
        RelativeLayout relative_page_progress;

        @BindView(R.id.progress_bar)
        ProgressBar progress_bar;

        public LoadingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

