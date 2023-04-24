package com.wishbook.catalog.home.home_groupie.view;

import android.view.View;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderGradientItem extends Item<HeaderGradientItem.HeaderViewHolder> {

    private String titleStringResId;

    boolean isViewAllShow;

    private View.OnClickListener onIconClickListener;

    public HeaderGradientItem(String titleStringResId, boolean isViewAllShow) {
        this.titleStringResId = titleStringResId;
        this.isViewAllShow = isViewAllShow;
        this.onIconClickListener = onIconClickListener;
    }

    @NonNull
    @Override
    public HeaderGradientItem.HeaderViewHolder createViewHolder(@NonNull View itemView) {
        return new HeaderGradientItem.HeaderViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull HeaderViewHolder viewHolder, int position) {
        viewHolder.txt_title.setText(titleStringResId);
    }

    @Override
    public int getLayout() {
        return R.layout.groupie_header_gradient;
    }


    public class HeaderViewHolder extends ViewHolder {

        @BindView(R.id.txt_title)
        TextView txt_title;

        private BannerItem.ItemClickListener clickListener;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}

