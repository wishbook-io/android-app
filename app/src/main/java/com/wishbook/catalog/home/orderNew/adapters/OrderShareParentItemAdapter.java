package com.wishbook.catalog.home.orderNew.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderShareParentItemAdapter extends RecyclerView.Adapter<OrderShareParentItemAdapter.CustomViewHolder> {


    ArrayList<Response_sellingoder_catalog> catalogListItems;
    Context context;

    public OrderShareParentItemAdapter(Context context, ArrayList<Response_sellingoder_catalog> catalogListItems) {
        this.catalogListItems = catalogListItems;
        this.context = context;
    }

    @Override
    public OrderShareParentItemAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_share_catalogwise, parent, false);
        return new OrderShareParentItemAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderShareParentItemAdapter.CustomViewHolder customViewHolder, final int position) {
        final Response_sellingoder_catalog catalog = catalogListItems.get(position);
        customViewHolder.txt_catalog_name.setText(catalog.getName());
        customViewHolder.recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        customViewHolder.recycler_view.setHasFixedSize(true);
        customViewHolder.recycler_view.setNestedScrollingEnabled(false);
        OrderShareItemSelectionAdapter orderShareItemSelectionAdapter = new OrderShareItemSelectionAdapter(context, (ArrayList<Response_Product>) catalog.getProducts());
        customViewHolder.recycler_view.setAdapter(orderShareItemSelectionAdapter);

        customViewHolder.txt_see_all.setPaintFlags(customViewHolder.txt_see_all.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        customViewHolder.txt_see_non.setPaintFlags(customViewHolder.txt_see_all.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        customViewHolder.txt_share_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Response_Product> temp = ((OrderShareItemSelectionAdapter) customViewHolder.recycler_view.getAdapter()).selectedItems();
                if (temp.size() > 0) {
                    try {
                        ProductObj[] productObjs = new ProductObj[temp.size()];
                        for (int i = 0; i < temp.size(); i++) {
                            ProductObj productObj = new ProductObj();
                            productObj.setImage(new ThumbnailObj(temp.get(i).getProduct_image(), temp.get(i).getProduct_image(), temp.get(i).getProduct_image()));
                            productObjs[i] = productObj;
                        }
                        ShareImageDownloadUtils.shareProducts((AppCompatActivity) context,null,
                                productObjs, StaticFunctions.SHARETYPE.OTHER, "",catalog.getName(),
                                false,false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Please select any one item to share", Toast.LENGTH_SHORT).show();
                }

            }
        });

        customViewHolder.txt_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll(position);
            }
        });

        customViewHolder.txt_see_non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNone(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return catalogListItems.size();
    }

    public void selectAll(int position) {
        for (Response_Product p :
                catalogListItems.get(position).getProducts()) {
            p.setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void selectNone(int position) {
        for (Response_Product p :
                catalogListItems.get(position).getProducts()) {
            p.setChecked(false);
        }
        notifyDataSetChanged();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.recycler_view)
        RecyclerView recycler_view;

        @BindView(R.id.txt_share_order)
        TextView txt_share_order;

        @BindView(R.id.txt_see_all)
        TextView txt_see_all;

        @BindView(R.id.txt_see_non)
        TextView txt_see_non;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
