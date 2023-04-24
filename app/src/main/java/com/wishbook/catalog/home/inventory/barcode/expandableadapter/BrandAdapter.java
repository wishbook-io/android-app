package com.wishbook.catalog.home.inventory.barcode.expandableadapter;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class BrandAdapter extends ExpandableRecyclerAdapter<BrandAdapter.MyParentViewHolder, BrandAdapter.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Response_Brands> brands=new ArrayList<>();
    private ArrayList<Response_catalog> catalogs =new ArrayList<>();
    private ArrayList<ProductObj> products =new ArrayList<>();
    private Context context;
    private CatalogsViewAdapter catalogsAdapter;
    private BrandAdapter adapter;
    private FragmentManager manager;



    public BrandAdapter(Context context, ArrayList<Response_Brands> itemList,FragmentManager manager) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.brands=itemList;     this.manager=manager;


    }


    @Override
    public BrandAdapter.MyParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.brand_list_item, parentViewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public BrandAdapter.MyChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.catalog_layout, childViewGroup, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(BrandAdapter.MyParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Response_Brands brand = (Response_Brands) parentListItem;
        parentViewHolder.brand_name.setText(brand.getName());


    }

    @Override
    public void onBindChildViewHolder(final BrandAdapter.MyChildViewHolder childViewHolder, int position, Object childListItem) {
        final Response_catalog catalog = (Response_catalog) childListItem;
            catalogs = new ArrayList<>();
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            childViewHolder.recyclerView.setLayoutManager(layoutManager);
            catalogs.add((Response_catalog) childListItem);
            catalogsAdapter = new CatalogsViewAdapter(context,catalogs,manager);
            childViewHolder.recyclerView.setAdapter(catalogsAdapter);
            catalogsAdapter.setExpandCollapseListener(new ExpandCollapseListener() {
                @Override
                public void onListItemExpanded(final int position) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                    HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context,"products",catalogs.get(position).getId()),null, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                Log.v("sync response ", response);
                                ProductObj[] response_product = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                                products = new ArrayList<ProductObj>(Arrays.asList(response_product));
                                catalogs.get(position).setProducts(products);
                                catalogsAdapter = new CatalogsViewAdapter(context,catalogs,manager);
                                childViewHolder.recyclerView.setAdapter(catalogsAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }

                @Override
                public void onListItemCollapsed(int position) {

                }
            });
    }



 @Override
    public void onParentListItemExpanded(final int position) {

 Log.v("expanded ", position+brands.get(position).getId()+"");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context,"catalogs","") +"?brand="+brands.get(position).getId()+"",null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response ", response);
                    Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
                    catalogs = new ArrayList<Response_catalog>(Arrays.asList(response_catalog));
                    brands.get(position).setResponse_catalogs(catalogs);
                    notifyParentItemChanged(position);
                    notifyChildItemChanged(position,position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    public class MyParentViewHolder extends ParentViewHolder{

        private TextView brand_name;
        private ImageView arrow;

        public MyParentViewHolder(View itemView) {
            super(itemView);

            brand_name = (TextView) itemView.findViewById(R.id.brand_name);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);


        }

        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);

  /*if (expanded) {
                image.setImageResource(R.drawable.collapse);
            } else {
                image.setImageResource(R.drawable.expand);
            }

            if (expanded) {
                arrow.setRotation(90);
            } else {
                arrow.setRotation(0);
            }
*/
        }
    }


    public class MyChildViewHolder extends ChildViewHolder {

        private RecyclerViewEmptySupport recyclerView;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerViewEmptySupport) itemView.findViewById(R.id.recycler_view);
            recyclerView.setEmptyView(itemView.findViewById(R.id.list_empty1));
        }
    }
}
