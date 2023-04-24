package com.wishbook.catalog.home.orders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.Fragment_SalesOrders_Holder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.CustomViewHolder> {
    private ArrayList<Response_sellingorder> feedItemList;
    private Context mContext;

    public SalesOrderAdapter(Context context, ArrayList<Response_sellingorder> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public void setData(ArrayList<Response_sellingorder> filteredModelList) {
        this.feedItemList=filteredModelList;
        notifyDataSetChanged();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView order_num;
        private final TextView totalrate;
        private final TextView companyname;
        private final TextView processstatus;
        private final TextView customerstatus;
        private final TextView date;
        private final TextView totalproducts;
        private final LinearLayout container;

        public CustomViewHolder(View view) {
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            totalrate = (TextView) view.findViewById(R.id.totalrate);
            companyname = (TextView) view.findViewById(R.id.companyname);
            processstatus = (TextView) view.findViewById(R.id.processstatus);
            date = (TextView) view.findViewById(R.id.date);
            totalproducts = (TextView) view.findViewById(R.id.totalproducts);
            container = (LinearLayout) view.findViewById(R.id.container);
            customerstatus = (TextView) view.findViewById(R.id.customerstatus);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.salesorderitem, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        if(feedItemList.get(i).getCompany_name()!=null) {
            customViewHolder.companyname.setText("For : "+StringUtils.capitalize(feedItemList.get(i).getCompany_name().toLowerCase().trim()));
            customViewHolder.companyname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hyperlinking
                    StaticFunctions.hyperLinking("buyer",feedItemList.get(i).getBuyer_table_id(),mContext);
                }
            });
        }
            customViewHolder.date.setText("Received On "+getformatedDate(StringUtils.capitalize(feedItemList.get(i).getDate().toLowerCase().trim())));
        customViewHolder.order_num.setText("#"+StringUtils.capitalize(feedItemList.get(i).getOrder_number().toLowerCase().trim()));
        customViewHolder.totalproducts.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_products() .toLowerCase().trim())+ " Pcs");
        customViewHolder.totalrate.setText("\u20B9" + feedItemList.get(i).getTotal_rate()+ " For ");
        customViewHolder.processstatus.setText(StringUtils.capitalize(StringUtils.capitalize(feedItemList.get(i).getProcessing_status().toLowerCase().trim())));
        setColor(feedItemList.get(i).getProcessing_status(),customViewHolder.processstatus);
        if(feedItemList.get(i).getPayment_status()!=null){
            customViewHolder.customerstatus.setText(StringUtils.capitalize(feedItemList.get(i).getPayment_status().toLowerCase().trim()));
            setColor(feedItemList.get(i).getPayment_status(),customViewHolder.customerstatus);
        }

        customViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeScaleUpAnimation(customViewHolder.container, 0,0,customViewHolder.container.getWidth(),customViewHolder.container.getHeight());

                Response_sellingorder order= new Response_sellingorder(feedItemList.get(i).getId());

                Application_Singleton.selectedOrder=order;

                Intent intent=new Intent(mContext, Activity_OrderDetailsNew.class);
                Bundle bundle=new Bundle();
                bundle.putBoolean("supplier",true);
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    if(Fragment_SalesOrders_Holder.fragment!=null) {
                        Fragment_SalesOrders_Holder.fragment.startActivityForResult(intent,ResponseCodes.SalesResponse,options.toBundle());
                    }else{
                        mContext.startActivity(intent,options.toBundle());
                    }
                }
                else{
                    if(Fragment_SalesOrders_Holder.fragment!=null) {
                        Fragment_SalesOrders_Holder.fragment.startActivityForResult(intent,ResponseCodes.SalesResponse);
                    }else{
                        mContext.startActivity(intent);
                    }
                }


                //((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new Activity_OrderDetails()).addToBackStack("orderdetails").commit();
                //Toast.makeText(mContext,"test"+i,Toast.LENGTH_SHORT).show();
            }
        });





    }

    private String getformatedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd, yyyy";
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
        return feedItemList.size();
    }

    public void setColor(String status,TextView textView) {
        if(status.equalsIgnoreCase("Draft") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Partially Paid")){
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        }
    }
}