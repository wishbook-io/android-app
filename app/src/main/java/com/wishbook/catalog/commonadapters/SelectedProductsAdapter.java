package com.wishbook.catalog.commonadapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.catalog.share.CatalogShareHolder;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectedProductsAdapter extends RecyclerView.Adapter<SelectedProductsAdapter.CustomViewHolder> {
    private ArrayList<Response_Selection> feedItemList;
    private AppCompatActivity mContext;

    public SelectedProductsAdapter(AppCompatActivity context, ArrayList<Response_Selection> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView sel_name;
        private final SimpleDraweeView sel_img;
        private final LinearLayout sel_del;
        private final LinearLayout sel_share;
        private final ImageButton sel_share_btn;

        public CustomViewHolder(View view) {
            super(view);
            sel_name=(TextView)view.findViewById(R.id.sel_name);
            sel_img= (SimpleDraweeView) view.findViewById(R.id.sel_img);
            sel_del=(LinearLayout)view.findViewById(R.id.sel_del);
            sel_share=(LinearLayout)view.findViewById(R.id.sel_share);
            sel_share_btn = (ImageButton)view.findViewById(R.id.sel_share_btn);

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selectedprodsitem, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {
        customViewHolder.sel_name.setText(WordUtils.capitalize(feedItemList.get(i).getName()));
        String image = feedItemList.get(i).getImage();
        if (image != null & !image.equals("")) {
           // StaticFunctions.loadImage(mContext,image,customViewHolder.sel_img,R.drawable.uploadempty);
           // Picasso.with(mContext).load(image).into(customViewHolder.sel_img);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.sel_img);
        }


        customViewHolder.sel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataPasser.selectedID=feedItemList.get(i).getId();
                Application_Singleton.CONTAINER_TITLE= StringUtils.capitalize(feedItemList.get(i).getName());
                Application_Singleton.CONTAINERFRAG=new Fragment_ProductsInSelection();
                Intent intent = new Intent(mContext, OpenContainer.class);
                intent.putExtra("ordertype", "selections");
                intent.putExtra("toolbarCategory", OpenContainer.MYSELECTION);
                mContext.startActivity(intent);
              //  mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new Fragment_ProductsInSelection()).addToBackStack(null).commit();


            }
        });
        if(Activity_Home.pref.getString("groupstatus", "0") .equals("2")){
            customViewHolder.sel_share_btn.setImageResource(R.drawable.ic_add_shopping_cart_24dp);
        }
        if(Activity_Home.pref.getString("company_type", "0") .equals("buyer")){
            customViewHolder.sel_share.setVisibility(View.GONE);
        }



        customViewHolder.sel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Activity_Home.pref.getString("groupstatus", "0") .equals("2")){
                    //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                    //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                    Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                    Bundle bundle = new Bundle();
                    bundle.putString("ordertype", "selections");
                    bundle.putString("ordervalue", feedItemList.get(i).getId());
                    createOrderFrag.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE=feedItemList.get(i).getName();
                    Application_Singleton.CONTAINERFRAG=createOrderFrag;
                    StaticFunctions.switchActivity(mContext, OpenContainer.class);

                   // mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, createOrderFrag).addToBackStack("createorder").commit();

                }
                else {
                    CatalogMinified selectedCatalog = new CatalogMinified(feedItemList.get(i).getId(),"selection");
                    Application_Singleton.shareCatalogHolder = selectedCatalog;
                    StaticFunctions.switchActivity(mContext, CatalogShareHolder.class);
                    /*Dialog_Catalogshare catalogshareDialog = new Dialog_Catalogshare(selectedCatalog);
                    catalogshareDialog.show(mContext.getSupportFragmentManager(), "share");*/


                   // mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new Fragment_ShareProductSelection()).addToBackStack("sharecatalog").commit();
                }
            }
        });
        customViewHolder.sel_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.LightDialogTheme);
                alert.setTitle("Delete");
                alert.setMessage("Do you want to delete this selection?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);

                                progressDialog.show();
//                                ArrayList<Response_Selection> selectionItems = new ArrayList<Response_Selection>();
//                                selectionItems.addAll(selectionItems);
//                                if (i < selectionItems.size()) {
//                                    selectionItems.remove(i);
//                                }
//                                ArrayList<String> ids = new ArrayList<String>();
//                                for (Response_Selection selectionItem : selectionItems) {
//                                    ids.add(selectionItem.getId());
//                                }


                                //  SelectionPatch selectionPatch = new SelectionPatch(ids);

                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(mContext);
                                Gson gson = new Gson();
                                HttpManager.getInstance(mContext).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(mContext,"selections_expand_false","") + feedItemList.get(i).getId() + "/",null, headers, false,new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {

                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {

                                        if (i < feedItemList.size()) {
                                            feedItemList.remove(i);
                                        }
                                        notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {
                                        StaticFunctions.showResponseFailedDialog(error);
                                        progressDialog.dismiss();
                                    }
                                });

//                                NetworkProcessor.with(mContext)
//                                        .load("DELETE", URLConstants.companyUrl(getActivity(),"selections","") + feedItemList.get(i).getId() + "/").addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
//                                        .asString().setCallback(new FutureCallback<String>() {
//                                    @Override
//                                    public void onCompleted(Exception e, String result) {
//                                        if (e == null & result != null) {
//                                            feedItemList.remove(i);
//                                            notifyDataSetChanged();
//                                        }
//                                        progressDialog.dismiss();
//                                    }
//                                });

                            }
                        }

                );
                alert.setNegativeButton("No", new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                );
                    alert.show();

                }
            });
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }


}