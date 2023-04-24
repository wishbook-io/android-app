package com.wishbook.catalog.home.myBusiness;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_SellerPayout extends GATrackedFragment {

    @BindView(R.id.linear_history)
    LinearLayout linear_history;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_payout, container, false);
        ButterKnife.bind(this, view);

        if(getActivity() instanceof OpenContainer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((OpenContainer) getActivity()).toolbar.setElevation(0);
            }
        }

        linear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomPayoutDetails bottomPayoutDetails =  BottomPayoutDetails.newInstance(null);
                bottomPayoutDetails.show(getActivity().getSupportFragmentManager(),"PayoutDetail");
            }
        });
        return view;
    }
}
