package com.wishbook.catalog.commonadapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.Response_Brands;

import java.util.ArrayList;

/**
 * Created by root on 11/4/17.
 */
public class BrandSellAdapter extends RecyclerView.Adapter<BrandSellAdapter.ViewHolder> {

private final AppCompatActivity context;
private ArrayList<Response_Brands> mDataset;

public ArrayList<Response_Brands> getCurrentData(){
        return mDataset;
        }

public class ViewHolder extends RecyclerView.ViewHolder {
    protected SimpleDraweeView imageView;
    protected TextView textView;

    public ViewHolder(View view) {
        super(view);
        this.imageView = (SimpleDraweeView) view.findViewById(R.id.brand_img);
        this.textView = (TextView) view.findViewById(R.id.brand_name);
     }
}

    public BrandSellAdapter(AppCompatActivity context, ArrayList<Response_Brands> myDataset) {
        mDataset = myDataset;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mybrandown_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getName());
        String image = mDataset.get(position).getImage().getThumbnail_small();
       /* if(mDataset.get(position).getTotal_catalogs()>1)
        {
            holder.count.setText(mDataset.get(position).getTotal_catalogs()+" Catalogs");
        }
        else {
            holder.count.setText(mDataset.get(position).getTotal_catalogs() + " Catalog");
        }*/
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(context,image,holder.imageView,R.drawable.uploadempty);
            StaticFunctions.loadFresco(context,image,holder.imageView);
            //Picasso.with(context).load(image).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}