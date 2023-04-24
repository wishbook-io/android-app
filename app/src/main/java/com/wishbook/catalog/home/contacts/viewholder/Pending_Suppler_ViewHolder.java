package com.wishbook.catalog.home.contacts.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wishbook.catalog.R;

/**
 * Created by root on 14/11/16.
 */
public class Pending_Suppler_ViewHolder extends RecyclerView.ViewHolder {

    public TextView sellerName, sellerNumber,sellerStatus,Resend,Approve;;
    public Pending_Suppler_ViewHolder(View view) {
        super(view);
        sellerName = (TextView) view.findViewById(R.id.seller_name);
        sellerNumber = (TextView) view.findViewById(R.id.seller_number);
        sellerStatus = (TextView) view.findViewById(R.id.seller_status);
        Resend = (TextView) view.findViewById(R.id.resend);
        Approve = (TextView) view.findViewById(R.id.approve);
    }
}
