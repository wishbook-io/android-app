package com.wishbook.catalog.home.myBusiness;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wishbook.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomPayoutDetails extends BottomSheetDialogFragment {

    @BindView(R.id.linear_toolbar)
    LinearLayout linear_toolbar;

    @BindView(R.id.img_close)
    ImageView img_close;


    public static BottomPayoutDetails newInstance(Bundle bundle) {
        BottomPayoutDetails f = new BottomPayoutDetails();
        if(bundle!=null)
            f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.seller_payout_detail_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

}
