package com.wishbook.catalog.home.contacts.viewholder;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.R;

/**
 * Created by root on 14/11/16.
 */
public class Contact_ViewHolder extends RecyclerView.ViewHolder {
    private final ImageView textInt;
    public TextView contactName, contactNumber;
    public CheckBox contactCheckedCb;
    public AppCompatSpinner spinner_grouptype;
    public TextView invite_contact;
    public Contact_ViewHolder(View view) {
        super(view);
        contactName = (TextView) view.findViewById(R.id.contact_name);
        contactNumber = (TextView) view.findViewById(R.id.contact_number);
        invite_contact = (TextView) view.findViewById(R.id.invite_contact);
        contactCheckedCb = (CheckBox) view.findViewById(R.id.contact_cb);
        textInt = (ImageView) view.findViewById(R.id.textInt);
        spinner_grouptype = (AppCompatSpinner) view.findViewById(R.id.spinner_grouptype);
    }
}
