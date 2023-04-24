package com.wishbook.catalog.home.cart.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.CouponViewHolder> {


    private Context mContext;
    private ArrayList<ResponseCouponList> couponsList;
    private String cart_id;
    private String userLanguage;

    public CouponListAdapter(Context mContext, ArrayList<ResponseCouponList> couponsList, String cart_id) {
        this.mContext = mContext;
        this.couponsList = couponsList;
        this.cart_id = cart_id;
        userLanguage = LocaleHelper.getLanguage(mContext);
    }

    @NonNull
    @Override
    public CouponListAdapter.CouponViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.coupon_list_item, viewGroup, false);
        return new CouponListAdapter.CouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponListAdapter.CouponViewHolder couponViewHolder, int position) {
        ResponseCouponList coupon = couponsList.get(position);
        if (coupon.getCode() != null && !coupon.getCode().isEmpty()) {
            couponViewHolder.txt_coupon_code.setText(coupon.getCode());
        }
        if (coupon.getDisplay_text() != null ) {
            couponViewHolder.txt_coupon_details.setVisibility(View.VISIBLE);
            if(userLanguage.equalsIgnoreCase("hi") && coupon.getDisplay_text().getHi()!=null) {
                if(coupon.getDisplay_text().getHi()!=null) {
                    couponViewHolder.txt_coupon_details.setText(Html.fromHtml(coupon.getDisplay_text().getHi()), TextView.BufferType.NORMAL);
                }
            } else {
                if(coupon.getDisplay_text().getEn()!=null) {
                    couponViewHolder.txt_coupon_details.setText(Html.fromHtml(coupon.getDisplay_text().getEn()), TextView.BufferType.NORMAL);
                }
            }
        } else {
            couponViewHolder.txt_coupon_details.setVisibility(View.GONE);
        }


        if (coupon.getValid_till() != null) {
            couponViewHolder.txt_coupon_validity.setVisibility(View.VISIBLE);

            String temp ="Validity: " +"<font color='#e02b2b' >"+"" +DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1,StaticFunctions.CLIENT_COUPON_DISPLAY_FORMAT,coupon.getValid_till()) +"</font>";
            couponViewHolder.txt_coupon_validity.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
        } else {
            couponViewHolder.txt_coupon_validity.setVisibility(View.GONE);
        }


        couponViewHolder.txt_coupon_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patchCoupon(coupon.getCode());
            }
        });


    }

    @Override
    public int getItemCount() {
        return couponsList.size();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_coupon_validity)
        TextView txt_coupon_validity;

        @BindView(R.id.txt_coupon_details)
        TextView txt_coupon_details;

        @BindView(R.id.txt_coupon_apply)
        TextView txt_coupon_apply;

        @BindView(R.id.txt_coupon_code)
        TextView txt_coupon_code;


        CouponViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void patchCoupon(String coupon_code) {
        try {
            final MaterialDialog progress_dialog = StaticFunctions.showProgressDialog(mContext, "Please wait..", "Loading..", false);
            progress_dialog.show();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("wb_coupon_code", coupon_code);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext, "coupon-apply", cart_id), jsonObject, headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progress_dialog != null && progress_dialog.isShowing()) {
                        progress_dialog.dismiss();
                    }
                    ((Activity) mContext).setResult(Activity.RESULT_OK);
                    ((Activity) mContext).finish();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress_dialog != null && progress_dialog.isShowing()) {
                        progress_dialog.dismiss();
                    }
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}