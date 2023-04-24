package com.wishbook.catalog.commonadapters.dropdownadapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.SellingCompany;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;

import java.util.ArrayList;

/**
 * Created by root on 31/1/17.
 */
public class SpinAdapterSupplier_MySelections  extends ArrayAdapter<SellingCompany> {

        private Activity context;
        private SellingCompany[] values;

        public SpinAdapterSupplier_MySelections(Activity context, int textViewResourceId,
                                                SellingCompany[] values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.length;
        }

        public SellingCompany getItem(int position){
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
