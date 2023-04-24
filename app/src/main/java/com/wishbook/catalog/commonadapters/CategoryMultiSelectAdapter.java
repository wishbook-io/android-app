package com.wishbook.catalog.commonadapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_Catagories;

/**
 * Created by root on 25/7/17.
 */

public class CategoryMultiSelectAdapter extends RecyclerView.Adapter<CategoryMultiSelectAdapter.ViewHolder> {

    private final AppCompatActivity context;
    private Response_Catagories[] mDataset;

    public Response_Catagories[] getCurrentData(){
        return mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.category_img);
            this.textView = (TextView) view.findViewById(R.id.category_name);
        }
    }

    public CategoryMultiSelectAdapter(AppCompatActivity context, Response_Catagories[] myDataset) {
        mDataset = myDataset;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mycategories_item_multiselect, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(mDataset[position].getCategory_name());
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
