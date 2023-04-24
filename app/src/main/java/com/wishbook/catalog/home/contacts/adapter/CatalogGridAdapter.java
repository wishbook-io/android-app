package com.wishbook.catalog.home.contacts.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;


public class CatalogGridAdapter extends RecyclerView.Adapter<CatalogGridAdapter.ViewHolder> {

    private int height = 800;
    private int width = 480;

    private ArrayList<Response_catalogMini> catalogMinis;
    private Context context;

    public CatalogGridAdapter(ArrayList<Response_catalogMini> catalogMinis, Context context) {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            this.width = size.x;
            this.height = size.y;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.catalogMinis = catalogMinis;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_items_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Response_catalogMini response_catalogMini = catalogMinis.get(position);
        if(catalogMinis.get(position).getImage()!=null
                && catalogMinis.get(position).getImage().getThumbnail_medium()!=null
                && !catalogMinis.get(position).getImage().getThumbnail_medium().isEmpty()){
            StaticFunctions.loadFresco(context,catalogMinis.get(position).getImage().getThumbnail_medium(),holder.All_item_img);
        }


       /* ImageLoader.getInstance().displayImage(catalogMinis.get(position).getThumbnail().getThumbnail_medium(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
        });*/

        holder.All_item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString("from","Product List");
                bundle.putString("product_id",response_catalogMini.getId());
                new NavigationUtils().navigateDetailPage(context,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalogMinis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout itemcontainer;
        private final SimpleDraweeView All_item_img;
        private AppCompatButton btn_add_to_cart,btn_add_images;
        private TextView txt_single_pc_price,txt_available_sizes;

        public ViewHolder(View itemView) {
            super(itemView);
            itemcontainer = (FrameLayout) itemView.findViewById(R.id.itemcontainer);
            All_item_img = (SimpleDraweeView) itemView.findViewById(R.id.item_img);
            btn_add_to_cart = itemView.findViewById(R.id.btn_add_to_cart);
            btn_add_images = itemView.findViewById(R.id.btn_add_images);
            txt_available_sizes = itemView.findViewById(R.id.txt_available_sizes);
            txt_single_pc_price = itemView.findViewById(R.id.txt_single_pc_price);


            btn_add_to_cart.setVisibility(View.GONE);
            btn_add_images.setVisibility(View.GONE);

            txt_available_sizes.setVisibility(View.GONE);
            txt_single_pc_price.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.2f));
            itemcontainer.setLayoutParams(lp);
        }
    }
}
