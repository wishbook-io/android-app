package com.wishbook.catalog.home.home_groupie.view;

import android.view.View;

import com.wishbook.catalog.R;
import com.wishbook.catalog.databinding.GroupieItemCarouselBinding;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.databinding.BindableItem;
import com.xwray.groupie.databinding.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A horizontally scrolling RecyclerView, for use in a vertically scrolling RecyclerView.
 */


public class CarouselItem extends BindableItem<GroupieItemCarouselBinding> {

    private GroupAdapter adapter;
    private RecyclerView.ItemDecoration carouselDecoration;

    public CarouselItem(RecyclerView.ItemDecoration itemDecoration, GroupAdapter adapter) {
        this.carouselDecoration = itemDecoration;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder<GroupieItemCarouselBinding> createViewHolder(@NonNull View itemView) {
        ViewHolder<GroupieItemCarouselBinding> viewHolder = super.createViewHolder(itemView);
        RecyclerView recyclerView = viewHolder.binding.recyclerView;
        recyclerView.addItemDecoration(carouselDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        return viewHolder;
    }

    @Override
    public void bind(@NonNull GroupieItemCarouselBinding viewBinding, int position) {
        viewBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getLayout() {
        return R.layout.groupie_item_carousel;
    }


    public void updateChildAdapter() {
        if (adapter != null) {
            notifyChanged();
        }
    }


}

