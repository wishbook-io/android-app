package com.wishbook.catalog.home.inventory;

import android.content.Context;
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
import com.wishbook.catalog.commonmodels.postpatchmodels.InventoryAddStock;
import com.wishbook.catalog.home.inventory.inwardstock.Inventory_Adapter_Stock;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 29/11/16.
 */
public class CatalogsAdapter  extends RecyclerView.Adapter<CatalogsAdapter.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<BrandsModel> brandsModels = new ArrayList<>();
    private ArrayList<CatalogsModel> catalogs =new ArrayList<>();
    private ArrayList<InventoryAddStock> products =new ArrayList<>();
    private Context context;
    private Inventory_Adapter_Stock productAdapter;
    private Boolean AllInventory = false;
    private Fragment_Inward_Stock fragment_inward_stock;


    public CatalogsAdapter(Context context, ArrayList<CatalogsModel> itemList, ArrayList<BrandsModel> brandsModels, Boolean AllInventory, Fragment_Inward_Stock fragment_inward_stock) {
        mInflater = LayoutInflater.from(context);
        catalogs=itemList;
        this.brandsModels=brandsModels;
        this.context=context;
        this.brandsModels=brandsModels;
        this.AllInventory=AllInventory;
        this.fragment_inward_stock=fragment_inward_stock;
    }


    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.catalog_list_item, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyParentViewHolder holder, final int position) {
        holder.catalog_name.setText(catalogs.get(position).getTitle());
        holder.catalog_container_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(catalogs.get(position).getExpanded()) {
                    catalogs.get(position).setExpanded(false);
                    holder.product_layout.setVisibility(View.GONE);
                    holder.arrow.setRotation(0);
                }
                else
                {
                    holder.arrow.setRotation(90);
                    catalogs.get(position).setExpanded(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    productAdapter = new Inventory_Adapter_Stock(context, catalogs.get(position).getProducts(),catalogs,brandsModels,AllInventory,fragment_inward_stock);
                    holder.recyclerView.setAdapter(new AlphaInAnimationAdapter(productAdapter));
                    holder.recyclerView.addItemDecoration(new DividerItemDecoration(context, null));
                    holder.product_layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalogs.size();
    }


    public class MyParentViewHolder extends ParentViewHolder {

        private TextView catalog_name;
        private ImageView arrow;
        private RecyclerView recyclerView;
        private RelativeLayout product_layout;
        private RelativeLayout catalog_container_main;
        public MyParentViewHolder(View itemView) {
            super(itemView);

            catalog_name = (TextView) itemView.findViewById(R.id.catalog_name);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            product_layout = (RelativeLayout) itemView.findViewById(R.id.product_layout);
            catalog_container_main = (RelativeLayout) itemView.findViewById(R.id.catalog_container_main);

        }

    }

}
