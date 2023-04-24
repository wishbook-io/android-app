package com.wishbook.catalog.commonadapters;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Product;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.CustomViewHolder> {
    private ArrayList<Response_Product> feedItemList;
    private Context mContext;

    public ProductItemAdapter(Context context, ArrayList<Response_Product> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final SimpleDraweeView prod_img;
        private final TextView prod_name;
        private final TextView prod_amount;
        private final TextView prod_quantity;

        public CustomViewHolder(View view) {
            super(view);
            prod_img = (SimpleDraweeView) view.findViewById(R.id.prod_img);
            prod_name = (TextView) view.findViewById(R.id.prod_name);
            prod_amount = (TextView) view.findViewById(R.id.prod_amount);
            prod_quantity = (TextView) view.findViewById(R.id.prod_quantity);

        }
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
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productitem, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
       customViewHolder.prod_name.setText(StringUtils.capitalize(feedItemList.get(i).getProduct_title().toLowerCase().trim()));
        customViewHolder.prod_amount.setText("\u20B9" +feedItemList.get(i).getRate());
        customViewHolder.prod_quantity.setText(feedItemList.get(i).getQuantity()+" Pcs");
        String image = feedItemList.get(i).getProduct_image();
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(mContext,image,customViewHolder.prod_img,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.prod_img);
            // Picasso.with(mContext).load(image).into(customViewHolder.prod_img);
        }
        customViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(feedItemList.get(customViewHolder.getAdapterPosition()).getProduct_image());
            }
        });
    }

    private String getformatedDate(String dat) {

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}