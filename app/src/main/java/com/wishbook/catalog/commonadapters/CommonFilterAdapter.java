package com.wishbook.catalog.commonadapters;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.postpatchmodels.CommmonFilterOptionModel;

import java.util.ArrayList;

/**
 * Created by root on 20/5/17.
 */

public class CommonFilterAdapter extends RecyclerView.Adapter<CommonFilterItemRecyclerViewHolder> {

    private ArrayList<CommmonFilterOptionModel> arrayList;
    private Context mContext;
    private ArrayList<Integer> selected = new ArrayList<Integer>();


    public CommonFilterAdapter(ArrayList arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public CommonFilterItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_filter_item_layout, parent, false);
        return new CommonFilterItemRecyclerViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final CommonFilterItemRecyclerViewHolder holder, final int position) {

        final CommmonFilterOptionModel model = arrayList.get(position);

        holder.filter_name.setText(model.getName());

        if(arrayList.get(holder.getAdapterPosition()).getSelected()){
            holder.is_selected.setChecked(true);
        }else{
            holder.is_selected.setChecked(false);
        }
        holder.filter_options_main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.is_selected.isChecked()){
                    holder.is_selected.setChecked(false);
                    arrayList.get(position).setSelected(false);
                    selected.remove(selected.indexOf(model.getId()));
                    Log.v("Filters", "item "+ model.getName() + "set " + model.getSelected());
                }else{
                    holder.is_selected.setChecked(true);
                    arrayList.get(position).setSelected(true);
                    selected.add(model.getId());
                    Log.v("Filters", "item "+ model.getName() + "set " + model.getSelected());
                }
            }
        });

        holder.is_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.is_selected.isChecked()){
                    //  holder.is_selected.setChecked(false);
                    arrayList.get(position).setSelected(true);
                    selected.add(model.getId());

                    Log.v("Filters", "item "+ model.getName() + "set " + model.getSelected());
                }else{
                    // holder.is_selected.setChecked(true);
                    arrayList.get(position).setSelected(false);
                    selected.remove(selected.indexOf(model.getId()));
                    Log.v("Filters", "item "+ model.getName() + "set " + model.getSelected());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    public ArrayList<Integer> getSelected(){

        return selected;
    }


    public void setSelected(ArrayList<Integer> selected) {
        this.selected = selected;
    }
}


class CommonFilterItemRecyclerViewHolder extends RecyclerView.ViewHolder  {
    // View holder for gridview recycler view as we used in listview
    public TextView filter_name;
    public CheckBox is_selected;
    public LinearLayout filter_options_main_container;


    public CommonFilterItemRecyclerViewHolder(View view) {
        super(view);
        // Find all views ids

        this.filter_options_main_container = (LinearLayout) itemView.findViewById(R.id.filter_options_main_container);

        this.filter_name = (TextView) view
                .findViewById(R.id.filter_name);
        this.is_selected = (CheckBox) view.findViewById(R.id.is_selected);

    }

}

