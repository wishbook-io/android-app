package com.wishbook.catalog.home.contacts.viewholder;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wishbook.catalog.R;

/**
 * Created by root on 15/11/16.
 */
public class Header_VIewHolder  extends RecyclerView.ViewHolder{
    public TextView title;
    public Header_VIewHolder(View view) {
        super(view);
        title= (TextView) view.findViewById(R.id.title);
    }
}
