package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;
import java.util.HashMap;

public class NonCatalogItemAdapter extends RecyclerView.Adapter<NonCatalogItemAdapter.ViewHolder> {

    private int height;
    private int width;
    private Context context;
    private int numberofGridSize;
    private String sectionTitle;
    private ArrayList<Response_catalogMini> itemData;
    private HashMap<String,String> hashMap;


    public NonCatalogItemAdapter(Context context, ArrayList itemData, int numberofGridSize, String headerTitle,HashMap<String,String> hashMap) {
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.numberofGridSize = numberofGridSize;
        this.sectionTitle = headerTitle;
        this.itemData = itemData;
        this.hashMap = hashMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_items_item_new, parent, false);
        return new NonCatalogItemAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (numberofGridSize == 3) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.2f));
            holder.itemcontainer.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 2.1f), Math.round(height / 3.2f));
            holder.itemcontainer.setLayoutParams(lp);
        }

        if(itemData.get(position).getImage()!=null)
            StaticFunctions.loadFresco(context, itemData.get(position).getImage().getThumbnail_small(), holder.thumb_img);

        holder.thumb_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params = hashMap;
                params.put("focus_position", String.valueOf(holder.getAdapterPosition()));
                new DeepLinkFunction(params, context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout itemcontainer;
        public final SimpleDraweeView thumb_img;
        public TextView txt_sold_by;

        public ViewHolder(View itemView) {
            super(itemView);
            itemcontainer = (RelativeLayout) itemView.findViewById(R.id.itemcontainer);
            thumb_img = (SimpleDraweeView) itemView.findViewById(R.id.item_img);
        }
    }
}
