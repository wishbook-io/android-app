package com.wishbook.catalog.commonadapters;

import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


import java.util.ArrayList;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;

/**
 * Created by nani on 16-04-2016.
 */
public class ReceivedProductsAdapter extends RecyclerView.Adapter<ReceivedProductsAdapter.CustomViewHolder> {
    private String catId = null;
    private boolean fullproduct = false;

    private ProductChangeListener productChangeListener;

    public interface ProductChangeListener{
        void onChange();
    }
    public ArrayList<ProductQunatityRate> getAllProducts() {
        ArrayList<ProductQunatityRate> productQunatityRates = new ArrayList<>();
        if(feedItemList!=null){
        for (ProductObjectQuantity obj : feedItemList) {
            if (obj.getQuantity() > 0) {
                ProductQunatityRate productQunatityRate = new ProductQunatityRate(obj.getFeedItemList().getId(), obj.getQuantity(), obj.getPrice());
                productQunatityRates.add(productQunatityRate);
            }
        }
        }
        return productQunatityRates;
    }

    public ArrayList<ProductObjectQuantity> getAllProducts(int quantity) {
        ArrayList<ProductObjectQuantity> productQunatityRates = new ArrayList<>();
        if(feedItemList!=null){
            for (ProductObjectQuantity obj : feedItemList) {
                if (obj.getQuantity() > 0) {
                    ProductObjectQuantity productQunatityRate = new ProductObjectQuantity(obj.getFeedItemList(), quantity, obj.getPrice());
                    productQunatityRates.add(productQunatityRate);
                }
            }
        }
        return productQunatityRates;
    }

    public String getCatId() {
        return catId;
    }


    private ArrayList<ProductObjectQuantity> feedItemList;
    private AppCompatActivity mContext;

    public ReceivedProductsAdapter()
    {}

    public ReceivedProductsAdapter(AppCompatActivity context, ArrayList<ProductObjectQuantity> feedItemList, String catId, boolean fullproduct,ProductChangeListener productChangeListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.productChangeListener=productChangeListener;
        this.fullproduct = fullproduct;
        this.catId = catId;
    }

    public ReceivedProductsAdapter(AppCompatActivity context,boolean fullproduct, ArrayList<ProductObjectQuantity> feedItemList,ProductChangeListener productChangeListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.productChangeListener=productChangeListener;
        this.fullproduct=fullproduct;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView prod_img;
        private final TextView prod_name;
        private final TextView prod_price;
        private final TextView qntytext;
        private final ImageButton minusbut;
        private final ImageButton plusbut;
        private final ImageView delete;


        public CustomViewHolder(View view) {
            super(view);
            prod_img = (SimpleDraweeView) view.findViewById(R.id.prod_img);
            prod_name = (TextView) view.findViewById(R.id.prod_name);
            prod_price = (TextView) view.findViewById(R.id.prod_price);
            qntytext = (TextView) view.findViewById(R.id.qntytext);
            minusbut = (ImageButton) view.findViewById(R.id.minusbut);
            plusbut = (ImageButton) view.findViewById(R.id.plusbut);
            delete = (ImageView) view.findViewById(R.id.delete);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productdetailitem, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        if(fullproduct)
        {
            customViewHolder.minusbut.setVisibility(View.GONE);
            customViewHolder.plusbut.setVisibility(View.GONE);
            customViewHolder.delete.setVisibility(View.GONE);


        }
        else
        {
            customViewHolder.minusbut.setVisibility(View.VISIBLE);
            customViewHolder.plusbut.setVisibility(View.VISIBLE);
            customViewHolder.delete.setVisibility(View.VISIBLE);
        }

        if (feedItemList.get(i).getFeedItemList().getSku() != null && !feedItemList.get(i).getFeedItemList().getSku().equals("null") && !feedItemList.get(i).getFeedItemList().getSku().equals("")) {
            customViewHolder.prod_name.setVisibility(View.VISIBLE);
            customViewHolder.prod_name.setText(feedItemList.get(i).getFeedItemList().getSku());
        }else{
            customViewHolder.prod_name.setVisibility(View.GONE);
        }

        customViewHolder.prod_price.setText("\u20B9" + feedItemList.get(i).getPrice());
        String image = feedItemList.get(i).getFeedItemList().getImage().getThumbnail_small();
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(mContext,image,customViewHolder.prod_img,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.prod_img);

           // Picasso.with(mContext).load(image).into(customViewHolder.prod_img);
        }
        customViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(feedItemList.get(i).getFeedItemList().getImage().getThumbnail_medium());
            }
        });
        customViewHolder.qntytext.setText("" + feedItemList.get(i).getQuantity());
        if (!fullproduct) {
            customViewHolder.minusbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (feedItemList.get(i).getQuantity() > 1) {
                        feedItemList.get(i).setQuantity(feedItemList.get(i).getQuantity() - 1);
                       /* feedItemList.get(i).setPrice(feedItemList.get(i).getQuantity() * Float.parseFloat(feedItemList.get(i).getFeedItemList().getPrice()));
                        notifyDataSetChanged();*/
                        customViewHolder.qntytext.setText("" + feedItemList.get(i).getQuantity());
                        productChangeListener.onChange();
                    }
                }
            });
            customViewHolder.plusbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedItemList.get(i).setQuantity(feedItemList.get(i).getQuantity() + 1);
                   /* feedItemList.get(i).setPrice(feedItemList.get(i).getQuantity() * Float.parseFloat(feedItemList.get(i).getFeedItemList().getPrice()));
                    notifyDataSetChanged();*/
                    customViewHolder.qntytext.setText("" + feedItemList.get(i).getQuantity());
                    productChangeListener.onChange();
                }
            });
        } else {
            customViewHolder.minusbut.setVisibility(View.GONE);
            customViewHolder.plusbut.setVisibility(View.GONE);
        }

        customViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("Delete")
                        .content("Do you want to delete this Product ?")
                        .negativeText("No")
                        .positiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                feedItemList.remove(feedItemList.get(i));
                                productChangeListener.onChange();
                                notifyDataSetChanged();
                            }
                        })
                        .show();


            }
        });
    }

    public int getCurrentTotalQuantity() {
        int total=0;
        for(ProductObjectQuantity productObj:feedItemList){
            try {
                total = total + productObj.getQuantity();
            }
            catch (Exception e){

            }
        }

        return total;
    }

    public float getCurrentTotal() {
        float total=0;
        for(ProductObjectQuantity productObj:feedItemList){
            try {
                total = total + (Float.parseFloat(""+productObj.getPrice())*productObj.getQuantity());
            }
            catch (Exception e){

            }
        }

        return total;
    }

    public void setMultiplier(int quantity) {
        float price = 0;
        ArrayList<ProductObjectQuantity> newFeed = new ArrayList<>();
        for (ProductObjectQuantity obj : feedItemList) {
            obj.setQuantity(quantity);
            obj.setPrice((Float.parseFloat(obj.getFeedItemList().getPrice())));
            newFeed.add(obj);
        }
        feedItemList = newFeed;
        notifyDataSetChanged();
        productChangeListener.onChange();
    }

    public void showImage(String imagepath) {
        Dialog mSplashDialog = new Dialog(mContext, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
           // StaticFunctions.loadImage(mContext,imagepath,image,R.drawable.uploadempty);
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