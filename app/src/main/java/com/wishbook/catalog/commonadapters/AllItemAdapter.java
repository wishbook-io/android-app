package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.Response_catalogapp;
import com.wishbook.catalog.commonmodels.responses.CatalogInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.SharedByMe;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by vignesh_streamoid on 14/05/16.
 */
public class AllItemAdapter extends  RecyclerView.Adapter<AllItemAdapter.ViewHolder>{

    private final int height;
    private final int width;
    private final AppCompatActivity context;
    private final List itemsInSection;
    private int numberofGridSize;
    private String sectionTitle;
    private Fragment fragment;
    private HashMap<String,String> params;

    public AllItemAdapter(AppCompatActivity context, List itemsInSection) {
        this.context=context;
        this.itemsInSection=itemsInSection;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }
    public AllItemAdapter(AppCompatActivity context, ArrayList itemsInSection ,int numberofGridSize) {
        this.context=context;
        this.itemsInSection=itemsInSection;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.numberofGridSize = numberofGridSize;
    }

    public AllItemAdapter(AppCompatActivity context, ArrayList itemsInSection , int numberofGridSize, String headerTitle, Fragment fragment) {
        this.context=context;
        this.itemsInSection=itemsInSection;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.numberofGridSize = numberofGridSize;
        this.sectionTitle = headerTitle;
        this.fragment = fragment;
    }

    public AllItemAdapter(AppCompatActivity context, ArrayList itemsInSection , int numberofGridSize, String headerTitle, Fragment fragment, HashMap<String,String> params) {
        this.context=context;
        this.itemsInSection=itemsInSection;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.numberofGridSize = numberofGridSize;
        this.sectionTitle = headerTitle;
        this.fragment = fragment;
        this.params = params;
    }


