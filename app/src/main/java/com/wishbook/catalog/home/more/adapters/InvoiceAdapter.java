package com.wishbook.catalog.home.more.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_Invoice;
import com.wishbook.catalog.home.more.invoice.InvoiceDetails;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.CustomViewHolder> {
    private Response_Invoice[] feedItemList;
    private Context mContext;

    public InvoiceAdapter(Context context, Response_Invoice[] feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
   /* public void setData(Response_Invoice[] filteredModelList) {
        this.feedItemList=filteredModelList;
        notifyDataSetChanged();
    }*/
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView order_num;
        private final TextView start_date;
        private final TextView end_date;
        private final TextView processstatus;
        private final TextView billing_amount;
        private final TextView balance_amount;
       private final LinearLayout container;

        public CustomViewHolder(View view) {
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            start_date = (TextView) view.findViewById(R.id.start_date);
            end_date = (TextView) view.findViewById(R.id.end_date);
            processstatus = (TextView) view.findViewById(R.id.processstatus);
            billing_amount = (TextView) view.findViewById(R.id.billed_amount);
            balance_amount = (TextView) view.findViewById(R.id.balance_amount);
            container = (LinearLayout) view.findViewById(R.id.container);
           }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_item, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {


        if(feedItemList[i].getStart_date()!=null) {
            customViewHolder.start_date.setText(getformatedDate(StringUtils.capitalize(feedItemList[i].getStart_date().toLowerCase().trim())));
        }
        if(feedItemList[i].getEnd_date()!=null) {
            customViewHolder.end_date.setText(getformatedDate(StringUtils.capitalize(feedItemList[i].getEnd_date().toLowerCase().trim())));
        }
        customViewHolder.order_num.setText("#"+StringUtils.capitalize(feedItemList[i].getId()));
        customViewHolder.billing_amount.setText("\u20B9" + feedItemList[i].getBilled_amount());
        customViewHolder.balance_amount.setText("\u20B9" + feedItemList[i].getBalance_amount());
        if(feedItemList[i].getStatus()!=null) {
            customViewHolder.processstatus.setText(StringUtils.capitalize(StringUtils.capitalize(feedItemList[i].getStatus().toLowerCase().trim())));
        }
       /* customViewHolder.customerstatus.setText(StringUtils.capitalize(feedItemList.get(i).getCustomer_status().toLowerCase().trim()));
        */customViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeScaleUpAnimation(customViewHolder.container, 0,0,customViewHolder.container.getWidth(),customViewHolder.container.getHeight());
                Application_Singleton.selectedInvoice=feedItemList[customViewHolder.getAdapterPosition()];
                Intent intent=new Intent(mContext, InvoiceDetails.class);
                    mContext.startActivity(intent);

                //((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new Activity_OrderDetails()).addToBackStack("orderdetails").commit();
                //Toast.makeText(mContext,"test"+i,Toast.LENGTH_SHORT).show();
            }
        });

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