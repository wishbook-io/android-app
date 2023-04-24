package com.wishbook.catalog.home.cart;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartProductViewModel;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CartProductView extends AppCompatActivity {


    @BindView(R.id.prod_price)
    AppCompatTextView prod_price;

    @BindView(R.id.sku_lable)
    AppCompatTextView sku_lable;

    @BindView(R.id.fabric)
    AppCompatTextView fabric;

    @BindView(R.id.work)
    AppCompatTextView work;

    @BindView(R.id.prod_img)
    ZoomableDraweeView prod_img;

    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.container)
    LinearLayout container;


    @BindView(R.id.container_product_work)
    LinearLayout container_product_work;

    @BindView(R.id.container_product_fabric)
    LinearLayout container_product_fabric;

    @BindView(R.id.container_product_sku)
    LinearLayout container_product_sku;

    @BindView(R.id.relative_progress)
    RelativeLayout relative_progress;

    @BindView(R.id.descontent)
    RelativeLayout descontent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_product_view);
        ButterKnife.bind(this);

        getData();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartProductView.this.finish();
            }
        });
        final boolean[] view_flag = {false};
        final GestureDetector gestureDetector = new GestureDetector(CartProductView.this, new CartProductView.SingleTapConfirm());

        prod_img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (gestureDetector.onTouchEvent(arg1)) {

                    if (descontent.getVisibility() == View.GONE) {

                        descontent.setVisibility(View.VISIBLE);

                    } else {
                        descontent.setVisibility(View.GONE);
                    }
                    return true;
                } else {

                }

                return false;
            }

        });

        prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view_flag[0]) {
                    back.setVisibility(View.INVISIBLE);
                } else {
                    back.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getData() {
        try {
            relative_progress.setVisibility(View.VISIBLE);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(CartProductView.this);
            HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "productsonlywithoutcatalog", "") + getIntent().getStringExtra("id") + "/", null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        CartProductViewModel response_product = Application_Singleton.gson.fromJson(response, CartProductViewModel.class);
                        if (response_product != null) {
                            relative_progress.setVisibility(View.GONE);
                            ImageUtils.loadFrescoZoombable(CartProductView.this, response_product.getImage().getFull_size(), prod_img);
                            if (response_product.getPrice() != null) {
                                prod_price.setText("\u20B9 " + response_product.getPrice());
                            } else {
                                prod_price.setVisibility(View.GONE);
                            }
                            if (response_product.getSku() != null   && !response_product.getSku().equals("null") && !response_product.getSku().isEmpty()) {
                                sku_lable.setText("Design no: " + response_product.getSku());
                            } else {
                                container_product_sku.setVisibility(View.GONE);
                            }
                            if (response_product.getFabric() != null && !response_product.getFabric().equals("null") && !response_product.getFabric().isEmpty()) {
                                fabric.setText("Fabric: " + response_product.getFabric());
                            } else {
                                container_product_fabric.setVisibility(View.GONE);
                            }
                            if (response_product.getWork() != null && !response_product.getWork().equals("null") && !response_product.getWork().isEmpty()) {
                                work.setText("Work: " + response_product.getWork());
                            } else {
                                container_product_work.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
    }
}