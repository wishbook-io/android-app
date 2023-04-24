package com.wishbook.catalog.home.contacts.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wishbook.catalog.R;

/**
 * Created by root on 14/11/16.
 */
public class Pending_Buyer_ViewHolder extends RecyclerView.ViewHolder {

    public TextView buyerName, buyerNumber, buyerStatus, Resend,Approve;

    public Pending_Buyer_ViewHolder(View view) {
        super(view);
        buyerName = (TextView) view.findViewById(R.id.buyer_name);
        buyerNumber = (TextView) view.findViewById(R.id.buyer_number);
        buyerStatus = (TextView) view.findViewById(R.id.buyer_status);
        Resend = (TextView) view.findViewById(R.id.resend);
        Approve = (TextView) view.findViewById(R.id.approve);


    }
}
