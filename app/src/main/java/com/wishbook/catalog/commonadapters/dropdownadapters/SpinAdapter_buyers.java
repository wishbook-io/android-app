package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.models.BuyersList;

public class SpinAdapter_buyers extends ArrayAdapter<BuyersList> {

    private Activity context;
    private BuyersList[] values;

    public SpinAdapter_buyers(Activity context, int textViewResourceId,
                              BuyersList[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.length;
    }

    public BuyersList getItem(int position){
       return values[position];
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
        label.setText(values[position].getCompany_name());

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
        label.setText(values[position].getCompany_name());

        return label;
    }
}