    @Override
    public AllItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_items_item_new, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllItemAdapter.ViewHolder holder, int position) {
        try {
            final Object object=itemsInSection.get(position);


            if(numberofGridSize == 3) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width/3.1f),Math.round(height/4.2f));
                holder.itemcontainer.setLayoutParams(lp);
                holder.txt_sold_by.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width/2.1f),Math.round(height/3.2f));
                holder.itemcontainer.setLayoutParams(lp);
                holder.txt_sold_by.setVisibility(View.VISIBLE);
            }


            if(object instanceof Response_Brands){
               /* ImageLoader.getInstance().displayImage(((Response_Brands) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);

                    }
                });*/
                StaticFunctions.loadFresco(context,((Response_Brands)itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img );

            }
            if(object instanceof Response_catalogapp){
               /* ImageLoader.getInstance().displayImage(((Response_catalogapp) itemsInSection.get(position)).getThumbnail().getThumbnail_medium(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);
                      
                    }
                });*/
                StaticFunctions.loadFresco(context,((Response_catalogapp)itemsInSection.get(position)).getThumbnail().getThumbnail_small(), holder.All_item_img );
            }

            if(object instanceof Response_Selection){
                /*ImageLoader.getInstance().displayImage(((Response_Selection) itemsInSection.get(position)).getImage(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);
                      
                    }
                });*/
                StaticFunctions.loadFresco(context,((Response_Selection)itemsInSection.get(position)).getImage(), holder.All_item_img );
            }
            if(object instanceof CatalogMinified){
                Log.i("TAG", "CatalogMinified: Medium ");
                /*ImageLoader.getInstance().displayImage(,holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);
                      
                    }
                });*/
                StaticFunctions.loadFresco(context,((CatalogMinified)itemsInSection.get(position)).getThumbnail().getThumbnail_small(), holder.All_item_img );
            }
            if(object instanceof SharedByMe){
                Log.i("TAG", "SharedByMe: Medium ");
                /*ImageLoader.getInstance().displayImage(((SharedByMe)itemsInSection.get(position)).getImage(),holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);

                    }
                });*/


                StaticFunctions.loadFresco(context,((SharedByMe)itemsInSection.get(position)).getImage(), holder.All_item_img );
            }
            if(object instanceof ProductObj){
                /*ImageLoader.getInstance().displayImage(((ProductObj)itemsInSection.get(position)).getImage().getThumbnail_medium(),holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);
                      
                    }
                });*/

                StaticFunctions.loadFresco(context,((ProductObj)itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img );
            }
            if(object instanceof Response_catalogMini){
                Log.i("TAG", "Response_catalogMini: Medium ");
               /* ImageLoader.getInstance().displayImage(((Response_catalogMini)itemsInSection.get(position)).getThumbnail().getThumbnail_medium(),holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                        super.onLoadingComplete(imageUri, view, src);

                    }
                });*/
                StaticFunctions.loadFresco(context,((Response_catalogMini)itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img );
                holder.txt_sold_by.setText(((Response_catalogMini)itemsInSection.get(position)).getBrand_name());
            }

            if(object instanceof CatalogInfo.Lastest_catalog) {

                StaticFunctions.loadFresco(context,((CatalogInfo.Lastest_catalog)itemsInSection.get(position)).getImage(), holder.All_item_img );
            }


            holder.All_item_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onItem Click: ");


                    Application_Singleton.trackEvent("Home"+sectionTitle, "Click","Product");


                    if(object instanceof Response_Brands){
                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("filtertype","brand");
                        bundle.putString("filtervalue",( (Response_Brands) object).getId());
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE="Catalogs";
                        Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                        //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                        StaticFunctions.switchActivity(context, OpenContainer.class);

                        // context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentCatalogs).addToBackStack(null).commit();

                    }
                    if(object instanceof CatalogMinified){
                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }

                        if(sectionTitle.equalsIgnoreCase(context.getResources().getString(R.string.from_brands_you_follow))){
                            Application_Singleton.CONTAINER_TITLE = sectionTitle;
                            Application_Singleton.CONTAINERFRAG = fragment;
                            Bundle bundle = new Bundle();
                            bundle.putInt(Application_Singleton.adapterFocusPosition,holder.getAdapterPosition());
                            fragment.setArguments(bundle);
                            StaticFunctions.switchActivity(context, OpenContainer.class);
                        } else {
                            params.put("focus_position",String.valueOf(holder.getAdapterPosition()));
                            new DeepLinkFunction(params,context);
                        }
                      /*  Application_Singleton.selectedshareCatalog = ((CatalogMinified)itemsInSection.get(position));
                       //this is for public catalog that is shared
                         Application_Singleton.selectedshareCatalog.setView_permission("push");
                            Application_Singleton.CONTAINER_TITLE = ((CatalogMinified) itemsInSection.get(position)).getTitle();
                            Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                            Intent intent = new Intent(context, OpenContainer.class);
                            intent.putExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                            context.startActivity(intent);*/

                        // focus on particular fragment list position

                    }
                    if(object instanceof SharedByMe){
                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }
                        Log.i("TAG", "onClick: SharedByMe");
                        SharedByMe sharedbyme = (SharedByMe) itemsInSection.get(holder.getAdapterPosition());
                        String shared= new Gson().toJson(sharedbyme);

                        Application_Singleton.selectedshareCatalog = new Gson().fromJson(shared,CatalogMinified.class);

                            Application_Singleton.CONTAINER_TITLE = ((SharedByMe) itemsInSection.get(holder.getAdapterPosition())).getTitle();
                            Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                            Intent intent = new Intent(context, OpenContainer.class);
                            intent.putExtra("toolbarCategory", OpenContainer.SHAREDBYME);
                            context.startActivity(intent);

                    }
                    if(object instanceof Response_catalogMini){
                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }
                        Log.i("TAG", "onClick: CatalogMini");
                       /* Response_catalogMini response_catalogMini = (Response_catalogMini) itemsInSection.get(position);
                        CatalogMinified response = new CatalogMinified(response_catalogMini.getId(),"catalog",response_catalogMini.getIs_disable(),response_catalogMini.getTitle(),response_catalogMini.getBrand_name(),response_catalogMini.getView_permission());
                        response.setIs_supplier_approved(response_catalogMini.getIs_supplier_approved());
                        response.setSupplier(response_catalogMini.getSupplier());
                        if(response_catalogMini.getSupplier_name()!=null) {
                            response.setSupplier_name(response_catalogMini.getSupplier_name());
                        }
                        response.setFull_catalog_orders_only(response_catalogMini.getFull_catalog_orders_only());
                        Application_Singleton.selectedshareCatalog = response;
                        Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
                        Application_Singleton.CONTAINER_TITLE = response_catalogMini.getTitle();
                        Application_Singleton.CONTAINERFRAG = gallery;
                        Intent intent = new Intent(context, OpenContainer.class);
                        intent.putExtra("pushId",response_catalogMini.getPush_user_id());
                        intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
                        intent.putExtra("Ordertype","catalog");
                        context.startActivity(intent);*/


                        // focus on particular fragment list position


                        if(sectionTitle.equalsIgnoreCase(context.getResources().getString(R.string.from_brands_you_follow))){
                            Application_Singleton.CONTAINER_TITLE = sectionTitle;
                            if(sectionTitle.equalsIgnoreCase(context.getResources().getString(R.string.from_trusted_seller))){
                                Application_Singleton.isPublicTrusted = true;
                            } else {Log.i("TAG", "onCreateView: Public Trusted False 1"+sectionTitle);}
                            Application_Singleton.CONTAINERFRAG = fragment;
                            Bundle bundle = new Bundle();
                            bundle.putInt(Application_Singleton.adapterFocusPosition,holder.getAdapterPosition());
                            fragment.setArguments(bundle);
                            Intent intent = new Intent(context, OpenContainer.class);

                            //intent.putExtra("toolbarCategory", OpenContainer.BROWSE);

                            context.startActivity(intent);
                        } else {
                            params.put("focus_position",String.valueOf(holder.getAdapterPosition()));
                            new DeepLinkFunction(params,context);
                        }
                    }



                    if(object instanceof Response_catalogapp){

//                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
//                        HttpManager.getInstance(context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.GETPRODUCTSBYCAT + ((Response_catalogapp)object).getId(), null, headers, true, new HttpManager.customCallBack() {
//                            @Override
//                            public void onCacheResponse(String response) {
//                                Log.v("cached response", response);
//                                onServerResponse(response, false);
//                            }
//
//                            @Override
//                            public void onServerResponse(String response, boolean dataUpdated) {
//                                Log.v("sync response", response);
//                                ProductObj[] productObjs = Application_Singleton.gson.fromJson(response, ProductObj[].class);
//                                ArrayList<ProductObj> list = new ArrayList<ProductObj>(Arrays.asList(productObjs));
//                                Application_Singleton.selectedCatalogProducts = list;
//                                Application_Singleton.CONTAINER_TITLE = "Products";
//                                Application_Singleton.CONTAINERFRAG = new Fragment_ProductsInCatalog();
//                                StaticFunctions.switchActivity(context, OpenContainer.class, false);
//                                //  mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_ProductsInCatalog()).addToBackStack("productsincat").commit();
//
//                            }
//
//                            @Override
//                            public void onResponseFailed(ErrorString error) {
//
//                            }
//                        });




                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }
                        Response_catalogapp response_catalogapp=((Response_catalogapp)object);
                        SharedByMe sharedByMe=new SharedByMe();
                        sharedByMe.setId(response_catalogapp.getId());
                        sharedByMe.setType("catalog");
                        sharedByMe.setTitle(response_catalogapp.getTitle());
                        String shared= new Gson().toJson(sharedByMe);
                        Application_Singleton.selectedshareCatalog = new Gson().fromJson(shared,CatalogMinified.class);
                        Application_Singleton.CONTAINER_TITLE = sharedByMe.getTitle();
                        Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                        Intent intent=new Intent(context,OpenContainer.class);
                        intent.putExtra("toolbarCategory", OpenContainer.CATALOGAPP);
                        context.startActivity(intent);

                    }
