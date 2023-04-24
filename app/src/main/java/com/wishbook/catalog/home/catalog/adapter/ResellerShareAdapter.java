package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerShareAdapter extends RecyclerView.Adapter<ResellerShareAdapter.ResellerGridViewHolder> {


    List<ProductObj> productObjs;
    Context context;
    private final int height;
    private final int width;
    boolean isSingleAvailable;
    ProductSelectionListener productSelectionListener;

    public ResellerShareAdapter(Context context, List<ProductObj> productObjs, boolean isSingleAvailable) {
        this.productObjs = productObjs;
        this.context = context;
        this.isSingleAvailable = isSingleAvailable;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    @Override
    public ResellerShareAdapter.ResellerGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_product_item, parent, false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.7f));
        view.findViewById(R.id.relative_main).setLayoutParams(lp);
        return new ResellerShareAdapter.ResellerGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ResellerShareAdapter.ResellerGridViewHolder customViewHolder, final int position) {
        final ProductObj productObj = productObjs.get(position);
        if (productObj.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, productObj.getImage().getThumbnail_small(), customViewHolder.prod_img);
        }

        if (isSingleAvailable) {
            if (productObj.isChecked()) {
                customViewHolder.frame_selected.setVisibility(View.VISIBLE);
            } else {
                customViewHolder.frame_selected.setVisibility(View.GONE);
            }
            customViewHolder.relative_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (productObj.isChecked()) {
                        productObj.setChecked(false);
                        customViewHolder.frame_selected.setVisibility(View.GONE);
                        if (productSelectionListener != null) {
                            productSelectionListener.selectionChanged();
                        }
                    } else {
                        productObj.setChecked(true);
                        customViewHolder.frame_selected.setVisibility(View.VISIBLE);
                        if (productSelectionListener != null) {
                            productSelectionListener.selectionChanged();
                        }
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productObjs.size();
    }


    public ProductObj[] getSelectedItems() {
        ArrayList<ProductObj> productObjs_selected = new ArrayList<>();
        for (ProductObj p :
                this.productObjs) {
            if (p.isChecked()) {
                productObjs_selected.add(p);
            }
        }
        return productObjs_selected.toArray(new ProductObj[productObjs_selected.size()]);
    }

    public interface ProductSelectionListener {
        void selectionChanged();
    }

    public void setProductSelectionListener(ProductSelectionListener productSelectionListener) {
        this.productSelectionListener = productSelectionListener;
    }

    public class ResellerGridViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.relative_main)
        RelativeLayout relative_main;

        @BindView(R.id.frame_selected)
        FrameLayout frame_selected;

        public ResellerGridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
