package com.wishbook.catalog.home.orders;


import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;

import butterknife.ButterKnife;

public class Fragment_Order_Holder_Version2 extends GATrackedFragment implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    Toolbar toolbar;
    CollapsingToolbarLayout toolbar1;

    private View v;
   /* @BindView(R.id.linear_purchase_order)
    LinearLayout linear_purchase_order;

    @BindView(R.id.linear_sales_order)
    LinearLayout linear_sales_order;

    @BindView(R.id.linear_brokerage_order)
    LinearLayout linear_brokerage_order;*/

    public Fragment_Order_Holder_Version2() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order_version5, container, false);
        ButterKnife.bind(this,v);

     toolbar  = (Toolbar) v.findViewById(R.id.flexible_example_toolbar);
        toolbar1  = (CollapsingToolbarLayout) v.findViewById(R.id.flexible_example_collapsing);

        AppBarLayout appbar = (AppBarLayout) v.findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);
        initView();
        return v;
    }

    public void initView() {
        /*linear_purchase_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ActivityOrderHolder.class).putExtra("type","purchase"));
            }
        });

        linear_sales_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ActivityOrderHolder.class).putExtra("type","sale"));
            }
        });

        linear_brokerage_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ActivityOrderHolder.class).putExtra("type","broker"));
            }
        });*/

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;

            }
        }
    }
}
