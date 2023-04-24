package com.wishbook.catalog.Utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.commonmodels.Response_catalogapp;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;

/**
 * Created by vignesh_streamoid on 14/05/16.
 */
public class WhatsappAdapterChoose extends  RecyclerView.Adapter<WhatsappAdapterChoose.ViewHolder>{

    private  int height=800;
    private  int width=480;
    private final AppCompatActivity context;
    private final List itemsInSection;

    ArrayList<Selections> selection=new ArrayList<>();


    public WhatsappAdapterChoose(AppCompatActivity context, ArrayList itemsInSection) {
        this.context=context;
        this.itemsInSection=itemsInSection;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            this.width = size.x;
            this.height = size.y;

            for (int i=0;i<itemsInSection.size();i++){
                selection.add(new Selections(i,false));
            }
        }
        catch (Exception e){

        }

    }

    @Override
    public WhatsappAdapterChoose.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_items_itemwhats, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WhatsappAdapterChoose.ViewHolder holder, final int position) {
        if (!selection.get(position).isSelected) {
            selection.get(position).setIsSelected(false);
            holder.sele.setVisibility(View.GONE);
        } else {
            selection.get(position).setIsSelected(true);
            holder.sele.setVisibility(View.VISIBLE);

        }
        holder.sele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection.get(position).setIsSelected(false);
                holder.sele.setVisibility(View.GONE);
            }
        });

        try {
            // Picasso.with(context).load(itemsInSection.get(position)).into(holder.thumb_img);
            final Object object=itemsInSection.get(position);

            holder.All_item_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getselectionCount(selection)>=30){
                        Toast.makeText(context, "Maximum selected !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selection.get(position).isSelected) {
                        selection.get(position).setIsSelected(false);
                        holder.sele.setVisibility(View.GONE);
                    } else {
                        selection.get(position).setIsSelected(true);
                        holder.sele.setVisibility(View.VISIBLE);
                    }
                    if (object instanceof Response_Brands) {
                        Bundle bundle = new Bundle();
                        bundle.putString("filtertype", "brand");
                        bundle.putString("filtervalue", ((Response_Brands) itemsInSection.get(position)).getId());
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentCatalogs).addToBackStack(null).commit();

                    }
                    if (object instanceof CatalogMinified) {
                        Application_Singleton.selectedshareCatalog = (CatalogMinified) itemsInSection.get(position);
                        context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_CatalogsGallery()).addToBackStack(null).commit();


                        //     Application_Singleton.selectedCatalogProducts = ((Response_catalog)itemsInSection.get(position)).getProducts();
                        //   context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_ProductsInCatalog()).addToBackStack(null).commit();

                    }

                    if (object instanceof Response_Selection) {
                        DataPasser.selectedID = ((Response_Selection) itemsInSection.get(position)).getId();
                        context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_ProductsInSelection()).addToBackStack(null).commit();

                    }
                    if (object instanceof Response_ShareStatus) {

                    }
                }
            });
            if(object instanceof Response_Brands){
                ImageLoader.getInstance().displayImage(((Response_Brands) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });

            }
            if(object instanceof Response_catalogapp){
                ImageLoader.getInstance().displayImage(((Response_catalogapp) itemsInSection.get(position)).getThumbnail().getThumbnail_small(), holder.All_item_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });

            }

            if(object instanceof Response_Selection){
                ImageLoader.getInstance().displayImage(((Response_Selection) itemsInSection.get(position)).getImage(), holder.All_item_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });

            }
            if(object instanceof Response_ShareStatus){
                ImageLoader.getInstance().displayImage(((Response_ShareStatus)itemsInSection.get(position)).getImage(),holder.All_item_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                });
            }
            if(object instanceof ProductObj){
                ImageLoader.getInstance().displayImage(((ProductObj)itemsInSection.get(position)).getImage().getThumbnail_medium(),holder.All_item_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                });
            }
        }catch (Exception e){

        }

    }

    private int getselectionCount(ArrayList<Selections> selection) {
        int i=0;
        for(Selections se:selection){
            if(se.isSelected){
                i++;
            }
        }
        return i;
    }
    private ArrayList<Integer> getselections(ArrayList<Selections> selection) {
        ArrayList<Integer> integers=new ArrayList<>();
        for(Selections se:selection){
            if(se.isSelected){
                integers.add(se.getPosition());
            }
        }
        return integers;
    }
    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public ArrayList getAllSelected() {
        ArrayList itemsselected=new ArrayList();
        for(Integer i:getselections(selection)){
            itemsselected.add(itemsInSection.get(i));
        }
        return itemsselected;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout itemcontainer;
        private final ImageView All_item_img;
        private final LinearLayout sele;

        public ViewHolder(View itemView) {
            super(itemView);
            itemcontainer=(FrameLayout)itemView.findViewById(R.id.itemcontainer);
            sele=(LinearLayout)itemView.findViewById(R.id.sele);
            All_item_img=(ImageView)itemView.findViewById(R.id.item_img);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Math.round(width/3.1f),Math.round(height/4.2f));
            itemcontainer.setLayoutParams(lp);
        }
    }


    public class Selections{
        int position;
        boolean isSelected;

        public Selections(int position, boolean isSelected) {
            this.position = position;
            this.isSelected = isSelected;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }
}