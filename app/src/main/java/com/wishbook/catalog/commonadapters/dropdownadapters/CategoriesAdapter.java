package com.wishbook.catalog.commonadapters.dropdownadapters;

/**
 * Created by Vigneshkarnika on 22/03/16.
 */

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.wishbook.catalog.home.more.adapters.RowItem;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final AppCompatActivity context;
    private ResponseHomeCategories[] mDataset;
    private ArrayList<RowItem> rowItems = new ArrayList<>();

    public ResponseHomeCategories[] getCurrentData() {
        return mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected SimpleDraweeView imageView;
        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (SimpleDraweeView) view.findViewById(R.id.category_img);
            this.textView = (TextView) view.findViewById(R.id.category_name);
        }
    }

    public CategoriesAdapter(AppCompatActivity context, ResponseHomeCategories[] myDataset, ArrayList<RowItem> RowItems) {
        mDataset = myDataset;
        this.context = context;
        this.rowItems = RowItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mycategories_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textView.setText(mDataset[position].getCategory_name());
        try {
            if (mDataset[position].getImage().getThumbnail_small() != null) {
                StaticFunctions.loadFresco(context, mDataset[position].getImage().getThumbnail_small(), holder.imageView);
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.finish();
                    String url = "https://app.wishbook.io/?type=catalog&ctype=public&view_type=public&category="+mDataset[holder.getAdapterPosition()].getId();
                    Uri intentUri = Uri.parse(url);
                    new DeepLinkFunction(SplashScreen.getQueryString(intentUri),context);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}