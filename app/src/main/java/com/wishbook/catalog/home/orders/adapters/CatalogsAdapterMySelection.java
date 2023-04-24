package com.wishbook.catalog.home.orders.adapters;

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
import com.wishbook.catalog.home.inventory.BrandsModel;
import com.wishbook.catalog.home.inventory.CatalogsModel;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 30/1/17.
 */

/**
 * Created by root on 29/11/16.
 */
public class CatalogsAdapterMySelection extends RecyclerView.Adapter<CatalogsAdapterMySelection.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<BrandsModel> brandsModels = new ArrayList<>();
    private ArrayList<CatalogsModel> catalogs =new ArrayList<>();
   // private ProductsSelection[] products =new ProductsSelection[]{};
    private Context context;
    public static ProductsMySelectionAdapter productAdapter;
    private BrandAdapterMySelection.ProductChangeListener productChangeListener;


    public CatalogsAdapterMySelection(Context context, ArrayList<CatalogsModel> itemList, ArrayList<BrandsModel> brandsModels, BrandAdapterMySelection.ProductChangeListener productChangeListener) {
        mInflater = LayoutInflater.from(context);
        catalogs=itemList;
        this.brandsModels=brandsModels;
        this.context=context;
        this.brandsModels=brandsModels;
        this.productChangeListener = productChangeListener;
    }



    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.catalog_list_item_selection, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyParentViewHolder holder, int position) {
        holder.catalog_name.setText(catalogs.get(position).getTitle());
        holder.catalog_container_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(catalogs.get(holder.getAdapterPosition()).getExpanded()) {
                    catalogs.get(holder.getAdapterPosition()).setExpanded(false);
                    holder.product_layout.setVisibility(View.GONE);
                    holder.arrow.setRotation(0);
                    productChangeListener.OnChange();
                }
                else
                {
                    holder.arrow.setRotation(90);
                    catalogs.get(holder.getAdapterPosition()).setExpanded(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    productAdapter = new ProductsMySelectionAdapter(context, catalogs.get(holder.getAdapterPosition()).getProductsSelections(),catalogs,brandsModels,productChangeListener);
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
