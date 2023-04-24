package com.wishbook.catalog.commonadapters.dropdownadapters;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;

public class SpinAdapter_grouptypes extends ArrayAdapter<Response_BuyerGroupType> {

    private AppCompatActivity context;
    private Response_BuyerGroupType[] values;

    public SpinAdapter_grouptypes(AppCompatActivity context, int textViewResourceId,
                                  Response_BuyerGroupType[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.length;
    }

    public Response_BuyerGroupType getItem(int position){
       return values[position];
    }

    public long getItemId(int position){
       return position;
    }

    public int getItemById(String id) {
        for (int i=0;i<values.length;i++){
            if(values[i].getId().equals(id)){
                return i;
            }
        }
        return 0;
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
        //label.setTextColor(Color.BLACK);
        label.setText(values[position].getName());

        return label;
    }
}