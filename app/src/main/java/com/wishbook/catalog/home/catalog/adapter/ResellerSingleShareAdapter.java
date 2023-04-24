package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.ProductObj;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerSingleShareAdapter extends RecyclerView.Adapter<ResellerSingleShareAdapter.ResellerSingleViewHolder> {


    List<ProductObj> productObjs;
    Context context;
    private final int height;
    private final int width;
    String resale_margin_per  = null;

    public ResellerSingleShareAdapter(Context context, List<ProductObj> productObjs) {
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
    public ResellerSingleShareAdapter.ResellerSingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resale_share_linear_item, parent, false);
        return new ResellerSingleShareAdapter.ResellerSingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ResellerSingleShareAdapter.ResellerSingleViewHolder holder, final int position) {
        final ProductObj productObj = productObjs.get(position);
        if (productObj.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, productObj.getImage().getThumbnail_small(), holder.prod_img);
        }
        holder.chk_product.setOnCheckedChangeListener(null);
        if (productObj.isAddMarginPriceSelected()) {
            holder.chk_product.setChecked(true);
        } else {
            holder.chk_product.setChecked(false);
        }


        if(productObj.getSingle_piece_price()!=null) {
            holder.txt_product_price.setText(productObj.getSingle_piece_price());
        }

        if(resale_margin_per!=null) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.##");
            double price = ((Float.parseFloat(productObj.getSingle_piece_price()) / 100) * Float.parseFloat(resale_margin_per)) + Float.parseFloat(productObj.getSingle_piece_price());
            holder.txt_product_resale_price.setText(decimalFormat.format(price));
        }

        holder.chk_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    productObj.setAddMarginPriceSelected(true);
                } else {
                    productObj.setAddMarginPriceSelected(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productObjs.size();
    }

    public void setDefaultMarginAmt(String resale_margin_per) {
        this.resale_margin_per = resale_margin_per;
    }


    public List<ProductObj> selectedItems() {
        ArrayList<ProductObj> selected_productObjs = new ArrayList<>();
        for (ProductObj p :
                productObjs) {
            if (p.isAddMarginPriceSelected()) {
                selected_productObjs.add(p);
            }
        }
        return selected_productObjs;
    }

    public class ResellerSingleViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.chk_product)
        CheckBox chk_product;

        @BindView(R.id.txt_design_name)
        TextView txt_design_name;

        @BindView(R.id.txt_product_price)
        TextView txt_product_price;

        @BindView(R.id.txt_product_resale_price)
        TextView txt_product_resale_price;

        public ResellerSingleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}