package com.wishbook.catalog.home.home_groupie.view;

import android.view.View;

import com.wishbook.catalog.R;
import com.wishbook.catalog.databinding.GroupieItemHeaderBinding;
import com.xwray.groupie.databinding.BindableItem;

import androidx.annotation.NonNull;

public class HeaderItem extends BindableItem<GroupieItemHeaderBinding> {

    private String titleStringResId;

    String subtitleResId;

    boolean isViewAllShow;

    HeaderItem.OnSeeMoreClickListener onSeeMoreClickListener;

    public HeaderItem(String title,boolean isViewAllShow) {
        this(title, null,isViewAllShow);
    }



    public HeaderItem(String titleStringResId, String subtitleResId, boolean isViewAllShow) {
        this.titleStringResId = titleStringResId;
        this.subtitleResId = subtitleResId;
        this.isViewAllShow = isViewAllShow;
    }

    @Override
    public int getLayout() {
        return R.layout.groupie_item_header;
    }

    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    public void setOnSeeMoreClickListener(HeaderItem.OnSeeMoreClickListener onSeeMoreClickListener) {
        this.onSeeMoreClickListener = onSeeMoreClickListener;
    }

    @Override
    public void bind(@NonNull GroupieItemHeaderBinding viewBinding, int position) {
        viewBinding.title.setText(titleStringResId);
        if (subtitleResId != null) {
            viewBinding.subtitle.setText(subtitleResId);
        }
        viewBinding.subtitle.setVisibility(subtitleResId != null ? View.VISIBLE : View.GONE);
        if (isViewAllShow) {
            viewBinding.btnViewAll.setVisibility(View.VISIBLE);
        } else {
            viewBinding.btnViewAll.setVisibility(View.GONE);
        }

        viewBinding.btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSeeMoreClickListener!=null) {
                    onSeeMoreClickListener.onSeeMoreClick();
                }
            }
        });
    }
}
