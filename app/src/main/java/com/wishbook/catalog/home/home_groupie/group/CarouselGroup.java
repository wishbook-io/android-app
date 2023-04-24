package com.wishbook.catalog.home.home_groupie.group;

import com.wishbook.catalog.home.home_groupie.view.CarouselItem;
import com.wishbook.catalog.home.home_groupie.view.CarouselProductWithSeeMoreItem;
import com.wishbook.catalog.home.home_groupie.view.CarouselWithBackGroundItem;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupDataObserver;
import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarouselGroup implements Group {

    private boolean isEmpty = true;
    private RecyclerView.Adapter adapter;
    private GroupDataObserver groupDataObserver;
    private Object carouselItem;
    private CarouselWithBackGroundItem carouselWithBackGroundItem;
    boolean isPublicBrand, isProductCarousel;

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            boolean empty = adapter.getItemCount() == 0;
            if (empty && !isEmpty) {
                isEmpty = empty;
                groupDataObserver.onItemRemoved((Group) carouselItem, 0);

            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            boolean empty = adapter.getItemCount() == 0;
            if (isEmpty && !empty) {
                isEmpty = empty;
                groupDataObserver.onItemRemoved((Group) carouselItem, 0);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            boolean empty = adapter.getItemCount() == 0;
            if (!empty) {
                isEmpty = empty;
                groupDataObserver.onItemRangeChanged((Group) carouselItem, 0, itemCount);
            }
        }
    };

    public CarouselGroup(RecyclerView.ItemDecoration itemDecoration, GroupAdapter adapter) {
        this.adapter = adapter;
        carouselItem = new CarouselItem(itemDecoration, adapter);
        isEmpty = adapter.getItemCount() == 0;
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public CarouselGroup(RecyclerView.ItemDecoration itemDecoration,
                         GroupAdapter adapter,
                         boolean isPublicBrand,
                         boolean isProductCarousel,String title) {
        this.adapter = adapter;
        this.isPublicBrand = isPublicBrand;
        this.isProductCarousel = isProductCarousel;
        if (isPublicBrand) {
            carouselItem = new CarouselWithBackGroundItem(itemDecoration, adapter);
        } else if (isProductCarousel) {
            carouselItem = new CarouselProductWithSeeMoreItem(itemDecoration, adapter,title);
        }
        isEmpty = adapter.getItemCount() == 0;
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }


    @Override
    public int getItemCount() {
        return isEmpty ? 0 : 1;
    }

    @NonNull
    @Override
    public Item getItem(int position) {
        if (position == 0 && !isEmpty) return (Item) carouselItem;
        else throw new IndexOutOfBoundsException();
    }

    @Override
    public int getPosition(@NonNull Item item) {
        return item == carouselItem && !isEmpty ? 0 : -1;
    }

    @Override
    public void registerGroupDataObserver(@NonNull GroupDataObserver groupDataObserver) {
        this.groupDataObserver = groupDataObserver;
    }

    @Override
    public void unregisterGroupDataObserver(@NonNull GroupDataObserver groupDataObserver) {
        this.groupDataObserver = null;
    }
}
