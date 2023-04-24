package com.wishbook.catalog.home.inventory.barcode.expandableadapter;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 18/11/16.
 */
public class CatalogAdapterExpandable extends RecyclerView.Adapter<CatalogAdapterExpandable.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Response_catalogMini> catalogs =new ArrayList<>();
    private ArrayList<ProductObj> products =new ArrayList<>();
    private Context context;
    private ProductAdapterExpandable productAdapter;
    private FragmentManager manager;
    private Fragment fragment;

    public CatalogAdapterExpandable(Context context, ArrayList<Response_catalogMini> itemList,FragmentManager manager,Fragment fragment) {
        mInflater = LayoutInflater.from(context);
        catalogs=itemList;
        this.context=context;
        this.manager=manager;
        this.fragment=fragment;
    }


    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.catalog_list_item, parent, false);
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
                }
                else
                {
                    holder.arrow.setRotation(90);
                    catalogs.get(holder.getAdapterPosition()).setExpanded(true);
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                    HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context,"products",catalogs.get(holder.getAdapterPosition()).getId()),null, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Log.v("sync response ", response);
                            ProductObj[] response_product = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            holder.recyclerView.setLayoutManager(layoutManager);
                            holder.recyclerView.setHasFixedSize(true);
                            holder.recyclerView.setNestedScrollingEnabled(false);
                            products = new ArrayList<ProductObj>(Arrays.asList(response_product));
                            productAdapter = new ProductAdapterExpandable(context, products,manager);
                            holder.recyclerView.setAdapter(new AlphaInAnimationAdapter(productAdapter));
                            holder.product_layout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalogs.size();
    }


    public class MyParentViewHolder extends ParentViewHolder{

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
