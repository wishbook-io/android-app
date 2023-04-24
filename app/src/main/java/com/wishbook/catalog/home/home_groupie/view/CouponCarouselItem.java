package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;
import com.xwray.groupie.Item;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponCarouselItem extends Item<CouponCarouselItem.CustomViewHolder> {

    ResponseCouponList data;
    Context mContext;
    Fragment fragment;
    CouponCarouselItem.OnSeeMoreClickListener onSeeMoreClickListener;
    DecimalFormat decimalFormat;


    public CouponCarouselItem(ResponseCouponList data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
        decimalFormat = new DecimalFormat("#.##");
    }


    @NonNull
    @Override
    public CouponCarouselItem.CustomViewHolder createViewHolder(@NonNull View itemView) {
        return new CouponCarouselItem.CustomViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull CustomViewHolder viewHolder, int position) {
        if (data.getDiscount_type() != null && data.getDiscount_type().equalsIgnoreCase("Fixed")) {
            if (data.getDiscount_amount() != null && !data.getDiscount_amount().isEmpty()) {
                viewHolder.txt_off.setText("\u20B9"+decimalFormat.format(Double.parseDouble(data.getDiscount_amount())) +"\noff");
            }
        } else if (data.getDiscount_type() != null && data.getDiscount_type().equalsIgnoreCase("Percentage")) {
            if (data.getDiscount_percentage() != null && !data.getDiscount_percentage().isEmpty()) {
                viewHolder.txt_off.setText(decimalFormat.format( Double.parseDouble(data.getDiscount_percentage())) +"%\n"+ "Off");
            }
        }

        if (data.getCode() != null) {
            viewHolder.txt_coupon_code.setText("Use Code " + data.getCode());
        }

        if (data.getValid_till() != null) {
            viewHolder.txt_expire_date.setText("Expire on " + DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, data.getValid_till()));
        }


        viewHolder.coupon_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NavigationUtils().navigateMyCart(mContext);
            }
        });

    }


    @Override
    public int getLayout() {
        return R.layout.coupon_horizontal_list_item;
    }


    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    public void setOnSeeMoreClickListener(CouponCarouselItem.OnSeeMoreClickListener onSeeMoreClickListener) {
        this.onSeeMoreClickListener = onSeeMoreClickListener;
    }

    public class CustomViewHolder extends com.xwray.groupie.ViewHolder {

        @BindView(R.id.txt_coupon_code)
        TextView txt_coupon_code;

        @BindView(R.id.txt_expire_date)
        TextView txt_expire_date;

        @BindView(R.id.txt_off)
        TextView txt_off;

        @BindView(R.id.coupon_card)
        CardView coupon_card;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}