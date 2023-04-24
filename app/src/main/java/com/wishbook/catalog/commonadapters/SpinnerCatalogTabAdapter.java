package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;

import java.util.List;

public class SpinnerCatalogTabAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> values;

    public SpinnerCatalogTabAdapter(Activity context, int textViewResourceId,
                                    List<String> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount() {
        return values.size();
    }

    public String getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinneritem, parent, false);
        }
        TextView label = (TextView) row.findViewById(R.id.spintext);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        label.setTextColor(Color.WHITE);
        label.setText(values.get(position));
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinneritem, parent, false);
        }
        TextView label = (TextView) row.findViewById(R.id.spintext);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position));
        return label;
    }
}