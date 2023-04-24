package com.wishbook.catalog.commonadapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.SelectionPatch;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductslistAdapter extends RecyclerView.Adapter<ProductslistAdapter.CustomViewHolder> {
    private String selectionId="0";
    private boolean delete = false;
    private ArrayList<ProductObj> feedItemList;
    private AppCompatActivity mContext;

    public ProductslistAdapter(AppCompatActivity context, ArrayList<ProductObj> feedItemList,String selectionId,boolean delete) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.selectionId=selectionId;
        this.delete = delete;
    }
/*
    public ProductslistAdapter(AppCompatActivity context, ArrayList<ProductObj> feedItemList, boolean delete) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.delete = delete;
    }*/

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final SimpleDraweeView prod_img;
        private final TextView prod_name;
        private final TextView prod_price;
        private final ImageButton prod_del;

        public CustomViewHolder(View view) {
            super(view);
            prod_img = (SimpleDraweeView) view.findViewById(R.id.prod_img);
            prod_name = (TextView) view.findViewById(R.id.prod_name);
            prod_price = (TextView) view.findViewById(R.id.prod_price);
            prod_del = (ImageButton) view.findViewById(R.id.prod_del);
            if (delete) {
                prod_del.setVisibility(View.GONE);
            }
        }
    }

    public ArrayList<ProductObj> getCurrentData() {
        return feedItemList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productselectionitem, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        customViewHolder.prod_name.setText(feedItemList.get(i).getTitle());
        customViewHolder.prod_price.setText("\u20B9" + feedItemList.get(i).getPrice());
        String image = feedItemList.get(i).getImage().getThumbnail_small();
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(mContext,image,customViewHolder.prod_img,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.prod_img);
       //     Picasso.with(mContext).load(image).into(customViewHolder.prod_img);
        }
        customViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(feedItemList.get(i).getImage().getThumbnail_medium());
            }
        });
        customViewHolder.prod_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AppTheme_Dark_Dialog);
                alert.setTitle("Delete");
                alert.setMessage("Do you want to delete selected product?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        final MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);
                        ArrayList<ProductObj> selectionItems = new ArrayList<ProductObj>();
                        selectionItems.addAll(feedItemList);
                        if (i < selectionItems.size()) {
                            selectionItems.remove(i);
                        }
                        ArrayList<String> ids = new ArrayList<String>();
                        for (ProductObj selectionItem : selectionItems) {
                            ids.add(selectionItem.getId());
                        }
//                        progressDialog.show();
//                        if (feedItemList.size() > 0) {
//                            if (feedItemList.get(i) != null) {
//                                feedItemList.remove(i);
//                            }
//                        }

                        ArrayList<String> prooductIds = new ArrayList<String>();
                        for (ProductObj prods : selectionItems) {
                            prooductIds.add(prods.getId());
                        }

                        String selection = selectionId;
                        if (prooductIds.size() > 1) {
                            SelectionPatch selectionPatch = new SelectionPatch(selection, prooductIds);

                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(mContext);
                            Gson gson = new Gson();
                            HttpManager.getInstance(mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mContext,"selections_expand_false","") + DataPasser.selectedID + "/", gson.fromJson(gson.toJson(selectionPatch), JsonObject.class), headers, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {

                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    if (i < feedItemList.size()) {
                                        feedItemList.remove(i);
                                    }
                                    notifyDataSetChanged();
                                    // progressDialog.dismiss();
                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {
                                  StaticFunctions.showResponseFailedDialog(error);
                                }
                            });


//
//                        new AsyncTask<Void, Void, String>() {
//                            @Override
//                            protected void onPostExecute(String s) {
//                                super.onPostExecute(s);
//                                feedItemList.remove(i);
//                                notifyDataSetChanged();
//                                progressDialog.dismiss();
//                            }
//
//                            @Override
//                            protected String doInBackground(Void... params) {
//                                try
//
//                                {
//                                    URL url = new URL(URLConstants.companyUrl(getActivity(),"products","") + feedItemList.get(i).getId() + "/");
//                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                                    connection.setRequestMethod("DELETE");
//                                    connection.setRequestProperty ("Authorization", "Token "+Application_Singleton.Token);
//                                    int responseCode = connection.getResponseCode();
//                                    Log.v("responsecode",""+responseCode);
//
//
//                                }
//
//                                catch(
//                                        Exception e
//                                        )
//
//                                {
//                                    e.printStackTrace();
//                                }
//                                return null;
//                            }
//
//
//                        }.execute();

//                            Unirest.delete(URLConstants.companyUrl(getActivity(),"products","") + feedItemList.get(i).getId() + "/").asJsonAsync(new Callback<JsonNode>() {
//                                @Override
//                                public void completed(HttpResponse<JsonNode> httpResponse) {
//                                    feedItemList.remove(i);
//                                }
//
//                                @Override
//                                public void failed(UnirestException e) {
//
//                                }
//
//                                @Override
//                                public void cancelled() {
//
//                                }
                            // });


                            //    HttpManager.getInstance(mContext).request(HttpManager.METHOD.PATCHWITHPROGRESS);

//                        NetworkProcessor.with(mContext)
//                                .load(URLConstants.companyUrl(getActivity(),"products","") + feedItemList.get(i).getId() + "?format=json").addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", ""))).addHeader("X-HTTP-Method-Override", "DELETE")
//                                .asString().setCallback(new FutureCallback<String>() {
//                            @Override
//                            public void onCompleted(Exception e, String result) {
//                                if (e == null & result != null) {
//                                    feedItemList.remove(i);
//                                    notifyDataSetChanged();
//                                }
//                                progressDialog.dismiss();
//                            }
//                        });
                        }
                        else{
                            SelectionPatch selectionPatch = new SelectionPatch(selection, prooductIds);

                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(mContext);
                            Gson gson = new Gson();
                            HttpManager.getInstance(mContext).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(mContext,"selections_expand_false","") + DataPasser.selectedID + "/", null, headers,false, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {

                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    if (i < feedItemList.size()) {
                                        feedItemList.remove(i);
                                    }
                                    notifyDataSetChanged();
                                    // progressDialog.dismiss();
                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {
                                   StaticFunctions.showResponseFailedDialog(error);
                                }
                            });
                        }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();

            }
        });

    }


    public void showImage(String imagepath) {
        Dialog mSplashDialog = new Dialog(mContext, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(mContext,imagepath,image,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,imagepath,image);
            //Picasso.with(mContext).load(imagepath).into(image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }



}