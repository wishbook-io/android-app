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
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

public class SpinAdapter_CatalogsMini extends ArrayAdapter<Response_catalogMini> {

    private Activity context;
    private Response_catalogMini[] values;

    public SpinAdapter_CatalogsMini(Activity context, int textViewResourceId,
                                    Response_catalogMini[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }
    public SpinAdapter_CatalogsMini(Activity context, int textViewResourceId,
                                   ArrayList<Response_catalogMini> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        Response_catalogMini[] array = new Response_catalogMini[values.size()];
        this.values =values.toArray(array);
    }
    public int getCount(){
       return values.length;
    }

    public Response_catalogMini getItem(int position){
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
        label.setText(values[position].getTitle());

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
        label.setText(values[position].getTitle());

        return label;
    }
}