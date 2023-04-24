package com.wishbook.catalog.home.orderNew.adapters;

import android.content.Context;
import android.graphics.Point;

import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderShareItemSelectionAdapter extends RecyclerView.Adapter<OrderShareItemSelectionAdapter.CustomViewHolder> {


    ArrayList<Response_Product> productObjs;
    Context context;
    private final int height;
    private final int width;

    public OrderShareItemSelectionAdapter(Context context, ArrayList<Response_Product> productObjs) {
        this.productObjs = productObjs;
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    @Override
    public OrderShareItemSelectionAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_product_item, parent, false);
        return new OrderShareItemSelectionAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderShareItemSelectionAdapter.CustomViewHolder customViewHolder, final int position) {
        final Response_Product productObj = productObjs.get(position);
        if (productObj.getProduct_image() != null) {
            StaticFunctions.loadFresco(context, productObj.getProduct_image(), customViewHolder.prod_img);
        }
  /*      customViewHolder.txt_product_name.setText(productObj.getProduct_title().toString());

        *//**
         * Default State
         *//*

        if (productObj.isChecked()) {
            customViewHolder.relative_main.setAlpha(0.5f);
            customViewHolder.relative_selected.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.relative_main.setAlpha(1f);
            customViewHolder.relative_selected.setVisibility(View.GONE);
        }


        customViewHolder.relative_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productObj.isChecked()) {
                    productObj.setChecked(false);
                    customViewHolder.relative_main.setAlpha(1f);
                    customViewHolder.relative_selected.setVisibility(View.GONE);
                } else {
                    productObj.setChecked(true);
                    customViewHolder.relative_main.setAlpha(0.5f);
                    customViewHolder.relative_selected.setVisibility(View.VISIBLE);
                }

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return productObjs.size();
    }


    public ArrayList<Response_Product> selectedItems() {
        ArrayList<Response_Product> selected_productObjs = new ArrayList<>();
        for (Response_Product p :
                productObjs) {
            if (p.isChecked()) {
                selected_productObjs.add(p);
            }
        }
        return selected_productObjs;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;



        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
