package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.Response_Brands;

public class SpinAdapter_brands extends ArrayAdapter<Response_Brands> {

    private Activity context;
    private Response_Brands[] values;

    public SpinAdapter_brands(Activity context, int textViewResourceId,
                              Response_Brands[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.length;
    }

    public Response_Brands getItem(int position){
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
        int padding = StaticFunctions.dpToPx(context,9);
        label.setPadding(padding,padding,padding,padding);
        label.setText(values[position].getName());

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
        label.setText(values[position].getName());

        return label;
    }
}