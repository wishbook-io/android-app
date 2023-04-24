package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.login.models.Response_States;

public class SpinAdapter_State extends ArrayAdapter<Response_States> {

    private Activity context;
    private Response_States[] values;

    public SpinAdapter_State(Activity context, int resource, int textViewResourceId,
                             Response_States[] values) {
        super(context, resource, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount() {
        return values.length;
    }

    public Response_States getItem(int position) {
        return values[position];
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinneritem, parent, false);
            }
            TextView label = (TextView) row.findViewById(R.id.spintext);
            //label.setTextColor(Color.GREEN);
            label.setText(values[position].getState_name());
            return label;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        try {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinneritem_new, null);
            }
            TextView label = (TextView) row.findViewById(R.id.spintext);
            label.setText(values[position].getState_name());
            return label;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}