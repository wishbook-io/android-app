package com.wishbook.catalog.home.home_groupie.view;

import android.view.View;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CarouselProductWithSeeMoreItem extends Item<CarouselProductWithSeeMoreItem.CustomViewHolder> {

    private GroupAdapter adapter;
    private RecyclerView.ItemDecoration carouselDecoration;
    CarouselProductWithSeeMoreItem.OnSeeMoreClickListener onSeeMoreClickListener;
    String title;

    public CarouselProductWithSeeMoreItem(RecyclerView.ItemDecoration itemDecoration, GroupAdapter adapter, String title) {
        this.carouselDecoration = itemDecoration;
        this.adapter = adapter;
        this.title = title;
    }


    @Override
    public void bind(@NonNull CarouselProductWithSeeMoreItem.CustomViewHolder viewHolder, int position) {
        viewHolder.recyclerViewItem.setAdapter(adapter);
        viewHolder.sectionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSeeMoreClickListener != null) {
                    onSeeMoreClickListener.onSeeMoreClick();
                }
            }
        });
    }

    @NonNull
    @Override
    public CarouselProductWithSeeMoreItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        CarouselProductWithSeeMoreItem.CustomViewHolder viewHolder = new CarouselProductWithSeeMoreItem.CustomViewHolder(itemView);
        viewHolder.recyclerViewItem.addItemDecoration(carouselDecoration);
        viewHolder.recyclerViewItem.setLayoutManager(new LinearLayoutManager(viewHolder.recyclerViewItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if(title!=null && !title.isEmpty()) {
            viewHolder.txt_title.setText(title);
        } else {
            viewHolder.txt_title.setText("Products");
        }
        return viewHolder;
    }


    @Override
    public int getLayout() {
        return R.layout.all_item_container_new;
    }


    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    public void setOnSeeMoreClickListener(CarouselProductWithSeeMoreItem.OnSeeMoreClickListener onSeeMoreClickListener) {
        this.onSeeMoreClickListener = onSeeMoreClickListener;
    }

    public class CustomViewHolder extends com.xwray.groupie.ViewHolder {

        @BindView(R.id.recyclerViewItem)
        RecyclerView recyclerViewItem;

        @BindView(R.id.sectionMore)
        TextView sectionMore;


        @BindView(R.id.txt_title)
        TextView txt_title;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}