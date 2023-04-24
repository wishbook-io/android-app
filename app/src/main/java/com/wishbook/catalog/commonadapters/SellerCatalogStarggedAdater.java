package com.wishbook.catalog.commonadapters;


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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerCatalogStarggedAdater extends RecyclerView.Adapter<SellerCatalogStarggedAdater.CustomViewHolder> {


    private Context context;
    private ArrayList<Response_catalogMini> response_catalogMinis;
    private final int height;
    private final int width;
    private ArrayList<Response_catalogMini> checkedArray  = new ArrayList<>();
    private UpdateCatalogListener updateListener;

    public SellerCatalogStarggedAdater(Context context, ArrayList<Response_catalogMini> response_catalogMinis) {
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
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_staragged_item, parent, false);
        return new SellerCatalogStarggedAdater.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,Math.round(height/3.2f));
        holder.itemcontainer.setLayoutParams(lp);

        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.itemcontainer.setLayoutParams(lp);*/
        StaticFunctions.loadFresco(context, response_catalogMinis.get(position).getThumbnail().getThumbnail_medium(), holder.prod_img);
        holder.txt_catalogname.setText(response_catalogMinis.get(position).getTitle());
        holder.txt_catalog_price.setText("\u20B9"+response_catalogMinis.get(position).getPrice_range()+"/Pc.");
        holder.check_catalog_id.setOnCheckedChangeListener(null);

        if(checkedArray.contains(response_catalogMinis.get(position))){
            holder.check_catalog_id.setChecked(true);
        }
        else
        {
            holder.check_catalog_id.setChecked(false);
        }

        holder.check_catalog_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkedArray.add(response_catalogMinis.get(position));
                } else {
                    if (checkedArray.contains(response_catalogMinis.get(position))) {
                        checkedArray.remove(response_catalogMinis.get(position));
                    }
                }
                notifyDataSetChanged();
                if(updateListener!=null)
                    updateListener.getTotalSelected(checkedArray.size());

            }
        });
        holder.itemcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.check_catalog_id.isChecked()) {
                    holder.check_catalog_id.setChecked(false);
                } else {
                    holder.check_catalog_id.setChecked(true);
                }
            }
        });
    }

    public void setSelectedListener (UpdateCatalogListener updateListener){
        this.updateListener = updateListener;
    }

    public interface UpdateCatalogListener{

        void getTotalSelected(int count);
    }

    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }

    public ArrayList<Response_catalogMini> getSelectedCatalog() {
        return checkedArray;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;
        @BindView(R.id.txt_catalogname)
        TextView txt_catalogname;
        @BindView(R.id.txt_catalog_price)
        TextView txt_catalog_price;
        @BindView(R.id.check_catalog_id)
        CheckBox check_catalog_id;

        @BindView(R.id.itemcontainer)
        LinearLayout itemcontainer;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
