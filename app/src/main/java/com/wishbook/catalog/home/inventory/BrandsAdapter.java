package com.wishbook.catalog.home.inventory;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.home.inventory.inwardstock.Fragment_Inward_Stock;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 29/11/16.
 */
public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<BrandsModel> brands=new ArrayList<>();
    private ArrayList<CatalogsModel> catalogs =new ArrayList<>();
   private Activity context;
    private CatalogsAdapter catalogsAdapter;
    private Boolean AllInventory = false;
    private Activity acitivty;
    private Fragment_Inward_Stock fragment_inward_stock;




    public BrandsAdapter(Activity context, ArrayList<BrandsModel> itemList, Boolean AllInventory, Fragment_Inward_Stock fragment_inward_stock) {
        mInflater = LayoutInflater.from(context);
        brands=itemList;
        this.context=context;
        this.AllInventory=AllInventory;
        this.fragment_inward_stock=fragment_inward_stock;
    }





    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.brand_list_item, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyParentViewHolder holder, int position) {
        holder.brand_name.setText(brands.get(position).getTitle());
        if(brands.get(position).getExpanded()==null){
            brands.get(position).setExpanded(false);
        }

        holder.brand_container_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(brands.get(holder.getAdapterPosition()).getExpanded()) {
                    brands.get(holder.getAdapterPosition()).setExpanded(false);
                    holder.catalog_layout.setVisibility(View.GONE);
                    holder.arrow.setRotation(0);
                }
                else
                {
                    holder.arrow.setRotation(90);
                    brands.get(holder.getAdapterPosition()).setExpanded(true);
                    for(int i=0;i<brands.get(holder.getAdapterPosition()).getCatalogs().size();i++)
                    {
                        brands.get(holder.getAdapterPosition()).getCatalogs().get(i).setExpanded(false);
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    catalogsAdapter = new CatalogsAdapter(context, brands.get(holder.getAdapterPosition()).getCatalogs(),brands,AllInventory,fragment_inward_stock);
                    holder.recyclerView.setAdapter(new AlphaInAnimationAdapter(catalogsAdapter));
                    holder.catalog_layout.setVisibility(View.VISIBLE);
                    holder.recyclerView.addItemDecoration(new DividerItemDecoration(context, null));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }


    public class MyParentViewHolder extends ParentViewHolder {

        private TextView brand_name;
        private ImageView arrow;
        private RecyclerView recyclerView;
        private RelativeLayout catalog_layout;
        private RelativeLayout brand_container_main;

        public MyParentViewHolder(View itemView) {
            super(itemView);

            brand_name = (TextView) itemView.findViewById(R.id.brand_name);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            catalog_layout = (RelativeLayout) itemView.findViewById(R.id.catalog_layout);
            brand_container_main = (RelativeLayout) itemView.findViewById(R.id.brand_container_main);

        }

        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);

          /*  if (expanded) {
                image.setImageResource(R.drawable.collapse);
            } else {
                image.setImageResource(R.drawable.expand);
            }*/
            if (expanded) {
                arrow.setRotation(90);
            } else {
                arrow.setRotation(0);
            }

        }
    }

}
