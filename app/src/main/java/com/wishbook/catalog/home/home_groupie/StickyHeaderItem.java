package com.wishbook.catalog.home.home_groupie;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.home.Fragment_Home2;
import com.wishbook.catalog.home.home_groupie.decoration.StickyItem;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StickyHeaderItem extends Item<StickyHeaderItem.HeaderViewHolder> implements StickyItem {

    Context context;
    Fragment fragment;
    HeaderViewHolder headerViewHolder;
    HashMap<String, String> paramsClone;

    public StickyHeaderItem(Context context, Fragment fragment, HashMap<String, String> paramsClone) {
        this.context = context;
        this.fragment = fragment;
        this.paramsClone = paramsClone;
    }

    @NonNull
    @Override
    public StickyHeaderItem.HeaderViewHolder createViewHolder(@NonNull View itemView) {
        headerViewHolder = new StickyHeaderItem.HeaderViewHolder(itemView);
        return headerViewHolder;
    }

    @Override
    public void bind(@NonNull StickyHeaderItem.HeaderViewHolder viewHolder, int position) {
        viewHolder.badge_filter_count.setBadgeCount(0, true);
        if (paramsClone != null) {
            viewHolder.badge_filter_count.setBadgeCount(getFilterCount(paramsClone));
        }
        viewHolder.linear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof Fragment_Home2) {
                    ((Fragment_Home2) fragment).openFilterScreen();
                }
            }
        });
    }


    @Override
    public int getLayout() {
        return R.layout.home_filter_bar;
    }

    public int getFilterCount(HashMap<String, String> params) {
        int filtercount = 0;
        if (params.size() > 0) {
            // to ignore for filter count
            filtercount = params.size();
            if (params.containsKey("view_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ctype")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("limit")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("offset")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ordering")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("min_price") && params.containsKey("max_price")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("saved_filter_id")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("catalog_type")) {
                filtercount = filtercount - 1;
            }


            if (params.containsKey("product_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("from")) {
                filtercount = filtercount - 1;
            }


            if (params.containsKey("product_availability")) {
                filtercount = filtercount - 1;
            }

            return filtercount;
        }

        return filtercount;
    }

    @Override
    public boolean isSticky() {
        return true;
    }


    public class HeaderViewHolder extends ViewHolder {

        @BindView(R.id.linear_filter)
        LinearLayout linear_filter;

        @BindView(R.id.badge_filter_count)
        MaterialBadgeTextView badge_filter_count;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}