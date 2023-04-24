package com.wishbook.catalog.login.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.wishbook.catalog.R;

import com.wishbook.catalog.login.models.TermsConditionModel;


/**
 * Created by root on 31/8/16.
 */
public class TermsConditionsAdapter extends RecyclerView.Adapter<TermsConditionsAdapter.CustomViewHolder>{

    Context context;
    private List<TermsConditionModel> rowItems=new ArrayList<>();

    public TermsConditionsAdapter(Context context,
                                 List<TermsConditionModel> items) {
        this.context = context;
        this.rowItems = items;
    }
    @Override
    public void onBindViewHolder(TermsConditionsAdapter.CustomViewHolder holder, int position) {

        holder.SubHeading.setText(rowItems.get(position).getSub_Heading());
        holder.SubDescription.setText(rowItems.get(position).getSub_Description());

    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terms_conditions_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView SubHeading,SubDescription;
        public CustomViewHolder(View view) {
            super(view);
            SubHeading = (TextView) view.findViewById(R.id.sub_heading);
            SubDescription = (TextView) view.findViewById(R.id.sub_description);

        }
    }
}
