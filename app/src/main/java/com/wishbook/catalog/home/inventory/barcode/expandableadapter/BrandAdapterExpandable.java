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

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by root on 18/11/16.
 */
public class BrandAdapterExpandable extends RecyclerView.Adapter<BrandAdapterExpandable.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Response_Brands> brands=new ArrayList<>();
    private ArrayList<Response_catalogMini> catalogs =new ArrayList<>();
    private ArrayList<Response_catalogMini> catalogsnew =new ArrayList<>();
    private Context context;
    private CatalogAdapterExpandable catalogsAdapter;
    private FragmentManager manager;
    private Fragment fragment;


    public BrandAdapterExpandable(Context context, ArrayList<Response_Brands> itemList,FragmentManager manager,Fragment fragment) {
        mInflater = LayoutInflater.from(context);
        this.brands=itemList;
        this.context=context;
        this.manager = manager;
        this.fragment=fragment;

    }



    public void  updatePosition(int position,Boolean status){
        brands.get(position).setExpanded(status);
        this.notifyDataSetChanged();
    }





    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.brand_list_item, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyParentViewHolder holder,  int position) {
        holder.brand_name.setText(brands.get(position).getName());

        Log.d("Scrolled",position + "Opened" + brands.get(position).getExpanded());

        if(brands.get(position).getExpanded())
        {
            holder.catalog_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.catalog_layout.setVisibility(View.GONE);
        }
        holder.brand_container_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(brands.get(holder.getAdapterPosition()).getExpanded())
                {
                    brands.get(holder.getAdapterPosition()).setExpanded(false);
                  //  updatePosition(position,false);
                    holder.catalog_layout.setVisibility(View.GONE);
                    holder.arrow.setRotation(0);
                }else
                {
                    brands.get(holder.getAdapterPosition()).setExpanded(true);
                  //  updatePosition(position,true);
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                    HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context,"catalogs","") +"?brand="+brands.get(holder.getAdapterPosition()).getId()+"", null, headers, true, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                Log.v("sync response", response);
                                Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                holder.recyclerView.setLayoutManager(layoutManager);
                                holder.recyclerView.setHasFixedSize(true);
                                holder.recyclerView.setNestedScrollingEnabled(false);
                                catalogs = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalog));
                                catalogsnew = new ArrayList<Response_catalogMini>();
                                for (int i = 0; i < catalogs.size(); i++) {
                                    catalogs.get(i).setExpanded(false);
                                    if (Integer.parseInt(catalogs.get(i).getTotal_products()) > 0) {
                                        catalogsnew.add(catalogs.get(i));
                                    }

                                }

                                catalogsAdapter = new CatalogAdapterExpandable(context, catalogsnew, manager, fragment);
                                holder.recyclerView.setAdapter(new AlphaInAnimationAdapter(catalogsAdapter));
                                holder.catalog_layout.setVisibility(View.VISIBLE);
                                holder.recyclerView.addItemDecoration(new DividerItemDecoration(context, null));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }
            }
        });


        /*if(brands.get(position).getExpanded()) {
            brands.get(position).setExpanded(false);
            holder.catalog_layout.setVisibility(View.GONE);
            holder.arrow.setRotation(0);
        }
        else
        {
            holder.arrow.setRotation(90);
            brands.get(position).setExpanded(true);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context,"catalogs","") +"?brand="+brands.get(position).getId()+"", null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);
                    Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    holder.recyclerView.setHasFixedSize(true);
                    holder.recyclerView.setNestedScrollingEnabled(false);
                    catalogs = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalog));
                    catalogsnew = new ArrayList<Response_catalogMini>();
                    for(int i=0;i<catalogs.size();i++)
                    {
                        catalogs.get(i).setExpanded(false);
                        if(Integer.parseInt(catalogs.get(i).getTotal_products())>0)
                        {
                            catalogsnew.add(catalogs.get(i))  ;
                        }

                    }

                    catalogsAdapter = new CatalogAdapterExpandable(context, catalogsnew,manager,fragment);
                    holder.recyclerView.setAdapter(new AlphaInAnimationAdapter(catalogsAdapter));
                    holder.catalog_layout.setVisibility(View.VISIBLE);
                    holder.recyclerView.addItemDecoration(new DividerItemDecoration(context, null));

                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }*/

    }

    @Override
    public int getItemCount() {
        return brands.size();
    }


    public class MyParentViewHolder extends ParentViewHolder{

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


    public class MyChildViewHolder extends ChildViewHolder {

        private RecyclerView recyclerView;

        public MyChildViewHolder(View itemView) {
            super(itemView);

        }
    }
}
