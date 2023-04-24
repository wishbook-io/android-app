package com.wishbook.catalog.home.more.adapters.treeview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Invoiceitem_set;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Invoice_details_item_Adapter extends RecyclerView.Adapter<Invoice_details_item_Adapter.CustomViewHolder> {
    private Invoiceitem_set[] feedItemList;
    private Context mContext;

    public Invoice_details_item_Adapter(Context context, Invoiceitem_set[] feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
   /* public void setData(Response_Invoice[] filteredModelList) {
        this.feedItemList=filteredModelList;
        notifyDataSetChanged();
    }*/
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView item_type;
        private final TextView quantity;
        private final TextView rate;
        private final TextView amount;


        public CustomViewHolder(View view) {
            super(view);
            item_type = (TextView) view.findViewById(R.id.item_type);
                quantity = (TextView) view.findViewById(R.id.quantity);
            rate = (TextView) view.findViewById(R.id.rate);
            amount = (TextView) view.findViewById(R.id.amount);

           }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_detail_item, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        customViewHolder.amount.setText("\u20B9" + feedItemList[i].getAmount());
        customViewHolder.rate.setText("\u20B9" + feedItemList[i].getAmount());
        customViewHolder.quantity.setText( feedItemList[i].getQty());
        customViewHolder.item_type.setText( feedItemList[i].getItem_type());


    }

    private String getformatedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return feedItemList.length;
    }
}