package com.wishbook.catalog.home.adapters;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QualityCatalogGridAdapter extends RecyclerView.Adapter<QualityCatalogGridAdapter.CustomViewHolder> {


    private Context context;
    private ArrayList<Response_catalogMini> response_catalogMinis;
    private final int height;
    private final int width;

    public QualityCatalogGridAdapter(Context context, ArrayList<Response_catalogMini> response_catalogMinis) {
        this.context = context;
        this.response_catalogMinis = response_catalogMinis;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    @Override
    public QualityCatalogGridAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_quality_grid_item, parent, false);
        return new QualityCatalogGridAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QualityCatalogGridAdapter.CustomViewHolder holder, final int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Math.round(height / 3.2f));
        holder.linear_root_container.setLayoutParams(lp);

        if (response_catalogMinis.get(position).getImage() != null
                && response_catalogMinis.get(position).getImage().getThumbnail_medium() != null) {
            StaticFunctions.loadFresco(context, response_catalogMinis.get(position).getImage().getThumbnail_medium(), holder.img_catalog);
        }
        holder.txt_catalog_name.setText(response_catalogMinis.get(position).getTitle());
    }


    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_catalog)
        SimpleDraweeView img_catalog;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.txt_catalog_price)
        TextView txt_catalog_price;
        @BindView(R.id.linear_root_container)
        LinearLayout linear_root_container;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}