//
                    if(object instanceof Response_Selection){
                        DataPasser.selectedID=((Response_Selection)itemsInSection.get(holder.getAdapterPosition())).getId();
                        Application_Singleton.CONTAINER_TITLE="Selection";
                        Application_Singleton.CONTAINERFRAG=new Fragment_ProductsInSelection();
                        StaticFunctions.switchActivity(context, OpenContainer.class);

                        // context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new Fragment_ProductsInSelection()).addToBackStack(null).commit();

                    }

                    if(object instanceof CatalogInfo.Lastest_catalog){
                        if(params!=null){
                            Application_Singleton.deep_link_filter = params;
                        } else {
                            Application_Singleton.deep_link_filter = null;
                        }
                        Bundle bundle = new Bundle();
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        bundle.putString("filtertype","most_viewed");
                        bundle.putString("filtervalue","true");
                        bundle.putInt(Application_Singleton.adapterFocusPosition,holder.getAdapterPosition());
                        fragmentCatalogs.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE="Most Viewed Catalogs";
                        Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                        StaticFunctions.switchActivity(context, OpenContainer.class);

                    }
//                    if(object instanceof Response_ShareStatus){
//
//                    }
                }
            }
            );


        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout itemcontainer;
        public final SimpleDraweeView All_item_img;
        public  TextView txt_sold_by;

        public ViewHolder(View itemView) {
            super(itemView);
            itemcontainer= (RelativeLayout) itemView.findViewById(R.id.itemcontainer);
            All_item_img=(SimpleDraweeView) itemView.findViewById(R.id.item_img);
            txt_sold_by = (TextView) itemView.findViewById(R.id.txt_sold_by);

        }
    }
}