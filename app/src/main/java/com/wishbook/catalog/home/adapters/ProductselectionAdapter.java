package com.wishbook.catalog.home.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.Fragment_ProductSelections;
import com.wishbook.catalog.home.models.ProductObj;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class ProductselectionAdapter extends RecyclerView.Adapter<ProductselectionAdapter.CustomViewHolder> {

    private ArrayList<ProductObj> feedItemList;
    private AppCompatActivity mContext;

    public ProductselectionAdapter(AppCompatActivity context, ArrayList<ProductObj> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;

    }

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
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productselectionitem, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder,  int i) {
        customViewHolder.prod_name.setText(feedItemList.get(i).getTitle());
        customViewHolder.prod_price.setText("\u20B9" + feedItemList.get(i).getPrice());
        String image = feedItemList.get(i).getImage().getThumbnail_small();
        if (image != null & !image.equals("")) {
           // StaticFunctions.loadImage(mContext,image,customViewHolder.prod_img,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.prod_img);
           // Picasso.with(mContext).load(image).into(customViewHolder.prod_img);
        }
        customViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(mContext, feedItemList.get(customViewHolder.getAdapterPosition()).getImage().getThumbnail_medium());
            }
        });
        customViewHolder.prod_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.LightDialogTheme);
                alert.setTitle("Delete");
                alert.setMessage("Do you want to delete selected product?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFromSelection(feedItemList.get(customViewHolder.getAdapterPosition()));
                      // StaticFunctions.setUpselectedProdCounter((AppCompatActivity) mContext,toolbar);
                        feedItemList.remove(customViewHolder.getAdapterPosition());
                        StaticFunctions.setUpselectedProdCounter(mContext,(Toolbar)mContext.findViewById(R.id.toolbar));
                        notifyDataSetChanged();
                        Fragment_ProductSelections.prod_selcount.setText("SELECTED PRODUCTS : " + feedItemList.size());
                        if(feedItemList.size()==0)
                        {
                            Fragment_ProductSelections.prod_selcount.setVisibility(View.GONE);
                            Fragment_ProductSelections.linearLayout.setVisibility(View.GONE);
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

    private boolean removeFromSelection(ProductObj productObj) {
        boolean alreadyExists = false;
        Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
        }.getType();
        String previousProds = Activity_Home.pref.getString("selectedProds", null);
        if (previousProds != null) {
            ArrayList<ProductObj> preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
            for (int i = 0; i < preseletedprods.size(); i++) {
                if (preseletedprods.get(i).getId().equals(productObj.getId())) {
                    alreadyExists = true;
                    preseletedprods.remove(i);
                }
            }
            if (alreadyExists) {
                Activity_Home.pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();
            } else {
                preseletedprods.add(productObj);
                Activity_Home.pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();

            }
        }
        return !alreadyExists;
    }

    public void showImage(Context context, String imagepath) {
        Dialog mSplashDialog = new Dialog(mContext, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(mContext,imagepath,image,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,imagepath,image);
           // Picasso.with(mContext).load(imagepath).into(image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}