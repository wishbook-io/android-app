package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.BuyerSeg;

public class SpinAdapter_buyergroups extends ArrayAdapter<BuyerSeg> {

    private Activity context;
    private ArrayList<BuyerSeg> values;

    public SpinAdapter_buyergroups(Activity context, int textViewResourceId,
                                   ArrayList<BuyerSeg> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.size();
    }

    public BuyerSeg getItem(int position){
       return values.get(position);
    }

    public long getItemId(int position){
       return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinneritem, parent, false);
        }
        TextView label = (TextView)row.findViewById(R.id.spintext);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getSegmentation_name());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinneritem, parent, false);
        }
        TextView label = (TextView)row.findViewById(R.id.spintext);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getSegmentation_name());

        return label;
    }
}