package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.login.models.Response_Cities;

public class SpinAdapter_City extends ArrayAdapter<Response_Cities> {

    private Activity context;
    private Response_Cities[] values;

    public SpinAdapter_City(Activity context, int textViewResourceId,
                            Response_Cities[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.length;
    }

    public Response_Cities getItem(int position){
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
        //label.setTextColor(Color.BLACK);
        try {
            label.setText(values[position].getCity_name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinneritem_new, parent, false);
        }
        TextView label = (TextView)row.findViewById(R.id.spintext);
       // label.setTextColor(Color.BLACK);
        label.setText(values[position].getCity_name());

        return label;
    }
}